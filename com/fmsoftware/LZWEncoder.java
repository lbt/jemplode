/* LZWEncoder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.fmsoftware;
import java.io.IOException;
import java.io.OutputStream;

class LZWEncoder
{
    private static final int EOF = -1;
    private int imgW;
    private int imgH;
    private byte[] pixAry;
    private int initCodeSize;
    private int remaining;
    private int curPixel;
    static final int BITS = 12;
    static final int HSIZE = 5003;
    int n_bits;
    int maxbits = 12;
    int maxcode;
    int maxmaxcode = 4096;
    int[] htab = new int[5003];
    int[] codetab = new int[5003];
    int hsize = 5003;
    int free_ent = 0;
    boolean clear_flg = false;
    int g_init_bits;
    int ClearCode;
    int EOFCode;
    int cur_accum = 0;
    int cur_bits = 0;
    int[] masks = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095,
		    8191, 16383, 32767, 65535 };
    int a_count;
    byte[] accum = new byte[256];
    
    LZWEncoder(int width, int height, byte[] pixels, int color_depth) {
	imgW = width;
	imgH = height;
	pixAry = pixels;
	initCodeSize = Math.max(2, color_depth);
    }
    
    void char_out(byte c, OutputStream outs) throws IOException {
	accum[a_count++] = c;
	if (a_count >= 254)
	    flush_char(outs);
    }
    
    void cl_block(OutputStream outs) throws IOException {
	cl_hash(hsize);
	free_ent = ClearCode + 2;
	clear_flg = true;
	output(ClearCode, outs);
    }
    
    void cl_hash(int _hsize) {
	for (int i = 0; i < _hsize; i++)
	    htab[i] = -1;
    }
    
    void compress(int init_bits, OutputStream outs) throws IOException {
	g_init_bits = init_bits;
	clear_flg = false;
	n_bits = g_init_bits;
	maxcode = MAXCODE(n_bits);
	ClearCode = 1 << init_bits - 1;
	EOFCode = ClearCode + 1;
	free_ent = ClearCode + 2;
	a_count = 0;
	int ent = nextPixel();
	int hshift = 0;
	for (int fcode = hsize; fcode < 65536; fcode *= 2)
	    hshift++;
	hshift = 8 - hshift;
	int hsize_reg = hsize;
	cl_hash(hsize_reg);
	output(ClearCode, outs);
	int c;
    while_16_:
	while ((c = nextPixel()) != -1) {
	    int fcode = (c << maxbits) + ent;
	    int i = c << hshift ^ ent;
	    if (htab[i] == fcode)
		ent = codetab[i];
	    else {
		if (htab[i] >= 0) {
		    int disp = hsize_reg - i;
		    if (i == 0)
			disp = 1;
		    do {
			if ((i -= disp) < 0)
			    i += hsize_reg;
			if (htab[i] == fcode) {
			    ent = codetab[i];
			    continue while_16_;
			}
		    } while (htab[i] >= 0);
		}
		output(ent, outs);
		ent = c;
		if (free_ent < maxmaxcode) {
		    codetab[i] = free_ent++;
		    htab[i] = fcode;
		} else
		    cl_block(outs);
	    }
	}
	output(ent, outs);
	output(EOFCode, outs);
    }
    
    void encode(OutputStream os) throws IOException {
	os.write(initCodeSize);
	remaining = imgW * imgH;
	curPixel = 0;
	compress(initCodeSize + 1, os);
	os.write(0);
    }
    
    void flush_char(OutputStream outs) throws IOException {
	if (a_count > 0) {
	    outs.write(a_count);
	    outs.write(accum, 0, a_count);
	    a_count = 0;
	}
    }
    
    final int MAXCODE(int _n_bits) {
	return (1 << _n_bits) - 1;
    }
    
    private int nextPixel() {
	if (remaining == 0)
	    return -1;
	remaining--;
	byte pix = pixAry[curPixel++];
	return pix & 0xff;
    }
    
    void output(int code, OutputStream outs) throws IOException {
	cur_accum &= masks[cur_bits];
	if (cur_bits > 0)
	    cur_accum |= code << cur_bits;
	else
	    cur_accum = code;
	for (cur_bits += n_bits; cur_bits >= 8; cur_bits -= 8) {
	    char_out((byte) (cur_accum & 0xff), outs);
	    cur_accum >>= 8;
	}
	if (free_ent > maxcode || clear_flg) {
	    if (clear_flg) {
		maxcode = MAXCODE(n_bits = g_init_bits);
		clear_flg = false;
	    } else {
		n_bits++;
		if (n_bits == maxbits)
		    maxcode = maxmaxcode;
		else
		    maxcode = MAXCODE(n_bits);
	    }
	}
	if (code == EOFCode) {
	    for (/**/; cur_bits > 0; cur_bits -= 8) {
		char_out((byte) (cur_accum & 0xff), outs);
		cur_accum >>= 8;
	    }
	    flush_char(outs);
	}
    }
}
