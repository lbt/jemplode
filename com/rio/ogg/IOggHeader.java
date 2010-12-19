/* IOggHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;

interface IOggHeader
{
    public OggHeaderID getHeaderID();
    
    public void read(LittleEndianInputStream littleendianinputstream)
	throws IOException;
}
