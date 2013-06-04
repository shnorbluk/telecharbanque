package com.github.shnorbluk.telecharbanque.util;

import android.os.*;
import com.github.shnorbluk.telecharbanque.*;

public abstract class AsynchTask<Return> extends AsyncTask<String, String, Return> implements UI
{
 protected String TAG;
 	@Override
	public void display(String message, boolean persistent)
	{
		Utils.logd(TAG, "display(",message, ",) ");
		publishProgress( message, Boolean.toString(persistent));

	}

	@Override
	public void onProgressUpdate(String... messages) {
		if (messages.length==0) {
			Utils.logd("AsynchTask", "Erreur 1 seul paramètre, message=",messages[0]);
		}
		MainActivity.display(messages);
	}
}
