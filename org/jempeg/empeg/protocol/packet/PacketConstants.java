/* PacketConstants - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol.packet;

public class PacketConstants
{
    public static final byte NAK_DROPOUT = 22;
    public static final byte TIMED_OUT = -62;
    public static final byte OPTYPE_REQUEST = 0;
    public static final byte OPTYPE_RESPONSE = 1;
    public static final byte OPTYPE_PROGRESS = 2;
    public static final byte OP_PING = 0;
    public static final byte OP_QUIT = 1;
    public static final byte OP_MOUNT = 2;
    public static final byte OP_WRITEFID = 4;
    public static final byte OP_READFID = 5;
    public static final byte OP_PREPAREFID = 6;
    public static final byte OP_STATFID = 7;
    public static final byte OP_DELETEFID = 8;
    public static final byte OP_REBUILD = 9;
    public static final byte OP_FSCK = 10;
    public static final byte OP_STATFS = 11;
    public static final byte OP_COMMAND = 12;
    public static final byte OP_GRABSCREEN = 13;
    public static final byte OP_INITIATESESSION = 14;
    public static final byte OP_SESSIONHEARTBEAT = 15;
    public static final byte OP_TERMINATESESSION = 16;
    public static final byte OP_COMPLETE = 17;
    public static final int USB_MAXPAYLOAD = 16384;
    public static final byte PSOH = 2;
}
