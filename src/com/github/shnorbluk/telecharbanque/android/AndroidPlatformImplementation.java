package com.github.shnorbluk.telecharbanque.android;

import java.io.InputStream;

import com.github.shnorbluk.telecharbanque.util.Bitmap;
import com.github.shnorbluk.telecharbanque.util.PlatformImplementation;

public class AndroidPlatformImplementation implements PlatformImplementation {
	private AssetManager assetMgr;

	@Override
	public String removeHtmlTags(String html) {
		  return android.text.Html.fromHtml( html).toString();
	}

	@Override
	public Bitmap loadBitmap(String file) {
		return BitmapFactory.decodeFile(file);
	}
	
	@Override
	public Bitmap loadBitmap(InputStream stream) {
		return BitmapFactory.decodeStream(stream);
	}

	@Override
	public InputStream loadResource(String path){
		return assetMgr.open(path);
	}
	
	@Override
	public int redPartOfColor(int color) {
		return Color.red(color);
	}
}
