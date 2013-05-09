package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import org.apache.http.client.*;

public class UpdateOperationsTask extends AsynchTask<String>
{
 private MoneycenterPersistence persistence;
	protected static String TAG="UpdateOperationsTask";

 public UpdateOperationsTask( HttpClient httpClient) {
  this. persistence = new MoneycenterPersistence (httpClient, this);
  if (Configuration.isSimuMode()) {
   persistence.setSimulationMode();
  }
 }

 void setSimulationMode () {
  persistence. setSimulationMode();
 }

 /** The system calls this to perform work in a worker thread and
   * delivers it the parameters given to AsyncTask.execute()
   */
 protected String doInBackground(String... m) {
  try {
   persistence.uploadPersistenceFile ();
  } catch (Exception e) {
   display(e.getMessage(), true);
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
