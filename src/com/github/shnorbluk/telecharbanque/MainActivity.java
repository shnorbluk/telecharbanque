package com.github.shnorbluk.telecharbanque;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.github.shnorbluk.telecharbanque.boursorama.*;
import java.io.*;
import org.apache.http.impl.client.*;

public class MainActivity extends Activity
{
 private static final String TAG="MainActivity";
 private static TextView ecran;
 private static AssetManager assetMgr;
 private static String text="";
	private final DefaultHttpClient httpClient = new DefaultHttpClient();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		assetMgr=getAssets();
        setContentView(R.layout.main);
		ecran=(TextView)findViewById(R.id.EditText01);
		Configuration.setPreferences(PreferenceManager.getDefaultSharedPreferences(this));
      
       Button button0 = (Button) findViewById(R.id.Button01);
		 button0 .setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
			 final DownloadMoneyCenterTask downTask = new DownloadMoneyCenterTask( httpClient );
           downTask .execute("");
         }
        });
   
       Button buttonUp = (Button) findViewById(R.id.ButtonUp);
		 buttonUp .setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
			 final UpdateOperationsTask uploadTask = new UpdateOperationsTask( httpClient );
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
		if(item.getItemId() == R.id.itemOptions) {
			startActivityForResult(new Intent(this, Preferences.class), 1);
		}
		return super.onOptionsItemSelected(item);
	}
 public static InputStream loadResource(String path) throws IOException {
  return assetMgr.open(path);
 }

 public static void display(String... messages) {
  if (messages[0] ==null || messages[0].contains("null")) {
      Log.d(TAG, "message est null"+Thread.currentThread().getStackTrace());
  }
  display( messages[0], messages.length==1);
 }
 static void display(String message, boolean persistent) {
  if(persistent) {
   text+= message+"\n";
   ecran.setText(text);
  } else {
   ecran.setText(text+message+"\n");
  }
 }
}

