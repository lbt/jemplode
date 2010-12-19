/* Lpc - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class Lpc
{
    Drft fft = new Drft();
    int ln;
    int m;
    
    static float lpc_from_data(float[] data, float[] lpc, int n, int m) {
	float[] aut = new float[m + 1];
	int j = m + 1;
	while (j-- != 0) {
	    float d = 0.0F;
	    for (int i = j; i < n; i++)
		d += data[i] * data[i - j];
	    aut[j] = d;
	}
	float error = aut[0];
	for (int i = 0; i < m; i++) {
	    float r = -aut[i + 1];
	    if (error == 0.0F) {
		for (int k = 0; k < m; k++)
		    lpc[k] = 0.0F;
		return 0.0F;
	    }
	    for (j = 0; j < i; j++)
		r -= lpc[j] * aut[i - j];
	    r /= error;
	    lpc[i] = r;
	    for (j = 0; j < i / 2; j++) {
		float tmp = lpc[j];
		lpc[j] += r * lpc[i - 1 - j];
		lpc[i - 1 - j] += r * tmp;
	    }
	    if (i % 2 != 0)
		lpc[j] += lpc[j] * r;
	    error *= 1.0 - (double) (r * r);
	}
	return error;
    }
    
    float lpc_from_curve(float[] curve, float[] lpc) {
	int n = ln;
	float[] work = new float[n + n];
	float fscale = (float) (0.5 / (double) n);
	for (int i = 0; i < n; i++) {
	    work[i * 2] = curve[i] * fscale;
	    work[i * 2 + 1] = 0.0F;
	}
	work[n * 2 - 1] = curve[n - 1] * fscale;
	n *= 2;
	fft.backward(work);
	int i = 0;
	int j = n / 2;
	while (i < n / 2) {
	    float temp = work[i];
	    work[i++] = work[j];
	    work[j++] = temp;
	}
	return lpc_from_data(work, lpc, n, m);
    }
    
    void init(int mapped, int m) {
	ln = mapped;
	this.m = m;
	fft.init(mapped * 2);
    }
    
    void clear() {
	fft.clear();
    }
    
    static float FAST_HYPOT(float a, float b) {
	return (float) Math.sqrt((double) (a * a + b * b));
    }
    
    void lpc_to_curve(float[] curve, float[] lpc, float amp) {
	for (int i = 0; i < ln * 2; i++)
	    curve[i] = 0.0F;
	if (amp != 0.0F) {
	    for (int i = 0; i < m; i++) {
		curve[i * 2 + 1] = lpc[i] / (4.0F * amp);
		curve[i * 2 + 2] = -lpc[i] / (4.0F * amp);
	    }
	    fft.backward(curve);
	    int l2 = ln * 2;
	    float unit = (float) (1.0 / (double) amp);
	    curve[0] = (float) (1.0 / (double) (curve[0] * 2.0F + unit));
	    for (int i = 1; i < ln; i++) {
		float real = curve[i] + curve[l2 - i];
		float imag = curve[i] - curve[l2 - i];
		float a = real + unit;
		curve[i] = (float) (1.0 / (double) FAST_HYPOT(a, imag));
	    }
	}
    }
}
