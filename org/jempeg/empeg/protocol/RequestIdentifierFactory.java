/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

import com.inzyme.typeconv.UINT32;

/**
* RequestIdentifierFactory creates unique packet
* ID's for use during Empeg protocol communications.
*
* @author Mike Schrag
* @version $Revision: 1.3 $
*/
public class RequestIdentifierFactory {
  private static long myID = 42;

  /**
  * Returns the next unique packet ID.
  *
  * @returns the next unique packet ID.
  */
  public static synchronized UINT32 reserve() {
//    long result = ((myID &      0x7F) +
//                  ((myID &    0x3F80) << 8) +
//                  ((myID &  0x1FC000) << 16) +
//                  ((myID & 0xFE00000) << 24) +
//                  0x80808080) & 0xFFFFFFFF;
//    myID ++;

    long result = myID ++;
    UINT32 packetID = new UINT32(result);
    return packetID;
  }
}
