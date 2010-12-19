/* InvalidPasswordException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.typeconv.STATUS;

public class InvalidPasswordException extends StatusFailedException
{
    public InvalidPasswordException(STATUS _status) {
	super(_status);
    }
}
