package com.github.shnorbluk.telecharbanque.net;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.net.*;
import java.io.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.conn.*;

public class HRequest
{
 private InputStream is;
 private static final String TAG = "HRequest";

 private HRequest( InputStream is ) {
  this.is=is;
 }

 private static void logd(Object... o) {
	 Utils.logd(TAG, o);
 }
 public static HRequest execReq(HttpClient client, HttpUriRequest req, UI gui) throws IOException, IllegalStateException {
  HttpResponse response=null;
  Log.d(TAG," execReq("+req);
 for (int delay = 1; delay <= 10; delay*=2){
   boolean ok=false;
   try {
    response = client.execute(req); 
   } catch ( HttpHostConnectException e) {
    e.printStackTrace();
    gui.display("Erreur sur la requête"+req, true);
   } catch (Exception e) {
    Log.e(TAG, " exception autre connexion", e);
    if(gui!=null) {
     gui.display(e.getMessage(), true);
    }
    e.printStackTrace();
   }
    if (response == null) {
     logd("response null");
     gui.display( "Nouvelle tentative dans "+delay+
     " secondes.", false);
     continue;
    }
    if (response.getEntity() == null) logd("entity null");
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
