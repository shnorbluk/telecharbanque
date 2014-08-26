package com.github.shnorbluk.telecharbanque.boursorama.moneycenter;

import android.text.*;
import android.webkit.*;
import com.github.shnorbluk.telecharbanque.util.*;
import java.text.*;
import java.util.*;

public class DisplayOperationListTask extends AsynchTask<String[]>
{
	private final WebView webView;

	public DisplayOperationListTask(WebView webView)
	{
		this.webView = webView;
	}
	protected String[] doInBackground(String[] p1)
	{
		final McOperationInDb ope = new McOperationInDb();
		ope.setDate(new Date());
		ope.setAmount(99.99f);
		ope.setLibelle("Opération bidon");
		ope.setCategoryLabel("categ");
		ope.setMemo("Bla bla bla");
		final String url = "javascript:addRow({date:'"
			+DateFormat.getDateInstance(DateFormat.SHORT).format(ope.getDate())
			+"',libelle:'"+TextUtils.htmlEncode(ope.getLibelle())
			+"',amount:"+ope.getAmount()
			+",categ:'"+ope.getCategoryLabel()
			+"',memo:'"+ope.getMemo()
			+"'});";
		webView.loadUrl(url);
		logd(url);
		return null;
	}
	private void logd(String m){
		Utils.logd("DisplayOperationListTask",m);
	}
	
}
