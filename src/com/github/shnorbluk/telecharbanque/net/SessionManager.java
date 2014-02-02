package com.github.shnorbluk.telecharbanque.net;

import java.io.*;

public interface SessionManager
{
 void connect() throws Exception ;
 int getSessionInformation();
	boolean isConnected();
//	void setConnected(boolean connected);
}


