package com.inzyme.io;

import java.io.IOException;
import java.io.InputStream;

import com.inzyme.text.StringUtils;

public class DebugInputStream extends InputStream {
	private String myName;
	private InputStream myProxiedInputStream;
	
	public DebugInputStream(String _name, InputStream _proxiedInputStream) {
		myName = _name;
		myProxiedInputStream = _proxiedInputStream;
	}
	
	public int read() throws IOException {
		int b = myProxiedInputStream.read();
		System.out.println("DebugInputStream.read (" + myName + "): " + StringUtils.toCharacterString(b));
		return b;
	}
	
	public int available() throws IOException {
		return myProxiedInputStream.available();
	}
	
	public void close() throws IOException {
		myProxiedInputStream.close();
	}
}
