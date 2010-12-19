/* VersionChange - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.versiontracker;

public class VersionChange
{
    private String myVersion;
    private String myFeature;
    
    public VersionChange(String _version, String _feature) {
	myVersion = _version;
	myFeature = _feature;
    }
    
    public String getVersion() {
	return myVersion;
    }
    
    public String getFeature() {
	return myFeature;
    }
}
