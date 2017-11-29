package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.testrunner.JMSTestRunner;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;

public class HandleObjectClass {
	private static Logger logger = LoggerFactory.getLogger(HandleObjectClass.class);
	AttributeHandleSetFactory attributeHandleSetFactory = null;
	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	private boolean decodeObjectClass(Node theSelectedNode, final IVCT_RTIambassador ivct_rti, String parentClassName, AttributeHandleSet attributeHandleBaseClassSet, Map<ObjectClassHandle, AttributeHandleSet> objectClassAttributeHandleMap, Map<AttributeHandle, String> attributeHandleDataTypeMap) {
		ObjectClassHandle och = null;
		String myClassName = null;
		String textPointer = null;
		AttributeHandleSet attributeHandleWorkingSet = attributeHandleSetFactory.create();

		logger.trace("HandleObjectClass.decodeObjectClass: enter");
		/*
		 * Fill published attribute handles from ancestor classes
		 */
		for (AttributeHandle aHandle : attributeHandleBaseClassSet) {
			attributeHandleWorkingSet.add(aHandle);
		}

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
		}
		// Do all on this level
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("name")) {
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Name: " + textPointer);
						if (parentClassName.isEmpty()) {
							myClassName = textPointer;
						} else {
							myClassName = parentClassName + "." + textPointer;
						}
						System.out.println("GET_OBJECT_CLASS_HANDLE: " + myClassName);
						och = ivct_rti.getObjectClassHandle(myClassName);
					}
					continue;
				}
				if (child.getNodeName().equals("attribute")) {
					decodeAttribute(child, ivct_rti, och, attributeHandleWorkingSet, attributeHandleDataTypeMap);
					continue;
				}
			} catch (Exception e) {
				logger.error("HandleObjectClass.decodeObjectClass: " + e);
				return true;
			}
		}

		// Do the child level
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("objectClass")) {
					if (decodeObjectClass(child, ivct_rti, myClassName, attributeHandleWorkingSet, objectClassAttributeHandleMap, attributeHandleDataTypeMap)) {
						return true;
					}
					continue;
				}
			} catch (Exception e) {
				logger.error("HandleObjectClass.decodeObjectClass: " + e);
				return true;
			}
		}
		if (attributeHandleWorkingSet.isEmpty() == false) {
			objectClassAttributeHandleMap.put(och, attributeHandleWorkingSet);
		}
		logger.trace("HandleObjectClass.decodeObjectClass: leave");
		return false;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	private boolean decodeAttribute(final Node theSelectedNode, final IVCT_RTIambassador ivct_rti, final ObjectClassHandle och, final AttributeHandleSet attributeHandleWorkingSet, final Map<AttributeHandle, String> attributeHandleDataTypeMap) {
		AttributeHandle aHandle = null;
		String nameStr = null;
		String dataTypeStr = null;
		String sharingStr = null;
		boolean gotName = false;
		boolean gotDataType = false;
		boolean gotPublish = false;

		logger.trace("HandleObjectClass.decodeAttribute: enter");
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("name")) {
					logger.trace("HandleObjectClass.decodeAttribute name enter");
					if (((Element) child).getFirstChild() != null) {
						nameStr = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Name: " + nameStr);
						aHandle = ivct_rti.getAttributeHandle(och, nameStr);
						gotName = true;
					}
					logger.trace("HandleObjectClass.decodeAttribute name leave");
					continue;
				}
				if (child.getNodeName().equals("sharing")) {
					logger.trace("HandleObjectClass.decodeAttribute sharing enter");
					if (((Element) child).getFirstChild() != null) {
						sharingStr = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Sharing: " + sharingStr);
						if (sharingStr.equals("Publish") || sharingStr.equals("PublishSubscribe")) {
							gotPublish = true;
						}
					}
					logger.trace("HandleObjectClass.decodeAttribute sharing leave");
					continue;
				}
				if (child.getNodeName().equals("dataType")) {
					logger.trace("HandleObjectClass.decodeAttribute dataType enter");
					if (((Element) child).getFirstChild() != null) {
						dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("DataType: " + dataTypeStr);
						gotDataType = true;
					}
					logger.trace("HandleObjectClass.decodeAttribute dataType leave");
					continue;
				}
			} catch (Exception e) {
				logger.error("HandleObjectClass.decodeAttribute: " + e);
				return true;
			}
		}

		// All found
		if (gotName && gotDataType && gotPublish) {
			attributeHandleWorkingSet.add(aHandle);
			System.out.println("HandleObjectClass.decodeAttribute OOOOOO b1 " + attributeHandleDataTypeMap);
			System.out.println("HandleObjectClass.decodeAttribute OOOOOO b2 " + aHandle);
			System.out.println("HandleObjectClass.decodeAttribute OOOOOO b3 " + dataTypeStr);
			attributeHandleDataTypeMap.put(aHandle, dataTypeStr);
			return false;
		}

		// An error occurred
		if (gotName == false) {
			logger.error("HandleObjectClass.decodeAttribute: missing name");
		}
		if (gotDataType == false) {
			logger.error("HandleObjectClass.decodeAttribute: missing dataType");
		}

		logger.trace("HandleObjectClass.decodeAttribute: leave");
		return true;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @param objectClassAttributeHandleMap the map of attributes to subscribe to
	 * @return true means error
	 */
	boolean decode(Node theSelectedNode, final IVCT_RTIambassador ivct_rti, String parentClassName, AttributeHandleSet attributeHandleBaseClassSet, Map<ObjectClassHandle, AttributeHandleSet> objectClassAttributeHandleMap, Map<AttributeHandle, String> attributeHandleDataTypeMap) {
		logger.trace("HandleObjectClass.decode: enter");
		String textPointer = null;

		try {
			attributeHandleSetFactory = ivct_rti.getAttributeHandleSetFactory();
		} catch (FederateNotExecutionMember | NotConnected e) {
			e.printStackTrace();
			logger.error("HandleObjectClass.decode: error " + e);
			return true;
		}

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
			System.out.println("decode: " + textPointer);
		}
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("objectClass")) {
				if (decodeObjectClass(child, ivct_rti, parentClassName, attributeHandleBaseClassSet, objectClassAttributeHandleMap, attributeHandleDataTypeMap)) {
					return true;
				}
			}
		}
		logger.trace("HandleObjectClass.decode: leave");
		return false;
	}
}
