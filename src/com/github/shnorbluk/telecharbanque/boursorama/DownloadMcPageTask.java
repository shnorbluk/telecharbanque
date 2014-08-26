package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.http.client.*;

public class DownloadMcPageTask extends AsynchTask<String[]>
{
	private int numpage;
	private static final String TAG="DownloadMcPageTask";
	private static String csvFileName;
	private final MoneycenterSession mcSession;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public void setCsvFileName(String csvFile) {
		this.csvFileName=csvFile;
	}
	public DownloadMcPageTask(int numpage, MoneycenterSession mcSession) throws ConnectionException
	{
		super();
		this.numpage = numpage;
		this.mcSession = mcSession;
	}
	
	@Override
	protected void onPreExecute () {
	}
	
	private static void logd(Object... o) {
		Utils.logd(TAG,o);
	}
	
	protected String[] doInBackground(String... pages) {
		try
		{
			List<McOperationInDb> operationList = mcSession.parseListPage (Configuration.isReloadListPages(), this, numpage);
			String text="";
			logd("Nombre d'opérations à exporter:",operationList.size());
			for ( McOperationInDb operation : operationList) {
				String id = operation.getId();
				for ( MoneycenterProperty property:MoneycenterProperty.values()) {
					text+=id+"."+ property.getName() + ".synced="+ property.getValue( operation )+"\n";
				}
				text+="\n";
			}
			String csv="";
			for ( McOperationInDb op: operationList) {
				if (!op.isSaved()) {
					logd("insert ", op);
					op.insert();
				} else if (!op.isUpToDate()){
					op.update();
				}
				
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
			
			return new String[]{text,csv};
		}
		catch (PatternNotFoundException e) {
			display("La chaine '"+e.getPattern()+"' n'a pas été trouvée dans la page numéro "+numpage, true);
			return null;
		} 
		catch (Exception e)
		{
			displayError(e);
            return null;
		}
	}

 @Override
 protected void onPostExecute(String[] result) {
     if (result != null) {
         try
         {
             Utils.writeToFile(result[0], MoneycenterPersistence.PERSISTENCE_FILE, true);
             Utils.writeToFile(result[1], csvFileName, true);
             display("La page "+numpage+" a été téléchargée.", false);
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
