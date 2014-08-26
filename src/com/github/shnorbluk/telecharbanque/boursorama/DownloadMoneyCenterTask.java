package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;
import java.util.*;
import org.apache.http.client.*;
import java.io.*;
import java.util.concurrent.*;

public class DownloadMoneyCenterTask 
  extends AsynchTask<String>
{
 protected static String TAG="DownloadMoneyCenterTask";
 private MoneycenterPersistence persistence;
//	private final SessionedBufferedHttpClient<BoursoramaClient> bhClient;
 //	private final HttpClient httpClient;
	
	public DownloadMoneyCenterTask ( HttpClient httpClient, Token boursoramaToken) {
	//	final BufferedHttpClient nhClient = new BufferedHttpClient(this, httpClient);
	//	final BoursoramaClient bClient = new BoursoramaClient(nhClient, this, token);
	//	bhClient= new SessionedBufferedHttpClient<BoursoramaClient>(nhClient, bClient);
	//	this.httpClient=httpClient;
		super();
		final MoneycenterClient mcClient= new MoneycenterClient(httpClient, this, boursoramaToken);
		this. persistence = new MoneycenterPersistence(mcClient);
 }

   /** The system calls this to perform work in a worker thread and
   * delivers it the parameters given to AsyncTask.execute()
   */
 protected String doInBackground (String... s) {
  try {
	  display("Téléchargement de toutes les pages jusqu'à aujourd'hui", false);
	  downloadAllPagesToDate(new Date());
	  final Calendar cal = Calendar.getInstance();
	  cal.add(Calendar.MONTH, -3);
	  downloadAllPagesToDate(cal.getTime()); 
  } catch (Exception e) {
   display(e.toString(), true);
   Log.e(TAG, "Erreur", e);
  }
  return null;
 }
	private void downloadAllPagesToDate(Date dateFin) throws IOException, ExecutionException, ConnectionException, InterruptedException{
		boolean saveAllMcHistory=Configuration.isSaveAllMcHistory();
		int firstPage= Configuration.getFirstPage();
		int lastPage=Configuration.getLastPage();
		boolean reloadListPages=Configuration.isReloadListPages();
		boolean saveUnchecked=true;
	//	final MoneycenterSession mcSession = new MoneycenterSession(bhClient, this, dateFin);
	//	SessionedBufferedHttpClient<MoneycenterSession> mhClient = new SessionedBufferedHttpClient<MoneycenterSession>(bhClient, mcSession);
		 
		persistence.downloadToPersistence(
			saveAllMcHistory, firstPage, lastPage, reloadListPages, saveUnchecked, dateFin);
	}
                                                                                                                                                                                                                                                                                                                                                                                                                        
}
