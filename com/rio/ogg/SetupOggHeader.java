/* SetupOggHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.util.ReflectionUtils;

public class SetupOggHeader extends AbstractOggHeader
{
    public SetupOggHeader(OggHeaderID _headerID) {
	super(_headerID);
    }
    
    public void read(LittleEndianInputStream _is) throws IOException {
	/* empty */
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
