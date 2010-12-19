/* Flags_3_0_NoSync - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.music.tag.id3v2;
import java.io.OutputStream;
import java.io.Serializable;

class Flags_3_0_NoSync extends Flags_3_0 implements Serializable, Cloneable
{
    protected Flags_3_0_NoSync() {
	this((byte) 0, (short) 0);
    }
    
    protected Flags_3_0_NoSync(byte flags) {
	this(flags, (short) 0);
    }
    
    protected Flags_3_0_NoSync(byte flags, short extFlags) {
	super(flags, extFlags);
    }
    
    protected OutputStream processUnsynchronization(OutputStream out) {
	return out;
    }
}
