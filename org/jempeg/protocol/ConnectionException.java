/* ConnectionException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import com.inzyme.exception.ChainedException;

public class ConnectionException extends ChainedException
{
    public ConnectionException(String _message) {
	super(_message);
    }
    
    public ConnectionException(String _message, Throwable _parent) {
	super(_message, _parent);
    }
}
