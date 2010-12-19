/* DebugOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.OutputStream;

import com.inzyme.text.StringUtils;

public class DebugOutputStream extends OutputStream
{
    private String myName;
    private OutputStream myProxiedOutputStream;
    
    public DebugOutputStream(String _name, OutputStream _proxiedOutputStream) {
	myName = _name;
	myProxiedOutputStream = _proxiedOutputStream;
    }
    
    public void write(int b) throws IOException {
	System.out.println("DebugOutputStream.write (" + myName + "): "
			   + StringUtils.toCharacterString(b));
	myProxiedOutputStream.write(b);
    }
    
    public void close() throws IOException {
	myProxiedOutputStream.close();
    }
}
