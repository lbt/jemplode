/* OggPageHeader - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.ogg;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianUtils;
import com.inzyme.typeconv.TypeConversionUtils;
import com.inzyme.util.ReflectionUtils;

class OggPageHeader
{
    private byte[] myHeaderData = new byte[27];
    private int myHeaderLength;
    private int myBodyLength;
    
    public OggPageHeader() {
	/* empty */
    }
    
    public static boolean containsPage(LittleEndianInputStream _is)
	throws IOException {
	if (_is.available() >= 27)
	    return true;
	return false;
    }
    
    public void read(LittleEndianInputStream _is)
	throws IOException, InvalidPageHeaderException {
	_is.read(myHeaderData);
	if (myHeaderData[0] != 79 || myHeaderData[1] != 103
	    || myHeaderData[2] != 103 || myHeaderData[3] != 83)
	    throw new InvalidPageHeaderException("Expected 'OggS' but parsed '"
						 + new String(myHeaderData, 0,
							      4)
						 + "'.");
	int length = TypeConversionUtils.toUnsigned8(myHeaderData[26]);
	byte[] lengths = new byte[length];
	_is.read(lengths);
	myHeaderLength = length + 27;
	for (int i = 0; i < length; i++)
	    myBodyLength += TypeConversionUtils.toUnsigned8(lengths[i]);
    }
    
    public long getSerialNumber() {
	long serialNumber = LittleEndianUtils.toUnsigned32(myHeaderData[14],
							   myHeaderData[15],
							   myHeaderData[16],
							   myHeaderData[17]);
	return serialNumber;
    }
    
    public long getPageNumber() {
	long pageNumber = LittleEndianUtils.toUnsigned32(myHeaderData[18],
							 myHeaderData[19],
							 myHeaderData[20],
							 myHeaderData[21]);
	return pageNumber;
    }
    
    public long getGranulePosition() {
	long granulePos
	    = LittleEndianUtils.toUnsigned64(myHeaderData[6], myHeaderData[7],
					     myHeaderData[8], myHeaderData[9],
					     myHeaderData[10],
					     myHeaderData[11],
					     myHeaderData[12],
					     myHeaderData[13]);
	return granulePos;
    }
    
    public int getHeaderLength() {
	return myHeaderLength;
    }
    
    public int getBodyLength() {
	return myBodyLength;
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
