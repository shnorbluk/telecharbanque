package com.github.shnorbluk.telecharbanque.net;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.message.*;

public abstract class SessionedBufferedHttpClient<SESSION extends SessionManager> extends BufferedHttpClient
{
 //private final SESSION sessionManager;
 private static final String TAG = "HClient";
 	private final BufferedHttpClient client;
 //private static final String TEMP_DIR = "/sdcard/Temp/telecharbanque/";
//	protected UI gui;
//	private final Token token;

//	public int getSessionInformation() throws ConnectionException {
//		connectIfNeeded();
//		return sessionManager.getSessionInformation();
//	}
	
	protected abstract boolean isConnected();
	protected abstract void connect() throws ConnectionException;
	
	@Override
	protected boolean connectIfNeeded() throws ConnectionException {
		client.connectIfNeeded();
		if (!isConnected()) {
			try {
				logd("Non connecté, connexion en cours via la classe ", getClass());
				connect();
				return true;
			} catch (Exception e) {
				throw new ConnectionException(e);
			}
		} else {
			logd("Déjà connecté, inutile de reconnecter");
			return false;
		}
		
	}
	
	public SessionedBufferedHttpClient( BufferedHttpClient client) {
		super(client.getCurrentTask(), client.getHttpClient());
//  this.sessionManager=sessionManager;
  this.client=client;
 }
 
 	@Override
 	protected HRequest executeRequest(HttpUriRequest requestget) throws IOException, IllegalStateException, ConnectionException {
		connectIfNeeded();
		return client.executeRequest( requestget);
	}
	
 private void logd(Object... o) {
	 Utils.logd(TAG,o);
 }
}
