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
 private final HClient hclient;
 private int nbOfPages = -1;
 private static final String TAG="MoneycenterSession";
 private static final String TODAY=new SimpleDateFormat("d-M-yyyy").format(new Date());
 
	public MoneycenterSession(HttpClient httpClient, UI gui ) {
		hclient = new HClient ( httpClient, new BoursoramaClient(gui,httpClient), gui);
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
  Log.d( TAG , "Categories");
  HashMap<String,String> categories = new HashMap< String,String >(); 
  String regex="\\{'type':'([^']+)','category':'([^']+)'\\}\">([^<]*)</a><b" ;
  Matcher matcher= Pattern.compile(regex).matcher(html);
  while (matcher.find()){ 
//   Log.d( TAG ,matcher.group(1)+" "+
//    matcher.group(2) +" "+ matcher.group(3) );
   categories.put( matcher.group(1)+
    matcher.group(2), matcher.group(3) );
  }
//  Log.d( TAG , "Sous-Categories");
  matcher= Pattern.compile( 
   "\\{'type':'([^']+)','category':'([^']+)',subcategory:'([^']*)'\\}\">([^<]*)</a>" ).matcher(html);
  while (matcher.find()){ 
   Log.d( TAG ,  categories.get(matcher.group(1)+ matcher.group(2)) +", "+ matcher.group(4) +" "+ matcher.group(3) );
  }
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






