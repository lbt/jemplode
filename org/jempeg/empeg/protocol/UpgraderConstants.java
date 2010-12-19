/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

/**
* Defines a set of constants that are
* used during the Empeg update
* process.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class UpgraderConstants {
  public static final int UPGRADER_VERSION = 2;

  // Packetsize for disk pumping
  public static final int PACKET_SIZE = 4096;

  // Chunk types in upgrade file
  public static final int CHUNK_INFO        = 0x00;
  public static final int CHUNK_WHAT        = 0x01;
  public static final int CHUNK_RELEASE     = 0x02;
  public static final int CHUNK_VERSION     = 0x03;
  public static final int CHUNK_HWREVS      = 0x04;
  public static final int CHUNK_UNTARDRIVE0 = 0x05;
  
  public static final int CHUNK_FLASHLOADER  = 0x10;
  public static final int CHUNK_FLASHKERNEL  = 0x11;
  public static final int CHUNK_FLASHRAMDISK = 0x12;
  public static final int CHUNK_FLASHRANDOM  = 0x13;
  
  public static final int CHUNK_PUMPHDA  = 0x20;
  public static final int CHUNK_PUMPHDA1 = 0x21;
  public static final int CHUNK_PUMPHDA2 = 0x22;
  public static final int CHUNK_PUMPHDA3 = 0x23;
  public static final int CHUNK_PUMPHDA4 = 0x24;
  public static final int CHUNK_PUMPHDA5 = 0x25;
  public static final int CHUNK_PUMPHDA6 = 0x26;
  public static final int CHUNK_PUMPHDA7 = 0x27;
  public static final int CHUNK_PUMPHDA8 = 0x28;
  
  public static final int CHUNK_PUMPHDB  = 0x30;
  public static final int CHUNK_PUMPHDB1 = 0x31;
  public static final int CHUNK_PUMPHDB2 = 0x32;
  public static final int CHUNK_PUMPHDB3 = 0x33;
  public static final int CHUNK_PUMPHDB4 = 0x34;
  public static final int CHUNK_PUMPHDB5 = 0x35;
  public static final int CHUNK_PUMPHDB6 = 0x36;
  public static final int CHUNK_PUMPHDB7 = 0x37;
  public static final int CHUNK_PUMPHDB8 = 0x38;
  
  public static final int CHUNK_PUMPHDC  = 0x40;
  public static final int CHUNK_PUMPHDC1 = 0x41;
  public static final int CHUNK_PUMPHDC2 = 0x42;
  public static final int CHUNK_PUMPHDC3 = 0x43;
  public static final int CHUNK_PUMPHDC4 = 0x44;
  public static final int CHUNK_PUMPHDC5 = 0x45;
  public static final int CHUNK_PUMPHDC6 = 0x46;
  public static final int CHUNK_PUMPHDC7 = 0x47;
  public static final int CHUNK_PUMPHDC8 = 0x48;
  
  public static final int CHUNK_UPGRADERVERSION = 0xff;

  // Status enums
  public static final int UPGRADE_CHECKFILE   = 0;
  public static final int UPGRADE_CHECKEDFILE = 1;

  public static final int UPGRADE_FINDUNIT    = 2;
  public static final int UPGRADE_CHECKID     = 3;
  public static final int UPGRADE_ERASE       = 4;
  public static final int UPGRADE_PROGRAM     = 5;
  public static final int UPGRADE_RESTART     = 6;

  public static final int UPGRADE_FINDPUMP    = 7;
  public static final int UPGRADE_PUMPWAIT    = 8;
  public static final int UPGRADE_PUMPSELECT  = 9;
  public static final int UPGRADE_PUMP        = 10;
  public static final int UPGRADE_PUMPDONE    = 11;

  public static final int UPGRADE_DONE        = 12;
  public static final int UPGRADE_ERRORS      = 13;

  /** @todo Unify these with the stuff in protocol_errors.mes?
  */
	public static final int ERROR_NOERROR       = 0;  /* 0 */
	public static final int ERROR_NOFILE        = 1;  /* 1 */
	public static final int ERROR_SHORTFILE     = 2;  /* 2 */
	public static final int ERROR_BADFILE       = 3;  /* 3 */
	public static final int ERROR_NOCONNECTION  = 4;  /* 4 */
	public static final int ERROR_NOUNIT        = 5;  /* 5 */
	public static final int ERROR_BADID         = 6;  /* 6 */
	public static final int ERROR_BADPROMPT     = 7;  /* 7 */
	public static final int ERROR_BADERASE      = 8;  /* 8 */
	public static final int ERROR_BADPROGRAM    = 9;  /* 9 */
	public static final int ERROR_BADFLASHFILE  = 10; /* 10 */
	public static final int ERROR_NOPUMP        = 11; /* 11 */
	public static final int ERROR_BADPUMPSELECT = 12; /* 12 */
	public static final int ERROR_BADPUMP       = 13; /* 13 */
	public static final int ERROR_BADPUMPFILE   = 14; /* 14 */
	public static final int ERROR_OLDUPGRADER   = 15; /* 15 */
	public static final int ERROR_HWREV         = 16; /* 16 */

  public static final int PRODUCT_C3_8M = 0x88c1;

  // Some definitions
  public static final int SOH = 1;
  public static final int ACK = 6;
  public static final int NAK = 21;
  public static final int CAN = 24;
  
  // Timeouts
  public static final int FINDUNIT_TIMEOUT         = 180000; // 180 seconds
  public static final int FINDUNIT_INTERVAL        = 100;    // 10Hz
  public static final int FINDUNIT_STATUS_INTERVAL = 1000;	 // 1Hz
}
