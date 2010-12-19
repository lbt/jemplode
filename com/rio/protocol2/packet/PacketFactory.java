/* PacketFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.text.ResourceBundleKey;

public class PacketFactory
{
    public static AbstractReplyPacket createReplyPacket
	(PacketHeader _header) throws PacketException {
	int type = (int) _header.getType().getValue();
	AbstractReplyPacket replyPacket;
	switch (type) {
	case 0:
	    replyPacket = new GetVersionReplyPacket(_header);
	    break;
	case 1:
	    replyPacket = new NakReplyPacket(_header);
	    break;
	case 2:
	    replyPacket = new ProgressReplyPacket(_header);
	    break;
	case 3:
	    replyPacket = new Login1ReplyPacket(_header);
	    break;
	case 4:
	    replyPacket = new Login2ReplyPacket(_header);
	    break;
	case 5:
	    replyPacket = new GetDeviceInfoReplyPacket(_header);
	    break;
	case 6:
	    replyPacket = new GetStorageInfoReplyPacket(_header);
	    break;
	case 7:
	    replyPacket = new GetDeviceSettingsReplyPacket(_header);
	    break;
	case 8:
	    replyPacket = new ChangeDeviceSettingsReplyPacket(_header);
	    break;
	case 9:
	    replyPacket = new LockReplyPacket(_header);
	    break;
	case 10:
	    replyPacket = new UnlockReplyPacket(_header);
	    break;
	case 11:
	    replyPacket = new PrepareReplyPacket(_header);
	    break;
	case 12:
	    replyPacket = new WriteReplyPacket(_header);
	    break;
	case 13:
	    replyPacket = new GetAllFileInfoReplyPacket(_header);
	    break;
	case 14:
	    replyPacket = new GetFileInfoReplyPacket(_header);
	    break;
	case 15:
	    replyPacket = new ChangeFileInfoReplyPacket(_header);
	    break;
	case 16:
	    replyPacket = new ReadReplyPacket(_header);
	    break;
	case 17:
	    replyPacket = new DeleteReplyPacket(_header);
	    break;
	case 18:
	    replyPacket = new FormatReplyPacket(_header);
	    break;
	case 19:
	    replyPacket = new ByeReplyPacket(_header);
	    break;
	case 20:
	    replyPacket = new DeviceOperationReplyPacket(_header);
	    break;
	default:
	    throw new PacketException
		      (new ResourceBundleKey("errors",
					     "protocol.packet.unknownType",
					     new Object[] { _header }));
	}
	return replyPacket;
    }
}
