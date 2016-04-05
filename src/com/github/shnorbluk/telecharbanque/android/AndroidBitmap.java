package com.github.shnorbluk.telecharbanque.android;

import com.github.shnorbluk.telecharbanque.util.Bitmap;

public class AndroidBitmap implements Bitmap {
	private android.graphics.Bitmap bitmap;
	
	@Override
	public int[] getPixels(int xpos, int ypos, int width, int height) {
		int[] digitPixels = new int[width*height];
		this.bitmap.getPixels(digitPixels, 0, width, xpos, ypos, width, height);
		return digitPixels;
	}
}
