package com.github.shnorbluk.telecharbanque.boursorama;
import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;

public class MoneycenterParser {
 private static final String TAG = "MoneycenterParser";

 public static MoneycenterOperation getOperationFromListExtract(
  String extract) throws PatternNotFoundException {
  MoneycenterOperation ope= new MoneycenterOperation();
  boolean isFrac= extract .indexOf("splitOf")>0;
  ope.setChecked(extract .indexOf("checked")!=-1);
  ope.setId( Utils.findGroupAfterPattern( extract ,"", "\\{'id':'(\\d+)'\\}" ));
  ope.setLibelle( Utils. findGroupAfterPattern( extract , "operation-libelle", "title=\"[^\"]*\">\\w*([^<]*)\\w*<").trim());
  if (!isFrac) {
   ope.setDate( Utils. findGroupAfterPattern( extract , "operation-date\">","([\\d/]*)"));
   ope.setAccount( Utils. findGroupAfterPattern( extract , "operation-account\" title=\"","([^\"]*)\"")); 
  }
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
	 Utils.logd(TAG, msg);
 }
}
