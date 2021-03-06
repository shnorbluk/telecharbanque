package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.text.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McOperationFromEdit implements MoneycenterOperation
{
	private String contract;
	private String typecontract;
	private String idcontract;
	private String patrimoine;
	private String typePatrimoine;
	private String groupe;
	private String dataPatrimoine;
	private String memo ;
	private String date;
	private String numCheque;
	private String id;
	private String libelle;
	private String account;

	private float amount ;
	private String categ;
	private String subcateg;

	private String categoryLabel;
	private static Logger LOGGER = LoggerFactory.getLogger(McOperationFromEdit.class);

	public void setCategoryLabel(String categoryLabel)
	{
		this.categoryLabel = categoryLabel;
	}

	public String getCategoryLabel()
	{
		return categoryLabel;
	}

	public void setSubCategory(String subcateg)
	{
		this.subcateg = subcateg;
	}

	public String getSubCategory()
	{
		return subcateg;
	}

	public void setCategory(String categ)
	{
		this.categ = categ;
	}

	public String getCategory()
	{
		return categ;
	}

	public void setAmount(float amount)
	{
		this.amount = amount;
	}

	public float getAmount()
	{
		return amount;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public String getAccount()
	{
		return account;
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
	public McOperationFromEdit (BufferedReader html, String id) throws PatternNotFoundException, IOException, ParseException {
		super();
		setId(id);
		String extract= Utils.getExtract(html, "id=\"form_edit_operation\">", "new_groupings\\[\\]");
		final String libelle = Utils.findGroupAfterPattern(extract, "editOperation\\[libelle\\]", "value=\"([^\"]*)");
		setLibelle( PlatformSpecific.getInstance().removeHtmlTags( libelle ));
		final String account = Utils. findGroupAfterPattern(extract,"editOperation\\[id_account\\]", "value=\"([^\"]*)");
		setAccount(PlatformSpecific.getInstance().removeHtmlTags( account ));
		setDate( Utils. findGroupAfterPattern(extract,"editOperation\\[date\\]", "value=\"([^\"]*)"));
		setNumCheque( Utils. findGroupAfterPattern(extract,"editOperation\\[num_cheque\\]", "value=\"([^\"]*)"));
		setAmount( Float.parseFloat(Utils. findGroupAfterPattern(extract,"editOperation\\[amount\\]", "value=\"([^\"]*)")));
		final String memo = Utils. findGroupAfterPattern(extract,"editOperation\\[memo\\]", ">([^<]*)</textarea");
		setMemo( PlatformSpecific.getInstance().removeHtmlTags( memo ));
		String categ= getValueFromSelect ( extract, "edit_operation_category");
		LOGGER.debug("categ="+categ);
		if (categ.length()>0) {
			categ= Utils. findGroupAfterPattern( categ,"category\":\"","([^\"]*)");
		}
		LOGGER.debug("categ="+categ);
		final String subcateg= getValueFromSelect ( extract, "edit_operation_subcategory");
		setCategory( categ+"."+subcateg);
		contract= Utils.findGroupAfterPattern(extract, "options_operation\\[contract\\]", "value=\"([^\"]*)\"  checked");
		typecontract= findGroupBetween(extract, "edit_operation_type_contract", "</select>", "value='([^']+)'");
		idcontract = getValueFromSelect (extract, "edit_operation_id_contract");
		patrimoine = Utils.findGroupAfterPattern(extract, "options_operation\\[patrimoine\\]", "value=\"([^\"]*)\"  checked");
		typePatrimoine = getValueFromSelect (extract, "edit_operation_groupe" );
		dataPatrimoine = getValueFromSelect ( extract, "edit_operation_patrimoine\"" );
		groupe = Utils.findGroupAfterPattern(extract, "options_operation_grp_", "value=\"([^\"]*)\"  checked");
	}
	public String toString() {
		return "memo :"+memo;
	}
	public String getMemo(){
		return memo;
	}
	public void setMemo (String operationMemo) {
		this.memo= operationMemo ;
	}
	public String getDateAsString() {
		return date;
	}
	public void setDate(String date) {
		this.date=date;
	}
	public String getNumCheque () {
		return numCheque;
	}
	public void setNumCheque(String numCheque) {
		this.numCheque=numCheque;
	}
	public String[] getAsParams (){
		String type=getAmount()>0?"credit":"debit";
		String[] categoryParts=getCategory().split("\\.");
		String subcateg="";
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
			"id", getId(),
			"editOperation[libelle]", getLibelle(),
			"editOperation[id_account]", getAccount(), 
			"editOperation[date]", getDateAsString(),
			"editOperation[num_cheque]", getNumCheque(),
			"editOperation[amount]", Float.toString(getAmount()), 
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
	private static String getValueFromSelect(String extract, String id) throws PatternNotFoundException {
		String raw=valFromSelect(extract, id) ;
		if(raw==null) return null;
		return PlatformSpecific.getInstance().removeHtmlTags( raw );
	}
	private static String valFromSelect (String extract, String id) throws PatternNotFoundException {
		String value = findGroupBetween(extract, id, "</select>", "value=\"([^\"]+)\"  checked" );
		LOGGER.debug("valfromselect checked="+value);
		if (value != null) return value;
		value = findGroupBetween(extract, id, "</select>", "value=\"([^\"]+)\" selected=\"selected\"" );
		LOGGER.debug("valfromselect selected="+value);
		if (value != null) return value;
		return findGroupBetween(extract, id, "</select>", "value=['\"]([^'\"]*)['\"]");
	}
	private static String findGroupBetween (String str, String regexBegin, String end, String regexGroup) throws PatternNotFoundException {
		String excerpt=getExtract(str, regexBegin, end);
		String result= Utils.findGroupAfterPattern(excerpt, "", regexGroup);
		return result;
	}
	private static String getExtract(String complete, String begin, String end) throws PatternNotFoundException {
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
	
}
