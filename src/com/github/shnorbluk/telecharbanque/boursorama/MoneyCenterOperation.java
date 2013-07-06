package com.github.shnorbluk.telecharbanque.boursorama;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class MoneycenterOperation
 {
 private String memo ;
 private String id;
 private String libelle;
 private String account;
 private Date date;
 private String num_cheque;
 private float amount ;
 private String categ;
 private String subcateg;
 private String contract;
 private String typecontract;
 private String idcontract;
 private String patrimoine;
 private String typePatrimoine;
 private String groupe;
 private String dataPatrimoine;
 private boolean checked;
	private String parent;
	private String categoryLabel;
	private static final String TAG="MoneycenterOperation";
	private static boolean log=false;
	private static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	public void setCategoryLabel(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}

	public String getCategoryLabel() {
		return categoryLabel;
	}
	public void setAmount(float amount)
	{
		this.amount = amount;
	}

	public float getAmount()
	{
		return amount;
	}

	public void setLibelle(String libelle)
	{
		this.libelle = libelle;
	}

	public String getLibelle()
	{
		return libelle;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

 public String toString() {
  return "memo :"+memo;
 }

public void setParent (String parent ) {
  this.parent = parent ;
 }

public String getParent (){
  return parent ;
 }

public void setDate (Date date ) {
  this. date = date ;
 }
public void setDateFromPage(String dateStr) throws ParseException {
 DATE_DISPLAY_FORMAT.parse(dateStr);
 }
public Date getDate (){
  return date ;
 }

 public String getDateForPage() {
	 return DATE_DISPLAY_FORMAT.format(date);
 }
 public boolean isChecked() {
  return checked;
 }

 public void setChecked (boolean checked) {
  this.checked = checked;
 }

 public String getMemo(){
  return memo;
 }

public String getAccount (){
  return account;
 }

 public void setMemo (String operationMemo) {
  this.memo= operationMemo ;
 }

 public void setNumCheque(String numCheque) {
  num_cheque = numCheque;
 }

 public void setAccount (String operationAccount) {
  account = operationAccount;
 }

 public void setCategory(String category) {
  categ= category;
 }

public String getCategory (){
  return categ;
 }

private void setSubCategory(String subcategory) {
  subcateg= subcategory;
 }

public String getSubCategory (){
  return subcateg;
 }
 String[] getAsParams (){
	 String type=amount>0?"credit":"debit";
	 String[] categoryParts=categ.split("\\.");
	 final String category;
	 if (categoryParts.length>0) {
	  category="{\"type\":\""+type+
	    "\",\"category\":\""+
		categoryParts[0]+ "\"}";
		if(categoryParts.length>1) {
			subcateg=categoryParts[1];
		}
	 } else {
		 category="";
	 }
  String[] params=new String[]{ 
 "id", id,
 "editOperation[libelle]", libelle,
 "editOperation[id_account]", account, 
 "editOperation[date]", MoneycenterOperation.DATE_DISPLAY_FORMAT.format(date),
 "editOperation[num_cheque]", num_cheque,
 "editOperation[amount]", Float.toString(amount), 
 "editOperation[memo]", memo, 
 "editOperation[category]", category,
 "editOperation[subcategory]", subcateg,
 "editOperation[new_subcategory]", "", 
 "options_operation[contract]", contract, 
 "editOperation[type_contract]", typecontract, 
 "editOperation[id_contract]", idcontract, 
 "options_operation[patrimoine]", patrimoine,
 "editOperation[type_patrimoine]", typePatrimoine, 
 "editOperation[data_patrimoine]", dataPatrimoine,
 "options_operation[grouping]", groupe, 
 "new_groupings[]", ""};
  return params;
 }

 private static String getExtract(String complete, String begin, String end) throws PatternNotFoundException {
	 logd("getExtract(..."+ begin+","+end);
   int start= complete.indexOf(begin);
   int finish = complete.indexOf(end, start);
   if (finish == -1) {
    throw new PatternNotFoundException(end);
   }
   if (start == -1) {
    throw new PatternNotFoundException(begin);
   }
   return complete.substring(start+begin.length(), finish);
  }

 private static String valFromSelect (String extract, String id) throws PatternNotFoundException {
   String value = findGroupBetween(extract, id, "</select>", "value=\"([^\"]+)\"  checked" );
   logd("valfromselect checked="+value);
   if (value != null) return value;
   value = findGroupBetween(extract, id, "</select>", "value=\"([^\"]+)\" selected=\"selected\"" );
	 logd("valfromselect selected="+value);
   if (value != null) return value;
   return findGroupBetween(extract, id, "</select>", "value=['\"]([^'\"]*)['\"]");
  }

 private static String getValueFromSelect(String extract, String id) throws PatternNotFoundException {
   String raw=valFromSelect(extract, id) ;
   if(raw==null) return null;
   return android.text.Html.fromHtml( raw ).toString();
  }

 private static String findGroupBetween (String str, String regexBegin, String end, String regexGroup) throws PatternNotFoundException {
   String excerpt=getExtract(str, regexBegin, end);
  // logd("excerpt="+excerpt);
   //logd("regexgroup="+regexGroup);
   String result= Utils.findGroupAfterPattern(excerpt, "", regexGroup);
   //logd("result="+result);
   return result;
 }

 MoneycenterOperation () {
 }
 
 private static void logd(Object o) {
	 if(log) {
	  Utils.logd(TAG,o);
	 }
 }

 public MoneycenterOperation (BufferedReader html, String id) throws PatternNotFoundException, IOException, ParseException { 
  this.id=id;
	 String extract= Utils.getExtract(html, "id=\"form_edit_operation\">", "new_groupings\\[\\]");
	 libelle = Utils.findGroupAfterPattern(extract, "editOperation\\[libelle\\]", "value=\"([^\"]*)");
	 libelle = android.text.Html.fromHtml( libelle ).toString();
  account = Utils. findGroupAfterPattern(extract,"editOperation\\[id_account\\]", "value=\"([^\"]*)");
	 account = android.text.Html.fromHtml( account ).toString();
  String dateStr = Utils. findGroupAfterPattern(extract,"editOperation\\[date\\]", "value=\"([^\"]*)");
  date = MoneycenterOperation.DATE_DISPLAY_FORMAT.parse(dateStr);
  num_cheque = Utils. findGroupAfterPattern(extract,"editOperation\\[num_cheque\\]", "value=\"([^\"]*)");
  amount = Float.parseFloat(Utils. findGroupAfterPattern(extract,"editOperation\\[amount\\]", "value=\"([^\"]*)"));
  memo = Utils. findGroupAfterPattern(extract,"editOperation\\[memo\\]", ">([^<]*)</textarea");
  memo = android.text.Html.fromHtml( memo ).toString();
  log=true;
  categ= getValueFromSelect ( extract, "edit_operation_category");
  logd("categ="+categ);
  if (categ.length()>0) {
   categ= Utils. findGroupAfterPattern( categ,"category\":\"","([^\"]*)");
  }
  logd("categ="+categ);
  log=false;
  subcateg= getValueFromSelect ( extract, "edit_operation_subcategory");
  categ= categ+"."+subcateg;
  contract= Utils.findGroupAfterPattern(extract, "options_operation\\[contract\\]", "value=\"([^\"]*)\"  checked");
  typecontract= findGroupBetween(extract, "edit_operation_type_contract", "</select>", "value='([^']+)'");
  idcontract = getValueFromSelect (extract, "edit_operation_id_contract");
  patrimoine = Utils.findGroupAfterPattern(extract, "options_operation\\[patrimoine\\]", "value=\"([^\"]*)\"  checked");
  typePatrimoine = getValueFromSelect (extract, "edit_operation_groupe" );
  dataPatrimoine = getValueFromSelect ( extract, "edit_operation_patrimoine\"" );
  groupe = Utils.findGroupAfterPattern(extract, "options_operation_grp_", "value=\"([^\"]*)\"  checked");
 }
}
