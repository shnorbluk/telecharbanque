package com.github.shnorbluk.telecharbanque.net;

import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.io.*;

public class FakeHClient extends HClient
{
 private static final String TAG = "FakeHClient";

 public FakeHClient (UI gui) {
  super(null,null, gui);
 }

 private static void logd(Object o) {
	 Utils.logd(TAG,o);
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
	 logd(url+params);
   if (params == null) {
    gui.display("Simulation get "+url,true);
   } else {
    gui.display("Simulation post "+url+" ", true);
   }
  return new StringBuffer (patternToCheck);
 }
}
