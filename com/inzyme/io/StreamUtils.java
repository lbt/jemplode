/* StreamUtils - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.io;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.inzyme.progress.ISimpleProgressListener;
import com.inzyme.util.Debug;

public class StreamUtils
{
    public static void copy
	(InputStream _in, OutputStream _out, int _bufferSize, long _length,
	 long _offset, long _totalLength, ISimpleProgressListener _listener)
	throws IOException {
	if (_length == -1L)
	    _length = 2147483647L;
	if (_listener != null)
	    _listener.progressReported(_offset, _totalLength);
	byte[] buffer = new byte[_bufferSize];
	int pos = 0;
	while ((long) pos < _length) {
	    int bytesRead
		= _in.read(buffer, 0, (int) Math.min((long) buffer.length,
						     _length - (long) pos));
	    if (bytesRead == -1)
		break;
	    _out.write(buffer, 0, bytesRead);
	    pos += bytesRead;
	    if (_listener != null)
		_listener.progressReported(_offset + (long) pos, _totalLength);
	}
	_out.flush();
    }
    
    public static void copy
	(InputStream _in, OutputStream _out, int _bufferSize, long _length,
	 ISimpleProgressListener _listener)
	throws IOException {
	copy(_in, _out, _bufferSize, _length, 0L, _length, _listener);
    }
    
    public static void copy(InputStream _in, OutputStream _out)
	throws IOException {
	copy(_in, _out, 2048, -1L, 0L, -1L, null);
    }
    
    public static void close(InputStream _is) {
	if (_is != null) {
	    try {
		_is.close();
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
    }
    
    public static void close(OutputStream _os) {
	if (_os != null) {
	    try {
		_os.close();
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
    }
    
    public static void close(Reader _reader) {
	if (_reader != null) {
	    try {
		_reader.close();
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
    }
    
    public static void close(Writer _writer) {
	if (_writer != null) {
	    try {
		_writer.close();
	    } catch (Throwable t) {
		Debug.println(t);
	    }
	}
    }
}
