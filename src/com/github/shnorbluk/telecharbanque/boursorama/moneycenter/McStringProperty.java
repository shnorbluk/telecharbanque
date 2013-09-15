package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import android.content.*;
import android.database.*;
import com.github.shnorbluk.telecharbanque.boursorama.*;

public abstract class McStringProperty<OPE extends MoneycenterOperation> extends MoneycenterProperty<String> 
{
	public McStringProperty(String name, String constraint) {
		super(name, "TEXT", constraint);
	}
	@Override
		public void setValue(McOperationInDb ope, Cursor cursor) {
			try {
				cursor.moveToFirst();
			setValue(ope, cursor.getString(ordinal()));
			} catch (CursorIndexOutOfBoundsException e) {
				throw new RuntimeException("property "+getName()+" ordinal="+
				ordinal()+" position="+cursor.getPosition()+" count="+cursor.getCount(), e);
			}
		}
		protected abstract void setValue(McOperationInDb ope, String val);
		
		@Override 
		public void put(ContentValues values, String key, String value){
			values.put(key, value);
		}
}
