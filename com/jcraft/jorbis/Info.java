/* Info - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;
import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;

public class Info
{
    private static final int OV_EBADPACKET = -136;
    private static final int OV_ENOTAUDIO = -135;
    private static byte[] _vorbis = "vorbis".getBytes();
    private static final int VI_TIMEB = 1;
    private static final int VI_FLOORB = 2;
    private static final int VI_RESB = 3;
    private static final int VI_MAPB = 1;
    private static final int VI_WINDOWB = 1;
    public int version;
    public int channels;
    public int rate;
    public int bitrate_upper;
    public int bitrate_nominal;
    public int bitrate_lower;
    int[] blocksizes = new int[2];
    int modes;
    int maps;
    int times;
    int floors;
    int residues;
    int books;
    int psys;
    InfoMode[] mode_param = null;
    int[] map_type = null;
    Object[] map_param = null;
    int[] time_type = null;
    Object[] time_param = null;
    int[] floor_type = null;
    Object[] floor_param = null;
    int[] residue_type = null;
    Object[] residue_param = null;
    StaticCodeBook[] book_param = null;
    PsyInfo[] psy_param = new PsyInfo[64];
    int envelopesa;
    float preecho_thresh;
    float preecho_clamp;
    
    public void init() {
	rate = 0;
    }
    
    public void clear() {
	for (int i = 0; i < modes; i++)
	    mode_param[i] = null;
	mode_param = null;
	for (int i = 0; i < maps; i++)
	    FuncMapping.mapping_P[map_type[i]].free_info(map_param[i]);
	map_param = null;
	for (int i = 0; i < times; i++)
	    FuncTime.time_P[time_type[i]].free_info(time_param[i]);
	time_param = null;
	for (int i = 0; i < floors; i++)
	    FuncFloor.floor_P[floor_type[i]].free_info(floor_param[i]);
	floor_param = null;
	for (int i = 0; i < residues; i++)
	    FuncResidue.residue_P[residue_type[i]].free_info(residue_param[i]);
	residue_param = null;
	for (int i = 0; i < books; i++) {
	    if (book_param[i] != null) {
		book_param[i].clear();
		book_param[i] = null;
	    }
	}
	book_param = null;
	for (int i = 0; i < psys; i++)
	    psy_param[i].free();
    }
    
    int unpack_info(Buffer opb) {
	version = opb.read(32);
	if (version != 0)
	    return -1;
	channels = opb.read(8);
	rate = opb.read(32);
	bitrate_upper = opb.read(32);
	bitrate_nominal = opb.read(32);
	bitrate_lower = opb.read(32);
	blocksizes[0] = 1 << opb.read(4);
	blocksizes[1] = 1 << opb.read(4);
	if (rate < 1 || channels < 1 || blocksizes[0] < 8
	    || blocksizes[1] < blocksizes[0] || opb.read(1) != 1) {
	    clear();
	    return -1;
	}
	return 0;
    }
    
    public int getBitrate() {
	int bitrate;
	if (bitrate_nominal > 0)
	    bitrate = bitrate_nominal;
	else if (bitrate_upper > 0) {
	    if (bitrate_upper > 0)
		bitrate = (bitrate_lower + bitrate_upper) / 2;
	    else
		bitrate = bitrate_upper;
	} else
	    bitrate = 0;
	return bitrate;
    }
    
    int unpack_books(Buffer opb) {
	books = opb.read(8) + 1;
	if (book_param == null || book_param.length != books)
	    book_param = new StaticCodeBook[books];
	for (int i = 0; i < books; i++) {
	    book_param[i] = new StaticCodeBook();
	    if (book_param[i].unpack(opb) != 0) {
		clear();
		return -1;
	    }
	}
	times = opb.read(6) + 1;
	if (time_type == null || time_type.length != times)
	    time_type = new int[times];
	if (time_param == null || time_param.length != times)
	    time_param = new Object[times];
	for (int i = 0; i < times; i++) {
	    time_type[i] = opb.read(16);
	    if (time_type[i] < 0 || time_type[i] >= 1) {
		clear();
		return -1;
	    }
	    time_param[i] = FuncTime.time_P[time_type[i]].unpack(this, opb);
	    if (time_param[i] == null) {
		clear();
		return -1;
	    }
	}
	floors = opb.read(6) + 1;
	if (floor_type == null || floor_type.length != floors)
	    floor_type = new int[floors];
	if (floor_param == null || floor_param.length != floors)
	    floor_param = new Object[floors];
	for (int i = 0; i < floors; i++) {
	    floor_type[i] = opb.read(16);
	    if (floor_type[i] < 0 || floor_type[i] >= 2) {
		clear();
		return -1;
	    }
	    floor_param[i]
		= FuncFloor.floor_P[floor_type[i]].unpack(this, opb);
	    if (floor_param[i] == null) {
		clear();
		return -1;
	    }
	}
	residues = opb.read(6) + 1;
	if (residue_type == null || residue_type.length != residues)
	    residue_type = new int[residues];
	if (residue_param == null || residue_param.length != residues)
	    residue_param = new Object[residues];
	for (int i = 0; i < residues; i++) {
	    residue_type[i] = opb.read(16);
	    if (residue_type[i] < 0 || residue_type[i] >= 3) {
		clear();
		return -1;
	    }
	    residue_param[i]
		= FuncResidue.residue_P[residue_type[i]].unpack(this, opb);
	    if (residue_param[i] == null) {
		clear();
		return -1;
	    }
	}
	maps = opb.read(6) + 1;
	if (map_type == null || map_type.length != maps)
	    map_type = new int[maps];
	if (map_param == null || map_param.length != maps)
	    map_param = new Object[maps];
	for (int i = 0; i < maps; i++) {
	    map_type[i] = opb.read(16);
	    if (map_type[i] < 0 || map_type[i] >= 1) {
		clear();
		return -1;
	    }
	    map_param[i]
		= FuncMapping.mapping_P[map_type[i]].unpack(this, opb);
	    if (map_param[i] == null) {
		clear();
		return -1;
	    }
	}
	modes = opb.read(6) + 1;
	if (mode_param == null || mode_param.length != modes)
	    mode_param = new InfoMode[modes];
	for (int i = 0; i < modes; i++) {
	    mode_param[i] = new InfoMode();
	    mode_param[i].blockflag = opb.read(1);
	    mode_param[i].windowtype = opb.read(16);
	    mode_param[i].transformtype = opb.read(16);
	    mode_param[i].mapping = opb.read(8);
	    if (mode_param[i].windowtype >= 1
		|| mode_param[i].transformtype >= 1
		|| mode_param[i].mapping >= maps) {
		clear();
		return -1;
	    }
	}
	if (opb.read(1) != 1) {
	    clear();
	    return -1;
	}
	return 0;
    }
    
    public int synthesis_headerin(Comment vc, Packet op) {
	Buffer opb = new Buffer();
	if (op != null) {
	    opb.readinit(op.packet_base, op.packet, op.bytes);
	    byte[] buffer = new byte[6];
	    int packtype = opb.read(8);
	    opb.read(buffer, 6);
	    if (buffer[0] != 118 || buffer[1] != 111 || buffer[2] != 114
		|| buffer[3] != 98 || buffer[4] != 105 || buffer[5] != 115)
		return -1;
	    switch (packtype) {
	    case 1:
		if (op.b_o_s == 0)
		    return -1;
		if (rate != 0)
		    return -1;
		return unpack_info(opb);
	    case 3:
		if (rate == 0)
		    return -1;
		return vc.unpack(opb);
	    case 5:
		if (rate == 0 || vc.vendor == null)
		    return -1;
		return unpack_books(opb);
	    }
	}
	return -1;
    }
    
    int pack_info(Buffer opb) {
	opb.write(1, 8);
	opb.write(_vorbis);
	opb.write(0, 32);
	opb.write(channels, 8);
	opb.write(rate, 32);
	opb.write(bitrate_upper, 32);
	opb.write(bitrate_nominal, 32);
	opb.write(bitrate_lower, 32);
	opb.write(ilog2(blocksizes[0]), 4);
	opb.write(ilog2(blocksizes[1]), 4);
	opb.write(1, 1);
	return 0;
    }
    
    int pack_books(Buffer opb) {
	opb.write(5, 8);
	opb.write(_vorbis);
	opb.write(books - 1, 8);
	for (int i = 0; i < books; i++) {
	    if (book_param[i].pack(opb) != 0)
		return -1;
	}
	opb.write(times - 1, 6);
	for (int i = 0; i < times; i++) {
	    opb.write(time_type[i], 16);
	    FuncTime.time_P[time_type[i]].pack(time_param[i], opb);
	}
	opb.write(floors - 1, 6);
	for (int i = 0; i < floors; i++) {
	    opb.write(floor_type[i], 16);
	    FuncFloor.floor_P[floor_type[i]].pack(floor_param[i], opb);
	}
	opb.write(residues - 1, 6);
	for (int i = 0; i < residues; i++) {
	    opb.write(residue_type[i], 16);
	    FuncResidue.residue_P[residue_type[i]].pack(residue_param[i], opb);
	}
	opb.write(maps - 1, 6);
	for (int i = 0; i < maps; i++) {
	    opb.write(map_type[i], 16);
	    FuncMapping.mapping_P[map_type[i]].pack(this, map_param[i], opb);
	}
	opb.write(modes - 1, 6);
	for (int i = 0; i < modes; i++) {
	    opb.write(mode_param[i].blockflag, 1);
	    opb.write(mode_param[i].windowtype, 16);
	    opb.write(mode_param[i].transformtype, 16);
	    opb.write(mode_param[i].mapping, 8);
	}
	opb.write(1, 1);
	return 0;
    }
    
    public int blocksize(Packet op) {
	Buffer opb = new Buffer();
	opb.readinit(op.packet_base, op.packet, op.bytes);
	if (opb.read(1) != 0)
	    return -135;
	int modebits = 0;
	for (int v = modes; v > 1; v >>>= 1)
	    modebits++;
	int mode = opb.read(modebits);
	if (mode == -1)
	    return -136;
	return blocksizes[mode_param[mode].blockflag];
    }
    
    private static int ilog2(int v) {
	int ret = 0;
	for (/**/; v > 1; v >>>= 1)
	    ret++;
	return ret;
    }
    
    public String toString() {
	return ("version:" + new Integer(version) + ", channels:"
		+ new Integer(channels) + ", rate:" + new Integer(rate)
		+ ", bitrate:" + new Integer(bitrate_upper) + ","
		+ new Integer(bitrate_nominal) + ","
		+ new Integer(bitrate_lower));
    }
}
