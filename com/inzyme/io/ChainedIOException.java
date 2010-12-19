/* ChainedIOException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;

import com.inzyme.exception.IChainedThrowable;

public class ChainedIOException extends IOException
    implements IChainedThrowable
{
    private Throwable myParent;
    
    public ChainedIOException(String s) {
	this(s, null);
    }
    
    public ChainedIOException(String s, Throwable _parent) {
	super(s);
	myParent = _parent;
    }
    
    public Throwable getParent() {
	return myParent;
    }
}
