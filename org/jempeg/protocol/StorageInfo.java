/* StorageInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;
import com.inzyme.util.ReflectionUtils;

public class StorageInfo
{
    private UINT32 myFiles;
    private UINT64 mySize;
    private UINT64 myFree;
    private UINT32 myHighestFID;
    
    public StorageInfo() {
	this(0L, 0L, 0L, 0L);
    }
    
    public StorageInfo(long _files, long _size, long _free, long _highestFID) {
	myFiles = new UINT32(_files);
	mySize = new UINT64(_size);
	myFree = new UINT64(_free);
	myHighestFID = new UINT32(_highestFID);
    }
    
    public long getFiles() {
	return myFiles.getValue();
    }
    
    public long getSize() {
	return mySize.getValue();
    }
    
    public long getFree() {
	return myFree.getValue();
    }
    
    public long getHighestFID() {
	return myHighestFID.getValue();
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	myFiles.read(_is);
	mySize.read(_is);
	myFree.read(_is);
	myHighestFID.read(_is);
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
