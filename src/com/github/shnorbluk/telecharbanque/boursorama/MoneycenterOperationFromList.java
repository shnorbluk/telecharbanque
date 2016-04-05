package com.github.shnorbluk.telecharbanque.boursorama;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.IMcOperationFromList;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.McOperationInDb;
import com.github.shnorbluk.telecharbanque.orm.MoneycenterProperty;

public class MoneycenterOperationFromList implements IMcOperationFromList
 {
	private static final SimpleDateFormat DATE_FORMAT_FOR_LIST = new SimpleDateFormat("dd/MM/yy");
	private static final Logger LOGGER = LoggerFactory.getLogger(MoneycenterOperationFromList.class);
	private String parent;
	private Date date;
	private boolean checked;
	private String id;
	private String libelle;
	private String account;

	private float amount ;
	private String categ;
	private String subcateg;

	private String categoryLabel;

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
	public boolean isChecked() {
		return checked;
	}

	public void setChecked (boolean checked) {
		this.checked = checked;
	}

	public void setDate (Date date ) {
		this. date = date ;
	}
	public Date getDate (){
		return date ;
	}
	public void setDateFromPage(String dateStr) throws ParseException {
		setDate(DATE_FORMAT_FOR_LIST.parse(dateStr));
	}
	public void setParent (String parent ) {
		this.parent = parent ;
	}

	public String getParent (){
		return parent ;
	}
	public boolean equals (McOperationInDb opeFromDb) {
		final boolean equal = this.getId().equals(opeFromDb.getId());
		MoneycenterProperty[] props = MoneycenterProperty.propertiesInList();
		for (MoneycenterProperty prop:props) {
			LOGGER.debug(prop.getName());
		}
		for (MoneycenterProperty prop:props) {
			Object val1=prop.getValueFromList(this);
			Object val2=prop.getValue(opeFromDb);
			LOGGER.debug("{}:{}=={}",new Object[]{prop.getName(),val1,val2});
			if (val1 == null) {
				if (val2==null) {
					LOGGER.debug("Les 2 valeurs sont nulles");
					continue;
				} else {
					return false;
				}
			}
			if (!val1.equals(val2)) {
				LOGGER.debug("Les valeurs sont differentes pour la propriete {}:{}!={}",
				new Object[]{prop.getName(),val1,val2});
				return false;
			}
		}
		return true;
	}
}
