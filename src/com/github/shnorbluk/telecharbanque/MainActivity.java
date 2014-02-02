package com.github.shnorbluk.telecharbanque;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.github.shnorbluk.telecharbanque.boursorama.*;
import com.github.shnorbluk.telecharbanque.boursorama.moneycenter.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.http.impl.client.*;
import org.orman.dbms.*;
import org.orman.dbms.sqliteandroid.*;
import org.orman.mapper.*;
import org.orman.mapper.exception.*;
import org.orman.util.logging.*;
import com.github.shnorbluk.telecharbanque.util.*;
import com.github.shnorbluk.telecharbanque.net.*;

public class MainActivity extends Activity
{
 private static final String TAG="MainActivity";
 private static TextView ecran = null;
 private static AssetManager assetMgr;
 private static String text="";
 private static TextView displayer= null;
	private final DefaultHttpClient httpClient = new DefaultHttpClient();
//	private final BoursoramaClient bClient = new BoursoramaClient(httpClient,gui);
	private final SQLiteMoneycenter db = new SQLiteMoneycenter(this);
	private DownloadMoneyCenterTask downTask = null;
	private final Token token = new Token(false); 

	private static abstract class Screen {
		private static Screen MAIN = new Screen(R.id.itemPrincipal)
		{
			@Override
			protected void display(final MainActivity activity) {
				activity.displayMainScreen();
			}
		};
		private static Screen OPERATION_LIST = new Screen(R.id.itemOperations) {
			@Override
			protected void display(final MainActivity activity) {
				activity.displayOperationListHtml();
			}
		};
		private final int id;
		protected abstract void display(final MainActivity activity);
		private Screen(int id) {
			this.id=id;
		}
		private int getId() {return id;}
		private static Screen[] values() {
			return new Screen[]{MAIN};
		}
	}
	private Screen currentScreen = Screen.MAIN;
	@Override
	public void onBackPressed() {
		if (downTask !=  null) {
			downTask.cancel(true);
		}
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		Database odb = new SQLiteAndroid(this, "telecharbanque.db");
		Log.setLevel(LoggingLevel.TRACE);
	
		MappingSession.registerDatabase(odb);
		try {
		MappingSession.registerEntity(CheckOperationChange.class);
			MappingSession.registerEntity(AttributesOperationChange.class);
			MappingSession.registerEntity(MCPropertyValue.class);
			MappingSession.registerEntity(McOperationInDb.class);
		} catch (MappingSessionAlreadyStartedException e) {
			// Pas grave
		}
		MappingSession.startSafe();
		
		assetMgr=getAssets();
		Configuration.setPreferences(PreferenceManager.getDefaultSharedPreferences(this));
		currentScreen.display(this);
    }
	
	private void displayMainScreen() 
	{
		setContentView(R.layout.main);
		ecran=(TextView)findViewById(R.id.EditText01);
		displayer=ecran;
		ecran.setText(text);
		Button button0 = (Button) findViewById(R.id.Button01);
		button0 .setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					downTask = new DownloadMoneyCenterTask( httpClient, token);
					try {
						downTask .execute("");
					} catch (Exception e) {
						display(e.toString(), true);
					}
				}
			});

		Button buttonUp = (Button) findViewById(R.id.ButtonUp);
		buttonUp .setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final UpdateOperationsTask uploadTask = new UpdateOperationsTask( httpClient, token );
					uploadTask .execute("");
				}
			});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menuprincipal, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean found=false;
		for (Screen screen:Screen.values()) {
			if(item.getItemId() == screen.getId()) {
				currentScreen=screen;
				currentScreen.display(this);
				found=true;
				break;
			}
		}
		if (found) {
			
		} else if(item.getItemId() == R.id.itemOptions) {
			startActivityForResult(new Intent(this, Preferences.class), 1);
		} else if(item.getItemId() == R.id.itemPointage) {
			displayOperationEditor();
		}
		return super.onOptionsItemSelected(item);
	}
	private void displayOperationListHtml () {
		setContentView(R.layout.list_operations);
		final WebView webView = (WebView) findViewById(R.id.webView1);
		final List<String> numbers = Arrays.asList(new String[] { "Date", "Montant", "Libellé", "Type", "Pointé", "Description", "01/01/1111", "Libellé bidon", "Type bidon", "N", "Description bidon", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, numbers);
		final String customHtml = "<html><body><table><tr><b><td>Date</td><td>Montant</td><td>Libell&eacute;</td><td>Type</td><td>Description></td></tr></b><tr><td>Exemple</td></tr></table></body></html>"; 
		webView.loadData(customHtml, "text/html", "UTF-8");
	}
	private void displayOperationListGrid () {
		setContentView(R.layout.list_operations_grid);
		final GridView gridView = (GridView) findViewById(R.id.grid);
		final List<String> values = new ArrayList<String>();
		values.addAll(Arrays.asList(new String[] { "Date", "Montant", "Libellé", "Type", "Pointé", "Description"}));
		final List<McOperationInDb> opList = Model.fetchAll(McOperationInDb.class);
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT,Locale.FRANCE);
		for (int index = 0; index <= 30; index ++) {
		McOperationInDb operation =opList.get(index);
		values.add(format.format(operation.getDate()));
		values.add(Float.toString(operation.getAmount()));
		values.add(operation.getLibelle());
		values.add(operation.getCategoryLabel());
		values.add(operation.isChecked()?"O":"N");
		values.add(operation.getMemo());
		}
	//	values.addAll(Arrays.asList(new String[] { "01/01/1111", "39.99", "Libellé bidon", "Type bidon", "N", "Description bidon", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values); 
		gridView.setAdapter(adapter);
	}
	private void displayOperationEditor() {
		setContentView(R.layout.edit_operation);
		Button buttonCheck = (Button) findViewById(R.id.ButtonOkPointage);
		final EditText idField = (EditText)findViewById(R.id.EditText02);
		buttonCheck.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final String operationId = idField.getText().toString();
					final TextView logPanel=(TextView)findViewById(R.id.EditOpeLog);
					logPanel.setText("Pointage de l'opération "+operationId);
					final CheckOperationTask checkTask = new CheckOperationTask( httpClient, token);
					checkTask .execute(operationId);
				}
			});
	}
 public static InputStream loadResource(String path) throws IOException {
  return assetMgr.open(path);
 }

 public static void display(String... messages) {
  if (messages[0] ==null || messages[0].equals("null")) {
      Utils.logd(TAG, "message est null"+Thread.currentThread().getStackTrace());
  }
  display( messages[0], Boolean.valueOf(messages[1]));
 }
 
	public static void display(String message, boolean persistent) {
		final String allDisplay=text+message+"\n";
  if(persistent) {
	  text=allDisplay;
 }
 		if (ecran != null) {
			ecran.setText(allDisplay);
		}
		}
}

