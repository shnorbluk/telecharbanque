package com.github.shnorbluk.telecharbanque.boursorama;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneycenterParser {
 private static final Logger LOGGER = LoggerFactory.getLogger(MoneycenterParser.class);
	

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
	  ope.setLibelle(Utils.findGroupAfterPattern(extract, "<tr data-header=\"", "([^\"]*)\"").trim());
	  String dateStr=Utils. findGroupAfterPattern( extract , "<td data-header=\"Date\" class=\"\">","([\\d/]*)");
   ope.setDateFromPage(dateStr);
	  ope.setAccount( Utils. findGroupAfterPattern( extract , "<td data-header=\"Compte\" class=\"\" title=\"","([^\"]*)\"")); 
  } else {
      ope.setDate(prev.getDate());
      ope.setAccount(prev.getAccount());
	  libellePattern = "title=\"[^\"]*\">\\w*([^<]*)\\w*<";
	  ope.setLibelle(Utils.findGroupAfterPattern(extract, "<span class=\"label\" style=\"cursor: pointer;\">", "([^<]+)<").trim());
  }
 
  String category;
  if ( extract .indexOf( "operation-category\"  title=\"" )>0) {
   category =Utils. findGroupAfterPattern( extract , "","operation-category\"  title=\"([^\"]*)\""); 
  } else {
   category= Utils. findGroupAfterPattern( extract , "operation-category\"",">\\s*([^< \\t\\n][^<]*[^\\s<])\\s*</"); 
  } 
  ope.setCategoryLabel(category);
  LOGGER.debug("categoryLabel="+ope.getCategoryLabel());
  String signe= extract .indexOf("vardown")>0?"-":"+";
  String montant= signe+ Utils. findGroupAfterPattern( extract , "operation-amount\"",">([\\d, ]*) &euro;</span>"); 
 ope.setAmount(Float.valueOf(montant.replace(",",".").replace(" ","")));
  LOGGER.debug("montant="+montant);
  if ( isFrac ) {
   LOGGER.debug("Opération fractionnée");
   ope.setParent (Utils. findGroupAfterPattern( extract , "", "splitOf(\\d*)"));
  }
  return ope;
 }
}