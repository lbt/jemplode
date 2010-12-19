/* ChangeFileInfoRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.util.Properties;

import com.inzyme.io.PaddedOutputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.rio.protocol2.PearlStringUtils;

public class ChangeFileInfoRequestPacket extends AbstractRequestPacket
{
    private UINT32 myFID;
    private Properties myFileInfo;
    
    public ChangeFileInfoRequestPacket(UINT32 _fid, Properties _fileInfo) {
	super(new PacketHeader(15));
	myFID = _fid;
	myFileInfo = _fileInfo;
    }
    
    public UINT32 getFID() {
	return myFID;
    }
    
    public Properties getFileInfo() {
	return myFileInfo;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myFID.write(_os);
	PaddedOutputStream pos = new PaddedOutputStream(_os, 4);
	pos.writeProperties(myFileInfo, PearlStringUtils.NAME_TO_ENCODING,
			    true);
	pos.pad();
    }
}
