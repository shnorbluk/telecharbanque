package com.github.shnorbluk.telecharbanque.boursorama;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.orman.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;

public class MoneycenterSession extends SessionedBufferedHttpClient
{
 //private final SessionedBufferedHttpClient boursoramaHttpClient;
 private int nbOfPages = -1;
 private static final Logger LOGGER = LoggerFactory.getLogger(MoneycenterSession.class);
 private final Date dateFin;
 private boolean connected = false;
 	private final BoursoramaClient boursoramaHttpClient;
	private final MoneycenterClient mcClient;
	private boolean reloadListPages = false;
	private boolean reloadOperationPages = false;
 
	public MoneycenterSession(final MoneycenterClient mcClient, Date dateFin ) {
		super(mcClient.getBoursoramaClient());
		this.mcClient = mcClient;
		boursoramaHttpClient = mcClient.getBoursoramaClient();
		this.dateFin=dateFin;
	//	mcHttpClient = new SessionedBufferedHttpClient<MoneycenterSession>(basicHttpClient, mcSession);
	}
	
//	public SessionedBufferedHttpClient<BoursoramaClient> getHttpClient() {
//		return boursoramaHttpClient;
//	}
	
	public void setReloadListPages(boolean reload) {
		this.reloadListPages = reload;
	}
	public boolean isConnected() {
		return connected;
	}
	public List<McOperationInDb> parseListPage(boolean reloadListPage, int numpage) throws PatternNotFoundException, IOException, ConnectionException, ParseException {
		LOGGER.debug("parseListPage({},{})",reloadListPage, numpage);
		BufferedReader html = getReaderFromUrl(
			"https://www.boursorama.com/moneycenter/monbudget/index.phtml?page=" + numpage
			, null, reloadListPage, "</tbody>");
		String extract=Utils.getExtract(html, "liste-operations-page", "</tbody>");
		List<McOperationInDb> list = new ArrayList<McOperationInDb>(42);
		String[] opes=extract.split("<tr");
        MoneycenterOperationFromList previousOpe=null;
		for (int partnum=1; partnum<opes.length; partnum++) {
			displayMessage("Opération "+ partnum+" sur "+ (opes.length-1)+" de la page "+numpage, false);
			String extr=opes[partnum];
			if ( extr .indexOf("class=\"createRule\"")>0) {
				LOGGER.debug("Proposition de catégorie");
				continue;
			}
			MoneycenterOperationFromList opeFromList = MoneycenterParser. getOperationFromListExtract( extr, previousOpe);
            previousOpe=opeFromList;
			String id = opeFromList.getId();
			LOGGER.debug("Récupération de l'opération ", id);

			//McOperationInDb opeFromDb = db.getOperation(id);
			final McOperationInDb opeFromDb = Model.fetchSingle(
				ModelQuery.select()
				.from(McOperationInDb.class)
				.where( C.eq(McOperationInDb.class, "id", id))
				.getQuery(), McOperationInDb.class);
			LOGGER.debug("Operation trouvee en base:",opeFromDb);
			final McOperationInDb opeForDb;
			if (opeFromDb == null) {
				opeForDb = new McOperationInDb();
			} else {
				opeForDb = opeFromDb;
				opeForDb.setUpToDate(!opeFromList.equals(opeFromDb));
			}
			McOperationFromEdit opFromEdit=mcClient.getOperation(id, reloadOperationPages);
			opeForDb.setSaved(opeFromDb != null);
			opeForDb.setChecked(opeFromList.isChecked());
			opeForDb.setParent( opeFromList.getParent());
			opeForDb.setAccount(opeFromList.getAccount());
			opeForDb.setCategoryLabel(opeFromList.getCategoryLabel());
			opeForDb.setLibelle(opFromEdit.getLibelle());
			opeForDb.setId(id);
			opeForDb.setAmount(opeFromList.getAmount());
			opeForDb.setDate(opeFromList.getDate());
			opeForDb.setMemo(opFromEdit.getMemo());
			opeForDb.setNumCheque(opFromEdit.getNumCheque());
			LOGGER.debug("Enregistrement de l'opération "+opeForDb.getId()+" dans la base de données");
			list.add(opeForDb);
		}
		return list;
	}
	
 public int getSessionInformation () {
  return nbOfPages; 
 }

		
	public void connect () throws ConnectionException {
		final Calendar c = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		final StringBuilder url=new StringBuilder()
		.append("https://www.boursorama.com/")
  		.append("moneycenter/monbudget/index.phtml?")
		.append("filters%5Bsearch%5D=&type=all")
		.append("&category=1&subcategory=1&filter=")
		.append("&filters%5Bid_account%5D%5B0%5D=");
		c.add(Calendar.MONTH, 1);
		for (int i=0; i>=-36; i--) {
			c.add (Calendar.MONTH, -1);
 			final String month = sdf.format(c.getTime());
			url.append( "&filters%5Bmonths%5D%5B")
			.append(month).append("%5D=").append(month);
		}
		try{
		final StringBuffer html= boursoramaHttpClient.loadString(
			url.toString(), null, true,"");
		 connected=true;
		 LOGGER.debug("Categories");
  HashMap<String,String> categories = new HashMap< String,String >(); 
  String regex="\\{'type':'([^']+)','category':'([^']+)'\\}\">([^<]*)</a><b" ;
  //Matcher matcher= Pattern.compile(regex).matcher(html);
  Scanner scanner = new Scanner(html.toString());
  StringBuilder sb = new StringBuilder();
		String found = scanner.findWithinHorizon(regex, 0);
  		while (found != null) {
			Matcher matcher= Pattern.compile(regex).matcher(found);
			matcher.find();
			String categ=matcher.group(1)+"."+
				matcher.group(2);
			String categName=matcher.group(3);
  			found = scanner.findWithinHorizon(regex, 0);
	  		categories.put( categ, categName);
  		}
  //while (matcher.find()){
//	  String categ=matcher.group(1)+"."+
//		  matcher.group(2);
//	  String categName=matcher.group(3);
//   categories.put( categ, categName);
//  }

		regex = "\\{'type':'([^']+)','category':'([^']+)',subcategory:'([^']*)'\\}\">([^<]*)</a>";
		Pattern pattern = Pattern.compile(regex);
		scanner= new Scanner(html.toString());
		for (String result = ""; result != null;) {
			result = scanner.findWithinHorizon(pattern, 0);
			if (result != null) {
				Matcher matcher= pattern.matcher(found);
				matcher.find();
				String categ=matcher.group(1)+'.'+matcher.group(2);
				sb.append("#categories.").
					append(categ).
					append('.').
					append(matcher.group(3)).
					append('=').
					append(categories.get(categ)).
					append(',').
					append(matcher.group(4)).
					append('\n');
			}
		}
		
  //matcher= Pattern.compile( regex
  //  ).matcher(html);
  //while (matcher.find()){ 
  // String categ=matcher.group(1)+'.'+matcher.group(2);
  // sb.append("#categories.").
	//	  append(categ).
	//	  append('.').
//		  append(matcher.group(3)).
//		  append('=').
//		  append(categories.get(categ)).
//		  append(',').
//		  append(matcher.group(4)).
//		  append('\n');
   //Log.d( TAG ,  categories.get(matcher.group(1)+ matcher.group(2)) +", "+ matcher.group(4) +" "+ matcher.group(3) );
  //}
  Utils.writeToFile(sb.toString(), "/sdcard/Temp/telecharbanque/categories.txt", false);
		findNbOfPages(html);
  	} catch (IOException e) {
		throw new ConnectionException(e);
	}
}
	
private void findNbOfPages(StringBuffer html) {
  String pages = Utils.getExtract ( html , "pagination","</div>");
  String nbPagesStr = "";
  int index = pages.lastIndexOf("page=")+5;
  while (true) {
   char c = pages.charAt(index);
   if (!Character.isDigit (c)) break;
   nbPagesStr+=c;
   index++;
  }
 
  nbOfPages= Integer.parseInt( nbPagesStr );
 }
}






