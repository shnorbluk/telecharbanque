package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.http.client.*;
import org.orman.mapper.*;

public class MoneycenterClient
{
//	private final BoursoramaClient bClient;
 //private SessionedBufferedHttpClient hclient;
 private static String TAG = "MoneycenterClient";
 private final UI gui;
	private static final String URL_EDIT_OPERATION = "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operation_edit.phtml";
//	private MoneycenterSession mcHttpClient = null;
	private final BufferedHttpClient basicHttpClient;
	private final BoursoramaClient boursoramaSession;
//	private final BoursoramaClient boursoramaHttpClient;

	public MoneycenterClient(final HttpClient httpClient, UI gui, final Token boursoramaToken) {
		basicHttpClient = new BufferedHttpClient(gui, httpClient);
		boursoramaSession = new BoursoramaClient(basicHttpClient, gui, boursoramaToken);
	//	boursoramaHttpClient = new SessionedBufferedHttpClient<BoursoramaClient>(basicHttpClient, boursoramaSession);
	//	initMcHttpClient();
	//	final SessionedBufferedHttpClient<BoursoramaClient> bhClient = new SessionedBufferedHttpClient<BoursoramaClient>(hClient, bClient);
		
  this.gui=gui;
	//final MoneycenterSession session = new MoneycenterSession(httpClient, gui);
//	 hclient= new HClient(httpClient, session, gui, bToken);
//	 bClient = new BoursoramaClient(httpClient,gui
 }
 	public UI getCurrentTask() {
		return gui;
	}
	public BoursoramaClient getBoursoramaClient() {
		return boursoramaSession;
	}

	
	
 void pointeOperation(String id, boolean pointed) throws IOException, ConnectionException {
  	boursoramaSession .loadString( 
    "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operations_check.phtml" ,
    new String[]{ "operations["+id+"]" ,pointed?"yes":"no"  }, true,"") ;
  gui.display("Opération "+id+" pointée.", true);
  logd("pointeOperation ",Thread.currentThread().getStackTrace());
 }

 private void logd(Object... o) {
	 Utils.logd(TAG,o);
 }

 public void postOperation (McOperationFromEdit ope) throws UnexpectedResponseException, IOException, ConnectionException {
   String[] params=ope.getAsParams();
	 String expected="Votre opération a bien été éditée";
	 expected="Votre op.ration a bien .t. .dit.e";
	 String file ="postOperation"+ope.getId();
   BufferedReader result = boursoramaSession .getReaderFromUrl( URL_EDIT_OPERATION , params, true,"", file) ;
   String line;
   logd("Lecture de la page ", file);
   while ((line= result.readLine())!= null) {
	   if (line.contains("Votre")) {
	   	 logd(line);
		}
   }
   result.reset();
   Scanner scanner=new Scanner(result);
   logd(scanner.findWithinHorizon("Votre", 0));
   scanner.reset();
   boolean found = scanner.findWithinHorizon(expected,0)!=null;
   scanner.close();
	markOperationPageAsObsolete(ope.getId());
   if(found) {
    gui.display("L'opération "+ope.getId()+" a été mise à jour avec succès.", true);
   } else {
    String message = "Erreur lors de la mise à jour de l'opération " +ope.getId()+".\n";
	message+= "Le texte '"+expected+"' n'est pas présent dans la réponse.\n";
	message+= "La réponse a été enregistrée dans le fichier "+file;
    gui.display( message, true); 
    Log.d(TAG, ope.getAsParams().toString());
    Log.d(TAG, Utils.toString(ope.getAsParams()));
    throw new UnexpectedResponseException (message);
   }
  }

	private BufferedReader getOperationPageAsReader(String ID_OPERATION, boolean fromNet ) throws IOException , ConnectionException{
		String[] params=new String[]{
			"id",  ID_OPERATION};
		return boursoramaSession.getReaderFromUrl( URL_EDIT_OPERATION ,params, fromNet, "new_groupings\\[\\]" );
	}
 
 private void markOperationPageAsObsolete(String id) {
	 String[] params=new String[]{
		 "id",  id};
	 boursoramaSession.markAsObsolete( URL_EDIT_OPERATION ,params);
 }

 private MoneycenterOperation getOperation( MoneycenterOperation operationFromListExtract, boolean forceReload ) {
	 
	 return operationFromListExtract;
 }
 
public McOperationFromEdit getOperation(String id, boolean online) throws IOException, PatternNotFoundException, ConnectionException, ParseException {
  BufferedReader html=getOperationPageAsReader(id, online );
  try {
	  McOperationFromEdit opeFromEdit = new McOperationFromEdit(html, id);
	  return opeFromEdit;
  } catch ( PatternNotFoundException e ) {
   gui.display("La chaine '"+
     e.getPattern()+
	 "' n'a pas été trouvée dans la page de l'opération "+id, true);
   throw e;
  } finally {
	  html.close();
  }
 }
	
}

