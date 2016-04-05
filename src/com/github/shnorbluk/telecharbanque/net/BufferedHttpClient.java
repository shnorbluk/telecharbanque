package com.github.shnorbluk.telecharbanque.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shnorbluk.telecharbanque.ui.MessageObserver;
import com.github.shnorbluk.telecharbanque.util.FileUtils;
import com.github.shnorbluk.telecharbanque.util.Utils;

public class BufferedHttpClient implements MessageObserver
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BufferedHttpClient.class);
	private static final String TEMP_DIR = "/sdcard/Temp/telecharbanque/";
	
	private final boolean simuMode;
	private final HttpClient httpClient;
	private final List<MessageObserver> messageObservers = new ArrayList<MessageObserver>(1);

	public BufferedHttpClient(final boolean simuMode, HttpClient httpClient )
	{
		this.simuMode = simuMode;
		this.httpClient = httpClient;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}
	
	protected boolean connectIfNeeded() throws ConnectionException
	{
		return false;
	}
	
	public void markAsObsolete(String url, String[] params) {
		String file = generateFilenameForRequest(url,params);
		LOGGER.debug("Deleting file {} because it is obsolete", file);
		boolean success = new File(file).delete();
		if (success) {
			LOGGER.info("Fichier {} supprimé avec succès", file);
		} else {
			LOGGER.error ("Le fichier {} n'a pas pu être supprimé.", file);
		}
	}
	
	@Deprecated
	private StringBuffer loadString(String url, String[] params, boolean fromNet, String patternToCheck, String fileName ) throws IOException, ConnectionException {
		String method=params==null?"get":"post";
		String filePath= TEMP_DIR+"/"+method+"/"+fileName+".html";
		LOGGER.info( "Looking for file {}", filePath);
		File file=new File( filePath );
		boolean fileDoesNotExist = ! file.exists();
		boolean net= fromNet || fileDoesNotExist;
		LOGGER.debug("fileDoesNotExist={}, fromNet={}", fileDoesNotExist, fromNet);
		if ( net ){
			return loadStringFromNet(url, params, filePath, patternToCheck );
		} else {
			return loadStringFromFile(url, params, filePath, patternToCheck );
		}
	}

	@Deprecated
	public StringBuffer loadString(String url, String[] params, boolean fromNet, String patternToCheck ) throws IOException, ConnectionException {
//		String fileName= url;
//		if (params!= null) {
//			fileName+="%3F";
//			for (int i=0; i<params.length; i+=2){
//				fileName += params[i] + "=" + params[i+1] +"&";
//			}
//		}
//		fileName = fileName .replace(":","") .replace("?","%3F").replace("|","%7C").replace("*","%2A").replace("\"", "%22");
		String fileName=generateFilenameForRequest(url, params);
		return loadString(url, params, fromNet, patternToCheck, fileName );
	}
	
	@Deprecated
	private StringBuffer loadStringFromFile(String url, String[] params, String filename, String patternToCheck) throws IOException, IllegalStateException, ConnectionException {
		LOGGER.debug("Loading file {}", filename );
		StringBuffer str= FileUtils.readFile(filename,"iso-8859-1");
		if (str.toString().indexOf(patternToCheck) != -1) {
			return str;
		} else {
			LOGGER.debug("La chaine "+patternToCheck+" n'a pas été trouvée dans le fichier " + filename+". Nouveau téléchargement du fichier.");
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
			StringBuffer str=FileUtils.readFile(filename,"iso-8859-1");
			LOGGER.debug( "str="+str.substring(0,30)+" pattern='"+patternToCheck+"'");
			if (str.indexOf(patternToCheck)>=0) {
				return str;
			} else {
				displayMessage ("Téléchargement de "+url+ " incomplet. Nouvelle tentative dans "+delay+" secondes.", true);
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ie) {
					displayMessage(ie.getMessage(), true);
				}
			}
		}
		String error= "Échec de téléchargement de "+url;
		displayMessage(error, true);
		throw new IOException(error);
	}
	
	private String generateFilenameForRequest(String url, String[] params) {
		String fileName= url;
		if (params!= null) {
			fileName+="%3F";			
			for (int i=0; i<params.length; i+=2){
				fileName += params[i] 
					+ "=" 
					+ params[i+1]
					+"&";
			}
		}
		fileName = fileName 
			.replace(":","") 
			.replace("?","%3F")
			.replace("|","%7C")
			.replace("*","%2A")
			.replace("\"", "%22");
		if (fileName.length() > 245) {
			fileName= fileName.substring(0, 245)
				+fileName.hashCode();
		}
		return fileName;
	}
	
	public BufferedReader getReaderFromUrl(String url, String[] params, boolean online, String patternToCheck ) throws IOException, ConnectionException {
		final boolean isOnline = online && !simuMode;
		String fileName=generateFilenameForRequest(url, params);
		return getReaderFromUrl(url, params, isOnline, patternToCheck, fileName );
	}
	
	public BufferedReader getReaderFromUrl(String url, String[] params, boolean online, String patternToCheck,String fileName ) throws IOException, ConnectionException {
		final boolean fromNet = online && !simuMode;
		String method=params==null?"get":"post";
		if (fileName.length() > 200) {
			try
			{
				final MessageDigest md = MessageDigest.getInstance("MD5");
				final byte[] hash = md.digest(fileName.getBytes());
				final String hashString = new BigInteger (hash).toString(16);
				fileName= fileName.substring(0,200) + hashString; 
			}
			catch (NoSuchAlgorithmException e)
			{
				displayMessage(e.getMessage(), true);
			}
		}
		String filePath= TEMP_DIR+"/"+method+"/"+fileName+".html";
		
		LOGGER.info( "Looking for file {}", filePath);
		File file=new File( filePath );
		boolean fileDoesNotExist = ! file.exists();
		boolean net= fromNet || fileDoesNotExist;
		LOGGER.debug("fileDoesNotExist={},fromNet={}",fileDoesNotExist,fromNet);
		if ( !net ){
			BufferedReader reader= getReaderFromFile(url, params, filePath );
			if (new Scanner(reader).findWithinHorizon(patternToCheck,0) != null ) {
				reader.reset();
				return reader;
			}
		}
		
		return getReaderFromUrlOnline(url, params, filePath, patternToCheck );
	}
	
	public void regisiterObserver(MessageObserver observer) {
		messageObservers.add(observer);
	}
	public void displayMessage(String msg, boolean persistent
			){
		for (final MessageObserver observer:messageObservers) {
			observer.displayMessage(msg, persistent);
		}
	}
	
	protected BufferedReader getReaderFromUrlOnline(String url, String[] params, String filename, String patternToCheck) throws IOException, IllegalStateException, ConnectionException {
		for(int delay=1; delay<10; delay*=2) {
			if (params == null) {
				httpget (url). save(filename) ;
			} else {
				httppost (url, params). save(filename) ;
			}
			BufferedReader str=FileUtils.getBufferedReaderFromFile(filename,"iso-8859-1");
			LOGGER.debug("pattern='{}'",patternToCheck);
			Scanner scanner =new Scanner(str);
			if (scanner.findWithinHorizon(patternToCheck, 0) != null) {
				return str;
			} else {

				displayMessage ("Téléchargement de "+url+ " incomplet. Nouvelle tentative dans "+delay+" secondes.", true); 
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ie) {
					displayMessage(ie.getMessage(), true); 
				}
			}
		}
		String error= "Échec de téléchargement de "+url+". La chaine '"+patternToCheck+"' n'a pas été trouvée dans la page à l'URL "+url ;
		displayMessage(error, true);
		throw new IOException(error);
	}
	
	protected HRequest executeRequest(HttpUriRequest requestget) throws IOException, IllegalStateException, ConnectionException {
		return HRequest.execReq(httpClient, requestget, this);
	}
	
	public HRequest httpget(String uri) throws IOException, IllegalStateException, ConnectionException {
		LOGGER.info("get {}",uri);
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
		return executeRequest (request);
	}

	private BufferedReader getReaderFromFile(String url, String[] params, String filename) throws IOException {
		LOGGER.info( "Loading file {}", filename );
		return FileUtils.getBufferedReaderFromFile(filename,"iso-8859-1");
	}
	
}
