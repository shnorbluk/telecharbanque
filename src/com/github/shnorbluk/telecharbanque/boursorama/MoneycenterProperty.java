package com.github.shnorbluk.telecharbanque.boursorama;

import android.content.*;
import android.database.*;
import java.text.*;
import java.util.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import android.webkit.*;

public abstract class MoneycenterProperty<VALUE>
 {
	 private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
 public static final McStringProperty ID=new McStringProperty("id", "NOT NULL PRIMARY KEY"){
	 @Override
	 public String getValue(MoneycenterOperation ope) {
		 return ope.getId();
	 }
	 @Override
	 public void setValue(McOperationInDb ope, String value) {
		 ope.setId(value);
	 }
 };
	public static final McStringProperty LIBELLE= new McStringProperty("libelle", "NOT NULL") {
		public String getValue ( MoneycenterOperation ope) {
			return ope.getLibelle();
		}
		@Override
		public void setValue(McOperationInDb ope, String val) {
			ope.setLibelle(val);
		}
 };
	public static final McStringProperty MEMO= new McStringProperty("memo", "") {
		@Override
		public String getValue (MoneycenterOperation ope) {
		 return ((McOperationInDb)ope).getMemo();
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
		public Date getValue ( MoneycenterOperation ope) {
		 return ((McOperationInDb)ope).getDate();
	 }
		public Date getValueFromList(MoneycenterOperationFromList ope) {
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
		public Float getValue ( MoneycenterOperation ope) {
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
	 public String getValue ( MoneycenterOperation ope) {
		 return ope.getCategory();
	 }
	 public void setValue(McOperationInDb ope, String val) {
		ope.setCategory(val);
	}
 };
	public static final McStringProperty CATEGLABEL =new McStringProperty("categoryLabel", "") {
		public String getValue (MoneycenterOperation ope) {
		 return ope.getCategoryLabel();
	 }
		public void setValue(McOperationInDb ope, String val) {
		ope.setCategoryLabel(val);
	}
 };
	public static final McStringProperty ACCOUNT= new McStringProperty("account", "NOT NULL"){
		public String getValue ( MoneycenterOperation ope) {
		 return ope.getAccount();
	 }
		public void setValue(McOperationInDb ope, String val) {
		ope.setAccount(val);
	}
 };
	public static final MoneycenterProperty<Boolean> CHECKED= new MoneycenterProperty<Boolean>("checked", "BOOLEAN", "NOT NULL") {
		public Boolean getValue ( MoneycenterOperation ope) {

			return ((IMcOperationFromList)ope) .isChecked();
	 }
	 @Override
	 public Boolean getValueFromList(McOperationInDb ope) {
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
		public String getValue ( MoneycenterOperation ope) {
		 return ((McOperationInDb)ope).getParent();
	 }
	 @Override
		public String getValueFromList(MoneycenterOperationFromList ope) {
			return ope.getParent();
		}
		public void setValue(McOperationInDb ope, String val) {
		 ope.setParent(val);
	}
 };
 private final String name;
 private final String type;
 private final String sqlConstraint;
 private int ord;
 private static int counter=0;
 protected MoneycenterProperty (String name, String type, String sqlConstraint) {
  this.name= name;
  this.type=type;
  this.sqlConstraint = sqlConstraint;
  ord=counter++;
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
 public VALUE getValueInDb( McOperationInDb ope) {
	 return getValue(ope);
 }
	protected abstract VALUE getValue ( MoneycenterOperation ope);
 public abstract void setValue(McOperationInDb ope, Cursor cursor);
 public abstract void put(ContentValues values, String name, VALUE value);
	public VALUE getValueFromList (MoneycenterOperationFromList ope) {
		return getValue(ope);
	}
 public static MoneycenterProperty[] values() {
	 return new MoneycenterProperty[] {
		 ID, LIBELLE, MEMO, DATE, 
		 AMOUNT, CATEGORY, 
		 CATEGLABEL, ACCOUNT, 
		 CHECKED, PARENT
	 };
 }
	public static MoneycenterProperty[] propertiesInList() {
		return new MoneycenterProperty[] {
			ID, LIBELLE, DATE, 
			AMOUNT, CATEGORY, 
			CATEGLABEL, ACCOUNT, 
			CHECKED, PARENT
		};
	}
 
}
