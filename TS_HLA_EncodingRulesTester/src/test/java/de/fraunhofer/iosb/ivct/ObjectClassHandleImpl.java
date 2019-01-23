package de.fraunhofer.iosb.ivct;

import hla.rti1516e.ObjectClassHandle;

public class ObjectClassHandleImpl implements ObjectClassHandle {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3854477015236459562L;
	private byte[] buffer;
	private int size;

	@Override
	public int encodedLength() {
		return size;
	}

	@Override
	public void encode(byte[] buffer, int offset) {
		this.buffer = buffer;
		this.size = offset;
	}
}
