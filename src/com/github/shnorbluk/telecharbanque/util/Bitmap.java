package com.github.shnorbluk.telecharbanque.util;

import java.io.InputStream;

public interface Bitmap {
	public int[] getPixels(int xpos, int ypos, int width, int height);
	public InputStream loadResource(String path);
}
