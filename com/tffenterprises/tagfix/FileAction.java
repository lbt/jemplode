/* FileAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.tagfix;
import java.io.File;
import java.io.IOException;

public interface FileAction
{
    public boolean performAction(File file) throws IOException;
    
    public void finish();
}
