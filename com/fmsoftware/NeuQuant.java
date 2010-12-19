/* NeuQuant - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.fmsoftware;

public class NeuQuant
{
    protected static final int netsize = 256;
    protected static final int prime1 = 499;
    protected static final int prime2 = 491;
    protected static final int prime3 = 487;
    protected static final int prime4 = 503;
    protected static final int minpicturebytes = 1509;
    protected static final int maxnetpos = 255;
    protected static final int netbiasshift = 4;
    protected static final int ncycles = 100;
    protected static final int intbiasshift = 16;
    protected static final int intbias = 65536;
    protected static final int gammashift = 10;
    protected static final int gamma = 1024;
    protected static final int betashift = 10;
    protected static final int beta = 64;
    protected static final int betagamma = 65536;
    protected static final int initrad = 32;
    protected static final int radiusbiasshift = 6;
    protected static final int radiusbias = 64;
    protected static final int initradius = 2048;
    protected static final int radiusdec = 30;
    protected static final int alphabiasshift = 10;
    protected static final int initalpha = 1024;
    protected int alphadec;
    protected static final int radbiasshift = 8;
    protected static final int radbias = 256;
    protected static final int alpharadbshift = 18;
    protected static final int alpharadbias = 262144;
    protected byte[] thepicture;
    protected int lengthcount;
    protected int samplefac;
    protected int[][] network;
    protected int[] netindex = new int[256];
    protected int[] bias = new int[256];
    protected int[] freq = new int[256];
    protected int[] radpower = new int[32];
    
    public NeuQuant(byte[] thepic, int len, int sample) {
	thepicture = thepic;
	lengthcount = len;
	samplefac = sample;
	network = new int[256][];
	for (int i = 0; i < 256; i++) {
	    network[i] = new int[4];
	    int[] p = network[i];
	    p[0] = p[1] = p[2] = (i << 12) / 256;
	    freq[i] = 256;
	    bias[i] = 0;
	}
    }
    
    public byte[] colorMap() {
	byte[] map = new byte[768];
	int[] index = new int[256];
	for (int i = 0; i < 256; i++)
	    index[network[i][3]] = i;
	int k = 0;
	for (int i = 0; i < 256; i++) {
	    int j = index[i];
	    map[k++] = (byte) network[j][0];
	    map[k++] = (byte) network[j][1];
	    map[k++] = (byte) network[j][2];
	}
	return map;
    }
    
    public void inxbuild() {
	int previouscol = 0;
	int startpos = 0;
	for (int i = 0; i < 256; i++) {
	    int[] p = network[i];
	    int smallpos = i;
	    int smallval = p[1];
	    for (int j = i + 1; j < 256; j++) {
		int[] q = network[j];
		if (q[1] < smallval) {
		    smallpos = j;
		    smallval = q[1];
		}
	    }
	    int[] q = network[smallpos];
	    if (i != smallpos) {
		int j = q[0];
		q[0] = p[0];
		p[0] = j;
		j = q[1];
		q[1] = p[1];
		p[1] = j;
		j = q[2];
		q[2] = p[2];
		p[2] = j;
		j = q[3];
		q[3] = p[3];
		p[3] = j;
	    }
	    if (smallval != previouscol) {
		netindex[previouscol] = startpos + i >> 1;
		for (int j = previouscol + 1; j < smallval; j++)
		    netindex[j] = i;
		previouscol = smallval;
		startpos = i;
	    }
	}
	netindex[previouscol] = startpos + 255 >> 1;
	for (int j = previouscol + 1; j < 256; j++)
	    netindex[j] = 255;
    }
    
    public void learn() {
	if (lengthcount < 1509)
	    samplefac = 1;
	alphadec = 30 + (samplefac - 1) / 3;
	byte[] p = thepicture;
	int pix = 0;
	int lim = lengthcount;
	int samplepixels = lengthcount / (3 * samplefac);
	int delta = samplepixels / 100;
	int alpha = 1024;
	int radius = 2048;
	int rad = radius >> 6;
	if (rad <= 1)
	    rad = 0;
	for (int i = 0; i < rad; i++)
	    radpower[i] = alpha * ((rad * rad - i * i) * 256 / (rad * rad));
	int step;
	if (lengthcount < 1509)
	    step = 3;
	else if (lengthcount % 499 != 0)
	    step = 1497;
	else if (lengthcount % 491 != 0)
	    step = 1473;
	else if (lengthcount % 487 != 0)
	    step = 1461;
	else
	    step = 1509;
	int i = 0;
	while (i < samplepixels) {
	    int b = (p[pix + 0] & 0xff) << 4;
	    int g = (p[pix + 1] & 0xff) << 4;
	    int r = (p[pix + 2] & 0xff) << 4;
	    int j = contest(b, g, r);
	    altersingle(alpha, j, b, g, r);
	    if (rad != 0)
		alterneigh(rad, j, b, g, r);
	    pix += step;
	    if (pix >= lim)
		pix -= lengthcount;
	    i++;
	    if (delta == 0)
		delta = 1;
	    if (i % delta == 0) {
		alpha -= alpha / alphadec;
		radius -= radius / 30;
		rad = radius >> 6;
		if (rad <= 1)
		    rad = 0;
		for (j = 0; j < rad; j++)
		    radpower[j]
			= alpha * ((rad * rad - j * j) * 256 / (rad * rad));
	    }
	}
    }
    
    public int map(int b, int g, int r) {
	int bestd = 1000;
	int best = -1;
	int i = netindex[g];
	int j = i - 1;
	while (i < 256 || j >= 0) {
	    if (i < 256) {
		int[] p = network[i];
		int dist = p[1] - g;
		if (dist >= bestd)
		    i = 256;
		else {
		    i++;
		    if (dist < 0)
			dist = -dist;
		    int a = p[0] - b;
		    if (a < 0)
			a = -a;
		    dist += a;
		    if (dist < bestd) {
			a = p[2] - r;
			if (a < 0)
			    a = -a;
			dist += a;
			if (dist < bestd) {
			    bestd = dist;
			    best = p[3];
			}
		    }
		}
	    }
	    if (j >= 0) {
		int[] p = network[j];
		int dist = g - p[1];
		if (dist >= bestd)
		    j = -1;
		else {
		    j--;
		    if (dist < 0)
			dist = -dist;
		    int a = p[0] - b;
		    if (a < 0)
			a = -a;
		    dist += a;
		    if (dist < bestd) {
			a = p[2] - r;
			if (a < 0)
			    a = -a;
			dist += a;
			if (dist < bestd) {
			    bestd = dist;
			    best = p[3];
			}
		    }
		}
	    }
	}
	return best;
    }
    
    public byte[] process() {
	learn();
	unbiasnet();
	inxbuild();
	return colorMap();
    }
    
    public void unbiasnet() {
	for (int i = 0; i < 256; i++) {
	    network[i][0] >>= 4;
	    network[i][1] >>= 4;
	    network[i][2] >>= 4;
	    network[i][3] = i;
	}
    }
    
    protected void alterneigh(int rad, int i, int b, int g, int r) {
	int lo = i - rad;
	if (lo < -1)
	    lo = -1;
	int hi = i + rad;
	if (hi > 256)
	    hi = 256;
	int j = i + 1;
	int k = i - 1;
	int m = 1;
	while (j < hi || k > lo) {
	    int a = radpower[m++];
	    if (j < hi) {
		int[] p = network[j++];
		try {
		    p[0] -= a * (p[0] - b) / 262144;
		    p[1] -= a * (p[1] - g) / 262144;
		    p[2] -= a * (p[2] - r) / 262144;
		} catch (Exception exception) {
		    /* empty */
		}
	    }
	    if (k > lo) {
		int[] p = network[k--];
		try {
		    p[0] -= a * (p[0] - b) / 262144;
		    p[1] -= a * (p[1] - g) / 262144;
		    p[2] -= a * (p[2] - r) / 262144;
		} catch (Exception exception) {
		    /* empty */
		}
	    }
	}
    }
    
    protected void altersingle(int alpha, int i, int b, int g, int r) {
	int[] n = network[i];
	n[0] -= alpha * (n[0] - b) / 1024;
	n[1] -= alpha * (n[1] - g) / 1024;
	n[2] -= alpha * (n[2] - r) / 1024;
    }
    
    protected int contest(int b, int g, int r) {
	int bestd = 2147483647;
	int bestbiasd = bestd;
	int bestpos = -1;
	int bestbiaspos = bestpos;
	for (int i = 0; i < 256; i++) {
	    int[] n = network[i];
	    int dist = n[0] - b;
	    if (dist < 0)
		dist = -dist;
	    int a = n[1] - g;
	    if (a < 0)
		a = -a;
	    dist += a;
	    a = n[2] - r;
	    if (a < 0)
		a = -a;
	    dist += a;
	    if (dist < bestd) {
		bestd = dist;
		bestpos = i;
	    }
	    int biasdist = dist - (bias[i] >> 12);
	    if (biasdist < bestbiasd) {
		bestbiasd = biasdist;
		bestbiaspos = i;
	    }
	    int betafreq = freq[i] >> 10;
	    freq[i] -= betafreq;
	    bias[i] += betafreq << 10;
	}
	freq[bestpos] += 64;
	bias[bestpos] -= 65536;
	return bestbiaspos;
    }
}
