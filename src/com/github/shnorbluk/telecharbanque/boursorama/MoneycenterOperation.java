package com.github.shnorbluk.telecharbanque.boursorama;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import java.text.*;
import javax.security.auth.*;

public interface MoneycenterOperation
 {
 
 
static final String TAG="MoneycenterOperation";
	static boolean log=false;
	
	public void setCategoryLabel(String categoryLabel);

	public String getCategoryLabel();
	public void setAmount(float amount);

	public float getAmount();

	public void setLibelle(String libelle);
	public String getLibelle();

	public void setId(String id);
	public String getId();

public String getAccount ();

 public void setAccount (String operationAccount);

 public void setCategory(String category);

public String getCategory ();
void setSubCategory(String subcategory);

public String getSubCategory ();
 
}
