package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.http.client.*;

public class MoneycenterPersistence
{
 static final String TEMP_DIR = "/sdcard/Temp/telecharbanque";
 public static final String PERSISTENCE_FILE = TEMP_DIR+"/moneycenter.txt";
	
 private static final String TAG = "MoneycenterPersistence";
 private final MoneycenterClient client;
 private final UI gui;
 private final HClient hclient;
	private final SQLiteMoneycenter db;

	public MoneycenterPersistence( HttpClient httpClient, UI gui, final SQLiteMoneycenter db) {
  this. client= new MoneycenterClient(httpClient, gui);
  hclient=client.getHClient();
  this.gui=gui;
  this.db=db;
 }

 public void setSimulationMode() {
	 client.setSimulationMode(true);
 }

 void exportToCsv ( List<MoneyCenterOperation> operations ) throws IOException {
  String csv="";
  for ( MoneyCenterOperation op: operations) {
   String memo=op.getMemo();
   boolean checked =op.isChecked ();
   String date =op.getDate ();
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
 
 void uploadOperations( String[] props) throws MalformedTextException, UnexpectedResponseException, IOException, ConnectionException, PatternNotFoundException {
  gui.display("Analyse des opérations à faire", true);
  HashMap<String,List<String[]>> pendingOperations=new HashMap<String,List<String[]>> ();
  for (String modif:props){
   
   if(modif.startsWith("#") || !modif.contains(".tosync=")) {
   // logd("On ignore la ligne "+modif);
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
    pointeOperation(id, checked);
   } else {
    Log.e(TAG, "Propriété "+action+" inconnue.");
    System.exit(1);
   }
  }
  doActions (pendingOperations);
  gui.display("Modifications effectuées avec succès", true);
 }

 void downloadToPersistence ( boolean saveAllMcPages, int firstPage,
   int lastPage, boolean reloadPages,
   boolean saveUnchecked ) throws ConnectionException, ExecutionException, InterruptedException, IOException {
	   if (saveAllMcPages) {
   firstPage=1;
   lastPage= hclient.getSessionInformation();
  } 
  Log.d( TAG, "Pages "+firstPage+" à "+ lastPage);
  int nbOfPages=lastPage-firstPage+1;
  int pageIndex=1;
  String csvFile="/sdcard/Temp/mccsv"+firstPage+"-"+lastPage+".csv";
  Utils.writeToFile("",PERSISTENCE_FILE,false);
  Utils.writeToFile("",csvFile,false);
  for (int numpage= firstPage; numpage<=lastPage; numpage++ ) {
   DownloadMcPageTask task = new DownloadMcPageTask(numpage, pageIndex++, nbOfPages, db);
   task.setMoneycenterClient(client);
   task.setReloadPages(reloadPages);
   task.setCsvFileName(csvFile);
   task.execute("");
   task.get();
  }
  gui.display("Opérations téléchargées", true);
 }

 public void uploadPersistenceFile() throws IOException, MalformedTextException, UnexpectedResponseException, ConnectionException, PatternNotFoundException {
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

 private void doActions(Map<String,List<String[]>> operations) throws UnexpectedResponseException, IOException, ConnectionException, PatternNotFoundException {
 if (!operations.isEmpty()) {
  gui.display("Opérations à faire:"+ Utils.toString(operations), true); 
 }
 Map<String,List<String[]>> syncMap = Collections.synchronizedMap(operations);
 for (Map.Entry<String,List<String[]>> entry: syncMap.entrySet() ){
  String id=entry.getKey();
  List<String[]> propList = entry.getValue();
  MoneyCenterOperation operation = client.getOperation(id, false);
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
  client.postOperation ( operation );
  declarePropertiesAsSynced (id, propList);
  operations.remove(id);
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

 private void declareLinesAsSynced ( List<String> p ) throws IOException {
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
  client. pointeOperation(id, pointed);
  logd("apres pointeOperation");
  declareLinesAsSynced (Arrays.<String>asList(id+".checked"));
 }
}
