/* JOrbisException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

public class JOrbisException extends Exception
{
    public JOrbisException() {
	/* empty */
    }
    
    public JOrbisException(String s) {
	super("JOrbis: " + s);
    }
}
