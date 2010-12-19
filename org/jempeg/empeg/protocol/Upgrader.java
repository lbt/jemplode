/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

import javax.comm.CommPortIdentifier;

import org.jempeg.protocol.ConnectionException;
import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IConnectionFactory;
import org.jempeg.protocol.IProtocolClient;
import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.discovery.IDevice;
import org.jempeg.protocol.discovery.IDiscoverer;
import org.jempeg.protocol.discovery.IDiscoveryListener;

import com.inzyme.io.FileSeekableInputStream;
import com.inzyme.io.SeekableInputStream;
import com.inzyme.progress.SilentProgressListener;
import com.inzyme.typeconv.CRC32;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;
import com.inzyme.typeconv.LittleEndianUtils;
import com.inzyme.util.Debug;

/* upgrader.cpp
*
* empeg upgrader class
*
* (C) 2000 empeg ltd, http://www.empeg.com
*
* Authors:
*   Hugo Fiennes <hugo@empeg.com>
*   Peter Hartley <peter@empeg.com>
*
* This software is licensed under the GNU General Public Licence (see file
* COPYING), unless you possess an alternative written licence from empeg ltd.
*
* (:Empeg Source Release 1.37 26-Oct-2001 17:20 mac:)
*
* version 0.07 20000629 HBF
*/

/** \class Upgrader 
 * This class deals with the processing of an upgrade package file, which
 * happens in several stages:
 * - Length sanity check
 * - CRC check of entire file
 * - Load text/release note portions into memory and tell the observer about them
 *   (so user can inspect release notes if necessary)
 * - Process each upgrade block in file, which fall into two categories:
 *   a. Flash upgrade - erases/reprograms a section of flash ROM
 *   b. Disk pump - sends a .gz image directly to a disk or disk partition
 * - Reboot
 *
 * (a reboot also occurs between upgrading flash and pumping the disk).
 *
 * Note that this code isn't pretty, but it does the job. There are different
 * protocols for downloading the flash as this talks to the original flash
 * downloader in the write-protected portion of the flash rom - and this has
 * never been tidied on the "it ain't broke" principle. This uses simple
 * checksums on blocks and a very strange command interface. Two extra
 * commands, 'l' and 'u' are supported on mk2 empegs which use the Intel
 * TE28F800C3B flash which has individually lockable blocks.
 *
 * The disk pumping code runs from the flash-based ramdisk on the empeg at
 * every boot of the system (and steps out of the way if it's not needed
 * fairly smartish) and streams a gzipped file direct to a block device -
 * be it the actual physical drive (used to partition drives) or a
 * partition on the drive. As the player partition is a read-only partition
 * with no user data on it, this allows easy upgrades to the player.
 *
 * To get status information to update dialogs, etc, during an upgrade,
 * you need to register an UpgradeObserver with the Upgrader class. This
 * gets called with status as the upgrade progresses.
 *
 * Version 0.05 - now supports hda5/6/7/8 and hdc5/6/7/8
 * Version 0.06 - flasher portion now deals with C3 flash chips (id 88c1)
 * Version 0.07 - deals with pumping hdb if necessary
 */
/**
* @author Mike Schrag
* @version $Revision: 1.14 $
*/
public class Upgrader {
  private IConnection myConnection;
  private SeekableInputStream myUploadData;
  private byte[] myBuf32 = new byte[4];
  private byte[] myBuffer = new byte[16384];
  private long mySection;
  private UpgradeListenerIfc myListener;
    
  // Type of flash
  private int myProduct;
    
  // Text sections from upgrade file
  private String myInfo;
  private String myWhat;
  private String myRelease;
  private String myVersion;

  public Upgrader() {
  }

  public void close() throws IOException, ConnectionException {
    dumpText();

    // close the stream
    if (myUploadData != null) {
      myUploadData.close();
      myUploadData = null;
    }

    // close the connection
    closeConnection();
  }

  // Check the upgrade file
  /**
  * Verify the upgrade file to make sure it's kosher.
  * Tell the observer about any text we find in it.
  */
  public void checkUpgrade(SeekableInputStream _upgradeData) throws IOException, ConnectionException {
    // Open file
    myUploadData = _upgradeData;
  
    // First, do a complete scan of the file building the CRC
    long fileLength = myUploadData.length();
  
    // Read claimed length
    long claimedLength = readUL();
  
    // Short file?
    if (fileLength < (claimedLength + 8)) {
      error(UpgraderConstants.ERROR_SHORTFILE);
      throw new IOException("Short file (length = " + fileLength + ", claimed length = " + claimedLength + ")");
    }
  
    // Tell user what we're doing
    status(UpgraderConstants.UPGRADE_CHECKFILE, 0, (int)claimedLength);
  
    // Calculate CRC of entire file
    CRC32 fileCRC = new CRC32();
    long chunk = 0;
    long i = 0;
    while (i < claimedLength) {
      // Deal with it a chunk at a time
      chunk = ((claimedLength - i) > 16384) ? 16384 : (claimedLength - i);
      myUploadData.readFully(myBuffer, 0, (int)chunk);
  
      // CRC this block
      fileCRC.update(myBuffer, 0, (int)chunk);
  
      // Next block
      i += chunk;
      status(UpgraderConstants.UPGRADE_CHECKFILE, (int)i, (int)claimedLength);
    }
  
    // See if CRC matches
    long theirCRC = readUL();
    if (fileCRC.getValue() != theirCRC) {
      error(UpgraderConstants.ERROR_BADFILE);
      throw new IOException("Mismatched CRC (" + fileCRC.getValue() + " != " + theirCRC);
    }
  
    // Dump any previously loaded text
    dumpText();
  
    // File is good: deal with chunks in upgrade file
    long currentPos = 4;
    long upgraderVersion = -1;
    while (currentPos < claimedLength && currentPos != 0) {
      // Seek to correct position in case last op overrun
      myUploadData.seek(currentPos);
  
      // Read blocktype & length
      long blockType = readUL();
      long blockLength = readUL();
  
      mySection = blockType;
      if (blockType == UpgraderConstants.CHUNK_UPGRADERVERSION) {
        upgraderVersion = readUL();
      } else if (blockType == UpgraderConstants.CHUNK_INFO) {
        myInfo = readString(blockLength);
      } else if (blockType == UpgraderConstants.CHUNK_WHAT) {
        myWhat = readString(blockLength);
      } else if (blockType == UpgraderConstants.CHUNK_RELEASE) {
        myRelease = readString(blockLength);
      } else if (blockType == UpgraderConstants.CHUNK_VERSION) {
        myVersion = readString(blockLength);
      }

      currentPos += (8 + blockLength);
    }
  
    // Right version?
    if (upgraderVersion > UpgraderConstants.UPGRADER_VERSION) {
      error(UpgraderConstants.ERROR_OLDUPGRADER);
      throw new IOException("Old upgrader");
    }
  
    // Tell observer about the text we've found
    if (myListener != null) {
      myListener.textLoaded(myInfo, myWhat, myRelease, myVersion);
    }
  
    // Done file
    status(UpgraderConstants.UPGRADE_CHECKEDFILE, 1, 1);
  }

  /**
  * Uploads the given stream of data to a specifc address of the Empeg.
  *
  * @param _connection the connection to upload data to
  * @param _uploadData the data to upload
  * @param _address the ram address to upload to
  * @throws IOException if the data cannot be uploaded
  */
  public void upload(IConnection _connection, SeekableInputStream _uploadData, int _address) throws IOException, ConnectionException, ProtocolException {
    try {
      myUploadData = _uploadData;
      myUploadData.seek(0);
  
      int length = (int)_uploadData.length();
      initializeConnection(_connection);
  
      startFlash();
  
      flashArea(_address, length);
    }
    finally {
      closeConnection();
    }
  }

  // Start the actual upgrade
  /**
  * @todo Unify this loop, the one in CheckUpgrade, and the one in
  *         pump/upgrade-player.cpp
  */
  public void doUpgrade(IConnection _connection) throws IOException, ConnectionException, ProtocolException {
    try {
      mySection = 0;
    
      // Open file
      myUploadData.seek(0);
    
      // Read claimed length
      long claimedLength = readUL();
    
      initializeConnection(_connection);

      startFlash();

      int hwRev = getHwRev();
    
      // File is good: deal with chunks in upgrade file
      long currentPos = 4;
      long start;
      boolean firstFlash = false;
      boolean firstPump = true;
      String partition;

      LittleEndianOutputStream eos = myConnection.getOutputStream();
      while (currentPos < claimedLength && currentPos != 0) {
        // Seek to correct position in case last operation overran
        myUploadData.seek(currentPos);
        // Read blocktype & length
        long blockType = readUL();
        long blockLength = readUL();
    
        mySection = blockType;
        if (blockType == UpgraderConstants.CHUNK_UPGRADERVERSION ||
            blockType == UpgraderConstants.CHUNK_INFO ||
            blockType == UpgraderConstants.CHUNK_WHAT ||
            blockType == UpgraderConstants.CHUNK_RELEASE) {
        } else if (blockType == UpgraderConstants.CHUNK_HWREVS) {
          boolean ok = false; // Assume not ours unless we explicitly match
          String text = readString(blockLength);
          StringTokenizer tokenizer = new StringTokenizer(text, "\n");
          while (!ok && tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int thisRev = Integer.parseInt(token, 16);
            if (thisRev == hwRev) {
              ok = true;
            }
          }
          if (!ok) {
            error(UpgraderConstants.ERROR_HWREV);
            currentPos = 0;
          }
        } else if (blockType == UpgraderConstants.CHUNK_FLASHLOADER ||
                   blockType == UpgraderConstants.CHUNK_FLASHKERNEL ||
                   blockType == UpgraderConstants.CHUNK_FLASHRAMDISK ||
                   blockType == UpgraderConstants.CHUNK_FLASHRANDOM) {
          // Deal with flash programming block: read start address
          start = readUL();
  
          // First one?
          if (firstFlash) {
            firstFlash = false;
            startFlash();
          }
  
          // Do the program
          flashArea((int)start, (int)blockLength - 4);
        } else if (blockType == UpgraderConstants.CHUNK_PUMPHDA ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC) {
          // First one?
          if (firstPump) {
            firstPump = false;
  
            // Have we flashed? If so, we'll need to send an 'r' to force a restart
            if (!firstFlash) {
              eos.write('r');
            }
  
            // Wait for pump to come up
            startPump();
          }
  
          // Build partition name
          partition = (blockType == UpgraderConstants.CHUNK_PUMPHDA) ? "/dev/hda" :
                      (blockType == UpgraderConstants.CHUNK_PUMPHDB) ? "/dev/hdb" : "/dev/hdc";
  
          // Do the pump
          partition(partition);
        } else if (blockType == UpgraderConstants.CHUNK_PUMPHDA1 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA2 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA3 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA4 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA5 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA6 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA7 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDA8 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB1 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB2 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB3 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB4 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB5 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB6 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB7 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDB8 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC1 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC2 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC3 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC4 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC5 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC6 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC7 ||
                   blockType == UpgraderConstants.CHUNK_PUMPHDC8) {
          // First one?
          if (firstPump) {
            firstPump = false;
  
            // Have we flashed? If so, we'll need to send an 'r' to force a restart
            if (!firstFlash) {
              eos.write('r');
            }
  
            // Wait for pump to come up
            startPump();
          }
  
          // Form filename
          if (blockType >= UpgraderConstants.CHUNK_PUMPHDA1 && blockType <= UpgraderConstants.CHUNK_PUMPHDA8) {
            partition = "/dev/hda" + (blockType - UpgraderConstants.CHUNK_PUMPHDA);
          } else if (blockType >= UpgraderConstants.CHUNK_PUMPHDB1 && blockType <= UpgraderConstants.CHUNK_PUMPHDB8) {
            partition = "/dev/hdb" + (blockType - UpgraderConstants.CHUNK_PUMPHDB);
          } else {
            partition = "/dev/hdc" + (blockType - UpgraderConstants.CHUNK_PUMPHDC);
          }
  
          // Do the pump
          pumpPartition(partition, (int)blockLength);
        }
    
        if (currentPos > 0) {
          // Move current position on
          currentPos += (8 + blockLength);
        }
      }
    
      // Reboot as necessary
      if (!firstPump) {
        // We've pumped, so we're still in pumper: force a reboot
        eos.write("reboot\r", "ISO-8859-1");
      } else if (!firstFlash) {
        // We've not pumped, but we have flashed: force a reboot
        eos.write('r');
      }
    
      // All done (currentpos==0 indicates upgrade process was aborted)
      if (currentPos <= 0) {
        // Something bad happened...
        status(UpgraderConstants.UPGRADE_ERRORS);
        throw new IOException("Something bad happened");
      }
    
      // All ok
      status(UpgraderConstants.UPGRADE_DONE);
    }
    finally {
      // It won't be valid after we return anyway.
      closeConnection();
    }
  }

  // Tell the upgrader who 
  public void setListener(UpgradeListenerIfc _listener) {
    // Save this observer: we call it when we have status to show
    myListener = _listener;
  }

  private void initializeConnection(IConnection _connection) throws IOException, ConnectionException, ProtocolException {
    // Now restart the player if it is running
    IProtocolClient protocolClient = new EmpegProtocolClient(_connection, new SilentProgressListener());
		myConnection = protocolClient.getConnection();
    if (protocolClient.isDeviceConnected() || protocolClient.isDeviceConnected()) {
			((EmpegProtocolClient)protocolClient).restartUnit(false, false);
    }
    myConnection.open();
  }

  private void closeConnection() throws IOException, ConnectionException {
    // It won't be valid after we return anyway.
    if (myConnection != null) {
      myConnection.close();
      myConnection = null;
    }
  }

  /**
  * Returns the the hardware revision # of this player.
  *
  * @returns the the hardware revision # of this player
  * @throws IOException if the player doesn't respond in an expected way
  */
  private int getHwRev() throws IOException, ConnectionException {
    LittleEndianInputStream eis = myConnection.getInputStream();
    LittleEndianOutputStream eos = myConnection.getOutputStream();
    eos.write('i');

    // If this player groks 'i' you'll get i\nXY ...
    // otherwise                           eh\n?
    
    int hwRev;
    myConnection.setTimeout(1000);
    char ch = (char)eis.read();
    if (ch == 'i') {
      myConnection.setTimeout(500);
      // CR NL x y \0
      if (eis.read() != '\r') {
        throw new IOException("Expected '\\r'");
      }
      if (eis.read() != '\n') {
        throw new IOException("Expected '\\n'");
      }
      hwRev = readHex(500, 2);
    } else {
      hwRev = 1; // Actually covers 1-6, i.e. all Mark 1s (plus Marvin, the Mark 2 prototype)
    }
    return hwRev;
  }

  /**
  * Read a 32 bit little-endian unsigned long from the file.
  */
  private long readUL() throws IOException {
    myUploadData.readFully(myBuf32);
		long b = LittleEndianUtils.toUnsigned32(myBuf32[0], myBuf32[1], myBuf32[2], myBuf32[3]);
    return b;
  }

  private String readString(long _length) throws IOException {
    byte[] bytes = new byte[(int)_length];
    myUploadData.readFully(bytes);
    String str;
    try {
      str = new String(bytes, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      Debug.println(e);
      str = new String(bytes);
    }
    return str;
  }

  /**
  * Waits for a string. waitFor times out only when there's no data for n seconds.
  * The whole string is sent in one, so will never span across calls.
  */
  private boolean waitFor(String _string, int _timeout) throws IOException, ConnectionException {
    long to = System.currentTimeMillis() + _timeout * 1000;

    myConnection.setTimeout(1000);
    LittleEndianInputStream eis = myConnection.getInputStream();
    int i = 0;
    int length = _string.length();
    char c0 = _string.charAt(0);
    while (i < length && System.currentTimeMillis() < to) {
      char ch = (char)eis.read();
//      System.out.print(ch);
      if (ch == _string.charAt(i)) {
        i ++;
      } else {
        i = (ch == c0) ? 1 : 0;
      }
    }
    boolean matches = (i == length);
    return matches;
  }

  /**
  * Read a hex value
  */
  private int readHex(int _timeout, int _digits) throws IOException, ConnectionException {
    byte[] buf = new byte[_digits];
    myConnection.setTimeout(_timeout * 1000);
    LittleEndianInputStream eis = myConnection.getInputStream();
    eis.read(buf);
    String hexStr;
    try {
      hexStr = new String(buf, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      Debug.println(e);
      hexStr = new String(buf);
    }
    int hex = Integer.parseInt(hexStr, 16);
    return hex;
  }

  /**
  * Read a decimal value
  */
  /**
  private int readDec(int _timeout) throws IOException, ConnectionException {
    myConnection.setTimeout(_timeout * 1000);
    LittleEndianInputStream eis = myConnection.getInputStream();
    char d;

    // Process data until we get a digit
    do {
      d = (char)eis.read();
    } while (!Character.isDigit(d));

    int result = (d - '0');

    // Process whole number
    do {
      d = (char)eis.read();
      if (Character.isDigit(d)) {
        result = (result * 10) + (d - '0');
      }
    } while (Character.isDigit(d));

    return result;
  }
  /**/

  /**
  * Generic command
  * @todo Find a better way to handle the specific errors from the flash chip.
  */
  private void commandAddress(char _command, int _address) throws IOException, ConnectionException {
    // Empty buffer first
    flushRx();

    // Send command
    LittleEndianInputStream eis = myConnection.getInputStream();
    LittleEndianOutputStream eos = myConnection.getOutputStream();

    eos.write(_command);

    myConnection.setTimeout(1000);
    char ch = (char)eis.read();
    if (ch != _command) {
      throw new IOException("Command mismatch (" + ch + " != " + _command + ")");
    }

    // Send address
    eos.write((_address >>  0) & 0xff);
    eos.write((_address >>  8) & 0xff);
    eos.write((_address >> 16) & 0xff);
    eos.write((_address >> 24) & 0xff);

    // Get 8-byte hex address reply and see if it matches
    int a = readHex(1, 8);

    if (a != _address) {
      throw new IOException("Address mismatch (" + a + " != " + _address + ")");
    }

    // Should now have a '-' showing it's working on the erase
    ch = (char)eis.read();
    if (ch != '-') {
      throw new IOException("Expected hyphen was not found (found '" + ch + "' instead).");
    }

    // Read result (leave 2 seconds)
    int res = readHex(2, 2);

    if (res != 0x80) { // It's a chip-specific error - see the todo comment, above.
      throw new IOException("Chip-specific error.  (" + res + " != 0x80)");
    }
  }

  /**
  * Erase page of flash
  */
  private void erasePage(int _address) throws IOException, ConnectionException {
    commandAddress('e', _address);
  }

  /**
  * Lock page of flash (C3 flash only)
  */
  private void lockPage(int _address) throws IOException, ConnectionException {
    commandAddress('l', _address);
  }

  /**
  * Unlock page of flash (C3 flash only)
  */
  private void unlockPage(int _address) throws IOException, ConnectionException {
    commandAddress('u', _address);
  }

  /**
  * Program flash rom from ram buffer
  */
  private void programBlock(int _address, int _length) throws IOException, ConnectionException {
    // Empty buffer first
    flushRx();
  
    // Send command
    myConnection.setTimeout(1000);
    LittleEndianInputStream eis = myConnection.getInputStream();
    LittleEndianOutputStream eos = myConnection.getOutputStream();
    eos.write('p');
  
    char ch = (char)eis.read();
    if (ch != 'p') {
      throw new IOException("Expected 'p'");
    }
  
    // Send address
    eos.write((_address >>  0) & 0xff);
    eos.write((_address >>  8) & 0xff);
    eos.write((_address >> 16) & 0xff);
    eos.write((_address >> 24) & 0xff);
  
    // Get 8-char hex address reply and see if it matches
    int reportedAddress = readHex(1, 8);
    if (reportedAddress != _address) {
      throw new IOException("Address " + _address + " != Reported Address " + reportedAddress);
    }
  
    // Should now have a '-' showing it's waiting for length
    ch = (char)eis.read();
    if (ch != '-') {
      throw new IOException("Expected hyphen");
    }
  
    // Send length
    eos.write((_length >> 0) & 0xff);
    eos.write((_length >> 8) & 0xff);
  
    // Get 4-char hex length reply and see if it matches
    int reportedLength = readHex(1, 4);
    if (reportedLength != _length) {
      throw new IOException("Length " + _length + " != Reported Length " + reportedLength);
    }
  
    // Should now have a '-' showing it's waiting for data
    ch = (char)eis.read();
    if (ch != '-') {
      throw new IOException("Expected hyphen");
    }
  
    // Send data
    int checksum = 0;
    for (int i = 0; i < _length; i ++) {
      checksum += myBuffer[i];
      eos.write(myBuffer[i]);
    }
  
    // Send checksum
    eos.write(checksum & 0xff);
  
    // Should get an 'ok' indicating good download
    ch = (char)eis.read();
    if (ch != 'o') {
      throw new IOException("Expected 'o'");
    }

    ch = (char)eis.read();
    if (ch != 'k') {
      throw new IOException("Expected 'k'");
    }

    ch = (char)eis.read();
    if (ch != '\r') {
      throw new IOException("Expected '\\r'");
    }

    ch = (char)eis.read();
    if (ch != '\n') {
      throw new IOException("Expected '\\n'");
    }
  
    // Should have program reply within a second
    ch = (char)eis.read();
    if (ch != 'o') {
      throw new IOException("Expected 'o'");
    }

    ch = (char)eis.read();
    if (ch != 'k') {
      throw new IOException("Expected 'k'");
    }
  }

  /**
  * Flush input buffer
  */
  private void flushRx() throws IOException, ConnectionException {
    myConnection.flushReceiveBuffer();
  }

  /**
  * Enter flash ROM download
  */
  private void startFlash() throws IOException, ConnectionException {
    // Waiting for unit to be powered on
    status(UpgraderConstants.UPGRADE_FINDUNIT);
    
    myConnection.setTimeout(1000);
    LittleEndianInputStream eis = myConnection.getInputStream();
    LittleEndianOutputStream eos = myConnection.getOutputStream();

    // Wait for powerup message
    String magic = "Flash";
    int i = 0;
    Timeout giveup = new Timeout(UpgraderConstants.FINDUNIT_TIMEOUT);
    Timeout statusInterval = new Timeout(UpgraderConstants.FINDUNIT_STATUS_INTERVAL);
    Timeout sendInterval = new Timeout(UpgraderConstants.FINDUNIT_INTERVAL);
    giveup.reset();
    statusInterval.reset();
//    sendInterval.reset();    // send ctrl-A at on first loop
    do {
      try {
        if (i >= magic.length()) {
          break;    // got string
        }
    
        if (statusInterval.isTimedOut()) {
          // update status
          status(UpgraderConstants.UPGRADE_FINDUNIT, 
                 (int)(UpgraderConstants.FINDUNIT_TIMEOUT - giveup.getRemainingMilliseconds()),
                 UpgraderConstants.FINDUNIT_TIMEOUT);
          statusInterval.reset();
        }
    
        if (sendInterval.isTimedOut()) {
//          System.out.println("Sending Ctrl-A");
          // send ctrl-A
          eos.write(UpgraderConstants.SOH);
          sendInterval.reset();
        }
    
        // wait the least amount of time required
        long remainingSend = sendInterval.getRemainingMilliseconds();
        long remainingGiveup = giveup.getRemainingMilliseconds();
        long waitTime = remainingSend;
        if (remainingGiveup < waitTime) {
          waitTime = remainingGiveup;
        }
        if (waitTime < 0) {
          waitTime = 0;
        }
    
//        myConnection.setTimeout(waitTime);
        char c = (char)eis.read();
//        System.out.print(c);
        if (c >= 0) {
          if (c == magic.charAt(i)) {
            i ++; // gotcha!
          } else {
            i = 0; // not gotcha!
          }
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    } while (!giveup.isTimedOut());
  
    // Found it?
    if (giveup.isTimedOut()) {
      error(UpgraderConstants.ERROR_NOUNIT);
      throw new IOException("No unit found.");
    }
  
    // Manufacturer ID: Look for '#' then 4 hex digits
    myConnection.setTimeout(1000);
    while (eis.read() != '#') {
    }
    //myManufacturer = readHex(1, 4);
		readHex(1, 4);
  
    // Product ID: Look for '#' then 4 hex digits
    while (eis.read() != '#') {
    }
    myProduct = readHex(1, 4);
  
    // Wait for prompt
    while (eis.read() != '?') {
    }
  
    // In case more than one ^A got through we'd better wait until everything goes quiet
  
    while (eis.read() != -1) {
    }
  }

  /**
  * Flash an area in the unit's flash ROM: erase as necessary
  */
  private void flashArea(int _address, int _length) throws IOException, ConnectionException {
    // Start with nothing...
    status(UpgraderConstants.UPGRADE_ERASE, 0, _length);
  
    // Erase area in question
    int a = _address;
    int d = 0;
    do {
      // Unlock page if necessary
      if (myProduct == UpgraderConstants.PRODUCT_C3_8M) {
        // Unlock the page
        unlockPage(a);
      }
  
      // Erase this page
      erasePage(a);
  
      // Move to next page (pages below 64k boundary are 8k, above are 64k)
      if (a < 0x10000) {
        a += 8192;
        d += 8192;
      } else {
        a += 65536;
        d += 65536;
      }
  
      // Update status
      status(UpgraderConstants.UPGRADE_ERASE, d, _length);
    } while (d < _length);
  
    // Program this area
    int done = 0;
    int chunk;
    status(UpgraderConstants.UPGRADE_PROGRAM, 0, _length);
    int p = _address;
    while (done < _length) {
      // Get chunk size: we can program a max of 16k at a time
      chunk = ((_length - done) > 16384) ? 16384 : (_length - done);
  
      // Read it from file
      myUploadData.readFully(myBuffer, 0, chunk);
  
      // Must be an even number of bytes: doesn't matter if the
      // last one is junk
      if ((chunk & 1) > 0) {
        chunk ++;
      }
  
      // Program it
      programBlock(p, chunk);
  
      // Move on to next block
      done += chunk;
      p += chunk;
  
      // Show status
      status(UpgraderConstants.UPGRADE_PROGRAM, done, _length);
    }
  
    // Lock pages if we have a C3 (the prom code will do lock-down)
    if (myProduct == UpgraderConstants.PRODUCT_C3_8M) {
      a = _address;
      d = 0;
      do {
        // Lock the page
        lockPage(a);
  
        // Move to next page (pages below 64k boundary are 8k, above are 64k)
        if (a < 0x10000) {
          a += 8192;
          d += 8192;
        } else {
          a += 65536;
          d += 65536;
        }
      } while (d < _length);
    }
  }

  /**
  * Enter disk pumper
  */
  private void startPump() throws IOException, ConnectionException {
    // Waiting for unit to be powered on
    status(UpgraderConstants.UPGRADE_FINDPUMP);

    // Wait for pump start note
    int timeout = 0;
    while (timeout <= 60) {
      // This works as waitfor times out only when there's no data for
      // n seconds - the whole string is sent in one, so will never span
      // waitfor calls
      status(UpgraderConstants.UPGRADE_FINDPUMP, timeout, 60);
      if (waitFor("Ctrl-A", 3)) {
        // Found it!
        break;
      } else {
        timeout ++;
      }
    }
  
    if (timeout > 60) {
      // Can't find it
      error(UpgraderConstants.ERROR_NOPUMP);
      throw new IOException("No pump");
    }
  
    // Send ctrl-A to drop into pump mode
    LittleEndianOutputStream eos = myConnection.getOutputStream();
    eos.write(UpgraderConstants.SOH);
  }

  /**
  * Pump a partition with gzipped data
  */
  private void pumpPartition(String _partitionName, int _length) throws IOException, ConnectionException {
    String buf;
  
    // Waiting for unit to be powered on
    status(UpgraderConstants.UPGRADE_PUMPWAIT);
  
    // Clear buffer
    flushRx();
  
    // Send a CR to elicit a pump response
    LittleEndianInputStream eis = myConnection.getInputStream();
    LittleEndianOutputStream eos = myConnection.getOutputStream();
    eos.write('\r');
  
    // Wait for pump prompt
    if (!waitFor("pump> ", 5)) {
      // Pump doesn't appear to be there
      error(UpgraderConstants.ERROR_NOPUMP);
      throw new IOException("No pump");
    }
  
    // Send partition select
    status(UpgraderConstants.UPGRADE_PUMPSELECT, 0, 1);
  
    buf = "device " + _partitionName;
    eos.write(buf, "ISO-8859-1");
    eos.write('\r');
    if (!waitFor(buf, 5)) {
      // Didn't select the partition correctly
      error(UpgraderConstants.ERROR_BADPUMPSELECT);
      throw new IOException("Bad Pump Select");
    }
  
    // Tell unit what to expect
    buf = "pump " + _length;
    eos.write(buf, "ISO-8859-1");
    eos.write('\r');
    if (!waitFor(buf, 5)) {
      // Incorrect pump response
      //TRACE("Incorrect pump response\n");
      error(UpgraderConstants.ERROR_BADPUMP);
      throw new IOException("Bad pump");
    }
  
    // Load first bufferful
    int chunk = (_length < UpgraderConstants.PACKET_SIZE) ? _length : UpgraderConstants.PACKET_SIZE;
    myUploadData.readFully(myBuffer, 0, chunk);
  
    int offset = 0;
    int errors = 0;
    // Write file PACKETSIZE at a time
    while (offset < _length && errors < 16) {
      // Send chunk
      eos.write(UpgraderConstants.SOH);
      eos.write(myBuffer, 0, UpgraderConstants.PACKET_SIZE);
  
      // Calculate CRC
      CRC32 crcObj = new CRC32();
      crcObj.update(myBuffer, 0, UpgraderConstants.PACKET_SIZE);
      long crc = crcObj.getValue();
  
      // Send CRC, LSB first
      // Flush buffer
      flushRx();
  
      eos.writeUnsigned32(crc);
  
      // Wait for ACK or NAK
      int l = 0;
      try {
        myConnection.setTimeout(20000);
        do {
          l = eis.read();
          if (l < 0) {
            l = UpgraderConstants.NAK;
            throw new IOException ("Received a NAK");
          }
        } while (l != UpgraderConstants.ACK && l != UpgraderConstants.NAK);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  
      // Resend packet on NAK
      if (l == UpgraderConstants.NAK) {
        errors ++;
        continue;
      }
  
      // Otherwise, move onto next buffer
      offset += UpgraderConstants.PACKET_SIZE;
  
      // Reset error count (it's max errors on each block)
      errors = 0;
  
      // Update status
      status(UpgraderConstants.UPGRADE_PUMP, (offset > _length) ? _length : offset, _length);
  
      // If we're not finished, read the next bit
      if (offset < _length) {
        if ((_length - offset) < UpgraderConstants.PACKET_SIZE) {
          chunk = _length - offset;
        } else {
          chunk = UpgraderConstants.PACKET_SIZE;
        }
  
        myUploadData.readFully(myBuffer, 0, chunk);
      }
    }
  
    // Finished?
    if (offset < _length) {
      // Sleep then send a CAN: the sleep will reset the remote state
      // machine, the CAN will abort the download
      // This seems to happen sometimes MAC 1999/08/17
      //TRACE("offset < length. offset=%d, length=%d\n", offset, length);
      try { Thread.sleep(4000); } catch (Throwable t) { }
      eos.write(UpgraderConstants.CAN);
  
      // Errors during the download. Oops.
      error(UpgraderConstants.ERROR_BADPUMP);
      throw new IOException("Bad pump");
    }
  
    // All sent OK!
    status(UpgraderConstants.UPGRADE_PUMPDONE, 1, 1);
  }

  /**
  * Get the size of a partition.
  */
  /**
  private int sizePartition(String _partitionName) throws IOException, ConnectionException {
    // Waiting for unit to be powered on
    status(UpgraderConstants.UPGRADE_PUMPWAIT);
  
    // Clear buffer
    flushRx();
  
    // Send a CR to elicit a pump response
    LittleEndianOutputStream eos = myConnection.getOutputStream();
    eos.write('\r');
  
    // Wait for pump prompt
    if (!waitFor("pump> ", 5)) {
      // Pump doesn't appear to be there
      error(UpgraderConstants.ERROR_NOPUMP);
      throw new IOException("No pump");
    }
  
    // Send partition select
    status(UpgraderConstants.UPGRADE_PUMPSELECT, 0, 1);
  
    String buf = "device " + _partitionName;
    eos.write(buf);
    eos.write('\r');

    if (!waitFor(buf, 5)) {
      // Didn't select the partition correctly
      error(UpgraderConstants.ERROR_BADPUMPSELECT);
      throw new IOException("Bad Pump Select");
    }
  
    // Get partition size
    eos.write("size\r");
  
    int partitionSize = readDec(2);
    return partitionSize;
  }
  /**/

  /**
  * Size partition
  */
  private void partition(String _partitionName) throws IOException, ConnectionException {
    // Waiting for unit to be powered on
    status(UpgraderConstants.UPGRADE_PUMPWAIT);
  
    // Clear buffer
    flushRx();
  
    // Send a CR to elicit a pump response
    LittleEndianOutputStream eos = myConnection.getOutputStream();
    eos.write('\r');
  
    // Wait for pump prompt
    if (!waitFor("pump> ", 5)) {
      // Pump doesn't appear to be there
      error(UpgraderConstants.ERROR_NOPUMP);
      throw new IOException("No pump");
    }
  
    // Send partition select
    status(UpgraderConstants.UPGRADE_PUMPSELECT, 0, 1);
  
    String buf = "partition " + _partitionName;
    eos.write(buf, "ISO-8859-1");
    eos.write('\r');
    if (!waitFor(buf, 5)) {
      // Didn't select the partition correctly
      error(UpgraderConstants.ERROR_BADPUMPSELECT);
      throw new IOException("Bad Pump Select");
    }
  }

  /**
  * Status reporting
  */
  private void status(int _state, int _current, int _max) {
    // ...this allows some shortcuts in the calling code...
    if (_current > _max) {
      _current = _max;
    }

    // Call observer if present
    if (myListener != null) {
      myListener.showStatus((int)mySection, _state, _current, _max);
    }
  }

  /**
  * Status reporting for when we don't have much to say
  */
  private void status(int _state) {
    status(_state, 0, 0);
  }

  /**
  * Status reporting for errors.
  */
  private void error(int _state) {
    if (myListener != null) {
      myListener.showError((int)mySection, _state);
    }
  }

  private void dumpText() throws IOException {
    // Dispose of the 4 text segments
    myInfo = null;
    myWhat = null;
    myRelease = null;
    myVersion = null;
  }

  public static void main(String[] _args) throws Throwable {
    FileSeekableInputStream fsis = null;
    try {
      File upgradeFile = new File(_args[0]);
      Upgrader u = new Upgrader();
      u.setListener(new BasicUpgradeListener());
      fsis = new FileSeekableInputStream(upgradeFile);
      u.checkUpgrade(fsis);
      CommPortIdentifier commPort = CommPortIdentifier.getPortIdentifier("COM1");
      SerialConnection conn = new SerialConnection(commPort, 115200);
      u.doUpgrade(conn);
    }
    finally {
      if (fsis != null) {
        fsis.close();
      }
    }
//    SerialEmpegDiscoverer serialDiscoverer = new SerialEmpegDiscoverer();
//    SerialDiscovererListener discovererListener = new SerialDiscovererListener(u);
//    serialDiscoverer.addEmpegDiscovererListener(discovererListener);
//    serialDiscoverer.discover(20000);
  }

  public static class SerialDiscovererListener implements IDiscoveryListener {
    private Upgrader myUpgrader;

    public SerialDiscovererListener(Upgrader _upgrader) {
      myUpgrader = _upgrader;
    }
    
		public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
      try {
        IConnectionFactory connFactory = _device.getConnectionFactory();
        myUpgrader.doUpgrade(connFactory.createConnection());
      }
      catch (Exception e) {
      	Debug.println(e);
      }
    }
  }
}
