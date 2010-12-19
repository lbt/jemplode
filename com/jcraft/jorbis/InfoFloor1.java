/* InfoFloor1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jorbis;

class InfoFloor1
{
    static final int VIF_POSIT = 63;
    static final int VIF_CLASS = 16;
    static final int VIF_PARTS = 31;
    int partitions;
    int[] partitionclass = new int[31];
    int[] class_dim = new int[16];
    int[] class_subs = new int[16];
    int[] class_book = new int[16];
    int[][] class_subbook = new int[16][];
    int mult;
    int[] postlist = new int[65];
    float maxover;
    float maxunder;
    float maxerr;
    int twofitminsize;
    int twofitminused;
    int twofitweight;
    float twofitatten;
    int unusedminsize;
    int unusedmin_n;
    int n;
    
    InfoFloor1() {
	for (int i = 0; i < class_subbook.length; i++)
	    class_subbook[i] = new int[8];
    }
    
    void free() {
	partitionclass = null;
	class_dim = null;
	class_subs = null;
	class_book = null;
	class_subbook = null;
	postlist = null;
    }
    
    Object copy_info() {
	InfoFloor1 info = this;
	InfoFloor1 ret = new InfoFloor1();
	ret.partitions = info.partitions;
	System.arraycopy(info.partitionclass, 0, ret.partitionclass, 0, 31);
	System.arraycopy(info.class_dim, 0, ret.class_dim, 0, 16);
	System.arraycopy(info.class_subs, 0, ret.class_subs, 0, 16);
	System.arraycopy(info.class_book, 0, ret.class_book, 0, 16);
	for (int j = 0; j < 16; j++)
	    System.arraycopy(info.class_subbook[j], 0, ret.class_subbook[j], 0,
			     8);
	ret.mult = info.mult;
	System.arraycopy(info.postlist, 0, ret.postlist, 0, 65);
	ret.maxover = info.maxover;
	ret.maxunder = info.maxunder;
	ret.maxerr = info.maxerr;
	ret.twofitminsize = info.twofitminsize;
	ret.twofitminused = info.twofitminused;
	ret.twofitweight = info.twofitweight;
	ret.twofitatten = info.twofitatten;
	ret.unusedminsize = info.unusedminsize;
	ret.unusedmin_n = info.unusedmin_n;
	ret.n = info.n;
	return ret;
    }
}
