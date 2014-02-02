package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.net.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.apache.http.client.*;

public class MoneycenterSession implements SessionManager
{
 private final SessionedBufferedHttpClient hclient;
 private int nbOfPages = -1;
 private static final String TAG="MoneycenterSession";
 private static final String TODAY=new SimpleDateFormat("d-M-yyyy").format(new Date());
 private boolean connected = false;
 
	public MoneycenterSession(SessionedBufferedHttpClient<BoursoramaClient> bhClient, UI gui ) {
		hclient = bhClient;
	}
	
	public boolean isConnected() {
		return connected;
	}

 public int getSessionInformation () {
  return nbOfPages; 
 }

	public void connect () throws Exception {
		 StringBuffer html= hclient.loadString( 
   "https://www.boursorama.com/patrimoine/"+
   "moneycenter/monbudget/operations.phtml?"+
   "filters%5BfromDate%5D=27-10-2006&"+
   "filters%5BtoDate%5D="+TODAY,
		 null, true,"");
		 connected=true;
  Log.d( TAG , "Categories");
  HashMap<String,String> categories = new HashMap< String,String >(); 
  String regex="\\{'type':'([^']+)','category':'([^']+)'\\}\">([^<]*)</a><b" ;
  Matcher matcher= Pattern.compile(regex).matcher(html);
  StringBuilder sb = new StringBuilder();
  while (matcher.find()){
	  String categ=matcher.group(1)+"."+
		  matcher.group(2);
	  String categName=matcher.group(3);
	//  sb.append("categories.").append(categ).append('=').
	  //  append(categName).append('\n');
//   Log.d( TAG ,matcher.group(1)+" "+
//    matcher.group(2) +" "+ matcher.group(3) );
   categories.put( categ, categName);
  }
//  Log.d( TAG , "Sous-Categories");
  matcher= Pattern.compile( 
   "\\{'type':'([^']+)','category':'([^']+)',subcategory:'([^']*)'\\}\">([^<]*)</a>" ).matcher(html);
  while (matcher.find()){ 
   String categ=matcher.group(1)+'.'+matcher.group(2);
   sb.append("#categories.").
		  append(categ).
		  append('.').
		  append(matcher.group(3)).
		  append('=').
		  append(categories.get(categ)).
		  append(',').
		  append(matcher.group(4)).
		  append('\n');
   Log.d( TAG ,  categories.get(matcher.group(1)+ matcher.group(2)) +", "+ matcher.group(4) +" "+ matcher.group(3) );
  }
  Utils.writeToFile(sb.toString(), "/sdcard/Temp/telecharbanque/categories.txt", false);
 findNbOfPages(html);
	}
	
private void findNbOfPages(StringBuffer html) {
  String pages = Utils.getExtract ( html , "pagination","</div>");
  String nbPagesStr = "";
  int index = pages.lastIndexOf("page=")+5;
  while (true) {
   char c = pages.charAt(index);
   if (!Character.isDigit (c)) break;
   nbPagesStr+=c;
   index++;
  }
 
  nbOfPages= Integer.parseInt( nbPagesStr );
 }
}






