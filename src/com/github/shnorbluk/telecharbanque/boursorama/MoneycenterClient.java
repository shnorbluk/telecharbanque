package com.github.shnorbluk.telecharbanque.boursorama;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;
import java.security.*;

public class MoneycenterClient
{
 private final BoursoramaClient bclient ;
 private final MoneycenterSession session;
 private HClient hclient;
 private static String TAG = "MoneycenterClient";
 private final UI gui;
	private static final String URL_EDIT_OPERATION = "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operation_edit.phtml";
	private int nbOfPages;
	

 public MoneycenterClient(HttpClient httpClient, UI gui ) {
  this.gui=gui;
	 bclient= new BoursoramaClient(gui,httpClient);
	 session = new MoneycenterSession(httpClient, gui);
	 hclient= new HClient(httpClient, session, gui);
 }

 public HClient getHClient()
 {
	 return hclient;
 }

 void setSimulationMode ( boolean simuMode) {
  bclient. setSimulationMode( simuMode);
  hclient=new FakeHClient(gui);
 }

 void pointeOperation(String id, boolean pointed) throws IOException, ConnectionException {
  hclient .loadString( 
    "https://www.boursorama.com/ajax/patrimoine/moneycenter/monbudget/operations_check.phtml" ,
    new String[]{ "operations["+id+"]" ,pointed?"yes":"no"  }, true,"") ;
  gui.display("Opération "+id+" pointée.", true);
  logd("pointeOperation ",Thread.currentThread().getStackTrace());
 }

 private void logd(Object... o) {
	 Utils.logd(TAG,o);
 }
 private List<MoneyCenterOperation> downloadMoneycenterOperations(
	 boolean saveAllMoneyCenterPages, int firstPage,
   int lastPage, boolean reloadPages,
   boolean saveChecked) throws IOException, PatternNotFoundException, ConnectionException {
  List<MoneyCenterOperation> operationList = new ArrayList<MoneyCenterOperation> ();
   if (saveAllMoneyCenterPages) {
   firstPage=1;
   lastPage= hclient.getSessionInformation();
  } 
  Log.d( TAG, "Pages "+firstPage+" à "+ lastPage);
  nbOfPages=lastPage-firstPage+1;
  int pageIndex=1;
  for (int numpage= firstPage; numpage<=lastPage; numpage++ ) {
   gui.display("Page "+(pageIndex++)+" sur "+nbOfPages+": Page "+numpage, true);
   int debut = -1,fin=-1;
   StringBuffer html;
   html=hclient.loadString(
     "https://www.boursorama.com/patrimoine/moneycenter/monbudget/operations.phtml?page="+numpage, null, reloadPages, "</tbody>" );
 
	 debut=html.indexOf( "liste-operations-page")+24;
	 fin= html.indexOf( "</tbody>", debut ); 
   String extract=html.substring(debut,fin);
   operationList.addAll ( parseListPage (extract));
  }
  return operationList;
 }

 private List<MoneyCenterOperation> parseListPage(String extract) throws PatternNotFoundException, IOException, ConnectionException {
  List<MoneyCenterOperation> list = new ArrayList<MoneyCenterOperation>();
  String[] opes=extract.split("<tr");
  for (int partnum=1; partnum<opes.length; partnum++) {
   gui.display("Opération "+ partnum+" sur "+ (opes.length-1), false);
  String extr=opes[partnum];
   if ( extr .indexOf("class=\"createRule\"")>0) {
    Log.d(TAG, "Proposition de catégorie");
    continue;
   }
   MoneyCenterOperation ope = MoneycenterParser. getOperationFromListExtract( extr);
   logd("Récupération de l'opération ", ope.getId());
   MoneyCenterOperation op=getOperation(ope.getId(), false);
   op.setChecked( ope.isChecked());
   op.setParent( ope.getParent());
   op.setAccount(ope.getAccount());
   op.setCategoryLabel(ope.getCategoryLabel());
   list.add( op);
  }
  return list;
 }

 public void postOperation (MoneyCenterOperation ope) throws UnexpectedResponseException, IOException, ConnectionException {
   String[] params=ope.getAsParams();
	 String expected="Votre opération a bien été éditée";
	 expected="Votre op.ration a bien .t. .dit.e";
	 String file ="postOperation"+ope.getId();
   BufferedReader result = hclient .getReaderFromUrl( URL_EDIT_OPERATION , params, true,"", file) ;
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
		return hclient.getReaderFromUrl( URL_EDIT_OPERATION ,params, fromNet, "new_groupings\\[\\]" );
	}
 
 private void markOperationPageAsObsolete(String id) {
	 String[] params=new String[]{
		 "id",  id};
	 hclient.markAsObsolete( URL_EDIT_OPERATION ,params);
 }

public MoneyCenterOperation getOperation(String id, boolean online) throws IOException, PatternNotFoundException, ConnectionException {
  BufferedReader html=getOperationPageAsReader(id, online );
  try {
   return new MoneyCenterOperation(html, id);
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

