package org.jempeg.tags.id3;

import java.io.IOException;

import com.inzyme.io.SeekableInputStream;

public interface ID3V2FrameListener {
	public void onFrame(SeekableInputStream _stream, ID3V2Frame _frame) throws IOException;
}
