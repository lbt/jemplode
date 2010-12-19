/* PngEncoder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.keypoint;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PngEncoder
{
    public static final boolean ENCODE_ALPHA = true;
    public static final boolean NO_ALPHA = false;
    public static final int FILTER_NONE = 0;
    public static final int FILTER_SUB = 1;
    public static final int FILTER_UP = 2;
    public static final int FILTER_LAST = 2;
    protected byte[] pngBytes;
    protected byte[] priorRow;
    protected byte[] leftBytes;
    protected Image image;
    protected int width;
    protected int height;
    protected int bytePos;
    protected int maxPos;
    protected int hdrPos;
    protected int dataPos;
    protected int endPos;
    protected CRC32 crc = new CRC32();
    protected long crcValue;
    protected boolean encodeAlpha;
    protected int filter;
    protected int bytesPerPixel;
    protected int compressionLevel;
    
    public PngEncoder() {
	this(null, false, 0, 0);
    }
    
    public PngEncoder(Image image) {
	this(image, false, 0, 0);
    }
    
    public PngEncoder(Image image, boolean encodeAlpha) {
	this(image, encodeAlpha, 0, 0);
    }
    
    public PngEncoder(Image image, boolean encodeAlpha, int whichFilter) {
	this(image, encodeAlpha, whichFilter, 0);
    }
    
    public PngEncoder(Image image, boolean encodeAlpha, int whichFilter,
		      int compLevel) {
	this.image = image;
	this.encodeAlpha = encodeAlpha;
	setFilter(whichFilter);
	if (compLevel >= 0 && compLevel <= 9)
	    compressionLevel = compLevel;
    }
    
    public void setImage(Image image) {
	this.image = image;
	pngBytes = null;
    }
    
    public byte[] pngEncode(boolean encodeAlpha) {
	byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
	if (image == null)
	    return null;
	width = image.getWidth(null);
	height = image.getHeight(null);
	pngBytes = new byte[(width + 1) * height * 3 + 200];
	maxPos = 0;
	bytePos = writeBytes(pngIdBytes, 0);
	hdrPos = bytePos;
	writeHeader();
	dataPos = bytePos;
	if (writeImageData()) {
	    writeEnd();
	    pngBytes = resizeByteArray(pngBytes, maxPos);
	} else
	    pngBytes = null;
	return pngBytes;
    }
    
    public byte[] pngEncode() {
	return pngEncode(encodeAlpha);
    }
    
    public void setEncodeAlpha(boolean encodeAlpha) {
	this.encodeAlpha = encodeAlpha;
    }
    
    public boolean getEncodeAlpha() {
	return encodeAlpha;
    }
    
    public void setFilter(int whichFilter) {
	filter = 0;
	if (whichFilter <= 2)
	    filter = whichFilter;
    }
    
    public int getFilter() {
	return filter;
    }
    
    public void setCompressionLevel(int level) {
	if (level >= 0 && level <= 9)
	    compressionLevel = level;
    }
    
    public int getCompressionLevel() {
	return compressionLevel;
    }
    
    protected byte[] resizeByteArray(byte[] array, int newLength) {
	byte[] newArray = new byte[newLength];
	int oldLength = array.length;
	System.arraycopy(array, 0, newArray, 0,
			 Math.min(oldLength, newLength));
	return newArray;
    }
    
    protected int writeBytes(byte[] data, int offset) {
	maxPos = Math.max(maxPos, offset + data.length);
	if (data.length + offset > pngBytes.length)
	    pngBytes
		= resizeByteArray(pngBytes,
				  pngBytes.length + Math.max(1000,
							     data.length));
	System.arraycopy(data, 0, pngBytes, offset, data.length);
	return offset + data.length;
    }
    
    protected int writeBytes(byte[] data, int nBytes, int offset) {
	maxPos = Math.max(maxPos, offset + nBytes);
	if (nBytes + offset > pngBytes.length)
	    pngBytes
		= resizeByteArray(pngBytes,
				  pngBytes.length + Math.max(1000, nBytes));
	System.arraycopy(data, 0, pngBytes, offset, nBytes);
	return offset + nBytes;
    }
    
    protected int writeInt2(int n, int offset) {
	byte[] temp = { (byte) (n >> 8 & 0xff), (byte) (n & 0xff) };
	return writeBytes(temp, offset);
    }
    
    protected int writeInt4(int n, int offset) {
	byte[] temp = { (byte) (n >> 24 & 0xff), (byte) (n >> 16 & 0xff),
			(byte) (n >> 8 & 0xff), (byte) (n & 0xff) };
	return writeBytes(temp, offset);
    }
    
    protected int writeByte(int b, int offset) {
	byte[] temp = { (byte) b };
	return writeBytes(temp, offset);
    }
    
    protected int writeString(String s, int offset) {
	return writeBytes(s.getBytes(), offset);
    }
    
    protected void writeHeader() {
	int startPos = bytePos = writeInt4(13, bytePos);
	bytePos = writeString("IHDR", bytePos);
	width = image.getWidth(null);
	height = image.getHeight(null);
	bytePos = writeInt4(width, bytePos);
	bytePos = writeInt4(height, bytePos);
	bytePos = writeByte(8, bytePos);
	bytePos = writeByte(encodeAlpha ? 6 : 2, bytePos);
	bytePos = writeByte(0, bytePos);
	bytePos = writeByte(0, bytePos);
	bytePos = writeByte(0, bytePos);
	crc.reset();
	crc.update(pngBytes, startPos, bytePos - startPos);
	crcValue = crc.getValue();
	bytePos = writeInt4((int) crcValue, bytePos);
    }
    
    protected void filterSub(byte[] pixels, int startPos, int width) {
	int offset = bytesPerPixel;
	int actualStart = startPos + offset;
	int nBytes = width * bytesPerPixel;
	int leftInsert = offset;
	int leftExtract = 0;
	for (int i = actualStart; i < startPos + nBytes; i++) {
	    leftBytes[leftInsert] = pixels[i];
	    pixels[i] = (byte) ((pixels[i] - leftBytes[leftExtract]) % 256);
	    leftInsert = (leftInsert + 1) % 15;
	    leftExtract = (leftExtract + 1) % 15;
	}
    }
    
    protected void filterUp(byte[] pixels, int startPos, int width) {
	int nBytes = width * bytesPerPixel;
	for (int i = 0; i < nBytes; i++) {
	    byte current_byte = pixels[startPos + i];
	    pixels[startPos + i]
		= (byte) ((pixels[startPos + i] - priorRow[i]) % 256);
	    priorRow[i] = current_byte;
	}
    }
    
    protected boolean writeImageData() {
	int rowsLeft = height;
	int startRow = 0;
	bytesPerPixel = encodeAlpha ? 4 : 3;
	Deflater scrunch = new Deflater(compressionLevel);
	ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);
	DeflaterOutputStream compBytes
	    = new DeflaterOutputStream(outBytes, scrunch);
	try {
	    int nRows;
	    for (/**/; rowsLeft > 0; rowsLeft -= nRows) {
		nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
				 rowsLeft);
		int[] pixels = new int[width * nRows];
		PixelGrabber pg = new PixelGrabber(image, 0, startRow, width,
						   nRows, pixels, 0, width);
		try {
		    pg.grabPixels();
		} catch (Exception e) {
		    System.err.println("interrupted waiting for pixels!");
		    return false;
		}
		if ((pg.getStatus() & 0x80) != 0) {
		    System.err.println("image fetch aborted or errored");
		    return false;
		}
		byte[] scanLines
		    = new byte[width * nRows * bytesPerPixel + nRows];
		if (filter == 1)
		    leftBytes = new byte[16];
		if (filter == 2)
		    priorRow = new byte[width * bytesPerPixel];
		int scanPos = 0;
		int startPos = 1;
		for (int i = 0; i < width * nRows; i++) {
		    if (i % width == 0) {
			scanLines[scanPos++] = (byte) filter;
			startPos = scanPos;
		    }
		    scanLines[scanPos++] = (byte) (pixels[i] >> 16 & 0xff);
		    scanLines[scanPos++] = (byte) (pixels[i] >> 8 & 0xff);
		    scanLines[scanPos++] = (byte) (pixels[i] & 0xff);
		    if (encodeAlpha)
			scanLines[scanPos++] = (byte) (pixels[i] >> 24 & 0xff);
		    if (i % width == width - 1 && filter != 0) {
			if (filter == 1)
			    filterSub(scanLines, startPos, width);
			if (filter == 2)
			    filterUp(scanLines, startPos, width);
		    }
		}
		compBytes.write(scanLines, 0, scanPos);
		startRow += nRows;
	    }
	    compBytes.close();
	    byte[] compressedLines = outBytes.toByteArray();
	    int nCompressed = compressedLines.length;
	    crc.reset();
	    bytePos = writeInt4(nCompressed, bytePos);
	    bytePos = writeString("IDAT", bytePos);
	    crc.update("IDAT".getBytes());
	    bytePos = writeBytes(compressedLines, nCompressed, bytePos);
	    crc.update(compressedLines, 0, nCompressed);
	    crcValue = crc.getValue();
	    bytePos = writeInt4((int) crcValue, bytePos);
	    scrunch.finish();
	    return true;
	} catch (IOException e) {
	    System.err.println(e.toString());
	    return false;
	}
    }
    
    protected void writeEnd() {
	bytePos = writeInt4(0, bytePos);
	bytePos = writeString("IEND", bytePos);
	crc.reset();
	crc.update("IEND".getBytes());
	crcValue = crc.getValue();
	bytePos = writeInt4((int) crcValue, bytePos);
    }
}
