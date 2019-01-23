package de.fraunhofer.iosb.ivct;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hla.rti1516e.ParameterHandle;

public class ParameterHandleImpl implements ParameterHandle {
	private Logger logger = LoggerFactory.getLogger(AttributeHandleImpl.class);
	private byte[] buffer;
	private int size;
	private static int ind = 1;

	@Override
	public int encodedLength() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public void encode(byte[] buffer, int offset) {
		byte[] bytes = ByteBuffer.allocate(4).putInt(ind++).array();
		logger.warn("ParameterHandleImpl: ind {}", ind);
		this.buffer = bytes;
		size = 4;
	}
}
