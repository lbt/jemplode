/* IPrimitive - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import java.io.IOException;

public interface IPrimitive
{
    public void updateCRC(CRC16 crc16);
    
    public void read(LittleEndianInputStream littleendianinputstream)
	throws IOException;
    
    public void write(LittleEndianOutputStream littleendianoutputstream)
	throws IOException;
    
    public int getLength();
}
