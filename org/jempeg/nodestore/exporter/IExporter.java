/* IExporter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore.exporter;
import java.io.IOException;
import java.io.OutputStream;

import org.jempeg.nodestore.PlayerDatabase;

public interface IExporter
{
    public void write
	(PlayerDatabase playerdatabase, OutputStream outputstream)
	throws IOException;
}
