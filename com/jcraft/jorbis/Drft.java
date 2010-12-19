/* Drft - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class Drft
{
    int n;
    float[] trigcache;
    int[] splitcache;
    static int[] ntryh = { 4, 2, 3, 5 };
    static float tpi = 6.2831855F;
    static float hsqt2 = 0.70710677F;
    static float taui = 0.8660254F;
    static float taur = -0.5F;
    static float sqrt2 = 1.4142135F;
    
    void backward(float[] data) {
	if (n != 1)
	    drftb1(n, data, trigcache, trigcache, n, splitcache);
    }
    
    void init(int n) {
	this.n = n;
	trigcache = new float[3 * n];
	splitcache = new int[32];
	fdrffti(n, trigcache, splitcache);
    }
    
    void clear() {
	if (trigcache != null)
	    trigcache = null;
	if (splitcache != null)
	    splitcache = null;
    }
    
    static void drfti1(int n, float[] wa, int index, int[] ifac) {
	int ntry = 0;
	int j = -1;
	int nl = n;
	int nf = 0;
	int state = 101;
    while_3_:
	for (;;) {
	    System.err.print("");
	    switch (state) {
	    case 101:
		if (++j < 4)
		    ntry = ntryh[j];
		else
		    ntry += 2;
		/* fall through */
	    case 104: {
		int nq = nl / ntry;
		int nr = nl - ntry * nq;
		if (nr != 0) {
		    state = 101;
		    break;
		}
		nf++;
		ifac[nf + 1] = ntry;
		nl = nq;
		if (ntry != 2) {
		    state = 107;
		    break;
		}
		if (nf == 1) {
		    state = 107;
		    break;
		}
		for (int i = 1; i < nf; i++) {
		    int ib = nf - i + 1;
		    ifac[ib + 1] = ifac[ib];
		}
		ifac[2] = 2;
	    }
		/* fall through */
	    case 107:
		if (nl != 1)
		    state = 104;
		else {
		    ifac[0] = n;
		    ifac[1] = nf;
		    float argh = tpi / (float) n;
		    int is = 0;
		    int nfm1 = nf - 1;
		    int l1 = 1;
		    if (nfm1 != 0) {
			for (int k1 = 0; k1 < nfm1; k1++) {
			    int ip = ifac[k1 + 2];
			    int ld = 0;
			    int l2 = l1 * ip;
			    int ido = n / l2;
			    int ipm = ip - 1;
			    for (j = 0; j < ipm; j++) {
				ld += l1;
				int i = is;
				float argld = (float) ld * argh;
				float fi = 0.0F;
				for (int ii = 2; ii < ido; ii += 2) {
				    fi++;
				    float arg = fi * argld;
				    wa[index + i++]
					= (float) Math.cos((double) arg);
				    wa[index + i++]
					= (float) Math.sin((double) arg);
				}
				is += ido;
			    }
			    l1 = l2;
			}
			break while_3_;
		    }
		    return;
		}
		break;
	    }
	}
    }
    
    static void fdrffti(int n, float[] wsave, int[] ifac) {
	if (n != 1)
	    drfti1(n, wsave, n, ifac);
    }
    
    static void dradf2(int ido, int l1, float[] cc, float[] ch, float[] wa1,
		       int index) {
	int t1 = 0;
	int t2;
	int t0 = t2 = l1 * ido;
	int t3 = ido << 1;
	for (int k = 0; k < l1; k++) {
	    ch[t1 << 1] = cc[t1] + cc[t2];
	    ch[(t1 << 1) + t3 - 1] = cc[t1] - cc[t2];
	    t1 += ido;
	    t2 += ido;
	}
	if (ido >= 2) {
	    if (ido != 2) {
		t1 = 0;
		t2 = t0;
		for (int k = 0; k < l1; k++) {
		    t3 = t2;
		    int t4 = (t1 << 1) + (ido << 1);
		    int t5 = t1;
		    int t6 = t1 + t1;
		    for (int i = 2; i < ido; i += 2) {
			t3 += 2;
			t4 -= 2;
			t5 += 2;
			t6 += 2;
			float tr2 = (wa1[index + i - 2] * cc[t3 - 1]
				     + wa1[index + i - 1] * cc[t3]);
			float ti2 = (wa1[index + i - 2] * cc[t3]
				     - wa1[index + i - 1] * cc[t3 - 1]);
			ch[t6] = cc[t5] + ti2;
			ch[t4] = ti2 - cc[t5];
			ch[t6 - 1] = cc[t5 - 1] + tr2;
			ch[t4 - 1] = cc[t5 - 1] - tr2;
		    }
		    t1 += ido;
		    t2 += ido;
		}
		if (ido % 2 == 1)
		    return;
	    }
	    t3 = t2 = (t1 = ido) - 1;
	    t2 += t0;
	    for (int k = 0; k < l1; k++) {
		ch[t1] = -cc[t2];
		ch[t1 - 1] = cc[t3];
		t1 += ido << 1;
		t2 += ido;
		t3 += ido;
	    }
	}
    }
    
    static void dradf4(int ido, int l1, float[] cc, float[] ch, float[] wa1,
		       int index1, float[] wa2, int index2, float[] wa3,
		       int index3) {
	int t0 = l1 * ido;
	int t1 = t0;
	int t4 = t1 << 1;
	int t2 = t1 + (t1 << 1);
	int t3 = 0;
	for (int k = 0; k < l1; k++) {
	    float tr1 = cc[t1] + cc[t2];
	    float tr2 = cc[t3] + cc[t4];
	    int t5;
	    ch[t5 = t3 << 2] = tr1 + tr2;
	    ch[(ido << 2) + t5 - 1] = tr2 - tr1;
	    ch[(t5 += ido << 1) - 1] = cc[t3] - cc[t4];
	    ch[t5] = cc[t2] - cc[t1];
	    t1 += ido;
	    t2 += ido;
	    t3 += ido;
	    t4 += ido;
	}
	if (ido >= 2) {
	    if (ido != 2) {
		t1 = 0;
		for (int k = 0; k < l1; k++) {
		    t2 = t1;
		    t4 = t1 << 2;
		    int t6;
		    int t5 = (t6 = ido << 1) + t4;
		    for (int i = 2; i < ido; i += 2) {
			t2 += 2;
			t3 = t2;
			t4 += 2;
			t5 -= 2;
			t3 += t0;
			float cr2 = (wa1[index1 + i - 2] * cc[t3 - 1]
				     + wa1[index1 + i - 1] * cc[t3]);
			float ci2 = (wa1[index1 + i - 2] * cc[t3]
				     - wa1[index1 + i - 1] * cc[t3 - 1]);
			t3 += t0;
			float cr3 = (wa2[index2 + i - 2] * cc[t3 - 1]
				     + wa2[index2 + i - 1] * cc[t3]);
			float ci3 = (wa2[index2 + i - 2] * cc[t3]
				     - wa2[index2 + i - 1] * cc[t3 - 1]);
			t3 += t0;
			float cr4 = (wa3[index3 + i - 2] * cc[t3 - 1]
				     + wa3[index3 + i - 1] * cc[t3]);
			float ci4 = (wa3[index3 + i - 2] * cc[t3]
				     - wa3[index3 + i - 1] * cc[t3 - 1]);
			float tr1 = cr2 + cr4;
			float tr4 = cr4 - cr2;
			float ti1 = ci2 + ci4;
			float ti4 = ci2 - ci4;
			float ti2 = cc[t2] + ci3;
			float ti3 = cc[t2] - ci3;
			float tr2 = cc[t2 - 1] + cr3;
			float tr3 = cc[t2 - 1] - cr3;
			ch[t4 - 1] = tr1 + tr2;
			ch[t4] = ti1 + ti2;
			ch[t5 - 1] = tr3 - ti4;
			ch[t5] = tr4 - ti3;
			ch[t4 + t6 - 1] = ti4 + tr3;
			ch[t4 + t6] = tr4 + ti3;
			ch[t5 + t6 - 1] = tr2 - tr1;
			ch[t5 + t6] = ti1 - ti2;
		    }
		    t1 += ido;
		}
		if ((ido & 0x1) != 0)
		    return;
	    }
	    t2 = (t1 = t0 + ido - 1) + (t0 << 1);
	    t3 = ido << 2;
	    t4 = ido;
	    int t5 = ido << 1;
	    int t6 = ido;
	    for (int k = 0; k < l1; k++) {
		float ti1 = -hsqt2 * (cc[t1] + cc[t2]);
		float tr1 = hsqt2 * (cc[t1] - cc[t2]);
		ch[t4 - 1] = tr1 + cc[t6 - 1];
		ch[t4 + t5 - 1] = cc[t6 - 1] - tr1;
		ch[t4] = ti1 - cc[t1 + t0];
		ch[t4 + t5] = ti1 + cc[t1 + t0];
		t1 += ido;
		t2 += ido;
		t4 += t3;
		t6 += ido;
	    }
	}
    }
    
    static void dradfg(int ido, int ip, int l1, int idl1, float[] cc,
		       float[] c1, float[] c2, float[] ch, float[] ch2,
		       float[] wa, int index) {
	int t2 = 0;
	float dcp = 0.0F;
	float dsp = 0.0F;
	float arg = tpi / (float) ip;
	dcp = (float) Math.cos((double) arg);
	dsp = (float) Math.sin((double) arg);
	int ipph = ip + 1 >> 1;
	int ipp2 = ip;
	int idp2 = ido;
	int nbd = ido - 1 >> 1;
	int t0 = l1 * ido;
	int t10 = ip * ido;
	int state = 100;
    while_4_:
	for (;;) {
	    switch (state) {
	    case 101: {
		if (ido == 1) {
		    state = 119;
		    break;
		}
		for (int ik = 0; ik < idl1; ik++)
		    ch2[ik] = c2[ik];
		int t1 = 0;
		for (int j = 1; j < ip; j++) {
		    t1 += t0;
		    t2 = t1;
		    for (int k = 0; k < l1; k++) {
			ch[t2] = c1[t2];
			t2 += ido;
		    }
		}
		int is = -ido;
		t1 = 0;
		if (nbd > l1) {
		    for (int j = 1; j < ip; j++) {
			t1 += t0;
			is += ido;
			t2 = -ido + t1;
			for (int k = 0; k < l1; k++) {
			    int idij = is - 1;
			    t2 += ido;
			    int t3 = t2;
			    for (int i = 2; i < ido; i += 2) {
				idij += 2;
				t3 += 2;
				ch[t3 - 1] = (wa[index + idij - 1] * c1[t3 - 1]
					      + wa[index + idij] * c1[t3]);
				ch[t3] = (wa[index + idij - 1] * c1[t3]
					  - wa[index + idij] * c1[t3 - 1]);
			    }
			}
		    }
		} else {
		    for (int j = 1; j < ip; j++) {
			is += ido;
			int idij = is - 1;
			t1 += t0;
			t2 = t1;
			for (int i = 2; i < ido; i += 2) {
			    idij += 2;
			    t2 += 2;
			    int t3 = t2;
			    for (int k = 0; k < l1; k++) {
				ch[t3 - 1] = (wa[index + idij - 1] * c1[t3 - 1]
					      + wa[index + idij] * c1[t3]);
				ch[t3] = (wa[index + idij - 1] * c1[t3]
					  - wa[index + idij] * c1[t3 - 1]);
				t3 += ido;
			    }
			}
		    }
		}
		t1 = 0;
		t2 = ipp2 * t0;
		if (nbd < l1) {
		    for (int j = 1; j < ipph; j++) {
			t1 += t0;
			t2 -= t0;
			int t3 = t1;
			int t4 = t2;
			for (int i = 2; i < ido; i += 2) {
			    t3 += 2;
			    t4 += 2;
			    int t5 = t3 - ido;
			    int t6 = t4 - ido;
			    for (int k = 0; k < l1; k++) {
				t5 += ido;
				t6 += ido;
				c1[t5 - 1] = ch[t5 - 1] + ch[t6 - 1];
				c1[t6 - 1] = ch[t5] - ch[t6];
				c1[t5] = ch[t5] + ch[t6];
				c1[t6] = ch[t6 - 1] - ch[t5 - 1];
			    }
			}
		    }
		} else {
		    for (int j = 1; j < ipph; j++) {
			t1 += t0;
			t2 -= t0;
			int t3 = t1;
			int t4 = t2;
			for (int k = 0; k < l1; k++) {
			    int t5 = t3;
			    int t6 = t4;
			    for (int i = 2; i < ido; i += 2) {
				t5 += 2;
				t6 += 2;
				c1[t5 - 1] = ch[t5 - 1] + ch[t6 - 1];
				c1[t6 - 1] = ch[t5] - ch[t6];
				c1[t5] = ch[t5] + ch[t6];
				c1[t6] = ch[t6 - 1] - ch[t5 - 1];
			    }
			    t3 += ido;
			    t4 += ido;
			}
		    }
		}
	    }
		/* fall through */
	    case 119: {
		for (int ik = 0; ik < idl1; ik++)
		    c2[ik] = ch2[ik];
		int t1 = 0;
		t2 = ipp2 * idl1;
		for (int j = 1; j < ipph; j++) {
		    t1 += t0;
		    t2 -= t0;
		    int t3 = t1 - ido;
		    int t4 = t2 - ido;
		    for (int k = 0; k < l1; k++) {
			t3 += ido;
			t4 += ido;
			c1[t3] = ch[t3] + ch[t4];
			c1[t4] = ch[t4] - ch[t3];
		    }
		}
		float ar1 = 1.0F;
		float ai1 = 0.0F;
		t1 = 0;
		t2 = ipp2 * idl1;
		int t3 = (ip - 1) * idl1;
		for (int l = 1; l < ipph; l++) {
		    t1 += idl1;
		    t2 -= idl1;
		    float ar1h = dcp * ar1 - dsp * ai1;
		    ai1 = dcp * ai1 + dsp * ar1;
		    ar1 = ar1h;
		    int t4 = t1;
		    int t5 = t2;
		    int t6 = t3;
		    int t7 = idl1;
		    for (int ik = 0; ik < idl1; ik++) {
			ch2[t4++] = c2[ik] + ar1 * c2[t7++];
			ch2[t5++] = ai1 * c2[t6++];
		    }
		    float dc2 = ar1;
		    float ds2 = ai1;
		    float ar2 = ar1;
		    float ai2 = ai1;
		    t4 = idl1;
		    t5 = (ipp2 - 1) * idl1;
		    for (int j = 2; j < ipph; j++) {
			t4 += idl1;
			t5 -= idl1;
			float ar2h = dc2 * ar2 - ds2 * ai2;
			ai2 = dc2 * ai2 + ds2 * ar2;
			ar2 = ar2h;
			t6 = t1;
			t7 = t2;
			int t8 = t4;
			int t9 = t5;
			for (int ik = 0; ik < idl1; ik++) {
			    ch2[t6++] += ar2 * c2[t8++];
			    ch2[t7++] += ai2 * c2[t9++];
			}
		    }
		}
		t1 = 0;
		for (int j = 1; j < ipph; j++) {
		    t1 += idl1;
		    t2 = t1;
		    for (int ik = 0; ik < idl1; ik++)
			ch2[ik] += c2[t2++];
		}
		if (ido < l1)
		    state = 132;
		else {
		    t1 = 0;
		    t2 = 0;
		    for (int k = 0; k < l1; k++) {
			t3 = t1;
			int t4 = t2;
			for (int i = 0; i < ido; i++)
			    cc[t4++] = ch[t3++];
			t1 += ido;
			t2 += t10;
		    }
		    state = 135;
		}
		break;
	    }
	    case 132:
		for (int i = 0; i < ido; i++) {
		    int t1 = i;
		    t2 = i;
		    for (int k = 0; k < l1; k++) {
			cc[t2] = ch[t1];
			t1 += ido;
			t2 += t10;
		    }
		}
		/* fall through */
	    case 135: {
		int t1 = 0;
		t2 = ido << 1;
		int t3 = 0;
		int t4 = ipp2 * t0;
		for (int j = 1; j < ipph; j++) {
		    t1 += t2;
		    t3 += t0;
		    t4 -= t0;
		    int t5 = t1;
		    int t6 = t3;
		    int t7 = t4;
		    for (int k = 0; k < l1; k++) {
			cc[t5 - 1] = ch[t6];
			cc[t5] = ch[t7];
			t5 += t10;
			t6 += ido;
			t7 += ido;
		    }
		}
		if (ido != 1) {
		    if (nbd < l1)
			state = 141;
		    else {
			t1 = -ido;
			t3 = 0;
			t4 = 0;
			int t5 = ipp2 * t0;
			for (int j = 1; j < ipph; j++) {
			    t1 += t2;
			    t3 += t2;
			    t4 += t0;
			    t5 -= t0;
			    int t6 = t1;
			    int t7 = t3;
			    int t8 = t4;
			    int t9 = t5;
			    for (int k = 0; k < l1; k++) {
				for (int i = 2; i < ido; i += 2) {
				    int ic = idp2 - i;
				    cc[i + t7 - 1]
					= ch[i + t8 - 1] + ch[i + t9 - 1];
				    cc[ic + t6 - 1]
					= ch[i + t8 - 1] - ch[i + t9 - 1];
				    cc[i + t7] = ch[i + t8] + ch[i + t9];
				    cc[ic + t6] = ch[i + t9] - ch[i + t8];
				}
				t6 += t10;
				t7 += t10;
				t8 += ido;
				t9 += ido;
			    }
			}
			return;
		    }
		    break;
		}
		return;
	    }
	    case 141: {
		int t1 = -ido;
		int t3 = 0;
		int t4 = 0;
		int t5 = ipp2 * t0;
		for (int j = 1; j < ipph; j++) {
		    t1 += t2;
		    t3 += t2;
		    t4 += t0;
		    t5 -= t0;
		    for (int i = 2; i < ido; i += 2) {
			int t6 = idp2 + t1 - i;
			int t7 = i + t3;
			int t8 = i + t4;
			int t9 = i + t5;
			for (int k = 0; k < l1; k++) {
			    cc[t7 - 1] = ch[t8 - 1] + ch[t9 - 1];
			    cc[t6 - 1] = ch[t8 - 1] - ch[t9 - 1];
			    cc[t7] = ch[t8] + ch[t9];
			    cc[t6] = ch[t9] - ch[t8];
			    t6 += t10;
			    t7 += t10;
			    t8 += ido;
			    t9 += ido;
			}
		    }
		}
		break while_4_;
	    }
	    }
	}
    }
    
    static void drftf1(int n, float[] c, float[] ch, float[] wa, int[] ifac) {
	int nf = ifac[1];
	int na = 1;
	int l2 = n;
	int iw = n;
	for (int k1 = 0; k1 < nf; k1++) {
	    int kh = nf - k1;
	    int ip = ifac[kh + 1];
	    int l1 = l2 / ip;
	    int ido = n / l2;
	    int idl1 = ido * l1;
	    iw -= (ip - 1) * ido;
	    na = 1 - na;
	    int state = 100;
	while_5_:
	    for (;;) {
		switch (state) {
		case 100:
		    if (ip != 4)
			state = 102;
		    else {
			int ix2 = iw + ido;
			int ix3 = ix2 + ido;
			if (na != 0)
			    dradf4(ido, l1, ch, c, wa, iw - 1, wa, ix2 - 1, wa,
				   ix3 - 1);
			else
			    dradf4(ido, l1, c, ch, wa, iw - 1, wa, ix2 - 1, wa,
				   ix3 - 1);
			state = 110;
		    }
		    break;
		case 102:
		    if (ip != 2)
			state = 104;
		    else if (na != 0)
			state = 103;
		    else {
			dradf2(ido, l1, c, ch, wa, iw - 1);
			state = 110;
		    }
		    break;
		case 103:
		    dradf2(ido, l1, ch, c, wa, iw - 1);
		    /* fall through */
		case 104:
		    if (ido == 1)
			na = 1 - na;
		    if (na != 0)
			state = 109;
		    else {
			dradfg(ido, ip, l1, idl1, c, c, c, ch, ch, wa, iw - 1);
			na = 1;
			state = 110;
		    }
		    break;
		case 109:
		    dradfg(ido, ip, l1, idl1, ch, ch, ch, c, c, wa, iw - 1);
		    na = 0;
		    /* fall through */
		case 110:
		    l2 = l1;
		    break while_5_;
		}
	    }
	}
	if (na != 1) {
	    for (int i = 0; i < n; i++)
		c[i] = ch[i];
	}
    }
    
    static void dradb2(int ido, int l1, float[] cc, float[] ch, float[] wa1,
		       int index) {
	int t0 = l1 * ido;
	int t1 = 0;
	int t2 = 0;
	int t3 = (ido << 1) - 1;
	for (int k = 0; k < l1; k++) {
	    ch[t1] = cc[t2] + cc[t3 + t2];
	    ch[t1 + t0] = cc[t2] - cc[t3 + t2];
	    t2 = (t1 += ido) << 1;
	}
	if (ido >= 2) {
	    if (ido != 2) {
		t1 = 0;
		t2 = 0;
		for (int k = 0; k < l1; k++) {
		    t3 = t1;
		    int t4;
		    int t5 = (t4 = t2) + (ido << 1);
		    int t6 = t0 + t1;
		    for (int i = 2; i < ido; i += 2) {
			t3 += 2;
			t4 += 2;
			t5 -= 2;
			t6 += 2;
			ch[t3 - 1] = cc[t4 - 1] + cc[t5 - 1];
			float tr2 = cc[t4 - 1] - cc[t5 - 1];
			ch[t3] = cc[t4] - cc[t5];
			float ti2 = cc[t4] + cc[t5];
			ch[t6 - 1] = (wa1[index + i - 2] * tr2
				      - wa1[index + i - 1] * ti2);
			ch[t6] = (wa1[index + i - 2] * ti2
				  + wa1[index + i - 1] * tr2);
		    }
		    t2 = (t1 += ido) << 1;
		}
		if (ido % 2 == 1)
		    return;
	    }
	    t1 = ido - 1;
	    t2 = ido - 1;
	    for (int k = 0; k < l1; k++) {
		ch[t1] = cc[t2] + cc[t2];
		ch[t1 + t0] = -(cc[t2 + 1] + cc[t2 + 1]);
		t1 += ido;
		t2 += ido << 1;
	    }
	}
    }
    
    static void dradb3(int ido, int l1, float[] cc, float[] ch, float[] wa1,
		       int index1, float[] wa2, int index2) {
	int t0 = l1 * ido;
	int t1 = 0;
	int t2 = t0 << 1;
	int t3 = ido << 1;
	int t4 = ido + (ido << 1);
	int t5 = 0;
	for (int k = 0; k < l1; k++) {
	    float tr2 = cc[t3 - 1] + cc[t3 - 1];
	    float cr2 = cc[t5] + taur * tr2;
	    ch[t1] = cc[t5] + tr2;
	    float ci3 = taui * (cc[t3] + cc[t3]);
	    ch[t1 + t0] = cr2 - ci3;
	    ch[t1 + t2] = cr2 + ci3;
	    t1 += ido;
	    t3 += t4;
	    t5 += t4;
	}
	if (ido != 1) {
	    t1 = 0;
	    t3 = ido << 1;
	    for (int k = 0; k < l1; k++) {
		int t7 = t1 + (t1 << 1);
		int t6 = t5 = t7 + t3;
		int t8 = t1;
		int t9;
		int t10 = (t9 = t1 + t0) + t0;
		for (int i = 2; i < ido; i += 2) {
		    t5 += 2;
		    t6 -= 2;
		    t7 += 2;
		    t8 += 2;
		    t9 += 2;
		    t10 += 2;
		    float tr2 = cc[t5 - 1] + cc[t6 - 1];
		    float cr2 = cc[t7 - 1] + taur * tr2;
		    ch[t8 - 1] = cc[t7 - 1] + tr2;
		    float ti2 = cc[t5] - cc[t6];
		    float ci2 = cc[t7] + taur * ti2;
		    ch[t8] = cc[t7] + ti2;
		    float cr3 = taui * (cc[t5 - 1] - cc[t6 - 1]);
		    float ci3 = taui * (cc[t5] + cc[t6]);
		    float dr2 = cr2 - ci3;
		    float dr3 = cr2 + ci3;
		    float di2 = ci2 + cr3;
		    float di3 = ci2 - cr3;
		    ch[t9 - 1] = (wa1[index1 + i - 2] * dr2
				  - wa1[index1 + i - 1] * di2);
		    ch[t9] = (wa1[index1 + i - 2] * di2
			      + wa1[index1 + i - 1] * dr2);
		    ch[t10 - 1] = (wa2[index2 + i - 2] * dr3
				   - wa2[index2 + i - 1] * di3);
		    ch[t10] = (wa2[index2 + i - 2] * di3
			       + wa2[index2 + i - 1] * dr3);
		}
		t1 += ido;
	    }
	}
    }
    
    static void dradb4(int ido, int l1, float[] cc, float[] ch, float[] wa1,
		       int index1, float[] wa2, int index2, float[] wa3,
		       int index3) {
	int t0 = l1 * ido;
	int t1 = 0;
	int t2 = ido << 2;
	int t3 = 0;
	int t6 = ido << 1;
	for (int k = 0; k < l1; k++) {
	    int t4 = t3 + t6;
	    int t5 = t1;
	    float tr3 = cc[t4 - 1] + cc[t4 - 1];
	    float tr4 = cc[t4] + cc[t4];
	    float tr1 = cc[t3] - cc[(t4 += t6) - 1];
	    float tr2 = cc[t3] + cc[t4 - 1];
	    ch[t5] = tr2 + tr3;
	    ch[t5 += t0] = tr1 - tr4;
	    ch[t5 += t0] = tr2 - tr3;
	    ch[t5 += t0] = tr1 + tr4;
	    t1 += ido;
	    t3 += t2;
	}
	if (ido >= 2) {
	    if (ido != 2) {
		t1 = 0;
		for (int k = 0; k < l1; k++) {
		    int t4;
		    int t5 = (t4 = t3 = (t2 = t1 << 2) + t6) + t6;
		    int t7 = t1;
		    for (int i = 2; i < ido; i += 2) {
			t2 += 2;
			t3 += 2;
			t4 -= 2;
			t5 -= 2;
			t7 += 2;
			float ti1 = cc[t2] + cc[t5];
			float ti2 = cc[t2] - cc[t5];
			float ti3 = cc[t3] - cc[t4];
			float tr4 = cc[t3] + cc[t4];
			float tr1 = cc[t2 - 1] - cc[t5 - 1];
			float tr2 = cc[t2 - 1] + cc[t5 - 1];
			float ti4 = cc[t3 - 1] - cc[t4 - 1];
			float tr3 = cc[t3 - 1] + cc[t4 - 1];
			ch[t7 - 1] = tr2 + tr3;
			float cr3 = tr2 - tr3;
			ch[t7] = ti2 + ti3;
			float ci3 = ti2 - ti3;
			float cr2 = tr1 - tr4;
			float cr4 = tr1 + tr4;
			float ci2 = ti1 + ti4;
			float ci4 = ti1 - ti4;
			int t8;
			ch[(t8 = t7 + t0) - 1] = (wa1[index1 + i - 2] * cr2
						  - wa1[index1 + i - 1] * ci2);
			ch[t8] = (wa1[index1 + i - 2] * ci2
				  + wa1[index1 + i - 1] * cr2);
			ch[(t8 += t0) - 1] = (wa2[index2 + i - 2] * cr3
					      - wa2[index2 + i - 1] * ci3);
			ch[t8] = (wa2[index2 + i - 2] * ci3
				  + wa2[index2 + i - 1] * cr3);
			ch[(t8 += t0) - 1] = (wa3[index3 + i - 2] * cr4
					      - wa3[index3 + i - 1] * ci4);
			ch[t8] = (wa3[index3 + i - 2] * ci4
				  + wa3[index3 + i - 1] * cr4);
		    }
		    t1 += ido;
		}
		if (ido % 2 == 1)
		    return;
	    }
	    t1 = ido;
	    t2 = ido << 2;
	    t3 = ido - 1;
	    int t4 = ido + (ido << 1);
	    for (int k = 0; k < l1; k++) {
		int t5 = t3;
		float ti1 = cc[t1] + cc[t4];
		float ti2 = cc[t4] - cc[t1];
		float tr1 = cc[t1 - 1] - cc[t4 - 1];
		float tr2 = cc[t1 - 1] + cc[t4 - 1];
		ch[t5] = tr2 + tr2;
		ch[t5 += t0] = sqrt2 * (tr1 - ti1);
		ch[t5 += t0] = ti2 + ti2;
		ch[t5 += t0] = -sqrt2 * (tr1 + ti1);
		t3 += ido;
		t1 += t2;
		t4 += t2;
	    }
	}
    }
    
    static void dradbg(int ido, int ip, int l1, int idl1, float[] cc,
		       float[] c1, float[] c2, float[] ch, float[] ch2,
		       float[] wa, int index) {
	int ipph = 0;
	int t0 = 0;
	int t10 = 0;
	int nbd = 0;
	float dcp = 0.0F;
	float dsp = 0.0F;
	int ipp2 = 0;
	int state = 100;
    while_6_:
	for (;;) {
	    switch (state) {
	    case 100: {
		t10 = ip * ido;
		t0 = l1 * ido;
		float arg = tpi / (float) ip;
		dcp = (float) Math.cos((double) arg);
		dsp = (float) Math.sin((double) arg);
		nbd = ido - 1 >>> 1;
		ipp2 = ip;
		ipph = ip + 1 >>> 1;
		if (ido < l1)
		    state = 103;
		else {
		    int t1 = 0;
		    int t2 = 0;
		    for (int k = 0; k < l1; k++) {
			int t3 = t1;
			int t4 = t2;
			for (int i = 0; i < ido; i++) {
			    ch[t3] = cc[t4];
			    t3++;
			    t4++;
			}
			t1 += ido;
			t2 += t10;
		    }
		    state = 106;
		}
		break;
	    }
	    case 103: {
		int t1 = 0;
		for (int i = 0; i < ido; i++) {
		    int t2 = t1;
		    int t3 = t1;
		    for (int k = 0; k < l1; k++) {
			ch[t2] = cc[t3];
			t2 += ido;
			t3 += t10;
		    }
		    t1++;
		}
	    }
		/* fall through */
	    case 106: {
		int t1 = 0;
		int t2 = ipp2 * t0;
		int t5;
		int t7 = t5 = ido << 1;
		for (int j = 1; j < ipph; j++) {
		    t1 += t0;
		    t2 -= t0;
		    int t3 = t1;
		    int t4 = t2;
		    int t6 = t5;
		    for (int k = 0; k < l1; k++) {
			ch[t3] = cc[t6 - 1] + cc[t6 - 1];
			ch[t4] = cc[t6] + cc[t6];
			t3 += ido;
			t4 += ido;
			t6 += t10;
		    }
		    t5 += t7;
		}
		if (ido == 1)
		    state = 116;
		else if (nbd < l1)
		    state = 112;
		else {
		    t1 = 0;
		    t2 = ipp2 * t0;
		    t7 = 0;
		    for (int j = 1; j < ipph; j++) {
			t1 += t0;
			t2 -= t0;
			int t3 = t1;
			int t4 = t2;
			t7 += ido << 1;
			int t8 = t7;
			for (int k = 0; k < l1; k++) {
			    t5 = t3;
			    int t6 = t4;
			    int t9 = t8;
			    int t11 = t8;
			    for (int i = 2; i < ido; i += 2) {
				t5 += 2;
				t6 += 2;
				t9 += 2;
				t11 -= 2;
				ch[t5 - 1] = cc[t9 - 1] + cc[t11 - 1];
				ch[t6 - 1] = cc[t9 - 1] - cc[t11 - 1];
				ch[t5] = cc[t9] - cc[t11];
				ch[t6] = cc[t9] + cc[t11];
			    }
			    t3 += ido;
			    t4 += ido;
			    t8 += t10;
			}
		    }
		    state = 116;
		}
		break;
	    }
	    case 112: {
		int t1 = 0;
		int t2 = ipp2 * t0;
		int t7 = 0;
		for (int j = 1; j < ipph; j++) {
		    t1 += t0;
		    t2 -= t0;
		    int t3 = t1;
		    int t4 = t2;
		    t7 += ido << 1;
		    int t8 = t7;
		    int t9 = t7;
		    for (int i = 2; i < ido; i += 2) {
			t3 += 2;
			t4 += 2;
			t8 += 2;
			t9 -= 2;
			int t5 = t3;
			int t6 = t4;
			int t11 = t8;
			int t12 = t9;
			for (int k = 0; k < l1; k++) {
			    ch[t5 - 1] = cc[t11 - 1] + cc[t12 - 1];
			    ch[t6 - 1] = cc[t11 - 1] - cc[t12 - 1];
			    ch[t5] = cc[t11] - cc[t12];
			    ch[t6] = cc[t11] + cc[t12];
			    t5 += ido;
			    t6 += ido;
			    t11 += t10;
			    t12 += t10;
			}
		    }
		}
	    }
		/* fall through */
	    case 116: {
		float ar1 = 1.0F;
		float ai1 = 0.0F;
		int t1 = 0;
		int t2;
		int t9 = t2 = ipp2 * idl1;
		int t3 = (ip - 1) * idl1;
		for (int l = 1; l < ipph; l++) {
		    t1 += idl1;
		    t2 -= idl1;
		    float ar1h = dcp * ar1 - dsp * ai1;
		    ai1 = dcp * ai1 + dsp * ar1;
		    ar1 = ar1h;
		    int t4 = t1;
		    int t5 = t2;
		    int t6 = 0;
		    int t7 = idl1;
		    int t8 = t3;
		    for (int ik = 0; ik < idl1; ik++) {
			c2[t4++] = ch2[t6++] + ar1 * ch2[t7++];
			c2[t5++] = ai1 * ch2[t8++];
		    }
		    float dc2 = ar1;
		    float ds2 = ai1;
		    float ar2 = ar1;
		    float ai2 = ai1;
		    t6 = idl1;
		    t7 = t9 - idl1;
		    for (int j = 2; j < ipph; j++) {
			t6 += idl1;
			t7 -= idl1;
			float ar2h = dc2 * ar2 - ds2 * ai2;
			ai2 = dc2 * ai2 + ds2 * ar2;
			ar2 = ar2h;
			t4 = t1;
			t5 = t2;
			int t11 = t6;
			int t12 = t7;
			for (int ik = 0; ik < idl1; ik++) {
			    c2[t4++] += ar2 * ch2[t11++];
			    c2[t5++] += ai2 * ch2[t12++];
			}
		    }
		}
		t1 = 0;
		for (int j = 1; j < ipph; j++) {
		    t1 += idl1;
		    t2 = t1;
		    for (int ik = 0; ik < idl1; ik++)
			ch2[ik] += ch2[t2++];
		}
		t1 = 0;
		t2 = ipp2 * t0;
		for (int j = 1; j < ipph; j++) {
		    t1 += t0;
		    t2 -= t0;
		    t3 = t1;
		    int t4 = t2;
		    for (int k = 0; k < l1; k++) {
			ch[t3] = c1[t3] - c1[t4];
			ch[t4] = c1[t3] + c1[t4];
			t3 += ido;
			t4 += ido;
		    }
		}
		if (ido == 1)
		    state = 132;
		else if (nbd < l1)
		    state = 128;
		else {
		    t1 = 0;
		    t2 = ipp2 * t0;
		    for (int j = 1; j < ipph; j++) {
			t1 += t0;
			t2 -= t0;
			t3 = t1;
			int t4 = t2;
			for (int k = 0; k < l1; k++) {
			    int t5 = t3;
			    int t6 = t4;
			    for (int i = 2; i < ido; i += 2) {
				t5 += 2;
				t6 += 2;
				ch[t5 - 1] = c1[t5 - 1] - c1[t6];
				ch[t6 - 1] = c1[t5 - 1] + c1[t6];
				ch[t5] = c1[t5] + c1[t6 - 1];
				ch[t6] = c1[t5] - c1[t6 - 1];
			    }
			    t3 += ido;
			    t4 += ido;
			}
		    }
		    state = 132;
		}
		break;
	    }
	    case 128: {
		int t1 = 0;
		int t2 = ipp2 * t0;
		for (int j = 1; j < ipph; j++) {
		    t1 += t0;
		    t2 -= t0;
		    int t3 = t1;
		    int t4 = t2;
		    for (int i = 2; i < ido; i += 2) {
			t3 += 2;
			t4 += 2;
			int t5 = t3;
			int t6 = t4;
			for (int k = 0; k < l1; k++) {
			    ch[t5 - 1] = c1[t5 - 1] - c1[t6];
			    ch[t6 - 1] = c1[t5 - 1] + c1[t6];
			    ch[t5] = c1[t5] + c1[t6 - 1];
			    ch[t6] = c1[t5] - c1[t6 - 1];
			    t5 += ido;
			    t6 += ido;
			}
		    }
		}
	    }
		/* fall through */
	    case 132:
		if (ido != 1) {
		    for (int ik = 0; ik < idl1; ik++)
			c2[ik] = ch2[ik];
		    int t1 = 0;
		    for (int j = 1; j < ip; j++) {
			int t2 = t1 += t0;
			for (int k = 0; k < l1; k++) {
			    c1[t2] = ch[t2];
			    t2 += ido;
			}
		    }
		    if (nbd > l1)
			state = 139;
		    else {
			int is = -ido - 1;
			t1 = 0;
			for (int j = 1; j < ip; j++) {
			    is += ido;
			    t1 += t0;
			    int idij = is;
			    int t2 = t1;
			    for (int i = 2; i < ido; i += 2) {
				t2 += 2;
				idij += 2;
				int t3 = t2;
				for (int k = 0; k < l1; k++) {
				    c1[t3 - 1]
					= (wa[index + idij - 1] * ch[t3 - 1]
					   - wa[index + idij] * ch[t3]);
				    c1[t3] = (wa[index + idij - 1] * ch[t3]
					      + wa[index + idij] * ch[t3 - 1]);
				    t3 += ido;
				}
			    }
			}
			return;
		    }
		    break;
		}
		return;
	    case 139: {
		int is = -ido - 1;
		int t1 = 0;
		for (int j = 1; j < ip; j++) {
		    is += ido;
		    t1 += t0;
		    int t2 = t1;
		    for (int k = 0; k < l1; k++) {
			int idij = is;
			int t3 = t2;
			for (int i = 2; i < ido; i += 2) {
			    idij += 2;
			    t3 += 2;
			    c1[t3 - 1] = (wa[index + idij - 1] * ch[t3 - 1]
					  - wa[index + idij] * ch[t3]);
			    c1[t3] = (wa[index + idij - 1] * ch[t3]
				      + wa[index + idij] * ch[t3 - 1]);
			}
			t2 += ido;
		    }
		}
		break while_6_;
	    }
	    }
	}
    }
    
    static void drftb1(int n, float[] c, float[] ch, float[] wa, int index,
		       int[] ifac) {
	int l2 = 0;
	int ip = 0;
	int ido = 0;
	int idl1 = 0;
	int nf = ifac[1];
	int na = 0;
	int l1 = 1;
	int iw = 1;
	for (int k1 = 0; k1 < nf; k1++) {
	    int state = 100;
	while_7_:
	    for (;;) {
		switch (state) {
		case 100:
		    ip = ifac[k1 + 2];
		    l2 = ip * l1;
		    ido = n / l2;
		    idl1 = ido * l1;
		    if (ip != 4)
			state = 103;
		    else {
			int ix2 = iw + ido;
			int ix3 = ix2 + ido;
			if (na != 0)
			    dradb4(ido, l1, ch, c, wa, index + iw - 1, wa,
				   index + ix2 - 1, wa, index + ix3 - 1);
			else
			    dradb4(ido, l1, c, ch, wa, index + iw - 1, wa,
				   index + ix2 - 1, wa, index + ix3 - 1);
			na = 1 - na;
			state = 115;
		    }
		    break;
		case 103:
		    if (ip != 2)
			state = 106;
		    else {
			if (na != 0)
			    dradb2(ido, l1, ch, c, wa, index + iw - 1);
			else
			    dradb2(ido, l1, c, ch, wa, index + iw - 1);
			na = 1 - na;
			state = 115;
		    }
		    break;
		case 106:
		    if (ip != 3)
			state = 109;
		    else {
			int ix2 = iw + ido;
			if (na != 0)
			    dradb3(ido, l1, ch, c, wa, index + iw - 1, wa,
				   index + ix2 - 1);
			else
			    dradb3(ido, l1, c, ch, wa, index + iw - 1, wa,
				   index + ix2 - 1);
			na = 1 - na;
			state = 115;
		    }
		    break;
		case 109:
		    if (na != 0)
			dradbg(ido, ip, l1, idl1, ch, ch, ch, c, c, wa,
			       index + iw - 1);
		    else
			dradbg(ido, ip, l1, idl1, c, c, c, ch, ch, wa,
			       index + iw - 1);
		    if (ido == 1)
			na = 1 - na;
		    /* fall through */
		case 115:
		    l1 = l2;
		    iw += (ip - 1) * ido;
		    break while_7_;
		}
	    }
	}
	if (na != 0) {
	    for (int i = 0; i < n; i++)
		c[i] = ch[i];
	}
    }
}
