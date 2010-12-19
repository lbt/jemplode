/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol.packet;

/**
* Constants definitions for use with packets.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class PacketConstants {
	public static final byte NAK_DROPOUT         = 22;
	public static final byte TIMED_OUT           = -62;
	
	public static final byte OPTYPE_REQUEST		   = 0;
	public static final byte OPTYPE_RESPONSE	   = 1;
	public static final byte OPTYPE_PROGRESS	   = 2;

	public static final byte OP_PING					   = 0;  // Ping: check remote presence
	public static final byte OP_QUIT					   = 1; 	// Quit: forces remote player to quit
	public static final byte OP_MOUNT	     		   = 2;	// Remount read-only or read-write
	public static final byte OP_WRITEFID			   = 4;	// Write to a FID
	public static final byte OP_READFID				   = 5;	// Read from a FID
	public static final byte OP_PREPAREFID		   = 6;	// Create an empty FID of a set length to write to
	public static final byte OP_STATFID				   = 7;  // Find out the length of a FID.
	public static final byte OP_DELETEFID			   = 8;	// Delete a FID
	public static final byte OP_REBUILD				   = 9;	// Rebuild databases (and save it if set r/w)
	public static final byte OP_FSCK					   = 10;	// fsck drives
	public static final byte OP_STATFS				   = 11;	// Stat a filing system
	public static final byte OP_COMMAND				   = 12;	// Send a generic command
	public static final byte OP_GRABSCREEN		   = 13;	// Grab a screen image
	public static final byte OP_INITIATESESSION  = 14;	// Obtain modification lock on player
	public static final byte OP_SESSIONHEARTBEAT = 15;	// "Hello, still here"
	public static final byte OP_TERMINATESESSION = 16;	// Relinquish modification lock
	public static final byte OP_COMPLETE         = 17;	// Notify sync complete

	public static final int USB_MAXPAYLOAD = 16384;
	
	public static final byte PSOH = 2;
}
