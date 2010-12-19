package org.jempeg.protocol;

import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.ReflectionUtils;

/**
 * Represents a protocol version.
 * 
 * @author Mike Schrag
 */
public class ProtocolVersion {
	/**
	 * The current version of the protocol.
	 */
	public static final ProtocolVersion CURRENT_VERSION = new ProtocolVersion(2, 0);

	private UINT32 myMajorVersion;
	private UINT32 myMinorVersion;
	
	/**
	 * Constructor for ProtocolVersion.
	 * 
	 * @param _majorVersion the protocol major version
	 * @param _minorVersion the protocol minor version
	 */
	public ProtocolVersion(int _majorVersion, int _minorVersion) {
		myMajorVersion = new UINT32(_majorVersion);
		myMinorVersion = new UINT32(_minorVersion);
	}
	
	/**
	 * Constructs an empty ProtocolVersion
	 */
	public ProtocolVersion() {
		this(0, 0);
	}
	
	/**
	 * Returns the major version of this protocol.
	 * 
	 * @return int the major version of this protocol
	 */
	public int getMajorVersion() {
		return (int)myMajorVersion.getValue();
	}
	
	/**
	 * Returns the minor version of this protocol.
	 *
	 * @return int the minor version of this protocol
	 */
	public int getMinorVersion() {
		return (int)myMinorVersion.getValue();
	}
	
	/**
	 * Reads this version from the given stream.
	 * 
	 * @param _eis the InputStream to read from
	 * @throws IOException if the version cannot be read 
	 */
	public void read(LittleEndianInputStream _eis) throws IOException {
		myMajorVersion.read(_eis);
		myMinorVersion.read(_eis);
	}
	
	/**
	 * Writes this version to the given stream.
	 * 
	 * @param _eos the OutputStream to write to
	 * @throws IOException if the version cannot be written
	 */
	public void write(LittleEndianOutputStream _eos) throws IOException {
		myMajorVersion.write(_eos);
		myMinorVersion.write(_eos); 
	}
	
	/**
	 * Returns whether or not this version is compatible with the given version.
	 * 
	 * @param _protocolVersion the version to check compatibility with
	 * @return boolean whether or not this version is compatible with the given version
	 */
	public boolean isCompatibleWith(ProtocolVersion _protocolVersion) {
		boolean compatible = (_protocolVersion.getMajorVersion() == getMajorVersion() && _protocolVersion.getMinorVersion() == getMinorVersion());
		return compatible;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
