package com.github.shnorbluk.telecharbanque.boursorama;

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
}
