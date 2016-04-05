package com.github.shnorbluk.telecharbanque.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class FileUtils {
    @Deprecated
	public static StringBuffer readFile( String filename, String enc) throws FileNotFoundException, IOException {
		FileInputStream fis=new FileInputStream(filename); 
		InputStreamReader fr=new InputStreamReader(fis, enc);
		BufferedReader br=new BufferedReader(fr);
		char[] buff=new char[255];
		StringWriter writer=new StringWriter();
		for (int i=br.read(buff,0,255); i>=0; i=br.read(buff,0,255) ) {
			writer.write (buff,0,i);  
		}
		fis.close(); 
		return writer.getBuffer();
	}
	
	public static BufferedReader getBufferedReaderFromFile ( String filename, String enc) throws FileNotFoundException, IOException {
		FileInputStream fis=new FileInputStream(filename); 
		InputStreamReader fr=new InputStreamReader(fis, enc);
		BufferedReader br=new BufferedReader(fr);
		br.mark(Integer.MAX_VALUE);
		return br;
	}
	
}
