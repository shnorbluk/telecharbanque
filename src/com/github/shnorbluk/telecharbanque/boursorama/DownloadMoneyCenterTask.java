package com.github.shnorbluk.telecharbanque.boursorama;
import android.os.*;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;
import java.util.*;
import org.apache.http.client.*;

public class DownloadMoneyCenterTask 
  extends AsyncTask<String, String, String> implements UI 
{
 private static String TAG="DownloadMoneyCenterTask";
 private MoneycenterPersistence persistence;
 static final String today=new SimpleDateFormat("d-M-yyyy").format(new Date());
 
 public DownloadMoneyCenterTask ( HttpClient httpClient) {
	 this. persistence = new MoneycenterPersistence(httpClient, this);
//	 persistence.setSimulationMode();
 }

 public void display(String... messages) {
   Log.d(TAG,"display("+Utils.toString(messages)+") "+ Thread.currentThread().getStackTrace()[3]);
   publishProgress( messages);
  }

  public void onProgressUpdate(String... messages) {
    MainActivity.display(messages);
  }
   
   /** The system calls this to perform work in a worker thread and
   * delivers it the parameters given to AsyncTask.execute()
   */
 protected String doInBackground (String... s) {
  try {
   boolean saveAll=Configuration.isSaveAllMcPages();
   int firstPage= Configuration.getFirstPage();
   int lastPage=Configuration.getLastPage();
   boolean reloadPages=Configuration.isReloadMcPages();
   boolean saveUnchecked=true;

   persistence.downloadToPersistence(
     saveAll, firstPage, lastPage, reloadPages, saveUnchecked) ;
   //persistence.exportToCsv(list); 
  } catch (Exception e) {
   publishProgress(e.toString());
   Log.e(TAG, "Erreur", e);
  }
  return null;
 }


}
