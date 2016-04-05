package com.github.shnorbluk.telecharbanque.android;

import android.preference.*;
import android.os.*;
import com.mycompany.myapp.*;

public class Preferences extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
