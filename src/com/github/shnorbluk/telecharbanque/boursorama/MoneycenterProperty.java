package com.github.shnorbluk.telecharbanque.boursorama;

public enum MoneycenterProperty {
	LIBELLE("libelle") {
		public String getValue ( MoneyCenterOperation ope) {
			return ope.getLibelle();
		}
 },
 MEMO("memo") {
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getMemo();
	 }
 },
 DATE("date") {
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getDate();
	 }
 },
 AMOUNT("amount") {
	 public String getValue ( MoneyCenterOperation ope) {
		 return Float.toString(ope.getAmount());
	 }
 },
 CATEGORY("category"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getCategory();
	 }
 },
 CATEGLABEL("categoryLabel") {
	 public String getValue (MoneyCenterOperation ope) {
		 return ope.getCategoryLabel();
	 }
 },
 ACCOUNT("account"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getAccount();
	 }
 },
 CHECKED("checked"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return Boolean.toString(ope.isChecked());
	 }
 },
 PARENT("parent"){
	 public String getValue ( MoneyCenterOperation ope) {
		 return ope.getParent();
	 }
 };
 private String name;
 MoneycenterProperty (String name) {
  this.name= name;
 }
 public String getName () {
  return name;
 }
 public abstract String getValue ( MoneyCenterOperation ope) ;
}
