package org.orman.util.logging;


/**
 * Just an usual logging tool outputs given log to <code>stdout</code> or
 * <code>stderr</code> if given log has at least threshold logging level. Will
 * not log values under given threshold level (default: WARN).
 * 
 * <p>
 * Default logging adapter for the framework.
 * </p>
 * 
 * @author ahmet alp balkan <ahmetalpbalkan at gmail.com>
 * 
 */
import com.github.shnorbluk.telecharbanque.util.*;
import java.util.*;

public class StandardLogger implements ILogger
 {
	LoggingLevel level = LoggingLevel.WARN;
	private long startMsecs;

	public StandardLogger() {
		this.startMsecs = System.currentTimeMillis();
	}

	public void trace(String message, Object... params) {
		try {
		if (isLoggable(LoggingLevel.TRACE))
			log("TRACE", String.format(message, params));
		} catch (Exception e) {
			Utils.logd("shnorblub","Logger orman exception:"+e);
			Utils.logd("shnorblub","Logger orman message=");
			for (int i=0; i<message.length(); i+=200) {
				int end=Math.min(i+200, message.length());
				android.util.Log.d("shnorblub",message.substring(i,end));
			}
			Utils.logd("shnorblub", "params="+Arrays.toString(params));
			throw new RuntimeException(e);
		}
	}

	public void debug(String message, Object... params) {
		if (isLoggable(LoggingLevel.DEBUG))
			log("DEBUG", String.format(message, params));
	}

	public void info(String message, Object... params) {
		if (isLoggable(LoggingLevel.INFO))
			log("INFO", String.format(message, params));
	}

	public void warn(String message, Object... params) {
		if (isLoggable(LoggingLevel.WARN))
			log("WARN", String.format(message, params));
	}

	public void error(String message, Object... params) {
		if (isLoggable(LoggingLevel.ERROR))
			logErr("ERROR", String.format(message, params));
	}

	public void fatal(String message, Object... params) {
		if (isLoggable(LoggingLevel.FATAL))
			logErr("FATAL", String.format(message, params));
	}

	private boolean isLoggable(LoggingLevel logLevel) {
		return (this.level.getValue() <= logLevel.getValue());
	}

	@Override
	public void setLevel(LoggingLevel level) {
		this.level = level == null ? LoggingLevel.WARN : level;
	}

	/* logger methods */

	private void log(String level, Object message) {
		System.out.println(getMsecs() + " " + " [" + level + "] " + message);
	}

	private void logErr(String level, Object message) {
		System.err.println(getMsecs() + " " + " [" + level + "] " + message);
	}

	private long getMsecs() {
		return System.currentTimeMillis() - startMsecs;
	}

}
