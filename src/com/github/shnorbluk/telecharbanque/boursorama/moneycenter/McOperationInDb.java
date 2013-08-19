package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import java.text.*;
import java.util.*;

public class McOperationInDb extends MoneycenterOperation
{
	private static final SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private String parent;
	private boolean upToDate=true;
	private String memo ;
	private Date date;
	public void setDate (Date date ) {
		this. date = date ;
	}
	public Date getDate (){
		return date ;
	}
	public String getDateAsString(){
		return DATE_DISPLAY_FORMAT.format(date);
	}
	public void setParent (String parent ) {
		this.parent = parent ;
	}

	public String getParent (){
		return parent ;
	}
	
	public void setUpToDate (boolean upToDate) {
		this.upToDate=upToDate;
	}
	public String getMemo(){
		return memo;
	}
	public void setMemo (String operationMemo) {
		this.memo= operationMemo ;
	}
}
