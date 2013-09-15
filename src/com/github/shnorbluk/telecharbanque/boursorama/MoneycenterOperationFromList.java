package com.github.shnorbluk.telecharbanque.boursorama;

import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;
import java.util.*;

public class MoneycenterOperationFromList extends MoneycenterOperation
 {
	private static final SimpleDateFormat DATE_FORMAT_FOR_LIST = new SimpleDateFormat("dd/MM/yy");
	private String parent;
	private Date date;
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
	private void logd(Object... o){
		Utils.logd("MoneycenterOperationFromList", o);
	}
	public boolean equals (McOperationInDb opeFromDb) {
		MoneycenterProperty[] props = MoneycenterProperty.propertiesInList();
		for (MoneycenterProperty prop:props) {
		Object val1=prop.getValueFromList(this);
		Object val2=prop.getValue(opeFromDb);
		logd(val1,"==",val2);
		if (val1 == null) return val2==null;
		if (!val1.equals(val2)) {
			throw new RuntimeException (
			"Les valeurs sont differentes pour la propriete "+
			prop.getName()+":"+val1+"!="+val2);
		}
		}
		return true;
	}
}
