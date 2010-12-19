package com.inzyme.filesystem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.inzyme.io.MemorySeekableInputStream;
import com.inzyme.io.SeekableInputStream;

public class MemoryImportFile implements IImportFile {
	private String myName;
	private byte[] myBytes;
	private Properties myTags;
  
	public MemoryImportFile(String _name, byte[] _bytes, Properties _tags) {
		myName = _name;
		myBytes = _bytes;
    myTags = _tags;
	}

  public Properties getTags() {
    return myTags;
  }
 
	public Object getID() throws IOException {
		return myName;
	}

	public String getName() {
		return myName;
	}

	public String getLocation() {
		return "memory";
	}

	public long getLength() throws IOException {
		return myBytes.length;
	}

	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(myBytes);
	}

	public SeekableInputStream getSeekableInputStream() throws IOException {
		return new MemorySeekableInputStream(myBytes);
	}

}
