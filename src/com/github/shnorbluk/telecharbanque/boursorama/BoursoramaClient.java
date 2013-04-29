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

	static void logd (String TAG, Object o) {
		Log.d(TAG, Utils.toString(o) + " (" + Thread.currentThread().getStackTrace()[3]+")");
	}
	
 private String[] decodePad(String filepath) throws IOException{
  Bitmap mBitmapKeyboard = BitmapFactory.decodeFile(filepath);
  String[] map=new String[10];
  int[] keybPixels=new int[55*31];
  for (int yKeyb:new int[]{20,53,86,119}) {
   for(int xKeyb:new int[]{20,77,134}){
    gui.display("Décodage du chiffre à la position "+ xKeyb+","+yKeyb);
    Log.i(TAG, xKeyb+","+yKeyb);
    mBitmapKeyboard.getPixels(keybPixels, 0, 55, xKeyb, yKeyb,55,31);
    int minDiff=Integer.MAX_VALUE;
    int mostAccurate=-1;
    for (int digit=0; digit<=10; digit++) {
     Bitmap digitBm= BitmapFactory.decodeStream(MainActivity.loadResource( digit+".png"));
     int[] digitPixels=new int[55*31];
     digitBm.getPixels(digitPixels, 0, 55, 0,0,55,31);
     int diff=0;
     for (int pixIndex=9*55+40;pixIndex<23*55+49; pixIndex++) {
      int pix = keybPixels[ pixIndex] ;
      int digitPix = digitPixels[ pixIndex ];
      int rpix=Color.red(pix);
      int rdigitPix =Color.red( digitPix );
      if ((rpix>=238 && rpix<=248)!=( rdigitPix >=238 && rdigitPix <=247)){
       diff++;
      }
     }
     Log.i (TAG, "digit="+digit+" diff="+diff);
     if(diff<minDiff) {
      minDiff =diff;
      mostAccurate=digit;
     }
    }
    Log.i (TAG, xKeyb+","+yKeyb+":"+mostAccurate);
    if(mostAccurate!=10){
     map[mostAccurate ]=xKeyb+","+yKeyb;
    }
   }
  }
  return map;
 }

 public void connect() throws Exception {
	 logd("Connexion ", Thread.currentThread().getStackTrace());
   boolean online=true;
   String password=Configuration.getBoursoramaPassword();
   gui.display("Connexion à Boursorama  en cours");
   //if (simu) return;
		  StringBuffer connectionPage=hclient.loadString("https://www.boursorama.com/connexion.phtml?", null, online, "");
		  String connectionPageStr=connectionPage.toString();
		  String imgUrl=Utils.findGroupAfterPattern(connectionPageStr, "<img id=\"img-pad", "src=\"([^\"]*)");
		  imgUrl="https://www.boursorama.com"+imgUrl;
		  String filePath= MoneycenterPersistence.TEMP_DIR+"/boursopad.gif";
		  if (online){
			  hclient.httpget( imgUrl).save( filePath );
		  }
		  String[] coordMap= decodePad (filePath);
		  String[] parts = connectionPageStr.split ("<area shape");
		  HashMap<String,String> codeMap=new HashMap<String,String>(12);
		  for(int i=1; i<parts.length; i++){
			  int end=parts[i].indexOf(",",21);
			  String coord=parts[i].substring(16,end);
			  end= parts[i]. indexOf("+=", 87);
			  String code=parts[i]. substring(end+4, end+8);
			  codeMap.put(coord,code);
		  }
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
		  gui.display("Connecté");
	  
 }
 
 private static void logd(Object o) {
	 Utils.logd(TAG, o);
 }

}

