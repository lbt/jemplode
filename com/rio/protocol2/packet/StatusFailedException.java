/* StatusFailedException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.text.ResourceBundleKey;
import com.inzyme.typeconv.STATUS;

public class StatusFailedException extends PacketException
{
    private STATUS myStatus;
    
    public StatusFailedException(STATUS _status) {
	super(new ResourceBundleKey("errors", _status.getResourceBundleKey()));
	myStatus = _status;
    }
    
    public STATUS getStatus() {
	return myStatus;
    }
}
