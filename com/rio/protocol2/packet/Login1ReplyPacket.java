/* Login1ReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.CharArray;
import com.inzyme.typeconv.LittleEndianInputStream;

public class Login1ReplyPacket extends AbstractReplyPacket
{
    private CharArray mySalt = new CharArray(16);
    
    public Login1ReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public CharArray getSalt() {
	return mySalt;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException {
	mySalt.read(_is);
    }
}
