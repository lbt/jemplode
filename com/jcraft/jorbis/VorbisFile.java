/* VorbisFile - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import java.io.File;
import java.io.InputStream;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.io.SeekableInputStream;
import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;

public class VorbisFile
{
    static final int CHUNKSIZE = 8500;
    static final int SEEK_SET = 0;
    static final int SEEK_CUR = 1;
    static final int SEEK_END = 2;
    static final int OV_FALSE = -1;
    static final int OV_EOF = -2;
    static final int OV_HOLE = -3;
    static final int OV_EREAD = -128;
    static final int OV_EFAULT = -129;
    static final int OV_EIMPL = -130;
    static final int OV_EINVAL = -131;
    static final int OV_ENOTVORBIS = -132;
    static final int OV_EBADHEADER = -133;
    static final int OV_EVERSION = -134;
    static final int OV_ENOTAUDIO = -135;
    static final int OV_EBADPACKET = -136;
    static final int OV_EBADLINK = -137;
    static final int OV_ENOSEEK = -138;
    InputStream datasource;
    boolean seekable = false;
    long offset;
    long end;
    SyncState oy;
    int links;
    long[] offsets;
    long[] dataoffsets;
    int[] serialnos;
    long[] pcmlengths;
    Info[] vi;
    Comment[] vc;
    long pcm_offset;
    boolean decode_ready;
    int current_serialno;
    int current_link;
    float bittrack;
    float samptrack;
    StreamState os;
    DspState vd;
    Block vb;
    
    public VorbisFile(String file) throws JOrbisException {
	oy = new SyncState();
	decode_ready = false;
	os = new StreamState();
	vd = new DspState();
	vb = new Block(vd);
	InputStream is = null;
	try {
	    is = new FileSeekableInputStream(new File(file));
	} catch (Exception e) {
	    throw new JOrbisException("VorbisFile: " + e.toString());
	}
	int ret = open(is, null, 0);
	if (ret == -1)
	    throw new JOrbisException("VorbisFile: open return -1");
    }
    
    public VorbisFile(InputStream is, byte[] initial, int ibytes)
	throws JOrbisException {
	oy = new SyncState();
	decode_ready = false;
	os = new StreamState();
	vd = new DspState();
	vb = new Block(vd);
	int ret = open(is, initial, ibytes);
    }
    
    private int get_data() {
	int index = oy.buffer(8500);
	byte[] buffer = oy.data;
	int bytes = 0;
	try {
	    bytes = datasource.read(buffer, index, 8500);
	} catch (Exception e) {
	    System.err.println(e);
	    return -128;
	}
	oy.wrote(bytes);
	if (bytes == -1)
	    bytes = 0;
	return bytes;
    }
    
    private void seek_helper(long offst) {
	fseek(datasource, offst, 0);
	offset = offst;
	oy.reset();
    }
    
    private int get_next_page(Page page, long boundary) {
	if (boundary > 0L)
	    boundary += offset;
	for (;;) {
	    if (boundary > 0L && offset >= boundary)
		return -1;
	    int more = oy.pageseek(page);
	    if (more < 0)
		offset -= (long) more;
	    else if (more == 0) {
		if (boundary == 0L)
		    return -1;
		int ret = get_data();
		if (ret == 0)
		    return -2;
		if (ret < 0)
		    return -128;
	    } else {
		int ret = (int) offset;
		offset += (long) more;
		return ret;
	    }
	}
    }
    
    private int get_prev_page(Page page) {
	long begin = offset;
	int offst = -1;
	while (offst == -1) {
	    begin -= 8500L;
	    if (begin < 0L)
		begin = 0L;
	    seek_helper(begin);
	    while (offset < begin + 8500L) {
		int ret = get_next_page(page, begin + 8500L - offset);
		if (ret == -128)
		    return -128;
		if (ret < 0)
		    break;
		offst = ret;
	    }
	}
	seek_helper((long) offst);
	int ret = get_next_page(page, 8500L);
	if (ret < 0)
	    return -129;
	return offst;
    }
    
    int bisect_forward_serialno(long begin, long searched, long end,
				int currentno, int m) {
	long endsearched = end;
	long next = end;
	Page page = new Page();
	while (searched < endsearched) {
	    long bisect;
	    if (endsearched - searched < 8500L)
		bisect = searched;
	    else
		bisect = (searched + endsearched) / 2L;
	    seek_helper(bisect);
	    int ret = get_next_page(page, -1L);
	    if (ret == -128)
		return -128;
	    if (ret < 0 || page.serialno() != currentno) {
		endsearched = bisect;
		if (ret >= 0)
		    next = (long) ret;
	    } else
		searched = (long) (ret + page.header_len + page.body_len);
	}
	seek_helper(next);
	int ret = get_next_page(page, -1L);
	if (ret == -128)
	    return -128;
	if (searched >= end || ret == -1) {
	    links = m + 1;
	    offsets = new long[m + 2];
	    offsets[m + 1] = searched;
	} else {
	    ret = bisect_forward_serialno(next, offset, end, page.serialno(),
					  m + 1);
	    if (ret == -128)
		return -128;
	}
	offsets[m] = begin;
	return 0;
    }
    
    int fetch_headers(Info vi, Comment vc, int[] serialno, Page og_ptr) {
	Page og = new Page();
	Packet op = new Packet();
	if (og_ptr == null) {
	    int ret = get_next_page(og, 8500L);
	    if (ret == -128)
		return -128;
	    if (ret < 0)
		return -132;
	    og_ptr = og;
	}
	if (serialno != null)
	    serialno[0] = og_ptr.serialno();
	os.init(og_ptr.serialno());
	vi.init();
	vc.init();
	int i = 0;
	while (i < 3) {
	    os.pagein(og_ptr);
	    for (/**/; i < 3; i++) {
		int result = os.packetout(op);
		if (result == 0)
		    break;
		if (result == -1) {
		    System.err.println("Corrupt header in logical bitstream.");
		    vi.clear();
		    vc.clear();
		    os.clear();
		    return -1;
		}
		if (vi.synthesis_headerin(vc, op) != 0) {
		    System.err.println("Illegal header in logical bitstream.");
		    vi.clear();
		    vc.clear();
		    os.clear();
		    return -1;
		}
	    }
	    if (i < 3 && get_next_page(og_ptr, 1L) < 0) {
		System.err.println("Missing header in logical bitstream.");
		vi.clear();
		vc.clear();
		os.clear();
		return -1;
	    }
	}
	return 0;
    }
    
    void prefetch_all_headers(Info first_i, Comment first_c, int dataoffset) {
	Page og = new Page();
	vi = new Info[links];
	vc = new Comment[links];
	dataoffsets = new long[links];
	pcmlengths = new long[links];
	serialnos = new int[links];
	for (int i = 0; i < links; i++) {
	    if (first_i != null && first_c != null && i == 0) {
		vi[i] = first_i;
		vc[i] = first_c;
		dataoffsets[i] = (long) dataoffset;
	    } else {
		seek_helper(offsets[i]);
		if (fetch_headers(vi[i], vc[i], null, null) == -1) {
		    System.err.println("Error opening logical bitstream #"
				       + (i + 1) + "\n");
		    dataoffsets[i] = -1L;
		} else {
		    dataoffsets[i] = offset;
		    os.clear();
		}
	    }
	    long end = offsets[i + 1];
	    seek_helper(end);
	    for (;;) {
		int ret = get_prev_page(og);
		if (ret == -1) {
		    System.err.println
			("Could not find last page of logical bitstream #" + i
			 + "\n");
		    vi[i].clear();
		    vc[i].clear();
		    break;
		}
		if (og.granulepos() != -1L) {
		    serialnos[i] = og.serialno();
		    pcmlengths[i] = og.granulepos();
		    break;
		}
	    }
	}
    }
    
    int make_decode_ready() {
	if (decode_ready)
	    System.exit(1);
	vd.synthesis_init(vi[0]);
	vb.init(vd);
	decode_ready = true;
	return 0;
    }
    
    int open_seekable() {
	Info initial_i = new Info();
	Comment initial_c = new Comment();
	Page og = new Page();
	int[] foo = new int[1];
	int ret = fetch_headers(initial_i, initial_c, foo, null);
	int serialno = foo[0];
	int dataoffset = (int) offset;
	os.clear();
	if (ret == -1)
	    return -1;
	seekable = true;
	fseek(datasource, 0L, 2);
	offset = ftell(datasource);
	long end = offset;
	end = (long) get_prev_page(og);
	if (og.serialno() != serialno) {
	    if (bisect_forward_serialno(0L, 0L, end + 1L, serialno, 0) < 0) {
		clear();
		return -128;
	    }
	} else if (bisect_forward_serialno(0L, end, end + 1L, serialno, 0)
		   < 0) {
	    clear();
	    return -128;
	}
	prefetch_all_headers(initial_i, initial_c, dataoffset);
	return raw_seek(0);
    }
    
    int open_nonseekable() {
	links = 1;
	vi = new Info[links];
	vi[0] = new Info();
	vc = new Comment[links];
	vc[0] = new Comment();
	int[] foo = new int[1];
	if (fetch_headers(vi[0], vc[0], foo, null) == -1)
	    return -1;
	current_serialno = foo[0];
	make_decode_ready();
	return 0;
    }
    
    void decode_clear() {
	os.clear();
	vd.clear();
	vb.clear();
	decode_ready = false;
	bittrack = 0.0F;
	samptrack = 0.0F;
    }
    
    int process_packet(int readp) {
	Page og = new Page();
	for (;;) {
	    if (decode_ready) {
		Packet op = new Packet();
		int result = os.packetout(op);
		if (result > 0) {
		    long granulepos = op.granulepos;
		    if (vb.synthesis(op) == 0) {
			int oldsamples = vd.synthesis_pcmout(null, null);
			vd.synthesis_blockin(vb);
			samptrack += (float) (vd.synthesis_pcmout(null, null)
					      - oldsamples);
			bittrack += (float) (op.bytes * 8);
			if (granulepos != -1L && op.e_o_s == 0) {
			    int link = seekable ? current_link : 0;
			    int samples = vd.synthesis_pcmout(null, null);
			    granulepos -= (long) samples;
			    for (int i = 0; i < link; i++)
				granulepos += pcmlengths[i];
			    pcm_offset = granulepos;
			}
			return 1;
		    }
		}
	    }
	    if (readp == 0)
		return 0;
	    if (get_next_page(og, -1L) < 0)
		return 0;
	    bittrack += (float) (og.header_len * 8);
	    if (decode_ready && current_serialno != og.serialno())
		decode_clear();
	    if (!decode_ready) {
		if (seekable) {
		    current_serialno = og.serialno();
		    int i;
		    for (i = 0; i < links; i++) {
			if (serialnos[i] == current_serialno)
			    break;
		    }
		    if (i == links)
			return -1;
		    current_link = i;
		    os.init(current_serialno);
		    os.reset();
		} else {
		    int[] foo = new int[1];
		    int ret = fetch_headers(vi[0], vc[0], foo, og);
		    current_serialno = foo[0];
		    if (ret != 0)
			return ret;
		    current_link++;
		    int i = 0;
		}
		make_decode_ready();
	    }
	    os.pagein(og);
	}
    }
    
    int clear() {
	vb.clear();
	vd.clear();
	os.clear();
	if (vi != null && links != 0) {
	    for (int i = 0; i < links; i++) {
		vi[i].clear();
		vc[i].clear();
	    }
	    vi = null;
	    vc = null;
	}
	if (dataoffsets != null)
	    dataoffsets = null;
	if (pcmlengths != null)
	    pcmlengths = null;
	if (serialnos != null)
	    serialnos = null;
	if (offsets != null)
	    offsets = null;
	oy.clear();
	return 0;
    }
    
    static int fseek(InputStream fis, long off, int whence) {
	if (fis instanceof SeekableInputStream) {
	    SeekableInputStream sis = (SeekableInputStream) fis;
	    try {
		if (whence == 0)
		    sis.seek(off);
		else if (whence == 2)
		    sis.seek(sis.length() - off);
		else
		    System.out
			.println("seek: " + whence + " is not supported");
	    } catch (Exception exception) {
		/* empty */
	    }
	    return 0;
	}
	try {
	    if (whence == 0)
		fis.reset();
	    fis.skip(off);
	} catch (Exception e) {
	    return -1;
	}
	return 0;
    }
    
    static long ftell(InputStream fis) {
	try {
	    if (fis instanceof SeekableInputStream) {
		SeekableInputStream sis = (SeekableInputStream) fis;
		return sis.tell();
	    }
	} catch (Exception exception) {
	    /* empty */
	}
	return 0L;
    }
    
    int open(InputStream is, byte[] initial, int ibytes) {
	return open_callbacks(is, initial, ibytes);
    }
    
    int open_callbacks(InputStream is, byte[] initial, int ibytes) {
	datasource = is;
	oy.init();
	if (initial != null) {
	    int index = oy.buffer(ibytes);
	    System.arraycopy(initial, 0, oy.data, index, ibytes);
	    oy.wrote(ibytes);
	}
	int ret;
	if (is instanceof SeekableInputStream)
	    ret = open_seekable();
	else
	    ret = open_nonseekable();
	if (ret != 0) {
	    datasource = null;
	    clear();
	}
	return ret;
    }
    
    public int streams() {
	return links;
    }
    
    public boolean seekable() {
	return seekable;
    }
    
    public int bitrate(int i) {
	if (i >= links)
	    return -1;
	if (!seekable && i != 0)
	    return bitrate(0);
	if (i < 0) {
	    long bits = 0L;
	    for (int j = 0; j < links; j++)
		bits += (offsets[j + 1] - dataoffsets[j]) * 8L;
	    return (int) Math.rint((double) ((float) bits / time_total(-1)));
	}
	if (seekable)
	    return (int) Math.rint((double) ((float) ((offsets[i + 1]
						       - dataoffsets[i])
						      * 8L)
					     / time_total(i)));
	if (vi[i].bitrate_nominal > 0)
	    return vi[i].bitrate_nominal;
	if (vi[i].bitrate_upper > 0) {
	    if (vi[i].bitrate_lower > 0)
		return (vi[i].bitrate_upper + vi[i].bitrate_lower) / 2;
	    return vi[i].bitrate_upper;
	}
	return -1;
    }
    
    public int bitrate_instant() {
	int _link = seekable ? current_link : 0;
	if (samptrack == 0.0F)
	    return -1;
	int ret
	    = (int) ((double) (bittrack / samptrack * (float) vi[_link].rate)
		     + 0.5);
	bittrack = 0.0F;
	samptrack = 0.0F;
	return ret;
    }
    
    public int serialnumber(int i) {
	if (i >= links)
	    return -1;
	if (!seekable && i >= 0)
	    return serialnumber(-1);
	if (i < 0)
	    return current_serialno;
	return serialnos[i];
    }
    
    public long raw_total(int i) {
	if (!seekable || i >= links)
	    return -1L;
	if (i < 0) {
	    long acc = 0L;
	    for (int j = 0; j < links; j++)
		acc += raw_total(j);
	    return acc;
	}
	return offsets[i + 1] - offsets[i];
    }
    
    public long pcm_total(int i) {
	if (i < 0) {
	    long acc = 0L;
	    for (int j = 0; j < links; j++)
		acc += pcm_total(j);
	    return acc;
	}
	return pcmlengths[i];
    }
    
    public float time_total(int i) {
	if (i < 0) {
	    float acc = 0.0F;
	    for (int j = 0; j < links; j++)
		acc += time_total(j);
	    return acc;
	}
	return (float) pcmlengths[i] / (float) vi[i].rate;
    }
    
    public int raw_seek(int pos) {
	if (!seekable)
	    return -1;
	if (pos < 0 || (long) pos > offsets[links]) {
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	}
	pcm_offset = -1L;
	decode_clear();
	seek_helper((long) pos);
	switch (process_packet(1)) {
	case 0:
	    pcm_offset = pcm_total(-1);
	    return 0;
	case -1:
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	default:
	    for (;;) {
		switch (process_packet(0)) {
		case 0:
		    return 0;
		case -1:
		    pcm_offset = -1L;
		    decode_clear();
		    return -1;
		}
	    }
	}
    }
    
    public int pcm_seek(long pos) {
	int link = -1;
	long total = pcm_total(-1);
	if (!seekable)
	    return -1;
	if (pos < 0L || pos > total) {
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	}
	for (link = links - 1; link >= 0; link--) {
	    total -= pcmlengths[link];
	    if (pos >= total)
		break;
	}
	long target = pos - total;
	long end = offsets[link + 1];
	long begin = offsets[link];
	int best = (int) begin;
	Page og = new Page();
	while (begin < end) {
	    long bisect;
	    if (end - begin < 8500L)
		bisect = begin;
	    else
		bisect = (end + begin) / 2L;
	    seek_helper(bisect);
	    int ret = get_next_page(og, end - bisect);
	    if (ret == -1)
		end = bisect;
	    else {
		long granulepos = og.granulepos();
		if (granulepos < target) {
		    best = ret;
		    begin = offset;
		} else
		    end = bisect;
	    }
	}
	if (raw_seek(best) != 0) {
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	}
	if (pcm_offset >= pos) {
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	}
	if (pos > pcm_total(-1)) {
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	}
	while (pcm_offset < pos) {
	    int target_0_ = (int) (pos - pcm_offset);
	    float[][][] _pcm = new float[1][][];
	    int[] _index = new int[getInfo(-1).channels];
	    int samples = vd.synthesis_pcmout(_pcm, _index);
	    if (samples > target_0_)
		samples = target_0_;
	    vd.synthesis_read(samples);
	    pcm_offset += (long) samples;
	    if (samples < target_0_ && process_packet(1) == 0)
		pcm_offset = pcm_total(-1);
	}
	return 0;
    }
    
    int time_seek(float seconds) {
	int link = -1;
	long pcm_total = pcm_total(-1);
	float time_total = time_total(-1);
	if (!seekable)
	    return -1;
	if (seconds < 0.0F || seconds > time_total) {
	    pcm_offset = -1L;
	    decode_clear();
	    return -1;
	}
	for (link = links - 1; link >= 0; link--) {
	    pcm_total -= pcmlengths[link];
	    time_total -= time_total(link);
	    if (seconds >= time_total)
		break;
	}
	long target
	    = (long) ((float) pcm_total
		      + (seconds - time_total) * (float) vi[link].rate);
	return pcm_seek(target);
    }
    
    public long raw_tell() {
	return offset;
    }
    
    public long pcm_tell() {
	return pcm_offset;
    }
    
    public float time_tell() {
	int link = -1;
	long pcm_total = 0L;
	float time_total = 0.0F;
	if (seekable) {
	    pcm_total = pcm_total(-1);
	    time_total = time_total(-1);
	    for (link = links - 1; link >= 0; link--) {
		pcm_total -= pcmlengths[link];
		time_total -= time_total(link);
		if (pcm_offset >= pcm_total)
		    break;
	    }
	}
	return (time_total
		+ (float) (pcm_offset - pcm_total) / (float) vi[link].rate);
    }
    
    public Info getInfo(int link) {
	if (seekable) {
	    if (link < 0) {
		if (decode_ready)
		    return vi[current_link];
		return null;
	    }
	    if (link >= links)
		return null;
	    return vi[link];
	}
	if (decode_ready)
	    return vi[0];
	return null;
    }
    
    public Comment getComment(int link) {
	if (seekable) {
	    if (link < 0) {
		if (decode_ready)
		    return vc[current_link];
		return null;
	    }
	    if (link >= links)
		return null;
	    return vc[link];
	}
	if (decode_ready)
	    return vc[0];
	return null;
    }
    
    int host_is_big_endian() {
	return 1;
    }
    
    int read(byte[] buffer, int length, int bigendianp, int word, int sgned,
	     int[] bitstream) {
	int host_endian = host_is_big_endian();
	int index = 0;
	for (;;) {
	    if (decode_ready) {
		float[][][] _pcm = new float[1][][];
		int[] _index = new int[getInfo(-1).channels];
		int samples = vd.synthesis_pcmout(_pcm, _index);
		float[][] pcm = _pcm[0];
		if (samples != 0) {
		    int channels = getInfo(-1).channels;
		    int bytespersample = word * channels;
		    if (samples > length / bytespersample)
			samples = length / bytespersample;
		    if (word == 1) {
			int off = sgned != 0 ? 0 : 128;
			for (int j = 0; j < samples; j++) {
			    for (int i = 0; i < channels; i++) {
				int val
				    = (int) (((double) pcm[i][_index[i] + j]
					      * 128.0)
					     + 0.5);
				if (val > 127)
				    val = 127;
				else if (val < -128)
				    val = -128;
				buffer[index++] = (byte) (val + off);
			    }
			}
		    } else {
			int off = sgned != 0 ? 0 : 32768;
			if (host_endian == bigendianp) {
			    if (sgned != 0) {
				for (int i = 0; i < channels; i++) {
				    int src = _index[i];
				    int dest = i;
				    for (int j = 0; j < samples; j++) {
					int val
					    = (int) (((double) pcm[i][src + j]
						      * 32768.0)
						     + 0.5);
					if (val > 32767)
					    val = 32767;
					else if (val < -32768)
					    val = -32768;
					buffer[dest] = (byte) (val >>> 8);
					buffer[dest + 1] = (byte) val;
					dest += channels * 2;
				    }
				}
			    } else {
				for (int i = 0; i < channels; i++) {
				    float[] src = pcm[i];
				    int dest = i;
				    for (int j = 0; j < samples; j++) {
					int val
					    = (int) ((double) src[j] * 32768.0
						     + 0.5);
					if (val > 32767)
					    val = 32767;
					else if (val < -32768)
					    val = -32768;
					buffer[dest]
					    = (byte) (val + off >>> 8);
					buffer[dest + 1] = (byte) (val + off);
					dest += channels * 2;
				    }
				}
			    }
			} else if (bigendianp != 0) {
			    for (int j = 0; j < samples; j++) {
				for (int i = 0; i < channels; i++) {
				    int val
					= (int) ((double) pcm[i][j] * 32768.0
						 + 0.5);
				    if (val > 32767)
					val = 32767;
				    else if (val < -32768)
					val = -32768;
				    val += off;
				    buffer[index++] = (byte) (val >>> 8);
				    buffer[index++] = (byte) val;
				}
			    }
			} else {
			    for (int j = 0; j < samples; j++) {
				for (int i = 0; i < channels; i++) {
				    int val
					= (int) ((double) pcm[i][j] * 32768.0
						 + 0.5);
				    if (val > 32767)
					val = 32767;
				    else if (val < -32768)
					val = -32768;
				    val += off;
				    buffer[index++] = (byte) val;
				    buffer[index++] = (byte) (val >>> 8);
				}
			    }
			}
		    }
		    vd.synthesis_read(samples);
		    pcm_offset += (long) samples;
		    if (bitstream != null)
			bitstream[0] = current_link;
		    return samples * bytespersample;
		}
	    }
	    switch (process_packet(1)) {
	    case 0:
		return 0;
	    case -1:
		return -1;
	    }
	}
    }
    
    public Info[] getInfo() {
	return vi;
    }
    
    public Comment[] getComment() {
	return vc;
    }
    
    public static void main(String[] arg) {
	try {
	    VorbisFile foo = new VorbisFile(arg[0]);
	    int links = foo.streams();
	    System.out.println("links=" + links);
	    Comment[] comment = foo.getComment();
	    Info[] info = foo.getInfo();
	    for (int i = 0; i < links; i++) {
		System.out.println(info[i]);
		System.out.println(comment[i]);
	    }
	    System.out.println("raw_total: " + foo.raw_total(-1));
	    System.out.println("pcm_total: " + foo.pcm_total(-1));
	    System.out.println("time_total: " + foo.time_total(-1));
	} catch (Exception e) {
	    System.err.println(e);
	}
    }
}
