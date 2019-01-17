package de.fraunhofer.iosb.ivct;

import java.nio.ByteBuffer;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.InteractionClassHandleFactory;
import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;;

public class InteractionClassHandleFactoryImpl implements InteractionClassHandleFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6366485031635594942L;
	private static int ind = 1;

	@Override
	public InteractionClassHandle decode(byte[] buffer, int offset)
			throws CouldNotDecode, FederateNotExecutionMember, NotConnected, RTIinternalError {
		byte[] bytes = ByteBuffer.allocate(4).putInt(ind++).array();
		InteractionClassHandle ich = new InteractionClassHandleImpl();
		ich.encode(bytes, 4);
		return ich;
	}

}
