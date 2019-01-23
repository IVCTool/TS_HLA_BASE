package de.fraunhofer.iosb.ivct;

import hla.rti1516e.InteractionClassHandle;

public class InteractionClassHandleImpl implements InteractionClassHandle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3503025835182893010L;
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
