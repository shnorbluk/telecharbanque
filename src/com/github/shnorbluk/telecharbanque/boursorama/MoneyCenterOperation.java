package com.github.shnorbluk.telecharbanque.boursorama;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import java.text.*;
import javax.security.auth.*;

public abstract class MoneycenterOperation
 {
 
 private String id;
 private String libelle;
 private String account;
 
 private String num_cheque;
 private float amount ;
 private String categ;
 private String subcateg;
 
 private boolean checked;
	
	private String categoryLabel;
	private static final String TAG="MoneycenterOperation";
	private static boolean log=false;
	
	
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

 public boolean isChecked() {
  return checked;
 }

 public void setChecked (boolean checked) {
  this.checked = checked;
 }

 

public String getAccount (){
  return account;
 }

 

 public void setNumCheque(String numCheque) {
  num_cheque = numCheque;
 }
 
 public String getNumCheque() {
	 return num_cheque;
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
 

 protected MoneycenterOperation () {
 }
 
 private static void logd(Object o) {
	 if(log) {
	  Utils.logd(TAG,o);
	 }
 }

 
}
