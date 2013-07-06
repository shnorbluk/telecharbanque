package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import android.content.*;
import android.database.*;
import com.github.shnorbluk.telecharbanque.boursorama.*;

public abstract class McStringProperty extends MoneycenterProperty<String> 
{
	public McStringProperty(String name, String constraint) {
		super(name, "TEXT", constraint);
	}
	@Override
		public void setValue(MoneycenterOperation ope, Cursor cursor) {
			setValue(ope, cursor.getString(ordinal()));
		}
		protected abstract void setValue(MoneycenterOperation ope, String val);
		
		@Override 
		public void put(ContentValues values, String key, String value){
			values.put(key, value);
		}
}
