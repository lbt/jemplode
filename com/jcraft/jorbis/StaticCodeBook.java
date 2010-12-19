/* StaticCodeBook - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

class StaticCodeBook
{
    int dim;
    int entries;
    int[] lengthlist;
    int maptype;
    int q_min;
    int q_delta;
    int q_quant;
    int q_sequencep;
    int[] quantlist;
    EncodeAuxNearestMatch nearest_tree;
    EncodeAuxThreshMatch thresh_tree;
    static final int VQ_FEXP = 10;
    static final int VQ_FMAN = 21;
    static final int VQ_FEXP_BIAS = 768;
    
    StaticCodeBook() {
	/* empty */
    }
    
    StaticCodeBook(int dim, int entries, int[] lengthlist, int maptype,
		   int q_min, int q_delta, int q_quant, int q_sequencep,
		   int[] quantlist, Object nearest_tree, Object thresh_tree) {
	this();
	this.dim = dim;
	this.entries = entries;
	this.lengthlist = lengthlist;
	this.maptype = maptype;
	this.q_min = q_min;
	this.q_delta = q_delta;
	this.q_quant = q_quant;
	this.q_sequencep = q_sequencep;
	this.quantlist = quantlist;
    }
    
    int pack(Buffer opb) {
	boolean ordered = false;
	opb.write(5653314, 24);
	opb.write(dim, 16);
	opb.write(entries, 24);
	int i;
	for (i = 1; i < entries; i++) {
	    if (lengthlist[i] < lengthlist[i - 1])
		break;
	}
	if (i == entries)
	    ordered = true;
	if (ordered) {
	    int count = 0;
	    opb.write(1, 1);
	    opb.write(lengthlist[0] - 1, 5);
	    for (i = 1; i < entries; i++) {
		int _this = lengthlist[i];
		int _last = lengthlist[i - 1];
		if (_this > _last) {
		    for (int j = _last; j < _this; j++) {
			opb.write(i - count, ilog(entries - count));
			count = i;
		    }
		}
	    }
	    opb.write(i - count, ilog(entries - count));
	} else {
	    opb.write(0, 1);
	    for (i = 0; i < entries; i++) {
		if (lengthlist[i] == 0)
		    break;
	    }
	    if (i == entries) {
		opb.write(0, 1);
		for (i = 0; i < entries; i++)
		    opb.write(lengthlist[i] - 1, 5);
	    } else {
		opb.write(1, 1);
		for (i = 0; i < entries; i++) {
		    if (lengthlist[i] == 0)
			opb.write(0, 1);
		    else {
			opb.write(1, 1);
			opb.write(lengthlist[i] - 1, 5);
		    }
		}
	    }
	}
	opb.write(maptype, 4);
	switch (maptype) {
	case 0:
	    break;
	case 1:
	case 2: {
	    if (quantlist == null)
		return -1;
	    opb.write(q_min, 32);
	    opb.write(q_delta, 32);
	    opb.write(q_quant - 1, 4);
	    opb.write(q_sequencep, 1);
	    int quantvals = 0;
	    switch (maptype) {
	    case 1:
		quantvals = maptype1_quantvals();
		break;
	    case 2:
		quantvals = entries * dim;
		break;
	    }
	    for (i = 0; i < quantvals; i++)
		opb.write(Math.abs(quantlist[i]), q_quant);
	    break;
	}
	default:
	    return -1;
	}
	return 0;
    }
    
    int unpack(Buffer opb) {
	if (opb.read(24) != 5653314) {
	    clear();
	    return -1;
	}
	dim = opb.read(16);
	entries = opb.read(24);
	if (entries == -1) {
	    clear();
	    return -1;
	}
	switch (opb.read(1)) {
	case 0:
	    lengthlist = new int[entries];
	    if (opb.read(1) != 0) {
		for (int i = 0; i < entries; i++) {
		    if (opb.read(1) != 0) {
			int num = opb.read(5);
			if (num == -1) {
			    clear();
			    return -1;
			}
			lengthlist[i] = num + 1;
		    } else
			lengthlist[i] = 0;
		}
	    } else {
		for (int i = 0; i < entries; i++) {
		    int num = opb.read(5);
		    if (num == -1) {
			clear();
			return -1;
		    }
		    lengthlist[i] = num + 1;
		}
	    }
	    break;
	case 1: {
	    int length = opb.read(5) + 1;
	    lengthlist = new int[entries];
	    int i = 0;
	    while (i < entries) {
		int num = opb.read(ilog(entries - i));
		if (num == -1) {
		    clear();
		    return -1;
		}
		int j = 0;
		while (j < num) {
		    lengthlist[i] = length;
		    j++;
		    i++;
		}
		length++;
	    }
	    break;
	}
	default:
	    return -1;
	}
	switch (maptype = opb.read(4)) {
	case 0:
	    break;
	case 1:
	case 2: {
	    q_min = opb.read(32);
	    q_delta = opb.read(32);
	    q_quant = opb.read(4) + 1;
	    q_sequencep = opb.read(1);
	    int quantvals = 0;
	    switch (maptype) {
	    case 1:
		quantvals = maptype1_quantvals();
		break;
	    case 2:
		quantvals = entries * dim;
		break;
	    }
	    quantlist = new int[quantvals];
	    for (int i = 0; i < quantvals; i++)
		quantlist[i] = opb.read(q_quant);
	    if (quantlist[quantvals - 1] == -1) {
		clear();
		return -1;
	    }
	    break;
	}
	default:
	    clear();
	    return -1;
	}
	return 0;
    }
    
    private int maptype1_quantvals() {
	int vals
	    = (int) Math.floor(Math.pow((double) entries, 1.0 / (double) dim));
	for (;;) {
	    int acc = 1;
	    int acc1 = 1;
	    for (int i = 0; i < dim; i++) {
		acc *= vals;
		acc1 *= vals + 1;
	    }
	    if (acc <= entries && acc1 > entries)
		return vals;
	    if (acc > entries)
		vals--;
	    else
		vals++;
	}
    }
    
    void clear() {
	/* empty */
    }
    
    float[] unquantize() {
	if (maptype == 1 || maptype == 2) {
	    float mindel = float32_unpack(q_min);
	    float delta = float32_unpack(q_delta);
	    float[] r = new float[entries * dim];
	    switch (maptype) {
	    case 1: {
		int quantvals = maptype1_quantvals();
		for (int j = 0; j < entries; j++) {
		    float last = 0.0F;
		    int indexdiv = 1;
		    for (int k = 0; k < dim; k++) {
			int index = j / indexdiv % quantvals;
			float val = (float) quantlist[index];
			val = Math.abs(val) * delta + mindel + last;
			if (q_sequencep != 0)
			    last = val;
			r[j * dim + k] = val;
			indexdiv *= quantvals;
		    }
		}
		break;
	    }
	    case 2:
		for (int j = 0; j < entries; j++) {
		    float last = 0.0F;
		    for (int k = 0; k < dim; k++) {
			float val = (float) quantlist[j * dim + k];
			val = Math.abs(val) * delta + mindel + last;
			if (q_sequencep != 0)
			    last = val;
			r[j * dim + k] = val;
		    }
		}
		break;
	    }
	    return r;
	}
	return null;
    }
    
    private static int ilog(int v) {
	int ret = 0;
	for (/**/; v != 0; v >>>= 1)
	    ret++;
	return ret;
    }
    
    static long float32_pack(float val) {
	int sign = 0;
	if (val < 0.0F) {
	    sign = -2147483648;
	    val = -val;
	}
	int exp = (int) Math.floor(Math.log((double) val) / Math.log(2.0));
	int mant
	    = (int) Math.rint(Math.pow((double) val, (double) (20 - exp)));
	exp = exp + 768 << 21;
	return (long) (sign | exp | mant);
    }
    
    static float float32_unpack(int val) {
	float mant = (float) (val & 0x1fffff);
	float exp = (float) ((val & 0x7fe00000) >>> 21);
	if ((val & ~0x7fffffff) != 0)
	    mant = -mant;
	return ldexp(mant, (int) exp - 20 - 768);
    }
    
    static float ldexp(float foo, int e) {
	return (float) ((double) foo * Math.pow(2.0, (double) e));
    }
}
