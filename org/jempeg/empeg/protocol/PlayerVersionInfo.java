/* PlayerVersionInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;

public class PlayerVersionInfo
{
    private String myVersion;
    private String myBeta;
    private int myProtocol;
    
    public PlayerVersionInfo(String _version, String _beta, int _protocol) {
	myVersion = _version;
	myBeta = _beta;
	myProtocol = _protocol;
    }
    
    public String getVersion() {
	return myVersion;
    }
    
    public String getBeta() {
	return myBeta;
    }
    
    public int getProtocol() {
	return myProtocol;
    }
}
