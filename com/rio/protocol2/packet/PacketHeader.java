/* PacketHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.text.ResourceBundleKey;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.UINT32;
import com.inzyme.util.ReflectionUtils;

public class PacketHeader
{
    private static final UINT32 MAGIC_NUMBER = new UINT32(2378525010L);
    private UINT32 myMagicNumber = new UINT32(MAGIC_NUMBER.getValue());
    private UINT32 myType;
    
    public PacketHeader() {
	this(-1);
    }
    
    public PacketHeader(int _type) {
	myType = new UINT32((long) _type);
    }
    
    public UINT32 getMagicNumber() {
	return myMagicNumber;
    }
    
    public UINT32 getType() {
	return myType;
    }
    
    public void write(LittleEndianOutputStream _os) throws PacketException {
	try {
	    myMagicNumber.write(_os);
	    myType.write(_os);
	} catch (IOException e) {
	    throw new PacketException
		      ((new ResourceBundleKey
			("errors", "protocol.packet.requestHeaderFailed")),
		       e);
	}
    }
    
    public void read(LittleEndianInputStream _is) throws PacketException {
	try {
	    myMagicNumber.read(_is);
	    if (!myMagicNumber.equals(MAGIC_NUMBER))
		throw new PacketException
			  (new ResourceBundleKey
			   ("errors", "protocol.packet.magicNumberMismatch"));
	    myType.read(_is);
	} catch (IOException e) {
	    throw new PacketException((new ResourceBundleKey
				       ("errors",
					"protocol.packet.replyHeaderFailed")),
				      e);
	}
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
