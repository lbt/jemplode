package org.jempeg.protocol;

import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.typeconv.UINT64;
import com.inzyme.util.ReflectionUtils;

/**
 * Contains the information about a device data store.
 *
 * @author Mike Schrag
 */
public class StorageInfo {
	private UINT32 myFiles;   // number of files
	private UINT64 mySize;    // bytes
	private UINT64 myFree;
	private UINT32 myHighestFID;
	
	/**
	 * Constructor for StorageInfo.
	 */
	public StorageInfo() {
		this(0, 0, 0, 0);
	}
	
	/**
	 * Constructor for StorageInfo
	 * 
	 * @param _files the number of files on the storage
	 * @param _size the size of the storage
	 * @param _free the bytes free on the storage
	 * @param _highestFID the highest FID on the device
	 */
	public StorageInfo(long _files, long _size, long _free, long _highestFID) {
		myFiles = new UINT32(_files);
		mySize = new UINT64(_size);
		myFree = new UINT64(_free);
		myHighestFID = new UINT32(_highestFID);
	}

	/**
	 * Returns the number of files on this store.
	 *
	 * @return long the number of files on this store
	 */
	public long getFiles() {
		return myFiles.getValue();
	}

	/**
	 * Returns the total size of this store.
	 *
	 * @return long the total size of this store
	 */
	public long getSize() {
		return mySize.getValue();
	}

	/**
	 * Returns the free space on this store.
	 *
	 * @return long the free space on this store
	 */
	public long getFree() {
		return myFree.getValue();
	}

	/**
	 * Returns the highest FID that is in use on this store.
	 *
	 * @return long the highest FID that is in use on this store
	 */
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
