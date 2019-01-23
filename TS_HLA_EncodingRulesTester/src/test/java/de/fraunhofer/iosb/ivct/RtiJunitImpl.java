package de.fraunhofer.iosb.ivct;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.InteractionClassHandleFactory;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectClassHandleFactory;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

public class RtiJunitImpl extends IVCT_NullRTIambassador {
	private Logger logger = LoggerFactory.getLogger(RtiJunitImpl.class);
	private boolean generateNewHandles = true;

	public void setGenerateNewHandles(boolean generateNewHandles) {
		this.generateNewHandles = generateNewHandles;
	}

	/*
	 * Interaction maps
	 */
	private InteractionClassHandleFactory interactionClassHandleFactory = new InteractionClassHandleFactoryImpl();
	private Map<InteractionClassHandle, String> interactionClassHandleNameMap = new HashMap<InteractionClassHandle, String>();
	private Map<String, InteractionClassHandle> interactionClassNameHandleMap = new HashMap<String, InteractionClassHandle>();
	private Map<InteractionClassHandle, Map<String, ParameterHandle>> interactionParameterNameMap = new HashMap<InteractionClassHandle, Map<String, ParameterHandle>>();

	/*
	 * Object maps
	 */
	private AttributeHandleSetFactoryImpl attributeHandleSetFactoryImpl = new AttributeHandleSetFactoryImpl();
	private Map<ObjectClassHandle, Map<String, AttributeHandle>> objectAttributeNameMap = new HashMap<ObjectClassHandle, Map<String, AttributeHandle>>();
    private Map<ObjectClassHandle, String> objectClassHandleNameMap = new HashMap<ObjectClassHandle, String>();
    private Map<String, ObjectClassHandle> objectClassNameHandleMap = new HashMap<String, ObjectClassHandle>();
    private ObjectClassHandleFactory objectClassHandleFactory = new ObjectClassHandleFactoryImpl();

	public AttributeHandleSetFactory getAttributeHandleSetFactory()
			throws
			FederateNotExecutionMember,
			NotConnected {
		return attributeHandleSetFactoryImpl;
	}

	public ObjectClassHandleFactory getObjectClassHandleFactory()
			throws
			FederateNotExecutionMember,
			NotConnected {
		this.logger.warn("getObjectClassHandleFactory not implemented");
		return null;
	}

	// 10.6
	public ObjectClassHandle getObjectClassHandle(String theName)
	throws
	NameNotFound,
	FederateNotExecutionMember,
	NotConnected,
	RTIinternalError {
		ObjectClassHandle och = objectClassNameHandleMap.get(theName);
		if (och == null) {
			try {
				och = objectClassHandleFactory.decode(null, 0);
				objectClassNameHandleMap.put(theName, och);
				objectClassHandleNameMap.put(och, theName);
				Map<String, AttributeHandle> attributeNameHandleMap = new HashMap<String, AttributeHandle>();
				int ind = theName.lastIndexOf(".");
				if (ind != -1) {
					String parentClassName = theName.substring(0, ind);
					ObjectClassHandle poch = objectClassNameHandleMap.get(parentClassName);
					Map<String, AttributeHandle> atts = objectAttributeNameMap.get(poch);
					if (atts != null) {
						attributeNameHandleMap.putAll(atts);
					}
				}
				objectAttributeNameMap.put(och, attributeNameHandleMap);

			} catch (CouldNotDecode e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    return och;
	}

	// 10.11
	public AttributeHandle getAttributeHandle(ObjectClassHandle whichClass, String theName)
					throws
					NameNotFound,
					InvalidObjectClassHandle,
					FederateNotExecutionMember,
					NotConnected,
					RTIinternalError {
		AttributeHandle ah = null;
		Map<String, AttributeHandle> attributeNameHandleMap = objectAttributeNameMap.get(whichClass);

		if (attributeNameHandleMap == null) {
			throw new InvalidObjectClassHandle("getAttributeHandle: unknown object class: " + whichClass + " " + theName);
		}
		else {
			ah = attributeNameHandleMap.get(theName);
			if (ah == null) {
				if (generateNewHandles == false) {
					throw new NameNotFound("getAttributeHandle: unknown attribute: " + whichClass + " " + theName);
				}
				ah = new AttributeHandleImpl();
				ah.encode(null, 0);
				attributeNameHandleMap.put(theName, ah);
			}
		}
		return ah;
	}

	// 10.15
	public InteractionClassHandle getInteractionClassHandle(String theName)
	throws
	NameNotFound,
	FederateNotExecutionMember,
	NotConnected,
	RTIinternalError {
		InteractionClassHandle ich = interactionClassNameHandleMap.get(theName);
		if (ich == null) {
			try {
				ich = interactionClassHandleFactory.decode(null, 0);
				interactionClassHandleNameMap.put(ich, theName);
				interactionClassNameHandleMap.put(theName, ich);
				Map<String, ParameterHandle> parameterNameHandleMap = new HashMap<String, ParameterHandle>();
				int ind = theName.lastIndexOf(".");
				if (ind != -1) {
					String parentClassName = theName.substring(0, ind);
					InteractionClassHandle poch = interactionClassNameHandleMap.get(parentClassName);
					Map<String, ParameterHandle> paramData = interactionParameterNameMap.get(poch);
					if (paramData != null) {
						parameterNameHandleMap.putAll(paramData);
					}
				}
				interactionParameterNameMap.put(ich, parameterNameHandleMap);
			} catch (CouldNotDecode e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    return ich;
	}

	// 10.17
	public ParameterHandle getParameterHandle(InteractionClassHandle whichClass, String theName)
	throws
	NameNotFound,
	InvalidInteractionClassHandle,
	FederateNotExecutionMember,
	NotConnected,
	RTIinternalError {
		ParameterHandle ph = null;
		Map<String, ParameterHandle> parameterNameHandleMap = interactionParameterNameMap.get(whichClass);
		if (parameterNameHandleMap == null) {
				throw new InvalidInteractionClassHandle("getParameterHandle: unknown interaction class: " + whichClass + " " + theName);
		}
		else {
			ph = parameterNameHandleMap.get(theName);
			if (ph == null) {
				if (generateNewHandles == false) {
					throw new NameNotFound("getParameterHandle: unknown parameter: " + whichClass + " " + theName);
				}
				ph = new ParameterHandleImpl();
				ph.encode(null, 0);
				parameterNameHandleMap.put(theName, ph);
			}
		}
		return ph;
	}
}
