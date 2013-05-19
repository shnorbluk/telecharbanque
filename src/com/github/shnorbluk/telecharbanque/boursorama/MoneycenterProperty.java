package com.github.shnorbluk.telecharbanque.boursorama;

public enum MoneycenterProperty {
	LIBELLE("libelle", "TEXT NOT NULL") {
		public String getValue ( MoneyCenterOperation ope) {
			return ope.getLibelle();
		}
 },
 MEMO("memo", "TEXT") {
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getMemo();
	 }
 },
 DATE("date", "TEXT NOT NULL") {
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getDate();
	 }
 },
 AMOUNT("amount", "REAL NOT NULL") {
	 public String getValue ( MoneyCenterOperation ope) {
		 return Float.toString(ope.getAmount());
	 }
 },
 CATEGORY("category", "TEXT"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getCategory();
	 }
 },
 CATEGLABEL("categoryLabel", "TEXT") {
	 public String getValue (MoneyCenterOperation ope) {
		 return ope.getCategoryLabel();
	 }
 },
 ACCOUNT("account", "TEXT NOT NULL"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getAccount();
	 }
 },
 CHECKED("checked", "BOOLEAN NOT NULL"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return Boolean.toString(ope.isChecked());
	 }
 },
 PARENT("parent", "TEXT"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getParent();
	 
	 }
 };
 private String name;
 private String sqlType;
 MoneycenterProperty (String name, String sqlType) {
  this.name= name;
  this.sqlType=sqlType;
 }
 public String getName () {
  return name;
 }
 public String getSqlType() {
	 return sqlType;
 }
 public abstract String getValue ( MoneyCenterOperation ope) ;
}
