/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

/**
* PlayerVersionInfo represents version information about an Empeg.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class PlayerVersionInfo {
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

