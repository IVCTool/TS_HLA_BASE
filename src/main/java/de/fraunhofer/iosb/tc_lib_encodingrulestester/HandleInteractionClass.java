package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleFactory;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;

public class HandleInteractionClass {
	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	private boolean decodeInteractionClass(Node theSelectedNode, final IVCT_RTIambassador ivct_rti, String parentClassName, Set<InteractionClassHandle> interactionHandleSet, Map<ParameterHandle, String> parameterHandleDataTypeMap) {
		InteractionClassHandle ich = null;
		String myClassName = null;
		String textPointer = null;

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
		}
		// Do all on this level
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			System.out.println("decodeInteractionClass: AAAAA " + parentClassName);
			try {
				System.out.println("decodeInteractionClass: BBBBB");
				if (child.getNodeName().equals("name")) {
					System.out.println("decodeInteractionClass: CCCCC");
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Name: " + textPointer);
						if (parentClassName.isEmpty()) {
							myClassName = textPointer;
						} else {
							myClassName = parentClassName + "." + textPointer;
						}
						System.out.println("GET_INTERACTION_CLASS_HANDLE: " + myClassName);
						ich = ivct_rti.getInteractionClassHandle(myClassName);
					}
					System.out.println("decodeInteractionClass: DDDDD");
					continue;
				}
				if (child.getNodeName().equals("sharing")) {
					System.out.println("Got sharing: " + child.getNodeName());
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Sharing: " + textPointer);
						if (textPointer.equals("Publish") || textPointer.equals("PublishSubscribe")) {
							interactionHandleSet.add(ich);
						}
					}
					continue;
				}
				if (child.getNodeName().equals("parameter")) {
					System.out.println("Got parameter: " + child.getNodeName());
					if (decodeParameter(child, ivct_rti, ich, parameterHandleDataTypeMap)) {
						return true;
					}
					continue;
				}
			} catch (Exception e) {
				System.out.println("HandleInteractionClass.decodeInteractionClass: " + e);
			}
		}
		// Do the child level
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("interactionClass")) {
					System.out.println("Got interactionClass: " + child.getNodeName());
					if (decodeInteractionClass(child, ivct_rti, myClassName, interactionHandleSet, parameterHandleDataTypeMap)) {
						return true;
					}
					continue;
				}
			} catch (Exception e) {
				System.out.println("HandleInteractionClass.decodeInteractionClass: " + e);
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces parser node
	 * @param ivct_rti the rti
	 * @param ich the interactionClassHandle
	 * @param parameterHandleDataTypeMap the parameterHandle dataType map
	 * @return true means error
	 */
	private boolean decodeParameter(final Node theSelectedNode, final IVCT_RTIambassador ivct_rti, final InteractionClassHandle ich, final Map<ParameterHandle, String> parameterHandleDataTypeMap) {
		ParameterHandle pHandle = null;
		String nameStr = null;
		String dataTypeStr = null;
		boolean gotName = false;
		boolean gotDataType = false;

		// Get parameter information
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("name")) {
					if (((Element) child).getFirstChild() != null) {
						nameStr = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("Name: " + nameStr);
						pHandle = ivct_rti.getParameterHandle(ich, nameStr);
						gotName = true;
					}
					continue;
				}
				if (child.getNodeName().equals("dataType")) {
					if (((Element) child).getFirstChild() != null) {
						dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
						System.out.println("dataType: " + dataTypeStr);
						gotDataType = true;
					}
					continue;
				}
			} catch (Exception e) {
				System.out.println("HandleInteractionClass.decodeParameter: " + e);
				return true;
			}
		}
		
		// All found
		if (gotName && gotDataType) {
			parameterHandleDataTypeMap.put(pHandle, dataTypeStr);
			return false;
		}
		
		// An error occurred
		if (gotName == false) {
			System.out.println("HandleInteractionClass.decodeParameter: missing name");
		}
		if (gotDataType == false) {
			System.out.println("HandleInteractionClass.decodeParameter: missing dataType");
		}
		return true;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	boolean decode(Node theSelectedNode, final IVCT_RTIambassador ivct_rti, String parentClass, Set<InteractionClassHandle> interactionHandleSet, Map<ParameterHandle, String> parameterHandleDataTypeMap) {
		String textPointer = null;

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
			System.out.println("decode: " + textPointer);
		}
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("interactionClass")) {
				if (decodeInteractionClass(child, ivct_rti, parentClass, interactionHandleSet, parameterHandleDataTypeMap)) {
					return true;
				}
			}
		}
		return false;
	}
}
