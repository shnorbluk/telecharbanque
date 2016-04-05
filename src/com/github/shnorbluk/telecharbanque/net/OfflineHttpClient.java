package com.github.shnorbluk.telecharbanque.net;

import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfflineHttpClient extends BufferedHttpClient
{
 private static final Logger LOGGER = LoggerFactory.getLogger(OfflineHttpClient.class);

 private OfflineHttpClient (BufferedHttpClient hClient) {
	 super(false, hClient.getHttpClient());
 }

 	@Override
	public BufferedReader getReaderFromUrl(String url, String[] params, boolean fromNet, String patternToCheck,String fileName ) throws IOException, ConnectionException {
		return super.getReaderFromUrl(url, params, false, patternToCheck, fileName);
	}
	
	@Override
	protected BufferedReader getReaderFromUrlOnline(String url, String[] params, String filename, String patternToCheck) throws IOException, IllegalStateException {
		loadStringFromNet(url, params, filename, patternToCheck);
		return new BufferedReader(new StringReader(patternToCheck));
	}
	
  @Override
 protected StringBuffer loadStringFromNet(String url, String[] params, String filename, String patternToCheck) throws IllegalStateException {
	 LOGGER.debug(url+params);
   if (params == null) {
	   displayMessage("Simulation get "+url,true);
   } else {
	   displayMessage("Simulation post "+url+" ", true);
   }
  return new StringBuffer (patternToCheck);
 }
}
