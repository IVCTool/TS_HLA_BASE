package de.fraunhofer.iosb.ivct;

import java.nio.ByteBuffer;

import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectClassHandleFactory;
import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

public class ObjectClassHandleFactoryImpl implements ObjectClassHandleFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3310670006645471547L;
	private static int ind = 1;

	@Override
	public ObjectClassHandle decode(byte[] buffer, int offset)
			throws CouldNotDecode, FederateNotExecutionMember, NotConnected, RTIinternalError {
		byte[] bytes = ByteBuffer.allocate(4).putInt(ind++).array();
		ObjectClassHandle och = new ObjectClassHandleImpl();
		och.encode(bytes, 4);
		return och;
	}
}
