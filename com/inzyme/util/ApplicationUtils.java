/* ApplicationUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.util;
import java.io.File;

public class ApplicationUtils
{
    public static boolean isMac() {
	if (System.getProperty("mrj.version") != null)
	    return true;
	return false;
    }
    
    public static File getSettingsFolder() {
	File settingsFolder;
	if (isMac())
	    settingsFolder = new File(System.getProperty("user.home"),
				      "Library/Application Support/Open Rio");
	else
	    settingsFolder
		= new File(System.getProperty("user.home"), ".openrio");
	if (!settingsFolder.exists())
	    settingsFolder.mkdirs();
	return settingsFolder;
    }
}
