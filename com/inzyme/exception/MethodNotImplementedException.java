/* MethodNotImplementedException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.exception;

public class MethodNotImplementedException extends ChainedRuntimeException
{
    public MethodNotImplementedException() {
	super("This method is not yet implemented.");
    }
    
    public MethodNotImplementedException(String _message) {
	super(_message);
    }
    
    public MethodNotImplementedException(String _message, Throwable _parent) {
	super(_message, _parent);
    }
}
