/* RequestIdentifierFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;
import com.inzyme.typeconv.UINT32;

public class RequestIdentifierFactory
{
    private static long myID = 42L;
    
    public static synchronized UINT32 reserve() {
	long result = myID++;
	UINT32 packetID = new UINT32(result);
	return packetID;
    }
}
