package com.github.shnorbluk.telecharbanque.util;

import java.io.InputStream;

public interface PlatformImplementation {

	String removeHtmlTags(String html);

	Bitmap loadBitmap(String file);
	public InputStream loadResource(String path);
	public Bitmap loadBitmap(InputStream stream);
	public int redPartOfColor(int color);
}
