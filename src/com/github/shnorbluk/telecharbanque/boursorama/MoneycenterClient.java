package com.github.shnorbluk.telecharbanque.boursorama;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shnorbluk.telecharbanque.PatternNotFoundException;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.McOperationFromEdit;
import com.github.shnorbluk.telecharbanque.net.BufferedHttpClient;
import com.github.shnorbluk.telecharbanque.net.ConnectionException;
import com.github.shnorbluk.telecharbanque.net.UnexpectedResponseException;
import com.github.shnorbluk.telecharbanque.ui.MessageObserver;
import com.github.shnorbluk.telecharbanque.util.Utils;

public class MoneycenterClient
{
//	private final BoursoramaClient bClient;
 //private SessionedBufferedHttpClient hclient;
 private static final Logger LOGGER = LoggerFactory.getLogger(MoneycenterClient.class);
	private static final String URL_EDIT_OPERATION = "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operation_edit.phtml";
//	private MoneycenterSession mcHttpClient = null;
	private final BufferedHttpClient basicHttpClient;
	private final BoursoramaClient boursoramaSession;
	private final List<MessageObserver> observers = new ArrayList<MessageObserver>();

	public MoneycenterClient(final HttpClient httpClient, final String username, String password) {
		basicHttpClient = new BufferedHttpClient(true, httpClient);
		boursoramaSession = new BoursoramaClient(basicHttpClient, username,password);
	//	boursoramaHttpClient = new SessionedBufferedHttpClient<BoursoramaClient>(basicHttpClient, boursoramaSession);
	//	initMcHttpClient();
	//	final SessionedBufferedHttpClient<BoursoramaClient> bhClient = new SessionedBufferedHttpClient<BoursoramaClient>(hClient, bClient);
		
	//final MoneycenterSession session = new MoneycenterSession(httpClient, gui);
//	 hclient= new HClient(httpClient, session, gui, bToken);
//	 bClient = new BoursoramaClient(httpClient,gui
 }
 
	public BoursoramaClient getBoursoramaClient() {
		return boursoramaSession;
	}

	public void addMessageObserver(MessageObserver observer) {
		observers.add(observer);
	}
	
	public void display(String msg, boolean persistent) {
		for (MessageObserver observer:observers){
			observer.displayMessage(msg, persistent);
		}
	}
	

 void pointeOperation(String id, boolean pointed) throws IOException, ConnectionException {
  	boursoramaSession .loadString( 
    "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operations_check.phtml" ,
    new String[]{ "operations["+id+"]" ,pointed?"yes":"no"  }, true,"") ;
  display("Opération "+id+" pointée.", true);
  LOGGER.debug("pointeOperation {}",Thread.currentThread().getStackTrace());
 }

 public void postOperation (McOperationFromEdit ope) throws UnexpectedResponseException, IOException, ConnectionException {
   String[] params=ope.getAsParams();
	 String expected="Votre opération a bien été éditée";
	 expected="Votre op.ration a bien .t. .dit.e";
	 String file ="postOperation"+ope.getId();
   BufferedReader result = boursoramaSession .getReaderFromUrl( URL_EDIT_OPERATION , params, true,"", file) ;
   String line;
   LOGGER.debug("Lecture de la page {}", file);
   while ((line= result.readLine())!= null) {
	   if (line.contains("Votre")) {
	   	 LOGGER.debug(line);
		}
   }
   result.reset();
   Scanner scanner=new Scanner(result);
   LOGGER.debug(scanner.findWithinHorizon("Votre", 0));
   scanner.reset();
   boolean found = scanner.findWithinHorizon(expected,0)!=null;
   scanner.close();
	markOperationPageAsObsolete(ope.getId());
   if(found) {
    display("L'opération "+ope.getId()+" a été mise à jour avec succès.", true);
   } else {
    String message = "Erreur lors de la mise à jour de l'opération " +ope.getId()+".\n";
	message+= "Le texte '"+expected+"' n'est pas présent dans la réponse.\n";
	message+= "La réponse a été enregistrée dans le fichier "+file;
    display( message, true); 
    LOGGER.debug(ope.getAsParams().toString());
    LOGGER.debug(Utils.toString(ope.getAsParams()));
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
   display("La chaine '"+
     e.getPattern()+
	 "' n'a pas été trouvée dans la page de l'opération "+id, true);
   throw e;
  } finally {
	  html.close();
  }
 }
	
}

