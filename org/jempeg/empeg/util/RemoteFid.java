/* RemoteFid - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.util;
import com.inzyme.text.StringUtils;
import com.inzyme.util.ReflectionUtils;

public class RemoteFid
{
    private String myPath;
    private String myName;
    private boolean myNewFidLayout;
    
    public RemoteFid(String _path) {
	int lastSlash = _path.lastIndexOf('/');
	myPath = _path.substring(0, lastSlash);
	myName = _path.substring(lastSlash + 1);
	myNewFidLayout = _path.indexOf('_') != -1;
    }
    
    public RemoteFid(int _drive, long _fid, boolean _newFidLayout) {
	myPath = getFidPath(_drive);
	if (_newFidLayout) {
	    String newFidName = StringUtils.pad(_fid, 8, 16).toLowerCase();
	    myPath = myPath + "_" + newFidName.substring(0, 5) + "/";
	    myName = newFidName.substring(5);
	} else
	    myName = Long.toHexString(_fid);
	myNewFidLayout = _newFidLayout;
    }
    
    public long getFid() {
	String fidStr;
	if (myNewFidLayout) {
	    int underscoreIndex = myPath.lastIndexOf('_');
	    fidStr = myPath.substring(underscoreIndex + 1,
				      myPath.length() - 1) + myName;
	} else
	    fidStr = myName;
	long fid = Long.parseLong(fidStr, 16);
	return fid;
    }
    
    public String getPath() {
	return myPath;
    }
    
    public String getName() {
	return myName;
    }
    
    public String getFullPath() {
	return myPath + myName;
    }
    
    public static String getFidPath(int _driveNum) {
	return "/empeg/fids" + _driveNum + "/";
    }
    
    public String toString() {
	return ReflectionUtils.toString(this);
    }
}
