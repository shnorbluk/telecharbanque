package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import java.text.*;
import java.util.*;
import org.orman.mapper.*;
import org.orman.mapper.annotation.Entity;
import org.orman.mapper.annotation.*;

@Entity
public class McOperationInDb extends Model<McOperationInDb> implements IMcOperationFromList
{
	private static final transient SimpleDateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	@PrimaryKey(autoIncrement=true)
	private int fakeId;
	private String parent;
	@NotNull
	private Date updateDate=new Date();
	@NotNull
	private String memo ;
	@NotNull
	private Date date;
	@NotNull
	private boolean checked;
	private String numCheque;
	@Index(unique=true)
	private String id;
	@NotNull
	private String libelle;
	@NotNull
	private String account;
@NotNull
	private float amount ;
	private String category;
	private String subCategory;

	private String categoryLabel;
	private transient boolean saved=true;
	private transient boolean upToDate=true;

	public String toString() {
		return "{id:"+id+
			",libelle:"+libelle+
			",account:"+account+
			",updateDate:"+updateDate+
			",memo:"+memo+
			",date:"+date+
			",checked:"+checked+"}";
	}
	public void setUpToDate(boolean upToDate)
	{
		this.upToDate = upToDate;
	}

	public boolean isUpToDate()
	{
		return upToDate;
	}

	public void setSaved(boolean saved)
	{
		this.saved=saved;
	}
	
	public boolean isSaved() {
		return saved;
	}

	public void setFakeId(int fakeId)
	{
		this.fakeId = fakeId;
	}

	public int getFakeId()
	{
		return fakeId;
	}

	public void setCategoryLabel(String categoryLabel)
	{
		this.categoryLabel = categoryLabel;
	}

	public String getCategoryLabel()
	{
		return categoryLabel;
	}

	public void setSubCategory(String subCategory)
	{
		this.subCategory = subCategory;
	}

	public String getSubCategory()
	{
		return subCategory;
	}

	public void setCategory(String categ)
	{
		this.category = categ;
	}

	public String getCategory()
	{
		return category;
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
	public String getDateAsString(){
		return DATE_DISPLAY_FORMAT.format(date);
	}
	public void setParent (String parent ) {
		this.parent = parent ;
	}

	public String getParent (){
		return parent ;
	}
	
	public void setUpdateDate (Date updateDate) {
		this.updateDate=updateDate;
	}
	public Date getUpdateDate () {
		return this.updateDate;
	}
	public String getMemo(){
		return memo;
	}
	public void setMemo (String operationMemo) {
		this.memo= operationMemo ;
	}
	public void setNumCheque(String numCheque) {
		this.numCheque = numCheque;
	}
	public String getNumCheque() {
		return numCheque;
	}
}
