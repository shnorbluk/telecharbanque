package com.github.shnorbluk.telecharbanque.net;

public class UrlReader
{
	private final String url;
	private String begin;
	private String end;
	private boolean online=true;
	public UrlReader(final String url) {
		this.url=url;
	}
	public void from(String begin){
		this.begin=begin;
	}
	public void to (String end) {
		this. end= end;
	}
}
