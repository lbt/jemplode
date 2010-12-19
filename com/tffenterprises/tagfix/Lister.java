/* Lister - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;

public class Lister implements FileAction
{
    int fileCounter = 0;
    
    public void finish() {
	/* empty */
    }
    
    public boolean performAction(File f) {
	System.out.println(f.getPath());
	fileCounter++;
	return true;
    }
}
