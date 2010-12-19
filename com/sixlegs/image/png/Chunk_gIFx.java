/* Chunk_gIFx - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;

final class Chunk_gIFx extends Chunk implements GifExtension
{
    private String identifier;
    private byte[] auth_code;
    private byte[] data;
    
    Chunk_gIFx() {
	super(1732855416);
    }
    
    protected void readData() throws IOException {
	identifier = in_data.readString(8, "US-ASCII");
	in_data.skip((long) (8 - identifier.length()));
	auth_code = new byte[3];
	in_data.readBytes(auth_code);
	data = new byte[bytesRemaining()];
	in_data.readBytes(data);
	img.data.gifExtensions.addElement(this);
    }
    
    public String getIdentifier() {
	return identifier;
    }
    
    public byte[] getAuthenticationCode() {
	return auth_code;
    }
    
    public byte[] getData() {
	return data;
    }
}
