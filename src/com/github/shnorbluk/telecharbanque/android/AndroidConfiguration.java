package com.github.shnorbluk.telecharbanque;

import android.content.*;
import com.github.shnorbluk.telecharbanque.util.*;

public class Configuration
{
	private static SharedPreferences pref;

	public static String getBoursoramaLogin() {
		return pref.getString("login", null);
	}
	public static String getBoursoramaPassword() {
		return pref.getString("password", null);
	}
	public static boolean isReloadOperationPages() {
		return pref.getBoolean("reloadOperations", false);
	}
	public static boolean isReloadListPages()
	{
//		logd("prefs=", pref.getAll());
		boolean b= pref.getBoolean("reloadListPages", true);
//		logd("reloadListPages="+b);
		return b;
	}
	private static void logd(Object... messages) {
		Utils.logd("Configuration", messages);
	}
	public static int getFirstPage() {
		return parseRange()[0];
	}
	private static int[] parseRange (){
		String str=pref.getString("mcPages", "1");
		String[] parts = str.split("-");
		final int first=Integer.parseInt(parts[0]);
		final int last;
		if (parts.length>1) {
			last=Integer.parseInt(parts[1]);
		} else {
			last=first;
		}
		return new int[]{first, last};
	}
 public static int getLastPage() {
	 return parseRange()[1];
 }
 
 public static boolean isSaveAllMcHistory() {
	 return pref.getBoolean("saveAllMcHistory", false);
 }
 
 public static boolean isSimuMode() {
	 return pref.getBoolean("simuMode", false);
 }
 
 public static void setPreferences(SharedPreferences pref) {
	 Configuration.pref=pref;
 }
}
