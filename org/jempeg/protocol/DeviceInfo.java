package org.jempeg.protocol;

import java.io.IOException;

import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

/**
 * Encapsulates all the information about this device.
 * 
 * @author Mike Schrag
 */
public class DeviceInfo {
	private CharArray myName;            // internal non-i18n name padded with zeros e.g. "Pearl"
	private CharArray mySoftwareVersion; // interpretation depends on 'name'
	private UINT32 myStorages;           // usually 1

	/**
	 * Constructor for DeviceInfo.
	 */
	public DeviceInfo() {
		myName = new CharArray(32);
		mySoftwareVersion = new CharArray(32);
		myStorages = new UINT32();
	}
	/**
	 * Returns the name of the device.
	 *
	 * @return String the name of the device
	 */
	public String getName() {
		return myName.getNullTerminatedStringValue("ISO-8859-1");
	}

	/**
	 * Returns the version of the software on the device (in a device-dependent
	 * format).
	 *
	 * @return String the version of the software on the device
	 */
	public String getSoftwareVersion() {
		return mySoftwareVersion.getNullTerminatedStringValue("ISO-8859-1");
	}

	/**
	 * Returns the number of "drives" on this device.
	 *
	 * @return int the number of "drives" on this device
	 */
	public int getStorages() {
		return (int)myStorages.getValue();
	}

	public void read(LittleEndianInputStream _is) throws IOException {
		myName.read(_is);
		mySoftwareVersion.read(_is);
		myStorages.read(_is);
	}
}
