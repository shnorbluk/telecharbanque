package com.github.shnorbluk.telecharbanque.util;

import java.io.InputStream;

public class PlatformSpecific implements PlatformImplementation {
	private static PlatformImplementation platformImplementation;
	private static final PlatformSpecific INSTANCE =new PlatformSpecific();
	
	public String removeHtmlTags(String html){
		return platformImplementation.removeHtmlTags(html);
	}
	
	@Override
	public Bitmap loadBitmap(final String file){
		return platformImplementation.loadBitmap(file);
	}

	public static PlatformSpecific getInstance() {
		return INSTANCE;
	}
	
	@Override
	public InputStream loadResource(String path){
		return platformImplementation.loadResource(path);

	}
	
	@Override
	public Bitmap loadBitmap(InputStream stream) {
		return platformImplementation.loadBitmap(stream);
	}
	
	@Override
	public int redPartOfColor(int color) {
		return platformImplementation.redPartOfColor(color);
	}
}
