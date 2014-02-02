package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.util.*;
import org.apache.http.impl.client.*;
import com.github.shnorbluk.telecharbanque.net.*;
import java.io.*;

public class CheckOperationTask extends AsynchTask<String>
{
	private final MoneycenterPersistence persistence;
	public CheckOperationTask( DefaultHttpClient httpClient, Token token) {
		final BufferedHttpClient hClient = new BufferedHttpClient(this, httpClient);
		final BoursoramaClient bClient = new BoursoramaClient(hClient, this, token);
		final SessionedBufferedHttpClient<BoursoramaClient> bhClient = new SessionedBufferedHttpClient<BoursoramaClient>(hClient, bClient);
		final MoneycenterSession mcSession = new MoneycenterSession(bhClient, this);
		final SessionedBufferedHttpClient<MoneycenterSession> mchClient = new SessionedBufferedHttpClient<MoneycenterSession>(bhClient,mcSession);
		MoneycenterClient mcClient = new MoneycenterClient(mchClient, this);
		this. persistence = new MoneycenterPersistence (mcClient, this);
	}
	private void logd(Object... o) {
		Utils.logd("CheckOperationTask", o);
	}
	
	@Override
	public String doInBackground(String... opeIds)
	{
		final String id=opeIds[0];
		final boolean checked=true;
		display("Pointage de l'opération "+id+":"+checked, false);
		final CheckOperationChange changeToDo = new CheckOperationChange(id, checked);
		try
		{
			changeToDo.insert();
			changeToDo.perform(persistence);
		}
		catch (Exception e)
		{

			display("Erreur "+e, true);
		}
		return null;
	}
	
	
}
