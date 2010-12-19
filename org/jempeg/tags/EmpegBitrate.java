/* EmpegBitrate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags;

public class EmpegBitrate
{
    private StringBuffer myBitrateString;
    private boolean myHasVBR;
    private boolean myHasChannels;
    private boolean myHasBitsPerSecond;
    
    public EmpegBitrate() {
	myBitrateString = new StringBuffer();
    }
    
    public EmpegBitrate(String _str) {
	myBitrateString = new StringBuffer(_str);
    }
    
    public String toString() {
	return myBitrateString.toString();
    }
    
    public boolean hasVBR() {
	isVBR();
	return myHasVBR;
    }
    
    public boolean hasChannels() {
	getChannels();
	return myHasChannels;
    }
    
    public boolean hasBitsPerSecond() {
	getBitsPerSecond();
	return myHasBitsPerSecond;
    }
    
    public boolean isVBR() {
	myHasVBR = false;
	boolean vbr = false;
	if (myBitrateString.length() > 2) {
	    switch (Character.toLowerCase(myBitrateString.charAt(0))) {
	    case 'v':
		myHasVBR = true;
		vbr = true;
		break;
	    case 'f':
		myHasVBR = true;
		vbr = false;
		break;
	    }
	}
	return vbr;
    }
    
    public int getChannels() {
	myHasChannels = false;
	int channels = 0;
	if (myBitrateString.length() > 2) {
	    switch (Character.toLowerCase(myBitrateString.charAt(1))) {
	    case 'm':
		myHasChannels = true;
		channels = 1;
		break;
	    case 's':
		myHasChannels = true;
		channels = 2;
		break;
	    }
	}
	return channels;
    }
    
    public int getBitsPerSecond() {
	myHasBitsPerSecond = false;
	int bps = 0;
	if (myBitrateString.length() > 3) {
	    try {
		bps = Integer.parseInt(myBitrateString.toString()
					   .substring(2)) * 1000;
		myHasBitsPerSecond = true;
	    } catch (Throwable t) {
		bps = 0;
	    }
	}
	return bps;
    }
    
    public void setVBR(boolean _vbr) {
	char v = _vbr ? 'v' : 'f';
	if (myBitrateString.length() >= 1)
	    myBitrateString.setCharAt(0, v);
	else
	    myBitrateString.append(v);
    }
    
    public void setChannels(int _channels) {
	char c;
	switch (_channels) {
	case 1:
	    c = 'm';
	    break;
	case 2:
	    c = 's';
	    break;
	default:
	    c = '?';
	}
	for (;;) {
	    if (myBitrateString.length() < 2)
		myBitrateString.append('?');
	    else {
		myBitrateString.setCharAt(1, c);
		break;
	    }
	}
    }
    
    public void setBitsPerSecond(int _bps) {
	String b = String.valueOf(_bps / 1000);
	if (myBitrateString.length() < 2) {
	    while (myBitrateString.length() < 2)
		myBitrateString.append('?');
	} else
	    myBitrateString.setLength(2);
	myBitrateString.append(b);
    }
}
