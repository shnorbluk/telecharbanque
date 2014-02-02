package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.orman.mapper.*;

public class MoneycenterClient
{
//	private final BoursoramaClient bClient;
 private SessionedBufferedHttpClient hclient;
 private static String TAG = "MoneycenterClient";
 private final UI gui;
	private static final String URL_EDIT_OPERATION = "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operation_edit.phtml";

	public MoneycenterClient(SessionedBufferedHttpClient<MoneycenterSession> mhClient, UI gui) {
  this.gui=gui;
  		this.hclient=mhClient;
	//final MoneycenterSession session = new MoneycenterSession(httpClient, gui);
//	 hclient= new HClient(httpClient, session, gui, bToken);
//	 bClient = new BoursoramaClient(httpClient,gui
 }

 	public int getNbOfPages() throws ConnectionException {
		return hclient.getSessionInformation();
	}
 //public HClient getHClient()
 //{
	// return hclient;
 //}

 //void setSimulationMode ( boolean simuMode) {
  //bClient. setSimulationMode( simuMode);
  //hclient=new FakeHClient(gui);
 //}
	public List<McOperationInDb> parseListPage(boolean reloadListPage, UI gui, int numpage) throws PatternNotFoundException, IOException, ConnectionException, ParseException {
		logd("parseListPage(",reloadListPage, numpage);
		BufferedReader html = hclient.getReaderFromUrl( "https://www.boursorama.com/patrimoine/moneycenter/monbudget/operations.phtml?page=" + numpage, null, reloadListPage, "</tbody>");
		String extract=Utils.getExtract(html, "liste-operations-page", "</tbody>");
		List<McOperationInDb> list = new ArrayList<McOperationInDb>(42);
		String[] opes=extract.split("<tr");
        MoneycenterOperationFromList previousOpe=null;
		for (int partnum=1; partnum<opes.length; partnum++) {
			gui.display("Opération "+ partnum+" sur "+ (opes.length-1)+" de la page "+numpage, false);
			String extr=opes[partnum];
			if ( extr .indexOf("class=\"createRule\"")>0) {
				logd("Proposition de catégorie");
				continue;
			}
			MoneycenterOperationFromList opeFromList = MoneycenterParser. getOperationFromListExtract( extr, previousOpe);
            previousOpe=opeFromList;
			String id = opeFromList.getId();
			logd("Récupération de l'opération ", id);

			//McOperationInDb opeFromDb = db.getOperation(id);
			final McOperationInDb opeFromDb = Model.fetchSingle(
				ModelQuery.select()
				.from(McOperationInDb.class)
				.where( C.eq(McOperationInDb.class, "id", id))
				.getQuery(), McOperationInDb.class);
			logd("Operation trouvee en base:",opeFromDb);
			final McOperationInDb opeForDb;
			if (opeFromDb == null) {
				opeForDb = new McOperationInDb();
			} else {
				opeForDb = opeFromDb;
				opeForDb.setUpToDate(!opeFromList.equals(opeFromDb));
			}
			McOperationFromEdit opFromEdit=this.getOperation(id, Configuration.isReloadOperationPages());
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
			logd("Enregistrement de l'opération "+opeForDb.getId()+" dans la base de données");
			list.add(opeForDb);
		}
		return list;
	}
 void pointeOperation(String id, boolean pointed) throws IOException, ConnectionException {
  hclient .loadString( 
    "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operations_check.phtml" ,
    new String[]{ "operations["+id+"]" ,pointed?"yes":"no"  }, true,"") ;
  gui.display("Opération "+id+" pointée.", true);
  logd("pointeOperation ",Thread.currentThread().getStackTrace());
 }

 private void logd(Object... o) {
	 Utils.logd(TAG,o);
 }

 public void postOperation (McOperationFromEdit ope) throws UnexpectedResponseException, IOException, ConnectionException {
   String[] params=ope.getAsParams();
	 String expected="Votre opération a bien été éditée";
	 expected="Votre op.ration a bien .t. .dit.e";
	 String file ="postOperation"+ope.getId();
   BufferedReader result = hclient .getReaderFromUrl( URL_EDIT_OPERATION , params, true,"", file) ;
   String line;
   logd("Lecture de la page ", file);
   while ((line= result.readLine())!= null) {
	   if (line.contains("Votre")) {
	   	 logd(line);
		}
   }
   result.reset();
   Scanner scanner=new Scanner(result);
   logd(scanner.findWithinHorizon("Votre", 0));
   scanner.reset();
   boolean found = scanner.findWithinHorizon(expected,0)!=null;
   scanner.close();
	markOperationPageAsObsolete(ope.getId());
   if(found) {
    gui.display("L'opération "+ope.getId()+" a été mise à jour avec succès.", true);
   } else {
    String message = "Erreur lors de la mise à jour de l'opération " +ope.getId()+".\n";
	message+= "Le texte '"+expected+"' n'est pas présent dans la réponse.\n";
	message+= "La réponse a été enregistrée dans le fichier "+file;
    gui.display( message, true); 
    Log.d(TAG, ope.getAsParams().toString());
    Log.d(TAG, Utils.toString(ope.getAsParams()));
    throw new UnexpectedResponseException (message);
   }
  }

	private BufferedReader getOperationPageAsReader(String ID_OPERATION, boolean fromNet ) throws IOException , ConnectionException{
		String[] params=new String[]{
			"id",  ID_OPERATION};
		return hclient.getReaderFromUrl( URL_EDIT_OPERATION ,params, fromNet, "new_groupings\\[\\]" );
	}
 
 private void markOperationPageAsObsolete(String id) {
	 String[] params=new String[]{
		 "id",  id};
	 hclient.markAsObsolete( URL_EDIT_OPERATION ,params);
 }

 private MoneycenterOperation getOperation( MoneycenterOperation operationFromListExtract, boolean forceReload ) {
	 
	 return operationFromListExtract;
 }
 
public McOperationFromEdit getOperation(String id, boolean online) throws IOException, PatternNotFoundException, ConnectionException, ParseException {
  BufferedReader html=getOperationPageAsReader(id, online );
  try {
	  McOperationFromEdit opeFromEdit = new McOperationFromEdit(html, id);
	  return opeFromEdit;
  } catch ( PatternNotFoundException e ) {
   gui.display("La chaine '"+
     e.getPattern()+
	 "' n'a pas été trouvée dans la page de l'opération "+id, true);
   throw e;
  } finally {
	  html.close();
  }
 }
	
}

