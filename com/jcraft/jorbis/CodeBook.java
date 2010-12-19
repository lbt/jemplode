/* CodeBook - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

class CodeBook
{
    int dim;
    int entries;
    StaticCodeBook c = new StaticCodeBook();
    float[] valuelist;
    int[] codelist;
    DecodeAux decode_tree;
    private int[] t = new int[15];
    
    int encode(int a, Buffer b) {
	b.write(codelist[a], c.lengthlist[a]);
	return c.lengthlist[a];
    }
    
    int errorv(float[] a) {
	int best = best(a, 1);
	for (int k = 0; k < dim; k++)
	    a[k] = valuelist[best * dim + k];
	return best;
    }
    
    int encodev(int best, float[] a, Buffer b) {
	for (int k = 0; k < dim; k++)
	    a[k] = valuelist[best * dim + k];
	return encode(best, b);
    }
    
    int encodevs(float[] a, Buffer b, int step, int addmul) {
	int best = besterror(a, step, addmul);
	return encode(best, b);
    }
    
    synchronized int decodevs_add(float[] a, int offset, Buffer b, int n) {
	int step = n / dim;
	if (t.length < step)
	    t = new int[step];
	for (int i = 0; i < step; i++) {
	    int entry = decode(b);
	    if (entry == -1)
		return -1;
	    t[i] = entry * dim;
	}
	int i = 0;
	int o = 0;
	while (i < dim) {
	    for (int j = 0; j < step; j++)
		a[offset + o + j] += valuelist[t[j] + i];
	    i++;
	    o += step;
	}
	return 0;
    }
    
    int decodev_add(float[] a, int offset, Buffer b, int n) {
	if (dim > 8) {
	    int i = 0;
	    while (i < n) {
		int entry = decode(b);
		if (entry == -1)
		    return -1;
		int t = entry * dim;
		int j = 0;
		while (j < dim)
		    a[offset + i++] += valuelist[t + j++];
	    }
	} else {
	    int i = 0;
	    while (i < n) {
		int entry = decode(b);
		if (entry == -1)
		    return -1;
		int t = entry * dim;
		int j = 0;
		switch (dim) {
		case 8:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 7:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 6:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 5:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 4:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 3:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 2:
		    a[offset + i++] += valuelist[t + j++];
		    /* fall through */
		case 1:
		    a[offset + i++] += valuelist[t + j++];
		    break;
		}
	    }
	}
	return 0;
    }
    
    int decodev_set(float[] a, int offset, Buffer b, int n) {
	int i = 0;
	while (i < n) {
	    int entry = decode(b);
	    if (entry == -1)
		return -1;
	    int t = entry * dim;
	    int j = 0;
	    while (j < dim)
		a[offset + i++] = valuelist[t + j++];
	}
	return 0;
    }
    
    int decodevv_add(float[][] a, int offset, int ch, Buffer b, int n) {
	int chptr = 0;
	int i = offset / ch;
	while (i < (offset + n) / ch) {
	    int entry = decode(b);
	    if (entry == -1)
		return -1;
	    int t = entry * dim;
	    for (int j = 0; j < dim; j++) {
		a[chptr++][i] += valuelist[t + j];
		if (chptr == ch) {
		    chptr = 0;
		    i++;
		}
	    }
	}
	return 0;
    }
    
    int decode(Buffer b) {
	int ptr = 0;
	DecodeAux t = decode_tree;
	int lok = b.look(t.tabn);
	if (lok >= 0) {
	    ptr = t.tab[lok];
	    b.adv(t.tabl[lok]);
	    if (ptr <= 0)
		return -ptr;
	}
	for (;;) {
	    switch (b.read1()) {
	    case 0:
		ptr = t.ptr0[ptr];
		break;
	    case 1:
		ptr = t.ptr1[ptr];
		break;
	    default:
		return -1;
	    }
	    if (ptr <= 0)
		return -ptr;
	}
    }
    
    int decodevs(float[] a, int index, Buffer b, int step, int addmul) {
	int entry = decode(b);
	if (entry == -1)
	    return -1;
	switch (addmul) {
	case -1: {
	    int i = 0;
	    int o = 0;
	    while (i < dim) {
		a[index + o] = valuelist[entry * dim + i];
		i++;
		o += step;
	    }
	    break;
	}
	case 0: {
	    int i = 0;
	    int o = 0;
	    while (i < dim) {
		a[index + o] += valuelist[entry * dim + i];
		i++;
		o += step;
	    }
	    break;
	}
	case 1: {
	    int i = 0;
	    int o = 0;
	    while (i < dim) {
		a[index + o] *= valuelist[entry * dim + i];
		i++;
		o += step;
	    }
	    break;
	}
	default:
	    System.err.println("CodeBook.decodeves: addmul=" + addmul);
	}
	return entry;
    }
    
    int best(float[] a, int step) {
	EncodeAuxNearestMatch nt = this.c.nearest_tree;
	EncodeAuxThreshMatch tt = this.c.thresh_tree;
	int ptr = 0;
	if (tt != null) {
	    int index = 0;
	    int k = 0;
	    int o = step * (dim - 1);
	    while (k < dim) {
		int i;
		for (i = 0; i < tt.threshvals - 1; i++) {
		    if (a[o] < tt.quantthresh[i])
			break;
		}
		index = index * tt.quantvals + tt.quantmap[i];
		k++;
		o -= step;
	    }
	    if (this.c.lengthlist[index] > 0)
		return index;
	}
	if (nt != null) {
	    do {
		float c = 0.0F;
		int p = nt.p[ptr];
		int q = nt.q[ptr];
		int k = 0;
		int o = 0;
		while (k < dim) {
		    c += ((double) (valuelist[p + k] - valuelist[q + k])
			  * ((double) a[o]
			     - ((double) (valuelist[p + k] + valuelist[q + k])
				* 0.5)));
		    k++;
		    o += step;
		}
		if ((double) c > 0.0)
		    ptr = -nt.ptr0[ptr];
		else
		    ptr = -nt.ptr1[ptr];
	    } while (ptr > 0);
	    return -ptr;
	}
	int besti = -1;
	float best = 0.0F;
	int e = 0;
	for (int i = 0; i < entries; i++) {
	    if (this.c.lengthlist[i] > 0) {
		float _this = dist(dim, valuelist, e, a, step);
		if (besti == -1 || _this < best) {
		    best = _this;
		    besti = i;
		}
	    }
	    e += dim;
	}
	return besti;
    }
    
    int besterror(float[] a, int step, int addmul) {
	int best = best(a, step);
	switch (addmul) {
	case 0: {
	    int i = 0;
	    int o = 0;
	    while (i < dim) {
		a[o] -= valuelist[best * dim + i];
		i++;
		o += step;
	    }
	    break;
	}
	case 1: {
	    int i = 0;
	    int o = 0;
	    while (i < dim) {
		float val = valuelist[best * dim + i];
		if (val == 0.0F)
		    a[o] = 0.0F;
		else
		    a[o] /= val;
		i++;
		o += step;
	    }
	    break;
	}
	}
	return best;
    }
    
    void clear() {
	/* empty */
    }
    
    private static float dist(int el, float[] ref, int index, float[] b,
			      int step) {
	float acc = 0.0F;
	for (int i = 0; i < el; i++) {
	    float val = ref[index + i] - b[i * step];
	    acc += val * val;
	}
	return acc;
    }
    
    int init_decode(StaticCodeBook s) {
	c = s;
	entries = s.entries;
	dim = s.dim;
	valuelist = s.unquantize();
	decode_tree = make_decode_tree();
	if (decode_tree == null) {
	    clear();
	    return -1;
	}
	return 0;
    }
    
    static int[] make_words(int[] l, int n) {
	int[] marker = new int[33];
	int[] r = new int[n];
	for (int i = 0; i < n; i++) {
	    int length = l[i];
	    if (length > 0) {
		int entry = marker[length];
		if (length < 32 && entry >>> length != 0)
		    return null;
		r[i] = entry;
		for (int j = length; j > 0; j--) {
		    if ((marker[j] & 0x1) != 0) {
			if (j == 1)
			    marker[1]++;
			else
			    marker[j] = marker[j - 1] << 1;
			break;
		    }
		    marker[j]++;
		}
		for (int j = length + 1; j < 33; j++) {
		    if (marker[j] >>> 1 != entry)
			break;
		    entry = marker[j];
		    marker[j] = marker[j - 1] << 1;
		}
	    }
	}
	for (int i = 0; i < n; i++) {
	    int temp = 0;
	    for (int j = 0; j < l[i]; j++) {
		temp <<= 1;
		temp |= r[i] >>> j & 0x1;
	    }
	    r[i] = temp;
	}
	return r;
    }
    
    DecodeAux make_decode_tree() {
	int top = 0;
	DecodeAux t = new DecodeAux();
	int[] ptr0 = t.ptr0 = new int[entries * 2];
	int[] ptr1 = t.ptr1 = new int[entries * 2];
	int[] codelist = make_words(c.lengthlist, c.entries);
	if (codelist == null)
	    return null;
	t.aux = entries * 2;
	for (int i = 0; i < entries; i++) {
	    if (c.lengthlist[i] > 0) {
		int ptr = 0;
		int j;
		for (j = 0; j < c.lengthlist[i] - 1; j++) {
		    int bit = codelist[i] >>> j & 0x1;
		    if (bit == 0) {
			if (ptr0[ptr] == 0)
			    ptr0[ptr] = ++top;
			ptr = ptr0[ptr];
		    } else {
			if (ptr1[ptr] == 0)
			    ptr1[ptr] = ++top;
			ptr = ptr1[ptr];
		    }
		}
		if ((codelist[i] >>> j & 0x1) == 0)
		    ptr0[ptr] = -i;
		else
		    ptr1[ptr] = -i;
	    }
	}
	t.tabn = ilog(entries) - 4;
	if (t.tabn < 5)
	    t.tabn = 5;
	int n = 1 << t.tabn;
	t.tab = new int[n];
	t.tabl = new int[n];
	for (int i = 0; i < n; i++) {
	    int p = 0;
	    int j = 0;
	    for (j = 0; j < t.tabn && (p > 0 || j == 0); j++) {
		if ((i & 1 << j) != 0)
		    p = ptr1[p];
		else
		    p = ptr0[p];
	    }
	    t.tab[i] = p;
	    t.tabl[i] = j;
	}
	return t;
    }
    
    private static int ilog(int v) {
	int ret = 0;
	for (/**/; v != 0; v >>>= 1)
	    ret++;
	return ret;
    }
}
