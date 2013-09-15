package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;

public class MoneycenterParser {
 private static final String TAG = "MoneycenterParser";
	

 public static MoneycenterOperationFromList getOperationFromListExtract(
  String extract, MoneycenterOperationFromList prev) throws PatternNotFoundException, ParseException {
  MoneycenterOperationFromList ope= new MoneycenterOperationFromList();
  boolean isFrac= extract .indexOf("splitOf")>0;
  ope.setChecked(extract .indexOf("checked")!=-1);
  ope.setId( Utils.findGroupAfterPattern( extract ,"", "\\{'id':'(\\d+)'\\}" ));
//  String shortLibelle = Utils. findGroupAfterPattern( extract , "operation-libelle", "title=\"[^\"]*\">\\w*([^<]*)\\w*<").trim();
  final String libellePattern;
  //if (shortLibelle.endsWith("...")) {
//	  shortLibelle=shortLibelle.substring(0, shortLibelle.length()-4);
//	  String longLibelle;
  //}
  
  if (!isFrac) {
	String dateStr=Utils. findGroupAfterPattern( extract , "operation-date\">","([\\d/]*)");
   ope.setDateFromPage(dateStr);
   ope.setAccount( Utils. findGroupAfterPattern( extract , "operation-account\" title=\"","([^\"]*)\"")); 
	  libellePattern = "title=\"([^\"]*)\">\\w*[^<]*\\w*<";
  } else {
      ope.setDate(prev.getDate());
      ope.setAccount(prev.getAccount());
	  libellePattern="title=\"[^\"]*\">\\w*([^<]*)\\w*<";
  }
  String libelle = Utils. findGroupAfterPattern( extract ,"operation-libelle",
  libellePattern).trim();
  ope.setLibelle(libelle);
  String category;
  if ( extract .indexOf( "operation-category\"  title=\"" )>0) {
   category =Utils. findGroupAfterPattern( extract , "","operation-category\"  title=\"([^\"]*)\""); 
  } else {
   category= Utils. findGroupAfterPattern( extract , "operation-category\"",">\\s*([^< \\t\\n][^<]*[^\\s<])\\s*</"); 
  } 
  ope.setCategoryLabel(category);
  logd("categoryLabel="+ope.getCategoryLabel());
  String signe= extract .indexOf("vardown")>0?"-":"+";
  String montant= signe+ Utils. findGroupAfterPattern( extract , "operation-amount\"",">([\\d, ]*) &euro;</span>"); 
 ope.setAmount(Float.valueOf(montant.replace(",",".").replace(" ","")));
  Log.d(TAG, "montant="+montant);
  if ( isFrac ) {
   Log.d(TAG, "Opération fractionnée");
   ope.setParent (Utils. findGroupAfterPattern( extract , "", "splitOf(\\d*)"));
  }
  return ope;
 }
 static void logd(String... msg) {
	 Utils.logd(TAG, (Object[])msg);
 }
}
