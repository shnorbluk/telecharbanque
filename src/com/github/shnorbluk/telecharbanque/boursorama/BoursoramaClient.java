package com.github.shnorbluk.telecharbanque.boursorama;
import android.graphics.*;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;

public class BoursoramaClient implements SessionManager
{
 
 private final UI gui;
 private static final String TAG = "BoursoramaClient";
	
	private HClient hclient;
	private boolean simu =true;
	
	BoursoramaClient ( final UI gui, final HttpClient httpClient) {
  super();
     this.gui=gui;
	 hclient = new HClient(httpClient, null, gui);
 }

 public int getSessionInformation() {
	 return -1;
 }
 
 void setSimulationMode ( boolean simuMode) {
  hclient = new FakeHClient(gui);
  simu = true;
 }

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
    gui.display("Décodage du chiffre à la position "+ xKeyb+","+yKeyb,false);
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

 public void connect() throws Exception {
	 logd("Connexion ");
   boolean online=true;
   String password=Configuration.getBoursoramaPassword();
   if (password == null) {
   	throw new Exception("Le mot de passe n'a pas ete saisi.");
   }
   gui.display("Connexion à Boursorama  en cours",true);
   //if (simu) return;
		  StringBuffer connectionPage=hclient.loadString("https://www.boursorama.com/connexion.phtml?", null, online, "");
		  String connectionPageStr=connectionPage.toString();
		  String imgUrl=Utils.findGroupAfterPattern(connectionPageStr, "<img id=\"login-pad_pad", "src=\"([^\"]*)");
		  imgUrl="https://www.boursorama.com"+imgUrl;
		  String filePath= MoneycenterPersistence.TEMP_DIR+"/boursopad.gif";
		  if (online){
			  hclient.httpget( imgUrl).save( filePath );
		  }
		  String[] coordMap= decodePad (filePath);
		  logd("coordMap=",coordMap);
		  String[] parts = connectionPageStr.split ("<area shape");
		  HashMap<String,String> codeMap=new HashMap<String,String>(12);
		  for(int i=1; i<parts.length; i++){
              String coord=Utils.findGroupAfterPattern(parts[i], "coords=","\"([0-9]+,[0-9]+)");
			  logd("coord=",coord);
			  String code=Utils.findGroupAfterPattern(parts[i], "\\+= '", "(....|)'");
			  logd("code=",code);
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
		  StringBuffer page = hclient.loadString(uri, params, online,"");
	      if (page.indexOf("Identifiant ou mot de passe incorrect")>0) {
		   throw new Exception("Identifiant ou mot de passe incorrect");
      	 }
		  gui.display("Connecté", true);
	  
 }
 
 private static void logd(Object... o) {
	 Utils.logd(TAG, o);
 }

}

