package com.github.shnorbluk.telecharbanque.util;

import android.util.*;
import com.github.shnorbluk.telecharbanque.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Utils
{
  private static final String TAG = "Utils";
  public static String toString(Object o) {
   if (o == null) return null;
   if (o instanceof Map) {
    String result="{";
    Map<?,?> map=(Map<?,?>)o;
    boolean first=true;
    for (Map.Entry<?,?> entry:map.entrySet()) {
     if (!first) result += ",";
     first=false;
     result += toString(entry.getKey())+":"+ toString(entry.getValue());
    }
     result += "}";
     return result;
   } else if (o.getClass().isArray()) {
    return Arrays.asList((Object[])o).toString();
   } else if (o instanceof List) {
    String result="{";
    boolean first=true;
    for (Object p:(List<?>)o) {
     result+=toString(p)+",";
    }
    return result+"}";
   } else {
    return o.toString();
   }
  }
  
  private static String substring (String str, int begin, int end) {
	  if (str==null) return null;
	  if (str.length() < end) {
		  end=str.length();
	  }
	  return str.substring(begin, end);
  }
 
 public static String getExtract(StringBuffer complete, String begin, String end) {
  int start= complete.indexOf(begin);
  int finish = complete.indexOf(end, start);
  return complete.substring(start+begin.length(), finish);
 }
 
	public static String getExtract(BufferedReader complete, String begin, String end) throws PatternNotFoundException, IOException {
		complete.reset();
		Scanner scanner = new Scanner(complete);
        scanner.findWithinHorizon(begin,0);
		String regex="(?s).*"+end;
		String extract = scanner.findWithinHorizon(regex,0);
		if (extract == null) {
			throw new PatternNotFoundException(regex);
		}
		return extract;
	}
	
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
	
 private static int indexOfEnd(String str, String regex) throws PatternNotFoundException {
  Matcher matcher=Pattern.compile(regex).matcher(str);
  if (matcher.find()){ 
   return matcher.end(); 
  } else {
   Log.e(TAG, "erreur dans indexOfEnd");
   Log.e(TAG, "regex="+regex);
  for(int last= regex.length()-1; last>0; last-- ) {
   String regex2=regex.substring(0,last);
   if (regex2.endsWith("\\")) continue;
   Matcher m=Pattern.compile(regex2).matcher(str);
   if( m.find() ) {
    break;
   }
  }
  Log.e(TAG, str);
  throw new PatternNotFoundException(regex);
 }
}

	 public static void writeToFile( String str, String filename, boolean append) throws IOException {
      Log.d(TAG," writeToFile("+substring(str, 0, 30)+","+filename);
     	new File(filename).getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(filename, append);
		OutputStreamWriter osw =new OutputStreamWriter(fos,"iso-8859-1");
		BufferedWriter bw=new BufferedWriter(osw); 
		StringReader reader=new StringReader(str);
		char[] cars = new char[255];
      int nb;
		while ((nb=reader.read(cars,0,255))>=0) {
			bw.write (cars,0,nb);  
		}
		bw.close();
		fos.close(); 
	} 
	
	public static String findGroupAfterPattern(String str, String regexStart, String regexGroup) throws PatternNotFoundException {
//		logd(TAG, "findGroupAfterPattern(...,"+regexStart+","+regexGroup);
		Pattern pattern=Pattern.compile( regexGroup );
  Matcher matcher=pattern.matcher(str); 
 int i= indexOfEnd(str,regexStart);
 //Log.d(TAG,"Recherche de "+regexGroup+" à partir de "+i);
 if (matcher.find( i )) {
  try {
   return matcher.group(1);
  } catch (Exception e) {
   Log.e(TAG,"La recherche à échoué", e);
   Log.e(TAG, "Chaine recherchée:"+regexGroup);
   Log.e(TAG, "Chaine trouvée:"+matcher.group(0));
   String error= "La recherche a échoué. " +
     "Chaine recherchée: " +regexGroup+
     ". Chaine trouvée:"+matcher.group(0);
   throw new PatternNotFoundException (error+e);
  }
 }
 return null;
} 
	public static void logd (String TAG, Object... o) {
		String msg="";
		for (Object obj :o) {
			msg+=toString(obj);
		}
		Log.d(TAG, msg + " (" + Thread.currentThread().getStackTrace()[4]+")");
	}
	
}
