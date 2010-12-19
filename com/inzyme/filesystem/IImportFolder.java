/* IImportFolder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.filesystem;
import java.io.IOException;

import com.inzyme.container.IContainer;

public interface IImportFolder extends IImportFile, IContainer
{
    public IImportFile[] getChildren() throws IOException;
}
