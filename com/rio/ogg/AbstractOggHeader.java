/* AbstractOggHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;

public abstract class AbstractOggHeader implements IOggHeader
{
    private OggHeaderID myHeaderID;
    
    public AbstractOggHeader(OggHeaderID _headerID) {
	myHeaderID = _headerID;
    }
    
    public OggHeaderID getHeaderID() {
	return myHeaderID;
    }
}
