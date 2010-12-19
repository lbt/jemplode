/* FileInfoEnumeration - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.inzyme.exception.ChainedRuntimeException;
import com.inzyme.io.PaddedInputStream;
import com.inzyme.io.StreamUtils;

public class FileInfoEnumeration implements Enumeration
{
    private PaddedInputStream myPaddedInputStream;
    private Properties myCurrentFileInfo;
    private boolean myShouldClose;
    
    public FileInfoEnumeration(PaddedInputStream _paddedInputStream,
			       boolean _shouldClose) {
	myPaddedInputStream = _paddedInputStream;
	Enumeration namesEnum = PearlStringUtils.NAME_TO_ENCODING.keys();
	while (namesEnum.hasMoreElements()) {
	    String name = (String) namesEnum.nextElement();
	    myPaddedInputStream.addStartsWithToEncodingMap(name + "=",
							   "ISO-8859-1");
	}
	myShouldClose = _shouldClose;
    }
    
    public boolean hasMoreElements() {
	try {
	    if (myCurrentFileInfo == null)
		myCurrentFileInfo = readNextFileInfo();
	    if (myCurrentFileInfo != null)
		return true;
	    return false;
	} catch (IOException e) {
	    throw new ChainedRuntimeException("Unable to read next file info.",
					      e);
	}
    }
    
    public Object nextElement() {
	try {
	    if (myPaddedInputStream == null)
		throw new NoSuchElementException
			  ("There are no more file info's on the stream.");
	    if (myCurrentFileInfo == null)
		myCurrentFileInfo = readNextFileInfo();
	    Properties currentFileInfo = myCurrentFileInfo;
	    myCurrentFileInfo = null;
	    return currentFileInfo;
	} catch (IOException e) {
	    throw new ChainedRuntimeException("Unable to read next file info.",
					      e);
	}
    }
    
    public Properties readNextFileInfo() throws IOException {
	Properties props;
	if (myPaddedInputStream != null) {
	    props = myPaddedInputStream.readProperties();
	    if (props == null) {
		myPaddedInputStream.pad();
		release();
	    }
	} else
	    props = null;
	return props;
    }
    
    public void release() {
	if (myShouldClose)
	    StreamUtils.close(myPaddedInputStream);
	myPaddedInputStream = null;
    }
}
