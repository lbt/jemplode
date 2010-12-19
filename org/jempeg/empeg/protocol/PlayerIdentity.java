/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package org.jempeg.empeg.protocol;

/**
* PlayerIdentity is a data structure that represents descriptive information
* about an Empeg.   Not much time was spent on this one :)
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class PlayerIdentity {
	private String myHWRev;
	private String mySerial;
	private String myBuild;
	private String myID;
	private String myRAM;
	private String myFlash;

	PlayerIdentity() {
	}

	public PlayerIdentity(String _hwRev, String _serial, String _build, String _id, String _ram, String _flash) {
		myHWRev = _hwRev;
		mySerial = _serial;
		myBuild = _build;
		myID = _id;
		myRAM = _ram;
		myFlash = _flash;
	}
	
	void setHWRev(String _hwRev) {
		myHWRev = _hwRev;
	}
		
	public String getHWRev() {
		return myHWRev;
	}
	
	void setSerial(String _serial) {
		mySerial = _serial;
	}
		
	public String getSerial() {
		return mySerial;
	}
	
	void setBuild(String _build) {
		myBuild = _build;
	}
	
	public String getBuild() {
		return myBuild;
	}
	
	void setID(String _id) {
		myID = _id;
	}
	
	public String getID() {
		return myID;
	}
	
	void setRAM(String _ram) {
		myRAM = _ram;
	}
	
	public String getRAM() {
		return myRAM;
	}

	void setFlash(String _flash) {	
		myFlash = _flash;
	}
	
	public String getFlash() {
		return myFlash;
	}
	
	public String toString() {
		return "[PlayerIdentity: hwRev = " + myHWRev + "; serial = " + mySerial + "; build = " + myBuild + "; id = " + myID + "; ram = " + myRAM + "; flash = " + myFlash + "]";
	}
}
	
