/* NotLoggedInException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.typeconv.STATUS;

public class NotLoggedInException extends StatusFailedException
{
    public NotLoggedInException(STATUS _status) {
	super(_status);
    }
}
