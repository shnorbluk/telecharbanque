package com.github.shnorbluk.telecharbanque;

import android.os.*;
import android.util.*;
import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.util.*;
import org.apache.http.client.*;

public class UpdateOperationsTask extends AsyncTask<String, String, String> implements UI 
{
 private MoneycenterPersistence persistence;

 public UpdateOperationsTask( HttpClient httpClient) {
  this. persistence = new MoneycenterPersistence (httpClient, this);
  if (Configuration.isSimuMode()) {
   persistence.setSimulationMode();
  }
 }

 public void display(String... messages) {
   Utils.logd(TAG, "display(",messages, ") ");
   publishProgress( messages);
  }

 void setSimulationMode () {
  persistence. setSimulationMode();
 }

 private static void logd (Object... objects) {
  String message="";
  for (Object o: objects) {
   message += Utils.toString(o);
  }
  Log.d(TAG, message + " (" + Thread.currentThread().getStackTrace()[3]+")");
 }
  
  public void onProgressUpdate(String... messages) {
    MainActivity.display(messages);
  }
   
  private static String TAG= "UpdateOperationsTask";
 /** The system calls this to perform work in a worker thread and
   * delivers it the parameters given to AsyncTask.execute()
   */
 protected String doInBackground(String... m) {
  try {
   persistence.uploadPersistenceFile ();
  } catch (Exception e) {
   publishProgress(e.getMessage());
   Log.e(TAG, "Erreur", e);
  }
  return null;
 }

/** The system calls this to perform work in the UI thread and delivers
  * the result from doInBackground()
  */
 protected void onPostExecute() {
  
 }
}
