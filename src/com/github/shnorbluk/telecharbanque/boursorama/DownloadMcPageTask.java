package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class DownloadMcPageTask extends AsynchTask<String[]>
{
	private int numpage;
	private int pageIndex;
	private int nbOfPages;
	private MoneycenterClient client;
	private boolean reloadListPage;
	private static final String TAG="DownloadMcPageTask";
	private static String csvFileName;
	private final SQLiteMoneycenter db;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public void setReloadListPage (boolean reloadListPage) {
	 this.reloadListPage=reloadListPage;
	}

	public void setMoneycenterClient (MoneycenterClient client) {
		this.client=client;
	}
	
	public void setCsvFileName(String csvFile) {
		this.csvFileName=csvFile;
	}
	public DownloadMcPageTask(int numpage, int pageIndex, int nbOfPages, SQLiteMoneycenter db)
	{
		this.numpage = numpage;
		this.pageIndex = pageIndex;
		this.nbOfPages = nbOfPages;
		this.db = db;
	}
	
	@Override
	protected void onPreExecute () {
		display("Page "+pageIndex+" sur "+nbOfPages+": Page "+numpage, false);
	}
	
	private static void logd(Object... o) {
		Utils.logd(TAG,o);
	}
	
	private List<McOperationInDb> parseListPage(String extract) throws PatternNotFoundException, IOException, ConnectionException, ParseException {
		List<McOperationInDb> list = new ArrayList<McOperationInDb>(42);
		String[] opes=extract.split("<tr");
        MoneycenterOperationFromList previousOpe=null;
		for (int partnum=1; partnum<opes.length; partnum++) {
			display("Opération "+ partnum+" sur "+ (opes.length-1)+" de la page "+numpage, false);
			String extr=opes[partnum];
			if ( extr .indexOf("class=\"createRule\"")>0) {
				logd("Proposition de catégorie");
				continue;
			}
			MoneycenterOperationFromList opeFromList = MoneycenterParser. getOperationFromListExtract( extr, previousOpe);
            previousOpe=opeFromList;
			logd("Récupération de l'opération ", opeFromList.getId());
			McOperationFromEdit opFromEdit=client.getOperation(opeFromList.getId(), Configuration.isReloadOperationPages());
			McOperationInDb opeForDb = new McOperationInDb();
			opeForDb.setChecked(opeFromList.isChecked());
			opeForDb.setParent( opeFromList.getParent());
			opeForDb.setAccount(opeFromList.getAccount());
			opeForDb.setCategoryLabel(opeFromList.getCategoryLabel());
			opeForDb.setLibelle(opFromEdit.getLibelle());
			opeForDb.setId(opeFromList.getId());
			opeForDb.setAmount(opeFromList.getAmount());
			opeForDb.setDate(opeFromList.getDate());
			opeForDb.setMemo(opFromEdit.getMemo());
			opeForDb.setNumCheque(opeFromList.getNumCheque());
			logd("Enregistrement de l'opération "+opeForDb.getId()+" dans la base de données");
			db.setOperation(opeForDb);
			list.add(opeForDb);
		}
		return list;
	}
	
	protected String[] doInBackground(String... pages) {
		List<McOperationInDb> operationList = new ArrayList<McOperationInDb>(42);
			int debut = -1,fin=-1;
		try
		{
			db.open();
			BufferedReader html = client.getHClient().getReaderFromUrl( "https://www.boursorama.com/patrimoine/moneycenter/monbudget/operations.phtml?page=" + numpage, null, reloadListPage, "</tbody>");
            String extract=Utils.getExtract(html, "liste-operations-page", "</tbody>");
			operationList.addAll ( parseListPage (extract));

		}
		catch (Exception e)
		{
			displayError(e);
            return null;
		}
		String text="";
		logd("Nombre d'opérations à exporter:",operationList.size());
		for ( McOperationInDb operation : operationList) {
			String id = operation.getId();
			for ( MoneycenterProperty property:MoneycenterProperty.values()) {
				text+=id+"."+ property.getName() + ".synced="+ property.getValue( operation )+"\n";
			}
			text+="\n";
		}
		logd("Texte a sauver pour la page:", text);
		String csv="";
		for ( McOperationInDb op: operationList) {
			String memo=op.getMemo();
			boolean checked =op.isChecked ();
			String date =DATE_FORMAT.format(op.getDate ());
			String compte =op.getAccount ();
			String libelleLong = op.getLibelle();
			String categ = op.getCategoryLabel ();
			float montant = op.getAmount ();
			String id = op.getId ();
			String parent = op.getParent ();
			String csvline = (checked?"O":"N")+
				";"+date+";"+MoneycenterPersistence.csvEscape(compte)+";"+libelleLong+
				";"+categ+";"+montant+";"+memo+";"+id+
				";"+MoneycenterPersistence.csvEscape(parent); 
			csv+=csvline+ "\n";
		}
		logd("CSV a sauver pour la page:", text);
	return new String[]{text,csv};
	}

 @Override
 protected void onPostExecute(String[] result) {
     if (result != null) {
         try
         {
             Utils.writeToFile(result[0], MoneycenterPersistence.PERSISTENCE_FILE, true);
             Utils.writeToFile(result[1], csvFileName, true);
             display("La page "+numpage+" a été téléchargée.", true);
         }
         catch (IOException e)
         {
             displayError(e);
         }
     }
 }

 private void displayError(Exception e) {
	 display( "Erreur dans la récupération de la page "+numpage+":"+e, true );
	 Log.e(TAG, "Erreur dans la récupération de la page "+numpage,e);
 }

}
