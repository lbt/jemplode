/* Floor0 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;

class Floor0 extends FuncFloor
{
    float[] lsp = null;
    
    void pack(Object i, Buffer opb) {
	InfoFloor0 info = (InfoFloor0) i;
	opb.write(info.order, 8);
	opb.write(info.rate, 16);
	opb.write(info.barkmap, 16);
	opb.write(info.ampbits, 6);
	opb.write(info.ampdB, 8);
	opb.write(info.numbooks - 1, 4);
	for (int j = 0; j < info.numbooks; j++)
	    opb.write(info.books[j], 8);
    }
    
    Object unpack(Info vi, Buffer opb) {
	InfoFloor0 info = new InfoFloor0();
	info.order = opb.read(8);
	info.rate = opb.read(16);
	info.barkmap = opb.read(16);
	info.ampbits = opb.read(6);
	info.ampdB = opb.read(8);
	info.numbooks = opb.read(4) + 1;
	if (info.order < 1 || info.rate < 1 || info.barkmap < 1
	    || info.numbooks < 1)
	    return null;
	for (int j = 0; j < info.numbooks; j++) {
	    info.books[j] = opb.read(8);
	    if (info.books[j] < 0 || info.books[j] >= vi.books)
		return null;
	}
	return info;
    }
    
    Object look(DspState vd, InfoMode mi, Object i) {
	Info vi = vd.vi;
	InfoFloor0 info = (InfoFloor0) i;
	LookFloor0 look = new LookFloor0();
	look.m = info.order;
	look.n = vi.blocksizes[mi.blockflag] / 2;
	look.ln = info.barkmap;
	look.vi = info;
	look.lpclook.init(look.ln, look.m);
	float scale
	    = (float) look.ln / toBARK((float) ((double) info.rate / 2.0));
	look.linearmap = new int[look.n];
	for (int j = 0; j < look.n; j++) {
	    int val
		= (int) Math.floor((double) (toBARK((float) ((double) info.rate
							     / 2.0
							     / (double) look.n
							     * (double) j))
					     * scale));
	    if (val >= look.ln)
		val = look.ln;
	    look.linearmap[j] = val;
	}
	return look;
    }
    
    static float toBARK(float f) {
	return (float) (13.1 * Math.atan(7.4E-4 * (double) f)
			+ 2.24 * Math.atan((double) (f * f) * 1.85E-8)
			+ 1.0E-4 * (double) f);
    }
    
    Object state(Object i) {
	EchstateFloor0 state = new EchstateFloor0();
	InfoFloor0 info = (InfoFloor0) i;
	state.codewords = new int[info.order];
	state.curve = new float[info.barkmap];
	state.frameno = -1L;
	return state;
    }
    
    void free_info(Object i) {
	/* empty */
    }
    
    void free_look(Object i) {
	/* empty */
    }
    
    void free_state(Object vs) {
	/* empty */
    }
    
    int forward(Block vb, Object i, float[] in, float[] out, Object vs) {
	return 0;
    }
    
    int inverse(Block vb, Object i, float[] out) {
	object = object_2_;
	break while_2_;
    }
    
    Object inverse1(Block vb, Object i, Object memo) {
	LookFloor0 look = (LookFloor0) i;
	InfoFloor0 info = look.vi;
	float[] lsp = null;
	if (memo instanceof float[])
	    lsp = (float[]) memo;
	int ampraw = vb.opb.read(info.ampbits);
	if (ampraw > 0) {
	    int maxval = (1 << info.ampbits) - 1;
	    float amp = (float) ampraw / (float) maxval * (float) info.ampdB;
	    int booknum = vb.opb.read(ilog(info.numbooks));
	    if (booknum != -1 && booknum < info.numbooks) {
		CodeBook b = vb.vd.fullbooks[info.books[booknum]];
		float last = 0.0F;
		if (lsp == null || lsp.length < look.m + 1)
		    lsp = new float[look.m + 1];
		else {
		    for (int j = 0; j < lsp.length; j++)
			lsp[j] = 0.0F;
		}
		for (int j = 0; j < look.m; j += b.dim) {
		    if (b.decodev_set(lsp, j, vb.opb, b.dim) == -1)
			return null;
		}
		int j = 0;
		while (j < look.m) {
		    int k = 0;
		    while (k < b.dim) {
			lsp[j] += last;
			k++;
			j++;
		    }
		    last = lsp[j - 1];
		}
		lsp[look.m] = amp;
		return lsp;
	    }
	}
	return null;
    }
    
    int inverse2(Block vb, Object i, Object memo, float[] out) {
	LookFloor0 look = (LookFloor0) i;
	InfoFloor0 info = look.vi;
	if (memo != null) {
	    float[] lsp = (float[]) memo;
	    float amp = lsp[look.m];
	    Lsp.lsp_to_curve(out, look.linearmap, look.n, look.ln, lsp, look.m,
			     amp, (float) info.ampdB);
	    return 1;
	}
	for (int j = 0; j < look.n; j++)
	    out[j] = 0.0F;
	return 0;
    }
    
    static float fromdB(float x) {
	return (float) Math.exp((double) x * 0.11512925);
    }
    
    private static int ilog(int v) {
	int ret = 0;
	for (/**/; v != 0; v >>>= 1)
	    ret++;
	return ret;
    }
    
    static void lsp_to_lpc(float[] lsp, float[] lpc, int m) {
	int m2 = m / 2;
	float[] O = new float[m2];
	float[] E = new float[m2];
	float[] Ae = new float[m2 + 1];
	float[] Ao = new float[m2 + 1];
	float[] Be = new float[m2];
	float[] Bo = new float[m2];
	for (int i = 0; i < m2; i++) {
	    O[i] = (float) (-2.0 * Math.cos((double) lsp[i * 2]));
	    E[i] = (float) (-2.0 * Math.cos((double) lsp[i * 2 + 1]));
	}
	int j;
	for (j = 0; j < m2; j++) {
	    Ae[j] = 0.0F;
	    Ao[j] = 1.0F;
	    Be[j] = 0.0F;
	    Bo[j] = 1.0F;
	}
	Ao[j] = 1.0F;
	Ae[j] = 1.0F;
	for (int i = 1; i < m + 1; i++) {
	    float B;
	    float A = B = 0.0F;
	    for (j = 0; j < m2; j++) {
		float temp = O[j] * Ao[j] + Ae[j];
		Ae[j] = Ao[j];
		Ao[j] = A;
		A += temp;
		temp = E[j] * Bo[j] + Be[j];
		Be[j] = Bo[j];
		Bo[j] = B;
		B += temp;
	    }
	    lpc[i - 1] = (A + Ao[j] + B - Ae[j]) / 2.0F;
	    Ao[j] = A;
	    Ae[j] = B;
	}
    }
    
    static void lpc_to_curve(float[] curve, float[] lpc, float amp,
			     LookFloor0 l, String name, int frameno) {
	float[] lcurve = new float[Math.max(l.ln * 2, l.m * 2 + 2)];
	if (amp == 0.0F) {
	    for (int j = 0; j < l.n; j++)
		curve[j] = 0.0F;
	} else {
	    l.lpclook.lpc_to_curve(lcurve, lpc, amp);
	    for (int i = 0; i < l.n; i++)
		curve[i] = lcurve[l.linearmap[i]];
	}
    }
}
