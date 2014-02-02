package com.github.shnorbluk.telecharbanque.net;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;

public class BufferedHttpClient
{
	private static final String TAG = "BufferedHttpClient";
	private static final String TEMP_DIR = "/sdcard/Temp/telecharbanque/";
	private static final boolean SIMU_MODE = Configuration.isSimuMode();
	
	private final UI currentTask;
	private final HttpClient httpClient;

	public BufferedHttpClient(UI currentTask, HttpClient httpClient )
	{
		this.currentTask = currentTask;
		this.httpClient = httpClient;
	}

	protected boolean connectIfNeeded() throws ConnectionException
	{
		return false;
	}
	
	public void markAsObsolete(String url, String[] params) {
		String file = generateFilenameForRequest(url,params);
		logd("Deleting file ",file, "because it is obsolete");
		boolean success = new File(file).delete();
		if (success) {
			logd("Fichier ",file, "supprimé avec succès");
		} else {
			Log.e(TAG, "Le fichier "+file+" n'a pas pu être supprimé.");
		}
	}
	
	@Deprecated
	private StringBuffer loadString(String url, String[] params, boolean fromNet, String patternToCheck, String fileName ) throws IOException, ConnectionException {
		String method=params==null?"get":"post";
		String filePath= TEMP_DIR+"/"+method+"/"+fileName+".html";
		Log.i(TAG, "Looking for file "+ filePath);
		File file=new File( filePath );
		boolean fileDoesNotExist = ! file.exists();
		boolean net= fromNet || fileDoesNotExist;
		logd("fileDoesNotExist="+fileDoesNotExist+",fromNet="+fromNet);
		if ( net ){
			return loadStringFromNet(url, params, filePath, patternToCheck );
		} else {
			return loadStringFromFile(url, params, filePath, patternToCheck );
		}
	}

	@Deprecated
	public StringBuffer loadString(String url, String[] params, boolean fromNet, String patternToCheck ) throws IOException, ConnectionException {
		String fileName= url;
		if (params!= null) {
			fileName+="%3F";
			for (int i=0; i<params.length; i+=2){
				fileName += params[i] + "=" + params[i+1] +"&";
			}
		}
		fileName = fileName .replace(":","") .replace("?","%3F").replace("|","%7C").replace("*","%2A").replace("\"", "%22");
		return loadString(url, params, fromNet, patternToCheck, fileName );
	}
	
	@Deprecated
	private StringBuffer loadStringFromFile(String url, String[] params, String filename, String patternToCheck) throws IOException, IllegalStateException, ConnectionException {
		logd("Loading file ", filename );
		StringBuffer str= Utils.readFile(filename,"iso-8859-1");
		if (str.toString().indexOf(patternToCheck) != -1) {
			return str;
		} else {
			logd("La chaine "+patternToCheck+" n'a pas été trouvée dans le fichier " + filename+". Nouveau téléchargement du fichier.");
			new File(filename).delete();
			return loadStringFromNet(url, params, filename, patternToCheck);
		}
	}
	
	@Deprecated
	protected StringBuffer loadStringFromNet(String url, String[] params, String filename, String patternToCheck) throws IOException, IllegalStateException, ConnectionException {
		for(int delay=1; delay<10; delay*=2) {
			if (params == null) {
				httpget (url). save(filename) ;
			} else {
				httppost (url, params). save(filename) ;
			}
			StringBuffer str=Utils.readFile(filename,"iso-8859-1");
			Log.d(TAG, "str="+str.substring(0,30)+" pattern='"+patternToCheck+"'");
			if (str.indexOf(patternToCheck)>=0) {
				return str;
			} else {
				currentTask.display ("Téléchargement de "+url+ " incomplet. Nouvelle tentative dans "+delay+" secondes.", true);
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ie) {
					currentTask.display(ie.getMessage(), true);
				}
			}
		}
		String error= "Échec de téléchargement de "+url;
		currentTask.display(error, true);
		throw new IOException(error);
	}
	
	private String generateFilenameForRequest(String url, String[] params) {
		String fileName= url;
		if (params!= null) {
			fileName+="%3F";
			for (int i=0; i<params.length; i+=2){
				fileName += params[i] + "=" + params[i+1] +"&";
			}
		}
		fileName = fileName .replace(":","") .replace("?","%3F").replace("|","%7C").replace("*","%2A").replace("\"", "%22");
		return fileName;
	}
	
	private void logd(Object... o) {
		Utils.logd( TAG,o);
	}
	
	public BufferedReader getReaderFromUrl(String url, String[] params, boolean online, String patternToCheck ) throws IOException, ConnectionException {
		final boolean isOnline = online && !SIMU_MODE;
		String fileName=generateFilenameForRequest(url, params);
		return getReaderFromUrl(url, params, isOnline, patternToCheck, fileName );
	}
	
	public BufferedReader getReaderFromUrl(String url, String[] params, boolean online, String patternToCheck,String fileName ) throws IOException, ConnectionException {
		final boolean fromNet = online && !Configuration.isSimuMode();
		String method=params==null?"get":"post";
		String filePath= TEMP_DIR+"/"+method+"/"+fileName+".html";
		Log.i(TAG, "Looking for file "+ filePath);
		File file=new File( filePath );
		boolean fileDoesNotExist = ! file.exists();
		boolean net= fromNet || fileDoesNotExist;
		logd("fileDoesNotExist="+fileDoesNotExist+",fromNet="+fromNet);
		if ( !net ){
			BufferedReader reader= getReaderFromFile(url, params, filePath );
			if (new Scanner(reader).findWithinHorizon(patternToCheck,0) != null ) {
				reader.reset();
				return reader;
			}
		}
		
		return getReaderFromUrlOnline(url, params, filePath, patternToCheck );
	}
	
	protected BufferedReader getReaderFromUrlOnline(String url, String[] params, String filename, String patternToCheck) throws IOException, IllegalStateException, ConnectionException {
		for(int delay=1; delay<10; delay*=2) {
			if (params == null) {
				httpget (url). save(filename) ;
			} else {
				httppost (url, params). save(filename) ;
			}
			BufferedReader str=Utils.getBufferedReaderFromFile(filename,"iso-8859-1");
			Log.d(TAG, "pattern='"+patternToCheck+"'");
			Scanner scanner =new Scanner(str);
			if (scanner.findWithinHorizon(patternToCheck, 0) != null) {
				return str;
			} else {
				currentTask.display ("Téléchargement de "+url+ " incomplet. Nouvelle tentative dans "+delay+" secondes.", true); 
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ie) {
					currentTask.display(ie.getMessage(), true); 
				}
			}
		}
		String error= "Échec de téléchargement de "+url;
		currentTask.display(error, true);
		throw new IOException(error);
	}
	
	protected HRequest executeRequest(HttpUriRequest requestget) throws IOException, IllegalStateException, ConnectionException {
		return HRequest.execReq(httpClient, requestget, currentTask);
	}
	
	public HRequest httpget(String uri) throws IOException, IllegalStateException, ConnectionException {
		Log.i(TAG, "get "+uri);
		HttpGet requestget = new HttpGet(uri);
		return executeRequest(requestget);
	}
	
	public HRequest httppost(String uri, String[] params) throws UnsupportedEncodingException, IOException, IllegalStateException, ConnectionException {
		HttpPost request = new HttpPost(uri);
		ArrayList nameValuePairs= new ArrayList(params.length/2);
		for ( int i=0; i < params.length; i++) {
			nameValuePairs.add(new BasicNameValuePair ( params[i++],params[i]));
		}
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8")); 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		request.getEntity().writeTo(baos);
		Log.i(TAG, "post "+uri);
		logd("params=",params);
		logd("request=",request);
		logd("nameValuePairs", nameValuePairs);
		return executeRequest (request);
	}

	private BufferedReader getReaderFromFile(String url, String[] params, String filename) throws IOException {
		Log.i(TAG, "Loading file "+ filename );
		return Utils.getBufferedReaderFromFile(filename,"iso-8859-1");
	}
	
}
