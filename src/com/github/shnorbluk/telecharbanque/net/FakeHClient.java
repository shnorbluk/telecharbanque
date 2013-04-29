package com.github.shnorbluk.telecharbanque.net;

import com.github.shnorbluk.telecharbanque.*;
import com.github.shnorbluk.telecharbanque.util.*;

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
 protected StringBuffer loadStringFromNet(String url, String[] params, String filename, String patternToCheck) throws IllegalStateException {
	 logd(url+params);
   if (params == null) {
    gui.display("Simulation get "+url);
   } else {
    gui.display("Simulation post "+url+" ");
   }
  return new StringBuffer (patternToCheck);
 }
}
