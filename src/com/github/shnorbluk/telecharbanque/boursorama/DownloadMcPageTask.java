package com.github.shnorbluk.telecharbanque.boursorama;

import android.os.*;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;

public class DownloadMcPageTask extends AsynchTask<String[]>
{
	private int numpage;
	private int pageIndex;
	private int nbOfPages;
	private MoneycenterClient client;
	private boolean reloadPages;
	private static final String TAG="DownloadMcPageTask";
	private static String csvFileName;

	public void setReloadPages (boolean reloadPages) {
	 this.reloadPages=reloadPages;
	}

	public void setMoneycenterClient (MoneycenterClient client) {
		this.client=client;
	}
	
	public void setCsvFileName(String csvFile) {
		this.csvFileName=csvFile;
	}
	public DownloadMcPageTask(int numpage, int pageIndex, int nbOfPages)
	{
		this.numpage = numpage;
		this.pageIndex = pageIndex;
		this.nbOfPages = nbOfPages;
	}
	
	@Override
	protected void onPreExecute () {
		display("Page "+pageIndex+" sur "+nbOfPages+": Page "+numpage, false);
	}
	
	private static void logd(Object o) {
		Utils.logd(TAG,o);
	}

	public void onProgressUpdate(String... messages) {
		MainActivity.display(messages);
	}
	
	private List<MoneyCenterOperation> parseListPage(String extract) throws PatternNotFoundException, IOException, ConnectionException {
		List<MoneyCenterOperation> list = new ArrayList<MoneyCenterOperation>();
		String[] opes=extract.split("<tr");
		for (int partnum=1; partnum<opes.length; partnum++) {
			display("Opération "+ partnum+" sur "+ (opes.length-1)+" de la page "+numpage, false);
			String extr=opes[partnum];
			if ( extr .indexOf("class=\"createRule\"")>0) {
				logd("Proposition de catégorie");
				continue;
			}
			MoneyCenterOperation ope = MoneycenterParser. getOperationFromListExtract( extr);
			MoneyCenterOperation op=client.getOperation(ope.getId(), Configuration.isReloadOperationPages());
			op.setChecked( ope.isChecked());
			op.setParent( ope.getParent());
			op.setAccount(ope.getAccount());
			op.setCategoryLabel(ope.getCategoryLabel());
			list.add( op);
		}
		return list;
	}
	
	protected String[] doInBackground(String... pages) {
		List<MoneyCenterOperation> operationList = new ArrayList<MoneyCenterOperation>(42);
			int debut = -1,fin=-1;
			StringBuffer html;
		try
		{
			html = client.getHClient().loadString(
				"https://www.boursorama.com/patrimoine/moneycenter/monbudget/operations.phtml?page=" + numpage, null, reloadPages, "</tbody>");
			debut=html.indexOf( "liste-operations-page")+24;
			fin= html.indexOf( "</tbody>", debut ); 
			String extract=html.substring(debut,fin);
			operationList.addAll ( parseListPage (extract));

		}
		catch (Exception e)
		{
			displayError(e);
		}
		String text="";
		for ( MoneyCenterOperation operation : operationList) {
			String id = operation.getId();
			for ( MoneycenterProperty property:MoneycenterProperty.values()) {
				text+=id+"."+ property.getName() + ".synced="+ property.getValue( operation )+"\n";
			}
			text+="\n";
		}
		String csv="";
		for ( MoneyCenterOperation op: operationList) {
			String memo=op.getMemo();
			boolean checked =op.isChecked ();
			String date =op.getDate ();
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
	return new String[]{text,csv};
	}

 @Override
 protected void onPostExecute(String[] result) {
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

 private void displayError(Exception e) {
	 display( "Erreur dans la récupération de la page "+numpage+e, true );
 }

}
