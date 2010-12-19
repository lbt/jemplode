package com.inzyme.typeconv;


public class LittleEndianByteToUINT32Buffer {
	private byte[] myBuffer;
	
	public LittleEndianByteToUINT32Buffer(byte[] _buffer) {
		myBuffer = _buffer;
	}
	
	public final byte[] getByteBuffer() {
		return myBuffer;
	}
	
	public final long[] getUINT32Buffer() {
		int length = getUINT32Size();
		long[] buffer = new long[length];
		for (int i = 0; i < buffer.length; i ++) {
			buffer[i] = getUINT32(i);
		}
		return buffer;
	}
	
	public final short getByte(int _index) {
		return myBuffer[_index];
	}
	
	public final void setByte(int _index, byte _value) {
		myBuffer[_index] = _value;
	}
	
	public final int getByteSize() {
		return myBuffer.length;
	}
	
	public final long getUINT32(int _index) {
		int newindex = _index * 4;
		long value = LittleEndianUtils.toUnsigned32(myBuffer[newindex], myBuffer[newindex + 1], myBuffer[newindex + 2], myBuffer[newindex + 3]);
		return value;
	}
	
	public final int getINT32(int _index) {
		int newindex = _index * 4;
		int value = LittleEndianUtils.toSigned32(myBuffer[newindex], myBuffer[newindex + 1], myBuffer[newindex + 2], myBuffer[newindex + 3]);
		return value;
	}
	
	public final void setUINT32(int _index, long _value) {
		int newindex = _index * 2;
		myBuffer[newindex] = (byte)(_value & 0xFF);
		myBuffer[newindex + 1] = (byte)((_value >> 8) & 0xFF);
		myBuffer[newindex + 2] = (byte)((_value >> 16) & 0xFF);
		myBuffer[newindex + 3] = (byte)((_value >> 32) & 0xFF);
	}
	
	public final int getUINT32Size() {
		return myBuffer.length >> 2;
	}
}
