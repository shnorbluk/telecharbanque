package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import java.util.*;
import org.orman.mapper.*;
import org.orman.mapper.annotation.Entity;
import org.orman.mapper.annotation.*;

@Entity
public class AttributesOperationChange extends Model<AttributesOperationChange> implements OperationChange
{

	public void performFromFile(MoneycenterPersistence persistence)
	{
		perform(persistence);
	}


	public List<MCPropertyValue> getPropertiesToSet()
	{
		return propertiesToSet;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id=id;
	}
	
	@Index(unique=true)
	private String id;
	@PrimaryKey(autoIncrement=true)
	public int fakeId;
	@NotNull
	@OneToMany(toType=AttributesOperationChange.class, onField="change")
	public EntityList<AttributesOperationChange, MCPropertyValue> propertiesToSet=new EntityList(AttributesOperationChange.class, MCPropertyValue.class, this) ;
	@NotNull
	private boolean done=false;
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done=done;
	}
	
	@Override
	public void perform (MoneycenterPersistence persistence) {
		//TODO
		done=true;
	}
	public AttributesOperationChange(String id) {
		this.id=id;
	}
	public AttributesOperationChange () {
		
	}
}
