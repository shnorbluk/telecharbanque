package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.net.*;
import java.io.*;

public class CheckOperationChange extends OperationChange
{
	private final boolean checked;
	private final AttributesOperationChange toWait;
	public CheckOperationChange(String id, boolean checked, AttributesOperationChange toWait ) {
		super(id);
		this.checked=checked;
		this.toWait=toWait;
	}
	
	@Override
	public void perform (MoneycenterPersistence persistence) throws ConnectionException, IOException {
		if (toWait != null && !toWait.isDone()) {
			toWait.perform(persistence);
		}
		persistence.pointeOperation(id,checked);
	}
}
