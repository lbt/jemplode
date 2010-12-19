/* ID3V2FrameListener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.tags.id3;
import java.io.IOException;

import com.inzyme.io.SeekableInputStream;

public interface ID3V2FrameListener
{
    public void onFrame(SeekableInputStream seekableinputstream,
			ID3V2Frame id3v2frame) throws IOException;
}
