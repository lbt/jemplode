/* IImportFileFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.IOException;

public interface IImportFileFactory
{
    public IImportFile createImportFile(String string, Object object)
	throws IOException;
}
