package com.github.shnorbluk.telecharbanque.net;

public class Token
{
	private boolean connected = false;
	
	public Token(boolean connected) {
		this.connected = connected;
	}

	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}

	public boolean isConnected()
	{
		return connected;
	}
}
