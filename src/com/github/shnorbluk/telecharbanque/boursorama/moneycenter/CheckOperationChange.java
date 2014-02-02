package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.net.*;
import java.io.*;
import java.util.*;
import org.orman.mapper.*;
import org.orman.mapper.annotation.*;

import org.orman.mapper.annotation.Entity;

@Entity
public class CheckOperationChange extends Model<CheckOperationChange> implements OperationChange
{

@PrimaryKey
private String id;

public void setChecked(boolean checked)
{
	this.checked = checked;
}

public boolean isChecked()
{
	return checked;
}
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id=id;
	}
	@NotNull
	private boolean checked;
	
	public CheckOperationChange(String id, boolean checked) {
		this.checked=checked;
		this.id=id;
	}
	
	public CheckOperationChange() {
		
	}
	
	@Override
	public void perform (MoneycenterPersistence persistence) throws ConnectionException, IOException {
		persistence.pointeOperation(id,checked);
	}
	@Override
	public void performFromFile(MoneycenterPersistence persistence) throws IOException, ConnectionException
	{
		perform(persistence);
		persistence.declareLinesAsSynced (Arrays.<String>asList(id+".checked"));
	}
}
