/* UnfilterInputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.sixlegs.image.png;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

final class UnfilterInputStream extends InputStream
{
    private final Chunk_IHDR header;
    private final int rowSize;
    private final int bpp;
    private final InflaterInputStream infstr;
    private final byte[] prev;
    private final byte[] cur;
    private int nextPass;
    private int rowsLeftInPass;
    private int bytesPerRow;
    private int pullSize;
    private int xc;
    private int xp;
    private int xPtr;
    private byte[] _b = new byte[1];
    
    UnfilterInputStream(PngImage img, InputStream s) {
	header = img.data.header;
	infstr = new InflaterInputStream(s, new Inflater(), 8192);
	bpp = Math.max(1, header.depth * header.samples / 8);
	int maxPassWidth = header.interlacer.getMaxPassWidth();
	int maxBytesPerRow = getByteWidth(maxPassWidth);
	rowSize = maxBytesPerRow + bpp;
	prev = new byte[rowSize];
	cur = new byte[rowSize];
	for (int i = 0; i < rowSize; i++)
	    cur[i] = (byte) 0;
    }
    
    private int getByteWidth(int pixels) {
	if (header.samples == 1) {
	    int dppb = 16 / header.depth;
	    int w2 = pixels * 2;
	    return w2 % dppb == 0 ? w2 / dppb : (w2 + dppb - w2 % dppb) / dppb;
	}
	return pixels * header.samples * header.depth / 8;
    }
    
    private int readRow() throws IOException {
	if (rowsLeftInPass == 0) {
	    while (rowsLeftInPass == 0 || bytesPerRow == 0) {
		if (nextPass >= header.interlacer.numPasses())
		    return -1;
		rowsLeftInPass = header.interlacer.getPassHeight(nextPass);
		bytesPerRow
		    = getByteWidth(header.interlacer.getPassWidth(nextPass));
		nextPass++;
	    }
	    pullSize = bytesPerRow + bpp;
	    for (int i = 0; i < pullSize; i++)
		prev[i] = (byte) 0;
	}
	rowsLeftInPass--;
	int filterType = infstr.read();
	if (filterType == -1)
	    return -1;
	if (filterType > 4 || filterType < 0)
	    throw new PngException("Bad filter type: " + filterType);
	int r;
	for (int needed = bytesPerRow; needed > 0; needed -= r) {
	    r = infstr.read(cur, bytesPerRow - needed + bpp, needed);
	    if (r == -1)
		return -1;
	}
	switch (filterType) {
	case 0:
	    break;
	case 1:
	    xc = bpp;
	    xp = 0;
	    while (xc < rowSize) {
		cur[xc] = (byte) (cur[xc] + cur[xp]);
		xc++;
		xp++;
	    }
	    break;
	case 2:
	    xc = bpp;
	    xp = 0;
	    while (xc < rowSize) {
		cur[xc] = (byte) (cur[xc] + prev[xc]);
		xc++;
		xp++;
	    }
	    break;
	case 3:
	    xc = bpp;
	    xp = 0;
	    while (xc < rowSize) {
		cur[xc]
		    = (byte) (cur[xc]
			      + ((0xff & cur[xp]) + (0xff & prev[xc])) / 2);
		xc++;
		xp++;
	    }
	    break;
	case 4:
	    xc = bpp;
	    xp = 0;
	    while (xc < rowSize) {
		cur[xc]
		    = (byte) (cur[xc] + Paeth(cur[xp], prev[xc], prev[xp]));
		xc++;
		xp++;
	    }
	    break;
	default:
	    throw new PngException("unrecognized filter type " + filterType);
	}
	System.arraycopy(cur, 0, prev, 0, rowSize);
	return 0;
    }
    
    private int Paeth(byte L, byte u, byte nw) {
	int a = 0xff & L;
	int b = 0xff & u;
	int c = 0xff & nw;
	int p = a + b - c;
	int pa = p - a;
	if (pa < 0)
	    pa = -pa;
	int pb = p - b;
	if (pb < 0)
	    pb = -pb;
	int pc = p - c;
	if (pc < 0)
	    pc = -pc;
	if (pa <= pb && pa <= pc)
	    return a;
	if (pb <= pc)
	    return b;
	return c;
    }
    
    public int read() throws IOException {
	read(_b, 0, 1);
	return _b[0] & 0xff;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
	int count = 0;
	int L;
	for (/**/; len > 0; len -= L) {
	    if (xPtr == 0) {
		if (readRow() == -1)
		    return count == 0 ? -1 : count;
		xPtr = bpp;
	    }
	    L = Math.min(len, pullSize - xPtr);
	    System.arraycopy(cur, xPtr, b, off, L);
	    count += L;
	    xPtr = (xPtr + L) % pullSize;
	    off += L;
	}
	return count;
    }
    
    public void close() throws IOException {
	super.close();
    }
}
