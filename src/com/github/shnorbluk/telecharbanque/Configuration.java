package com.github.shnorbluk.telecharbanque;

import android.content.*;

public class Configuration
{
 private static final int firstPage = 1;
 private static final int lastPage = 1;
	private static final boolean reloadMcPages = true;
	private static final boolean reloadOperationPages=true;
	private static final boolean saveAllMcPages =false;
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
	 return lastPage;
 }
 
 public static boolean isSaveAllMcPages() {
	 return saveAllMcPages;
 }
 
 public static boolean isSimuMode() {
	 return pref.getBoolean("simuMode", false);
 }
 
 public static void setPreferences(SharedPreferences pref) {
	 Configuration.pref=pref;
 }
}
