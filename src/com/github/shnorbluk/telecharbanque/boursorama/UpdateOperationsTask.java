package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import org.apache.http.client.*;
import com.github.shnorbluk.telecharbanque.net.*;
import org.apache.http.client.params.*;

public class UpdateOperationsTask extends AsynchTask<String>
{
 private MoneycenterPersistence persistence;
	protected static String TAG="UpdateOperationsTask";

 public UpdateOperationsTask( HttpClient httpClient, Token token) {
	 final BufferedHttpClient hClient = new BufferedHttpClient(this, httpClient);
	 final BoursoramaClient bClient = new BoursoramaClient(hClient, this, token);
	 final SessionedBufferedHttpClient<BoursoramaClient> bhClient = new SessionedBufferedHttpClient<BoursoramaClient>(hClient, bClient);
	 final MoneycenterSession mcSession = new MoneycenterSession(bhClient, this);
	 final SessionedBufferedHttpClient<MoneycenterSession> mchClient = new SessionedBufferedHttpClient<MoneycenterSession>(bhClient, mcSession);
	 final MoneycenterClient mcClient = new MoneycenterClient(mchClient, this);
	 this. persistence = new MoneycenterPersistence (mcClient, this);
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
