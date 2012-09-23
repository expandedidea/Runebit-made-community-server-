/*
* @ Author - Digistr.
* @ Info - Only Vital Methods for the 474 Protocal have been added.
*/

package com.packet;

public class Packet {

	public final short ID;
	private final byte[] buffer;
	private int readerIndex = 0;

	public Packet(int id, byte[] buffer) {
		ID = (short)id;
		this.buffer = buffer;
	}

	public void skipBytes(int skipped) {
		readerIndex += skipped;
	}

	public void resetReaderIndex() {
		readerIndex = 0;
	}

	public int readableBytes() {
		return buffer.length - readerIndex;
	}

	public int capacity() {
		return buffer.length;
	}

	public byte[] array() {
		return buffer;
	}

	public int readerIndex() {
		return readerIndex;
	}

	public int readByte() {
		return buffer[readerIndex++] & 255;
	}
	
	public int readByteA() {
		return (buffer[readerIndex++] - 128) & 255;
	}

	public int readByteS() {
		return (128 - buffer[readerIndex++]) & 255;
	}

	public int readByteC() {
		return (-buffer[readerIndex++]) & 255;
	}

	public int readShort() {
		return (readByte() << 8) | readByte();
	}

	public int readShortA() {
		return (readByte() << 8) | readByteA();
	}

	public int readLEShort() {
		return readByte() | (readByte() << 8);
	}

	public int readLEShortA() {
		return readByteA() | (readByte() << 8);
	}

	public int readInt() {
		return (readByte() << 24) | (readByte() << 16) | (readByte() << 8) | readByte();
	}

	public int readLEInt() {
		return readByte() | (readByte() << 8) | (readByte() << 16) | (readByte() << 24);
	}

	public int readInt1() {
		return (readByte() << 8) | readByte() | (readByte() << 24) | (readByte() << 16); 
	}

	public int readInt2() {
		return (readByte() << 16) | (readByte() << 24) | readByte() | (readByte() << 8); 
	}

	public long readLong() {
        	return ((0xffffffffL & (long)readInt()) << 32) + (0xffffffffL & (long)readInt());
	}

	public long readLong1() {
        	return ((0xffffffffL & (long)readInt1()) << 32) + (0xffffffffL & (long)readInt1());
	}

	public String readString() {
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer[readerIndex++]) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}
}
