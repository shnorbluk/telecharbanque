package com.github.shnorbluk.telecharbanque.boursorama;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.util.*;
import org.orman.mapper.*;

public class SQLiteMoneycenter extends SQLiteOpenHelper
{
	private static final String TAG="SQLiteMoneycenter";
	private static final String TABLE_OPERATIONS="operations";
	private static final String COL_UPTODATE ="uptodate";
	private static final String TABLE_CATEGORIES="categories";
	private static final String COL_ID = "id";
	private static final String COL_CATEGORY = "category";
	private static final String COL_SUBCATEGORY = "subcategory";
	private static final String COL_CATEGORY_LABEL="categoryLabel";
	private static final String COL_DEBIT="debit";
	private static final String TABLE_ACCOUNT="account";
	
	private SQLiteDatabase bdd=null;
	public SQLiteMoneycenter(Context context) {
		super(context, "moneycenter.db", null, 7);
		
	}
	
	public void open() {
		if (bdd == null) {
			bdd= getWritableDatabase();
		}
	}
	
	McOperationInDb getOperation(String id) {
		open();
		McOperationInDb ope=new McOperationInDb();
	//	Cursor cursor=bdd.query(TABLE_OPERATIONS, null, MoneycenterProperty.ID.getName()+"='"+id+"'", null, null, null, null, null);
		Cursor cursor=bdd.rawQuery("select * from operations where id = ?", new String[]{id});
		if (cursor.getCount() == 0) return null;
		for (MoneycenterProperty prop:MoneycenterProperty.values()) {
			prop.setValue(ope, cursor);
		}
		return ope;
	}
	
	void setOperation(McOperationInDb operation) {
		open();
		ContentValues values = new ContentValues();
		for (MoneycenterProperty property:MoneycenterProperty.values()) {
			property.put(values, property.getName(), property.getValue(operation));
		}
		values.put(COL_UPTODATE, true);
		logd("Insertion des valeurs:",values);
		bdd.replace(TABLE_OPERATIONS, null, values);
		logd("Insertion réussie");
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
		    " ("+COL_CATEGORY+" TEXT NOT NULL, "+
			COL_SUBCATEGORY+" TEXT, "+
			COL_CATEGORY_LABEL+" TEXT NOT NULL, "+
			COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
			COL_DEBIT+" BOOLEAN);");
		db.execSQL("CREATE TABLE "+ TABLE_ACCOUNT+
		  "( account STRING PRIMARY KEY, "+
		  "solde REAL NOT NULL);");
	}
	
	private void logd(Object... o) {
		Utils.logd(TAG, o);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int p2, int p3)
	{
		 db.execSQL("DROP TABLE IF EXISTS "+TABLE_OPERATIONS+";");
		 final String QUERY = "DROP TABLE IF EXISTS "+TABLE_CATEGORIES+";";
		 db.execSQL(QUERY);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_ACCOUNT +";");
		 onCreate(db);
	}
	
//	private static <T> List<T> getObectsFromDb(BdTableDescriptor<T> table, Cursor cursor) {
/*		final List<T> objs=new ArrayList(cursor.getCount());
		for (cursor.moveToFirst();!cursor.isAfterLast(); cursor.moveToNext()) {
			T obj =TABLE_CHANGES.createObject();
			DbColumn[] cols=TABLE_CHANGES.getColumns();
			for (int i=0; i< cols.length;i++){
				DbColumn col=cols[i];
				col.saveToObject(cursor);
			}
		}
	}*/
	public List<? extends OperationChange> listChangesToApply() {
		return Model.fetchAll(CheckOperationChange.class);
	}
	
}
