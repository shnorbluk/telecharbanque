package com.github.shnorbluk.telecharbanque.boursorama;

import android.content.*;
import android.database.sqlite.*;
import com.github.shnorbluk.telecharbanque.util.*;

public class SQLiteMoneycenter extends SQLiteOpenHelper
{
	private static final String TAG="SQLiteMoneycenter";
	private static final String TABLE_OPERATIONS="operations";
	private static final String COL_UPTODATE ="uptodate";
	private static final String TABLE_CATEGORIES="categories";
	private static final String COL_CATEGORY = "category";
	private SQLiteDatabase bdd=null;
	public SQLiteMoneycenter(Context context) {
		super(context, "moneycenter.db", null, 1);
		
	}
	
	private void open() {
		if (bdd == null) {
			bdd= getWritableDatabase();
		}
	}
	
	void setOperation(MoneyCenterOperation operation) {
		open();
		ContentValues values = new ContentValues();
		for (MoneycenterProperty property:MoneycenterProperty.values()) {
			values.put(property.getName(), property.getValue(operation));
		}
		values.put(COL_UPTODATE, true);
		logd("Insertion des valeurs:",values);
		bdd.insert(TABLE_OPERATIONS, null, values);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		boolean first=true;
		String QUERY="CREATE "+
		  "TABLE "+TABLE_OPERATIONS+" (";
		for (MoneycenterProperty property:MoneycenterProperty.values()) {
			if (!first) {
				QUERY+=", ";
			} else {
				first=false;
			}
	    	QUERY += property.getName()+" "+
			  property.getSqlType();
		}
		QUERY+= ", "+COL_UPTODATE+" BOOLEAN NOT NULL);";
		logd("Exécution de la requête "+QUERY);
		  db.execSQL(QUERY);
		  db.execSQL("CREATE TABLE "+TABLE_CATEGORIES+
		    " ("+COL_CATEGORY+" TEXT NOT NULL);");
	}
	
	private void logd(Object... o) {
		Utils.logd(TAG, o);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int p2, int p3)
	{
		 final String QUERY = "DROP TABLE operations;";
		 db.execSQL(QUERY);
		 onCreate(db);
	}
	
	
}
