/* DspState - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

public class DspState
{
    static final float M_PI = 3.1415927F;
    static final int VI_TRANSFORMB = 1;
    static final int VI_WINDOWB = 1;
    int analysisp;
    Info vi;
    int modebits;
    float[][] pcm;
    int pcm_storage;
    int pcm_current;
    int pcm_returned;
    float[] multipliers;
    int envelope_storage;
    int envelope_current;
    int eofflag;
    int lW;
    int W;
    int nW;
    int centerW;
    long granulepos;
    long sequence;
    long glue_bits;
    long time_bits;
    long floor_bits;
    long res_bits;
    float[][][][][] window;
    Object[][] transform = new Object[2][];
    CodeBook[] fullbooks;
    Object[] mode;
    byte[] header;
    byte[] header1;
    byte[] header2;
    
    public DspState() {
	window = new float[2][][][][];
	window[0] = new float[2][][][];
	window[0][0] = new float[2][][];
	window[0][1] = new float[2][][];
	window[0][0][0] = new float[2][];
	window[0][0][1] = new float[2][];
	window[0][1][0] = new float[2][];
	window[0][1][1] = new float[2][];
	window[1] = new float[2][][][];
	window[1][0] = new float[2][][];
	window[1][1] = new float[2][][];
	window[1][0][0] = new float[2][];
	window[1][0][1] = new float[2][];
	window[1][1][0] = new float[2][];
	window[1][1][1] = new float[2][];
    }
    
    private static int ilog2(int v) {
	int ret = 0;
	for (/**/; v > 1; v >>>= 1)
	    ret++;
	return ret;
    }
    
    static float[] window(int type, int window, int left, int right) {
	float[] ret = new float[window];
	switch (type) {
	case 0: {
	    int leftbegin = window / 4 - left / 2;
	    int rightbegin = window - window / 4 - right / 2;
	    for (int i = 0; i < left; i++) {
		float x = (float) (((double) i + 0.5) / (double) left
				   * 3.1415927410125732 / 2.0);
		x = (float) Math.sin((double) x);
		x *= x;
		x *= 1.5707963705062866;
		x = (float) Math.sin((double) x);
		ret[i + leftbegin] = x;
	    }
	    for (int i = leftbegin + left; i < rightbegin; i++)
		ret[i] = 1.0F;
	    for (int i = 0; i < right; i++) {
		float x
		    = (float) (((double) (right - i) - 0.5) / (double) right
			       * 3.1415927410125732 / 2.0);
		x = (float) Math.sin((double) x);
		x *= x;
		x *= 1.5707963705062866;
		x = (float) Math.sin((double) x);
		ret[i + rightbegin] = x;
	    }
	    break;
	}
	default:
	    return null;
	}
	return ret;
    }
    
    int init(Info vi, boolean encp) {
	this.vi = vi;
	modebits = ilog2(vi.modes);
	transform[0] = new Object[1];
	transform[1] = new Object[1];
	transform[0][0] = new Mdct();
	transform[1][0] = new Mdct();
	((Mdct) transform[0][0]).init(vi.blocksizes[0]);
	((Mdct) transform[1][0]).init(vi.blocksizes[1]);
	window[0][0][0] = new float[1][];
	window[0][0][1] = window[0][0][0];
	window[0][1][0] = window[0][0][0];
	window[0][1][1] = window[0][0][0];
	window[1][0][0] = new float[1][];
	window[1][0][1] = new float[1][];
	window[1][1][0] = new float[1][];
	window[1][1][1] = new float[1][];
	for (int i = 0; i < 1; i++) {
	    window[0][0][0][i]
		= window(i, vi.blocksizes[0], vi.blocksizes[0] / 2,
			 vi.blocksizes[0] / 2);
	    window[1][0][0][i]
		= window(i, vi.blocksizes[1], vi.blocksizes[0] / 2,
			 vi.blocksizes[0] / 2);
	    window[1][0][1][i]
		= window(i, vi.blocksizes[1], vi.blocksizes[0] / 2,
			 vi.blocksizes[1] / 2);
	    window[1][1][0][i]
		= window(i, vi.blocksizes[1], vi.blocksizes[1] / 2,
			 vi.blocksizes[0] / 2);
	    window[1][1][1][i]
		= window(i, vi.blocksizes[1], vi.blocksizes[1] / 2,
			 vi.blocksizes[1] / 2);
	}
	fullbooks = new CodeBook[vi.books];
	for (int i = 0; i < vi.books; i++) {
	    fullbooks[i] = new CodeBook();
	    fullbooks[i].init_decode(vi.book_param[i]);
	}
	pcm_storage = 8192;
	pcm = new float[vi.channels][];
	for (int i = 0; i < vi.channels; i++)
	    pcm[i] = new float[pcm_storage];
	lW = 0;
	W = 0;
	centerW = vi.blocksizes[1] / 2;
	pcm_current = centerW;
	mode = new Object[vi.modes];
	for (int i = 0; i < vi.modes; i++) {
	    int mapnum = vi.mode_param[i].mapping;
	    int maptype = vi.map_type[mapnum];
	    mode[i]
		= FuncMapping.mapping_P[maptype].look(this, vi.mode_param[i],
						      vi.map_param[mapnum]);
	}
	return 0;
    }
    
    public int synthesis_init(Info vi) {
	init(vi, false);
	pcm_returned = centerW;
	centerW -= vi.blocksizes[W] / 4 + vi.blocksizes[lW] / 4;
	granulepos = -1L;
	sequence = -1L;
	return 0;
    }
    
    DspState(Info vi) {
	this();
	init(vi, false);
	pcm_returned = centerW;
	centerW -= vi.blocksizes[W] / 4 + vi.blocksizes[lW] / 4;
	granulepos = -1L;
	sequence = -1L;
    }
    
    public int synthesis_blockin(Block vb) {
	if (centerW > vi.blocksizes[1] / 2 && pcm_returned > 8192) {
	    int shiftPCM = centerW - vi.blocksizes[1] / 2;
	    shiftPCM = pcm_returned < shiftPCM ? pcm_returned : shiftPCM;
	    pcm_current -= shiftPCM;
	    centerW -= shiftPCM;
	    pcm_returned -= shiftPCM;
	    if (shiftPCM != 0) {
		for (int i = 0; i < vi.channels; i++)
		    System.arraycopy(pcm[i], shiftPCM, pcm[i], 0, pcm_current);
	    }
	}
	lW = W;
	W = vb.W;
	nW = -1;
	glue_bits += (long) vb.glue_bits;
	time_bits += (long) vb.time_bits;
	floor_bits += (long) vb.floor_bits;
	res_bits += (long) vb.res_bits;
	if (sequence + 1L != vb.sequence)
	    granulepos = -1L;
	sequence = vb.sequence;
	int sizeW = vi.blocksizes[W];
	int _centerW = centerW + vi.blocksizes[lW] / 4 + sizeW / 4;
	int beginW = _centerW - sizeW / 2;
	int endW = beginW + sizeW;
	int beginSl = 0;
	int endSl = 0;
	if (endW > pcm_storage) {
	    pcm_storage = endW + vi.blocksizes[1];
	    for (int i = 0; i < vi.channels; i++) {
		float[] foo = new float[pcm_storage];
		System.arraycopy(pcm[i], 0, foo, 0, pcm[i].length);
		pcm[i] = foo;
	    }
	}
	switch (W) {
	case 0:
	    beginSl = 0;
	    endSl = vi.blocksizes[0] / 2;
	    break;
	case 1:
	    beginSl = vi.blocksizes[1] / 4 - vi.blocksizes[lW] / 4;
	    endSl = beginSl + vi.blocksizes[lW] / 2;
	    break;
	}
	for (int j = 0; j < vi.channels; j++) {
	    int _pcm = beginW;
	    int i = 0;
	    for (i = beginSl; i < endSl; i++)
		pcm[j][_pcm + i] += vb.pcm[j][i];
	    for (/**/; i < sizeW; i++)
		pcm[j][_pcm + i] = vb.pcm[j][i];
	}
	if (granulepos == -1L)
	    granulepos = vb.granulepos;
	else {
	    granulepos += (long) (_centerW - centerW);
	    if (vb.granulepos != -1L && granulepos != vb.granulepos) {
		if (granulepos > vb.granulepos && vb.eofflag != 0)
		    _centerW -= granulepos - vb.granulepos;
		granulepos = vb.granulepos;
	    }
	}
	centerW = _centerW;
	pcm_current = endW;
	if (vb.eofflag != 0)
	    eofflag = 1;
	return 0;
    }
    
    public int synthesis_pcmout(float[][][] _pcm, int[] index) {
	if (pcm_returned < centerW) {
	    if (_pcm != null) {
		for (int i = 0; i < vi.channels; i++)
		    index[i] = pcm_returned;
		_pcm[0] = pcm;
	    }
	    return centerW - pcm_returned;
	}
	return 0;
    }
    
    public int synthesis_read(int bytes) {
	if (bytes != 0 && pcm_returned + bytes > centerW)
	    return -1;
	pcm_returned += bytes;
	return 0;
    }
    
    public void clear() {
	/* empty */
    }
}
