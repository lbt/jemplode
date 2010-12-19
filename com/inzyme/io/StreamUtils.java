package com.inzyme.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.util.Debug;

public class StreamUtils {
	/**
	* Copies all the data from an inputstream to an outputstream (with progress)
	*
	* @param _in the inputstream to copy from
	* @param _out the outputstream to copy to
	* @param _bufferSize the size of the buffer to copy with
	* @param _length the number of bytes to read/write
	* @param _offset the offset to prepend to progress (for progress)
	* @param _totalLength the total length of this stream (for progress)
	* @param _listener the progress listener (for progress)
	* @throws IOException if the copy fails
	*/
	public static void copy(InputStream _in, OutputStream _out, int _bufferSize, long _length, long _offset, long _totalLength, ISimpleProgressListener _listener) throws IOException {
		if (_length == -1) {
			_length = Integer.MAX_VALUE;
		}
		
		if (_listener != null) {
			_listener.progressReported(_offset, _totalLength);
		}
	
		byte[] buffer = new byte[_bufferSize];
		int pos = 0;
		while (pos < _length) {
			int bytesRead = _in.read(buffer, 0, (int) Math.min(buffer.length, _length - pos));
			if (bytesRead == -1) {
				break;
			}
			_out.write(buffer, 0, bytesRead);
			pos += bytesRead;
	
			if (_listener != null) {
				_listener.progressReported(_offset + pos, _totalLength);
			}
		}
	
		_out.flush();
	}

	/**
	* Copies all the data from an inputstream to an outputstream (with progress)
	*
	* @param _in the inputstream to copy from
	* @param _out the outputstream to copy to
	* @param _bufferSize the size of the buffer to copy with
	* @param _length the number of bytes to read/write
	* @param _listener the progress listener
	* @throws IOException if the copy fails
	*/
	public static void copy(InputStream _in, OutputStream _out, int _bufferSize, long _length, ISimpleProgressListener _listener) throws IOException {
		StreamUtils.copy(_in, _out, _bufferSize, _length, 0, _length, _listener);
	}

	/**
	* Copies all the data from an inputstream to an outputstream
	*
	* @param _in the inputstream to copy from
	* @param _out the outputstream to copy to
	* @throws IOException if the copy fails
	*/
	public static void copy(InputStream _in, OutputStream _out) throws IOException {
		StreamUtils.copy(_in, _out, 2048, -1, 0, -1, null);
	}
	
	public static void close(InputStream _is) {
		if (_is != null) {
			try {
				_is.close();
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
	}
	
	public static void close(OutputStream _os) {
		if (_os != null) {
			try {
				_os.close();
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
	}
	
	public static void close(Reader _reader) {
		if (_reader != null) {
			try {
				_reader.close();
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
	}
	
	public static void close(Writer _writer) {
		if (_writer != null) {
			try {
				_writer.close();
			}
			catch (Throwable t) {
				Debug.println(t);
			}
		}
	}
	
}
