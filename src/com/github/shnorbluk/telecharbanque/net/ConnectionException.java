package com.github.shnorbluk.telecharbanque.net;
import java.util.*;

public class ConnectionException extends Exception
{
	ConnectionException(Exception e) {
		super(e);
	}
}
