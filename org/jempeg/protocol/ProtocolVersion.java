/* ProtocolVersion - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.ReflectionUtils;

public class ProtocolVersion
{
    public static final ProtocolVersion CURRENT_VERSION
	= new ProtocolVersion(2, 0);
    private UINT32 myMajorVersion;
    private UINT32 myMinorVersion;
    
    public ProtocolVersion(int _majorVersion, int _minorVersion) {
	myMajorVersion = new UINT32((long) _majorVersion);
	myMinorVersion = new UINT32((long) _minorVersion);
    }
    
    public ProtocolVersion() {
	this(0, 0);
    }
    
    public int getMajorVersion() {
	return (int) myMajorVersion.getValue();
    }
    
    public int getMinorVersion() {
	return (int) myMinorVersion.getValue();
    }
    
    public void read(LittleEndianInputStream _eis) throws IOException {
	myMajorVersion.read(_eis);
	myMinorVersion.read(_eis);
    }
    
    public void write(LittleEndianOutputStream _eos) throws IOException {
	myMajorVersion.write(_eos);
	myMinorVersion.write(_eos);
    }
    
    public boolean isCompatibleWith(ProtocolVersion _protocolVersion) {
	boolean compatible
	    = (_protocolVersion.getMajorVersion() == getMajorVersion()
	       && _protocolVersion.getMinorVersion() == getMinorVersion());
	return compatible;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
