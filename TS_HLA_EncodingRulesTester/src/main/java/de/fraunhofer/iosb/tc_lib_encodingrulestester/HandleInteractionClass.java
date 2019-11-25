/*
Copyright 2017, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleFactory;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.RTIinternalError;

public class HandleInteractionClass {
	private static Logger logger = LoggerFactory.getLogger(HandleInteractionClass.class);
	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	private boolean decodeInteractionClass(Node theSelectedNode, final RTIambassador ivct_rti, String parentClassName, Map<InteractionClassHandle, Set<ParameterHandle>> interactionHandleMap, Map<ParameterHandle, String> parameterHandleDataTypeMap) {
		logger.trace("HandleInteractionClass.decodeInteractionClass: enter");
		InteractionClassHandle ich = null;
		String myClassName = null;
		String textPointer = null;
		Set<ParameterHandle> parameterHandleSet = new HashSet<ParameterHandle>();
		// Inherit parameters from the parent class
		if (parentClassName.isEmpty() == false) {
			logger.trace("HandleInteractionClass.decodeInteractionClass: parentClassName" + parentClassName);
			try {
				InteractionClassHandle pch = ivct_rti.getInteractionClassHandle(parentClassName);
				Set<ParameterHandle> parentParameterHandleSet = interactionHandleMap.get(pch);
				if (parentParameterHandleSet != null) {
					logger.trace("HandleInteractionClass.decodeInteractionClass: parentParameterHandleSet.size: " + parentParameterHandleSet.size());
					parameterHandleSet = new HashSet<ParameterHandle>(parentParameterHandleSet);
				}
			} catch (NameNotFound e) {

			} catch (FederateNotExecutionMember e) {

		    } catch (NotConnected e) {

		    } catch (RTIinternalError e) {

		    }
		}

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
		}
		// Do all on this level
		boolean gotPublish = false;
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("name")) {
					logger.trace("HandleInteractionClass.decodeInteractionClass: name enter");
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						logger.trace("Name: " + textPointer);
						if (parentClassName.isEmpty()) {
							myClassName = textPointer;
						} else {
							myClassName = parentClassName + "." + textPointer;
						}
						logger.trace("RTI GET_INTERACTION_CLASS_HANDLE: " + myClassName);
						ich = ivct_rti.getInteractionClassHandle(myClassName);
					}
					logger.trace("HandleInteractionClass.decodeInteractionClass: name leave");
					continue;
				}
				if (child.getNodeName().equals("sharing")) {
					logger.trace("HandleInteractionClass.decodeInteractionClass: sharing enter");
					if (((Element) child).getFirstChild() != null) {
						textPointer = ((Element) child).getFirstChild().getNodeValue();
						logger.trace("Sharing: " + textPointer);
						if (textPointer.equals("Publish") || textPointer.equals("PublishSubscribe")) {
							gotPublish = true;
						}
					}
					logger.trace("HandleInteractionClass.decodeInteractionClass: sharing leave");
					continue;
				}
				if (child.getNodeName().equals("parameter")) {
					logger.trace("HandleInteractionClass.decodeInteractionClass: parameter enter");
					logger.trace("Got parameter: " + child.getNodeName());
					if (decodeParameter(child, ivct_rti, ich, parameterHandleDataTypeMap, parameterHandleSet)) {
						return true;
					}
					logger.trace("HandleInteractionClass.decodeInteractionClass: parameter leave");
					continue;
				}
			} catch (Exception e) {
				logger.error("HandleInteractionClass.decodeInteractionClass: " + e);
			}
		}
		if (gotPublish) {
            interactionHandleMap.put(ich, parameterHandleSet);
		}
		// Do the child level
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// ---------------------------------------------------------------------------
			try {
				if (child.getNodeName().equals("interactionClass")) {
					logger.trace("Got interactionClass: " + child.getNodeName());
					if (decodeInteractionClass(child, ivct_rti, myClassName, interactionHandleMap, parameterHandleDataTypeMap)) {
						return true;
					}
					continue;
				}
			} catch (Exception e) {
				logger.error("HandleInteractionClass.decodeInteractionClass: " + e);
				return true;
			}
		}
		logger.trace("HandleInteractionClass.decodeInteractionClass: leave");
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
	private boolean decodeParameter(final Node theSelectedNode, final RTIambassador ivct_rti, final InteractionClassHandle ich, final Map<ParameterHandle, String> parameterHandleDataTypeMap, Set<ParameterHandle> parameterHandleSet) {
		logger.trace("HandleInteractionClass.decodeParameter: enter");
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
					logger.trace("HandleInteractionClass.decodeParameter: name enter");
					if (((Element) child).getFirstChild() != null) {
						nameStr = ((Element) child).getFirstChild().getNodeValue();
						logger.trace("Name: " + nameStr);
						pHandle = ivct_rti.getParameterHandle(ich, nameStr);
						parameterHandleSet.add(pHandle);
						gotName = true;
					}
					logger.trace("HandleInteractionClass.decodeParameter: name leave");
					continue;
				}
				if (child.getNodeName().equals("dataType")) {
					logger.trace("HandleInteractionClass.decodeParameter: dataType enter");
					if (((Element) child).getFirstChild() != null) {
						dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
						logger.trace("dataType: " + dataTypeStr);
						gotDataType = true;
					}
					logger.trace("HandleInteractionClass.decodeParameter: dataType leave");
					continue;
				}
			} catch (Exception e) {
				logger.error("HandleInteractionClass.decodeParameter: " + e);
				return true;
			}
		}
		
		// All found
		if (gotName && gotDataType) {
			parameterHandleDataTypeMap.put(pHandle, dataTypeStr);
			logger.trace("HandleInteractionClass.decodeParameter: leave");
			return false;
		}
		
		// An error occurred
		if (gotName == false) {
			logger.error("HandleInteractionClass.decodeParameter: missing name");
		}
		if (gotDataType == false) {
			logger.error("HandleInteractionClass.decodeParameter: missing dataType");
		}
		return true;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	boolean decode(Node theSelectedNode, final RTIambassador ivct_rti, String parentClass, Map<InteractionClassHandle, Set<ParameterHandle>> interactionHandleMap, Map<ParameterHandle, String> parameterHandleDataTypeMap) {
		logger.trace("HandleInteractionClass.decode: enter");
		String textPointer = null;

		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
			logger.trace("HandleInteractionClass.decode: " + textPointer);
		}
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("interactionClass")) {
				if (decodeInteractionClass(child, ivct_rti, parentClass, interactionHandleMap, parameterHandleDataTypeMap)) {
					return true;
				}
			}
		}
		logger.trace("HandleInteractionClass.decode: leave");
		return false;
	}
}
