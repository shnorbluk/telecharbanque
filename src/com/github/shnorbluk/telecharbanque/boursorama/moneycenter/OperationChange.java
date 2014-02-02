package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.net.*;
import java.io.*;

public interface OperationChange
{

	public void performFromFile(MoneycenterPersistence persistence)throws IOException, ConnectionException;

	public abstract void perform(MoneycenterPersistence persistence) throws ConnectionException, IOException;
	public String getId();
	public void setId(String id);
}
