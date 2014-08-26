package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.http.client.*;
import org.orman.mapper.*;

public class MoneycenterPersistence
{
 static final String TEMP_DIR = "/sdcard/Temp/telecharbanque";
 public static final String PERSISTENCE_FILE = TEMP_DIR+"/moneycenter.txt";
	
 private static final String TAG = "MoneycenterPersistence";
 private final MoneycenterClient mcClient;
 private final UI gui;
 //private final HClient hClient;

	public MoneycenterPersistence( MoneycenterClient mcClient) {
  //this. mcClient= new MoneycenterClient(httpClient, gui, db);
  //MoneycenterSession mcSession;
  this.mcClient = mcClient;
  //hclient=client.getHClient();
  this.gui=mcClient.getCurrentTask();
 }

 //public void setSimulationMode() {
	// mcClient.setSimulationMode(true);
 //}

 void exportToCsv ( List<McOperationInDb> operations ) throws IOException {
  String csv="";
  for ( McOperationInDb op: operations) {
   String memo=op.getMemo();
   boolean checked =op.isChecked ();
   String date =op.getDateAsString ();
   String compte =op.getAccount ();
   String libelleLong = op.getLibelle();
   String categ = op.getCategoryLabel ();
   float montant = op.getAmount ();
   String id = op.getId ();
   String parent = op.getParent ();
   String csvline = (checked?"O":"N")+
";"+date+";"+csvEscape(compte)+";"+libelleLong+
";"+categ+";"+montant+";"+memo+";"+id+
";"+csvEscape(parent); 
   csv+=csvline+ "\n";
  }
  String f="/sdcard/Temp/mccsv.csv";
  Utils.writeToFile("", f, true);
	 Utils.writeToFile(csv, f, false);
  gui.display("Les opérations ont été enregistrées dans le fichier "+f+".", true);
 }

 public static String csvEscape(String s) {
	 if (s== null) {
		 return "";
	 }
	 return s;
 }
 
	private void uploadChanges(List<? extends OperationChange> changes)throws ConnectionException, IOException {
	 for(final OperationChange change:changes) {
		 change.performFromFile(this);
	 }
 }
 void uploadOperations( String[] props) throws MalformedTextException, UnexpectedResponseException, IOException, ConnectionException, PatternNotFoundException, ParseException {
  gui.display("Analyse des opérations à faire", true);
  HashMap<String,List<String[]>> pendingOperations=new HashMap<String,List<String[]>> ();
  for (String modif:props){
   
   if(modif.startsWith("#") || !modif.contains(".tosync=")) {
	   if(modif.contains("tosync"))
		   logd("On ignore la ligne ",
		   modif,
		   modif.contains(".tosync="),
		   modif.startsWith("#"));
    continue;
   }
	  Log.d(TAG,modif);
   String[] prop=modif.split("=");
   String key=prop[0];
   String value=prop[1].trim();
   prop=key.split("\\.");
   String id=prop[0];
   if (!id.matches("[0-9]+")) {
    String error= "L'id '"+id+"' n'est pas au format correct.";
    gui.display(error,true);
    throw new MalformedTextException (error);
   }
   String action=prop[1];
   Log.d( TAG,"action="+action);
   if("memo".equals(action) || "type".equals(action) || "category".equals(action) || "subcategory".equals(action) || "categ".equals(action)){
    if ( !pendingOperations.containsKey(id) ){
     pendingOperations.put(id, new ArrayList<String[]>() );
    }
    pendingOperations.get(id).add( new String [] {action,value});
   } else if("checked".equals(action)){
    boolean checked=Boolean.valueOf(value);
    doActions (pendingOperations);
    gui.display("Pointage de l'opération "+id+":"+checked, false);
	final CheckOperationChange changeToDo = new CheckOperationChange(id, checked);
	final List<CheckOperationChange> changesToDo=Arrays.asList(changeToDo);
	uploadChanges(changesToDo);
   } else {
    Log.e(TAG, "Propriété "+action+" inconnue.");
    System.exit(1);
   }
  }
  doActions (pendingOperations);
  gui.display("Modifications effectuées avec succès", true);
 }

 void downloadToPersistence ( boolean saveAllMcHistory, int firstPage,
   int lastPage,
   boolean reloadListPages,
   boolean saveUnchecked, Date dateFin) throws ConnectionException, ExecutionException, InterruptedException, IOException {
	   gui.display("Téléchargement des pages pour sauvegarde", false);
	   logd("downloadToPersistence("+saveAllMcHistory+firstPage+","+lastPage+reloadListPages+saveUnchecked);
	   final MoneycenterSession mcSession =new MoneycenterSession(mcClient, dateFin) ;
//	   final MoneycenterSession mcHttpClient = new SessionedBuMoneycenterSession>(mcClient.getBoursoramaHttpClient(),mcSession);
	if (saveAllMcHistory) {
   		firstPage=1;
   		lastPage= mcSession.getSessionInformation();
  	} 
  Log.d( TAG, "Pages "+firstPage+" à "+ lastPage+" reloadListPages="+ reloadListPages);
  int nbOfPages=lastPage-firstPage+1;
  int pageIndex=1;
  String csvFile="/sdcard/Temp/mccsv"+firstPage+"-"+lastPage+".csv";
  Utils.writeToFile("",PERSISTENCE_FILE,false);
  Utils.writeToFile("",csvFile,false);
  for (int numpage= firstPage; numpage<=lastPage; numpage++ ) {
	  gui.display("Page " + pageIndex + " sur " + nbOfPages + ": Page " + numpage, false);
   DownloadMcPageTask task = new DownloadMcPageTask(numpage, mcSession);
   task.setCsvFileName(csvFile);
   task.execute("");
   if (task.get() == null) {
       break;
    }
  }
  gui.display("Opérations téléchargées", true);
 }

 public void uploadPersistenceFile() throws IOException, MalformedTextException, UnexpectedResponseException, ConnectionException, PatternNotFoundException, ParseException {
  StringBuffer modifs=Utils.readFile ( PERSISTENCE_FILE, "iso-8859-1");
	char firstChar=modifs.charAt(0);
	char lastChar=modifs.charAt(modifs.length()-1);
  if (! (""+firstChar+lastChar).matches("[0-9#][\\s\\w]") ) {
	  String error="Le fichier d'entrée "+PERSISTENCE_FILE+" est mal formé. Il doit être en ISO-8859-1, commencer par un chiffre et finir par un caractère.\n";
	  error += "Le premier caractère est '"+modifs.charAt(0)+"' et devrait être un chiffre ou #.\n";
	  error += "Le dernier caractère est '"+modifs.charAt(modifs.length()-1) +"' et devrait être \\s ou \\w.\n";
   throw new MalformedTextException ( error);
  }
  String[] lines=modifs.toString().split("\n");
  uploadOperations (lines);
 }

 private void doActions(Map<String,List<String[]>> operations) throws UnexpectedResponseException, IOException, ConnectionException, PatternNotFoundException, ParseException {
 if (!operations.isEmpty()) {
  gui.display("Opérations à faire:"+ Utils.toString(operations), true); 
 }
 Map<String,List<String[]>> syncMap = Collections.synchronizedMap(operations);
 Iterator<Map.Entry<String,List<String[]>>> it=syncMap.entrySet().iterator();
 while (it.hasNext()) {
  Map.Entry<String,List<String[]>> entry= it.next();
  String id=entry.getKey();
  List<String[]> propList = entry.getValue();
  McOperationFromEdit operation = mcClient.getOperation(id, false);
  for ( String[] property : propList ) {
   String action= property[0];
   String value =property[1];
   Log.d(TAG, action+"="+value);
   if("memo".equals(action) ){
    operation .setMemo(value);
   } else if ("category".equals(action) ){
    operation .setCategory( value);
   } 
  }
  String[] params= operation .getAsParams();  
  Log.i(TAG, "Paramètres à poster:'"+Utils.toString( params)+"'");
  mcClient.postOperation ( operation );
  declarePropertiesAsSynced (id, propList);
  it.remove();
 }
}

 private static void logd (Object... o) {
  Utils.logd(TAG, o);
 }

 private void declarePropertiesAsSynced ( String id, List<String[]> propList) throws IOException {
  List<String> p = new ArrayList<String>( propList.size());
  for (String[] prop:propList) {
   String property= id+"."+prop[0];
   p.add(property);
  }
  declareLinesAsSynced (p);
 }

	public void declareLinesAsSynced ( List<String> p ) throws IOException {
		logd("declareLinesAsSynced(",p);
		StringBuffer content = Utils.readFile(PERSISTENCE_FILE,"iso-8859-1");
		String contentStr=content.toString();
		for (String property:p) {
			String find=property +".tosync";
			String replace=property +".synced";
			logd("Remplacement de "+find+" par "+replace);
			logd(find+" trouvé à la position "+contentStr.indexOf(find));
			contentStr=contentStr.replace( find, replace );
		}
		Utils.writeToFile( contentStr, PERSISTENCE_FILE, false );
	}

 public void pointeOperation(String id, boolean pointed) throws ConnectionException, IOException {
  mcClient.pointeOperation(id, pointed);
 }
}
