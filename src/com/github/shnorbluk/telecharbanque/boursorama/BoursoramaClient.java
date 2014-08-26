package com.github.shnorbluk.telecharbanque.boursorama;
import android.graphics.*;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;

public class BoursoramaClient extends SessionedBufferedHttpClient
{
 
 private final UI currentTask;
 private static final String TAG = "BoursoramaClient";
	
	private BufferedHttpClient hClient;
	private Token token;
//	private boolean simu =true;
	
	public BoursoramaClient ( final BufferedHttpClient hClient, UI currentTask, Token token) {
  super(hClient);
     this.currentTask=currentTask;
	 this.hClient = hClient;
	 this.token=token;
//	 hclient = new HClient(httpClient, null, currentTask, bToken);
 }

 public int getSessionInformation() {
	 return -1;
 }
 	public boolean isConnected() {
		return token.isConnected();
	}
 
 //void setSimulationMode ( boolean simuMode) {
  //hclient = new FakeHClient(currentTask);
  //hClient.setSimulationMode(simuMode);
  //simu = true;
 //}

	private static final int DIGIT_WIDTH=100;
	private static final int DIGIT_HEIGHT=60;
	private static final int FIRST_SIGNIFICANT_ROW=0;
	private static final int LAST_SIGNIFICANT_ROW=59;
 private String[] decodePad(String filepath) throws IOException{
  Bitmap mBitmapKeyboard = BitmapFactory.decodeFile(filepath);
  String[] map=new String[10];
	 int[] keybPixels=new int[DIGIT_WIDTH*DIGIT_HEIGHT];
	 for (int yKeyb=0; yKeyb<=3*DIGIT_HEIGHT; yKeyb += DIGIT_HEIGHT) {
   for(int xKeyb=0; xKeyb<=2*DIGIT_WIDTH; xKeyb+=DIGIT_WIDTH){
    currentTask.display("Décodage du chiffre à la position "+ xKeyb+","+yKeyb,false);
    Log.i(TAG, xKeyb+","+yKeyb);
    mBitmapKeyboard.getPixels(keybPixels, 0, DIGIT_WIDTH, xKeyb, yKeyb,DIGIT_WIDTH, DIGIT_HEIGHT);
    int minDiff=Integer.MAX_VALUE;
    int mostAccurate=-1;
	String diffs="";
    for (int digit=0; digit<=10; digit++) {
     Bitmap digitBm= BitmapFactory.decodeStream(MainActivity.loadResource( digit+".png"));
     int[] digitPixels=new int[DIGIT_WIDTH*DIGIT_HEIGHT];
		digitBm.getPixels(digitPixels, 0, DIGIT_WIDTH, 0,0,DIGIT_WIDTH, DIGIT_HEIGHT);
     int diff=0;
     for (int pixIndex=FIRST_SIGNIFICANT_ROW*DIGIT_WIDTH;pixIndex<LAST_SIGNIFICANT_ROW*DIGIT_WIDTH; pixIndex++) {
      int pix = keybPixels[ pixIndex] ;
      int digitPix = digitPixels[ pixIndex ];
      int rpix=Color.red(pix);
      int rdigitPix =Color.red( digitPix );
      if ((rpix>=238 && rpix<=248)!=( rdigitPix >=238 && rdigitPix <=247)){
       diff++;
      }
     }
	 diffs+=diff+" ";
     if(diff<minDiff) {
      minDiff =diff;
      mostAccurate=digit;
     }
    }
    Log.i (TAG, xKeyb+","+yKeyb+":"+mostAccurate+" "+diffs);
    if(mostAccurate!=10){
     map[mostAccurate ]=xKeyb+","+yKeyb;
    }
   }
  }
  return map;
 }

	@Override
 protected void connect() throws ConnectionException {
	 logd("Connexion ");
   boolean online=true;
   String password=Configuration.getBoursoramaPassword();
   if (password == null) {
   	throw new ConnectionException("Le mot de passe n'a pas ete saisi.");
   }
   currentTask.display("Connexion à Boursorama  en cours",true);
   		try {
		  StringBuffer connectionPage=hClient.loadString("https://www.boursorama.com/connexion.phtml?", null, true, "");
		  String connectionPageStr=connectionPage.toString();
		  String imgUrl=Utils.findGroupAfterPattern(connectionPageStr, "<img id=\"login-pad_pad", "src=\"([^\"]*)");
		  imgUrl="https://www.boursorama.com"+imgUrl;
		  String filePath= MoneycenterPersistence.TEMP_DIR+"/boursopad.gif";
		  if (online){
			  hClient.httpget( imgUrl).save( filePath );
		  }
		  String[] coordMap= decodePad (filePath);
		  String[] parts = connectionPageStr.split ("<area shape");
		  HashMap<String,String> codeMap=new HashMap<String,String>(12);
		  for(int i=1; i<parts.length; i++){
              String coord=Utils.findGroupAfterPattern(parts[i], "coords=","\"([0-9]+,[0-9]+)");
			  String code=Utils.findGroupAfterPattern(parts[i], "\\+= '", "(....|)'");
			  
			  codeMap.put(coord,code);
		  }
		  logd("codeMap=",codeMap);
		  String encoded="";
		  for(char digitChar:password.toCharArray()){
			  int digit=Character.digit( digitChar,10);
			  String digitCode=codeMap.get(coordMap[digit]);
			  encoded+= digitCode;
		  }
		  String[] params=new String[]{
			  "login", Configuration.getBoursoramaLogin(),
			  "password", encoded,
			  "password_fake", "*******",
			  "submit2", "Se+Connecter",
			  "is_first", "0"
		  }; 
		  String uri="https://www.boursorama.com/logunique.phtml";
		  StringBuffer page = hClient.loadString(uri, params, true, "");
		 if (page.indexOf("Identifiant ou mot de passe incorrect")>0) {
			 throw new ConnectionException("Identifiant ou mot de passe incorrect");
      	 }
		 } catch (IOException e){
			 throw new ConnectionException(e);
		 } catch (PatternNotFoundException e) {
			 throw new ConnectionException (e);
		 }

		 token.setConnected(true);
		  currentTask.display("Connecté", true);
	  
 }
 
 private static void logd(Object... o) {
	 Utils.logd(TAG, o);
 }

}

