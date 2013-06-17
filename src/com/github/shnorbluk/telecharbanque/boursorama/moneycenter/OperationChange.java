package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.net.*;
import java.io.*;

public abstract class OperationChange
{
	protected String id;
	protected OperationChange(String id) {
		this.id=id;
	}
	public abstract void perform(MoneycenterPersistence persistence) throws ConnectionException, IOException;
}
