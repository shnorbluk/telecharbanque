package com.github.shnorbluk.telecharbanque.boursorama;

import android.content.*;
import android.database.*;
import java.text.*;
import java.util.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import android.webkit.*;

public abstract class MoneycenterProperty<T>
 {
	 private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
 public static final McStringProperty ID=new McStringProperty("id", "NOT NULL PRIMARY KEY"){
	 @Override
	 public String getValue(McOperationInDb ope) {
		 return ope.getId();
	 }
	 @Override
	 public void setValue(McOperationInDb ope, String value) {
		 ope.setId(value);
	 }
 };
	public static final McStringProperty LIBELLE= new McStringProperty("libelle", "NOT NULL") {
		public String getValue ( McOperationInDb ope) {
			return ope.getLibelle();
		}
		@Override
		public void setValue(McOperationInDb ope, String val) {
			ope.setLibelle(val);
		}
 };
	public static final McStringProperty MEMO= new McStringProperty("memo", "") {
	 public String getValue ( McOperationInDb ope) {
		 return ope.getMemo();
	 }
	 @Override
		public void setValue(McOperationInDb ope, String val) {
			ope.setMemo(val);
		}
 };
	public static final MoneycenterProperty<Date> DATE= new MoneycenterProperty<Date>("date", "TEXT", "NOT NULL") {
		@Override
		public void put (ContentValues values, String key, Date value) {
			values.put(key, DB_DATE_FORMAT.format(value));
		}
		public Date getValue ( McOperationInDb ope) {
		 return ope.getDate();
	 }
		public void setValue(McOperationInDb ope, Cursor cursor) {
			try {
				ope.setDate(DB_DATE_FORMAT.parse(cursor.getString(ordinal())));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			
		}
 };
	public static final MoneycenterProperty<Float> AMOUNT= new MoneycenterProperty<Float>("amount", "REAL", "NOT NULL") {
		public Float getValue ( McOperationInDb ope) {
		 return ope.getAmount();
	 }
	 @Override
	 public void put(ContentValues values, String key, Float value) {
		 values.put(key, value);
	 }
		public void setValue(McOperationInDb ope, Cursor cursor){
		 ope.setAmount(cursor.getFloat(ordinal()));
	 }
 };
 public static final McStringProperty CATEGORY= new McStringProperty("category", ""){
	 public String getValue ( McOperationInDb ope) {
		 return ope.getCategory();
	 }
	 public void setValue(McOperationInDb ope, String val) {
		ope.setCategory(val);
	}
 };
	public static final McStringProperty CATEGLABEL =new McStringProperty("categoryLabel", "") {
		public String getValue (McOperationInDb ope) {
		 return ope.getCategoryLabel();
	 }
		public void setValue(McOperationInDb ope, String val) {
		ope.setCategoryLabel(val);
	}
 };
	public static final McStringProperty ACCOUNT= new McStringProperty("account", "NOT NULL"){
		public String getValue ( McOperationInDb ope) {
		 return ope.getAccount();
	 }
		public void setValue(McOperationInDb ope, String val) {
		ope.setAccount(val);
	}
 };
	public static final MoneycenterProperty<Boolean> CHECKED= new MoneycenterProperty<Boolean>("checked", "BOOLEAN", "NOT NULL") {
		public Boolean getValue ( McOperationInDb ope) {
		 return ope.isChecked();
	 }
		public void setValue(McOperationInDb ope, Cursor cursor) {
		ope.setChecked(cursor.getInt(ordinal())==1);
	}
	@Override
	public void put(ContentValues values, String name, Boolean value) {
		values.put(name, value);
	}
 };
	public static final McStringProperty PARENT = new McStringProperty("parent", ""){
		public String getValue ( McOperationInDb ope) {
		 return ope.getParent();
	 }
		public void setValue(McOperationInDb ope, String val) {
		 ope.setParent(val);
	}
 };
 private final String name;
 private final String type;
 private final String sqlConstraint;
 private static int ord=0;
 protected MoneycenterProperty (String name, String type, String sqlConstraint) {
  this.name= name;
  this.type=type;
  this.sqlConstraint = sqlConstraint;
 }
 protected int ordinal() {
  return ord;
 }
 public String getName () {
  return name;
 }
 public String getSqlType() {
	 return type+" "+sqlConstraint;
 }
 public abstract T getValue ( McOperationInDb ope) ;
 public abstract void setValue(McOperationInDb ope, Cursor cursor);
 public abstract void put(ContentValues values, String name, T value);
 public static MoneycenterProperty[] values() {
	 return new MoneycenterProperty[] {
		 ID, LIBELLE, MEMO, DATE, 
		 AMOUNT, CATEGORY, 
		 CATEGLABEL, ACCOUNT, 
		 CHECKED, PARENT
	 };
 }
 
}
