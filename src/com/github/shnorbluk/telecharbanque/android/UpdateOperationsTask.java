package com.github.shnorbluk.telecharbanque.android;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.BoursoramaClient;
import com.github.shnorbluk.telecharbanque.boursorama.MoneycenterClient;
import com.github.shnorbluk.telecharbanque.boursorama.MoneycenterPersistence;
import com.github.shnorbluk.telecharbanque.util.*;
import org.apache.http.client.*;
import com.github.shnorbluk.telecharbanque.net.*;
import org.apache.http.client.params.*;

public class UpdateOperationsTask extends AsynchTask<String>
{
 private MoneycenterPersistence persistence;
	protected static String TAG="UpdateOperationsTask";

 public UpdateOperationsTask( HttpClient httpClient, Token boursoramaToken) {
	 final BufferedHttpClient bufferedHttpClient = new BufferedHttpClient(this, httpClient);
	 final BoursoramaClient bClient = new BoursoramaClient(bufferedHttpClient, this, boursoramaToken);
	 
//	 final MoneycenterSession mcSession = new MoneycenterSession(bhClient, this);
//	 final BoursoramaClient mchClient = new BoursoramaClient(bufferedHttpClient, bClient);
	 final MoneycenterClient mcClient = new MoneycenterClient(httpClient, this, boursoramaToken);
	 this. persistence = new MoneycenterPersistence (mcClient);
  //if (Configuration.isSimuMode()) {
  // persistence.setSimulationMode();
 // }
 }

 //void setSimulationMode () {
//  persistence. setSimulationMode();
 //}

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
