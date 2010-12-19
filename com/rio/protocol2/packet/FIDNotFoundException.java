/* FIDNotFoundException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.typeconv.STATUS;

public class FIDNotFoundException extends StatusFailedException
{
    public FIDNotFoundException(STATUS _status) {
	super(_status);
    }
}
