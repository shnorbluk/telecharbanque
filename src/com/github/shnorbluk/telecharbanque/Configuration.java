package com.github.shnorbluk.telecharbanque;

import android.content.*;

public class Configuration
{
 private static final int firstPage = 1;
	private static final boolean reloadMcPages = true;
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
	public static boolean isReloadMcPages()
	{
		return reloadMcPages;
	}
	public static int getFirstPage() {
		return firstPage;
	}
 public static int getLastPage() {
	 return Integer.parseInt(pref.getString("lastMcPage", "1"));
 }
 
 public static boolean isSaveAllMcPages() {
	 return pref.getBoolean("saveAllMcPages", false);
 }
 
 public static boolean isSimuMode() {
	 return pref.getBoolean("simuMode", false);
 }
 
 public static void setPreferences(SharedPreferences pref) {
	 Configuration.pref=pref;
 }
}
