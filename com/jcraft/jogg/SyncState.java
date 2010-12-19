/* SyncState - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.jcraft.jogg;

public class SyncState
{
    public byte[] data;
    int storage;
    int fill;
    int returned;
    int unsynced;
    int headerbytes;
    int bodybytes;
    private Page pageseek = new Page();
    private byte[] chksum = new byte[4];
    
    public int clear() {
	data = null;
	return 0;
    }
    
    public int buffer(int size) {
	if (returned != 0) {
	    fill -= returned;
	    if (fill > 0)
		System.arraycopy(data, returned, data, 0, fill);
	    returned = 0;
	}
	if (size > storage - fill) {
	    int newsize = size + fill + 4096;
	    if (data != null) {
		byte[] foo = new byte[newsize];
		System.arraycopy(data, 0, foo, 0, data.length);
		data = foo;
	    } else
		data = new byte[newsize];
	    storage = newsize;
	}
	return fill;
    }
    
    public int wrote(int bytes) {
	if (fill + bytes > storage)
	    return -1;
	fill += bytes;
	return 0;
    }
    
    public int pageseek(Page og) {
	object = object_1_;
	break;
    }
    
    public int pageout(Page og) {
	for (;;) {
	    int ret = pageseek(og);
	    if (ret > 0)
		return 1;
	    if (ret == 0)
		return 0;
	    if (unsynced == 0) {
		unsynced = 1;
		return -1;
	    }
	}
    }
    
    public int reset() {
	fill = 0;
	returned = 0;
	unsynced = 0;
	headerbytes = 0;
	bodybytes = 0;
	return 0;
    }
    
    public void init() {
	/* empty */
    }
}
