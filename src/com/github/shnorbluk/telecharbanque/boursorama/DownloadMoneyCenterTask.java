package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;
import java.util.*;
import org.apache.http.client.*;

public class DownloadMoneyCenterTask 
  extends AsynchTask<String>
{
 protected static String TAG="DownloadMoneyCenterTask";
 private MoneycenterPersistence persistence;
 static final String today=new SimpleDateFormat("d-M-yyyy").format(new Date());
	
 
	public DownloadMoneyCenterTask ( HttpClient httpClient, Token token ) {
		final BufferedHttpClient nhClient = new BufferedHttpClient(this, httpClient);
		final BoursoramaClient bClient = new BoursoramaClient(nhClient, this, token);
		SessionedBufferedHttpClient<BoursoramaClient> bhClient= new SessionedBufferedHttpClient<BoursoramaClient>(nhClient, bClient);
		final MoneycenterSession mcSession = new MoneycenterSession(bhClient, this);
		SessionedBufferedHttpClient<MoneycenterSession> mhClient = new SessionedBufferedHttpClient<MoneycenterSession>(bhClient, mcSession);
		MoneycenterClient mcClient= new MoneycenterClient(mhClient, this);
	 	this. persistence = new MoneycenterPersistence(mcClient, this);
		//this.persistence = mcPersistence;
 }

   /** The system calls this to perform work in a worker thread and
   * delivers it the parameters given to AsyncTask.execute()
   */
 protected String doInBackground (String... s) {
  try {
   boolean saveAllMcHistory=Configuration.isSaveAllMcHistory();
   int firstPage= Configuration.getFirstPage();
   int lastPage=Configuration.getLastPage();
   boolean reloadListPages=Configuration.isReloadListPages();
   boolean saveUnchecked=true;

   persistence.downloadToPersistence(
     saveAllMcHistory, firstPage, lastPage, reloadListPages, saveUnchecked) ;
  // persistence.exportToCsv(list); 
  } catch (Exception e) {
   display(e.toString(), true);
   Log.e(TAG, "Erreur", e);
  }
  return null;
 }


}
