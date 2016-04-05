package com.github.shnorbluk.telecharbanque.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shnorbluk.telecharbanque.ui.MessageObserver;
import com.github.shnorbluk.telecharbanque.util.Utils;

public class HRequest
{
 private final InputStream is;
 private static final Logger LOGGER = LoggerFactory.getLogger(HRequest.class);

 private HRequest( InputStream is ) {
  this.is=is;
 }

 private static void displayMessage (MessageObserver[] observers, String msg, boolean p) {
	 for (MessageObserver observer:observers){
		 observer.displayMessage(msg, p);
	 }
 }
 public static HRequest execReq(HttpClient client, HttpUriRequest req, MessageObserver... observers) throws IOException, IllegalStateException {
  HttpResponse response=null;
  LOGGER.debug(" execReq("+req);
 for (int delay = 1; delay <= 10; delay*=2){
   boolean ok=false;
   try {
    response = client.execute(req); 
   } catch ( HttpHostConnectException e) {
    LOGGER.error("Echec de connexion à la requête "+req, e);
    displayMessage(observers, "Erreur sur la requête "+req, true);
   } catch (Exception e) {
    LOGGER.error("Exception pour autre raison que connexion", e);
    displayMessage(observers, e.getMessage(), true);
   }
    if (response == null) {
     LOGGER.debug("response null");
     displayMessage( observers, "Nouvelle tentative dans "+delay+
     " secondes.", false);
     continue;
    }
    if (response.getEntity() == null) 
    	LOGGER.debug("entity null");
    InputStream is= response.getEntity().getContent() ; 
    return new HRequest(is);
  }
  throw new ConnectException("La requete a echoue");
 }
 public StringBuilder getContent () throws UnsupportedEncodingException, IOException { 
   InputStreamReader b= new InputStreamReader(is,"ISO-8859-1") ;
   BufferedReader in = new BufferedReader (b); 
   StringBuilder sb = new StringBuilder(""); 
   String line = ""; 
   String NL = System.getProperty("line.separator"); 
   while ((line = in.readLine()) != null) { 
    sb.append(line + NL); 
   } 
   in.close();
   is.close();
   return sb; 
  } 
 public void save(String filename) throws FileNotFoundException, IOException {
   new File(filename).getParentFile().mkdirs();
   FileOutputStream fos=new FileOutputStream(filename);  
   byte[] cars = new byte[255];
   int nb;
   while ((nb=is.read(cars))>=0) {  
    fos.write (cars,0,nb);  
   } 
   fos.flush ();
   fos.close(); 
   is.close();
  }
}
