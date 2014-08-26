package com.github.shnorbluk.telecharbanque.net;
import java.util.*;

public class ConnectionException extends Exception
{
	public ConnectionException(Exception e) {
		super(e);
	}
	public ConnectionException(String message) {
		super(message);
	}
}
