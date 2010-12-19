/* AbstractStatusReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.STATUS;
import com.inzyme.util.Debug;

import org.jempeg.protocol.ProtocolException;

public abstract class AbstractStatusReplyPacket extends AbstractReplyPacket
{
    private STATUS myStatus = new STATUS();
    
    public AbstractStatusReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public STATUS getStatus() {
	return myStatus;
    }
    
    public void checkStatus() throws StatusFailedException {
	if (!myStatus.isSucceeded()) {
	    Debug.println(4,
			  ("AbstractStatusReplyPacket.checkStatus: status = "
			   + myStatus));
	    if (myStatus.equals(4, 0, 2, 93))
		throw new NotLoggedInException(myStatus);
	    if (myStatus.equals(4, 0, 2, 91))
		throw new InvalidPasswordException(myStatus);
	    if (myStatus.equals(4, 0, 3, 2))
		throw new FIDNotFoundException(myStatus);
	    throw new StatusFailedException(myStatus);
	}
    }
    
    public void read(LittleEndianInputStream _is) throws PacketException {
	super.read(_is);
	checkStatus();
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	myStatus.read(_is);
    }
}
