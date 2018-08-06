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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;

public class DataTreeBuilder {
    private static Logger logger = LoggerFactory.getLogger(DataTreeBuilder.class);
	// Specific classes to handle the program logic
	private HandleDataTypes handleDataTypes = new HandleDataTypes();
	private HandleInteractionClass handleInteractionClass = new HandleInteractionClass();
	private HandleObjectClass handleObjectClass = new HandleObjectClass();
	// The dataTypes
	private HlaDataTypes hlaDataTypes;
	// The RTI ambassador
	private IVCT_RTIambassador ivct_rti;
	// The private references to the data structures to be filled
	private Map<AttributeHandle, String> attributeHandleDataTypeMap;
	private Map<ObjectClassHandle, AttributeHandleSet> objectClassAttributeHandleMap;
	private Map<ParameterHandle, String> parameterHandleDataTypeMap;
	private Set<InteractionClassHandle> interactionHandleSet;
	// The cache of attributes of base object classes for internal use
	private AttributeHandleSet attributeHandleBaseClassSet = null;
	AttributeHandleSetFactory attributeHandleSetFactory = null;

	/**
	 * This class will handle all the necessary logic to process the Xerces xml contents
	 *
	 * @param ivct_rti the RTI ambassador to use
	 * @param hlaDataTypes the dataType handler
	 * @param interactionHandleSet the interactions to subscribe to
	 * @param parameterHandleDataTypeMap the parameter dataType mapper
	 * @param objectClassAttributeHandleMap the attribute dataType mapper
	 * @throws TcInconclusive in case of rti error
	 */
	public DataTreeBuilder(final IVCT_RTIambassador ivct_rti, final HlaDataTypes hlaDataTypes, final Set<InteractionClassHandle> interactionHandleSet, Map<ParameterHandle, String> parameterHandleDataTypeMap, Map<ObjectClassHandle, AttributeHandleSet> objectClassAttributeHandleMap, Map<AttributeHandle, String> attributeHandleDataTypeMap) throws TcInconclusive {
		logger.trace("DataTreeBuilder: enter");
		this.ivct_rti = ivct_rti;
		this.hlaDataTypes = hlaDataTypes;
		this.interactionHandleSet = interactionHandleSet;
		this.parameterHandleDataTypeMap = parameterHandleDataTypeMap;
		this.objectClassAttributeHandleMap = objectClassAttributeHandleMap;
		this.attributeHandleDataTypeMap = attributeHandleDataTypeMap;
		try {
			this.attributeHandleSetFactory = ivct_rti.getAttributeHandleSetFactory();
			this.attributeHandleBaseClassSet = this.attributeHandleSetFactory.create();
		} catch (FederateNotExecutionMember e) {
			e.printStackTrace();
			throw new TcInconclusive("DataTreeBuilder: FederateNotExecutionMember");
		}
		catch (NotConnected e) {
			e.printStackTrace();
			throw new TcInconclusive("DataTreeBuilder: NotConnected");
		}
		logger.trace("DataTreeBuilder: leave");
	}
	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @return true means error
	 */
	public boolean buildData(Node theSelectedNode) {
		logger.trace("DataTreeBuilder.buildData: enter");
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			try {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("objects")) {
					logger.trace("Got objects!");
					// Only needed during interaction processing
					String parentObjectClassName = new String();
					if (((Element) child).getFirstChild() != null) {
						handleObjectClass.decode(child, this.ivct_rti, parentObjectClassName, attributeHandleBaseClassSet, objectClassAttributeHandleMap, attributeHandleDataTypeMap);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("interactions")) {
					logger.trace("Got interactions!");
					// Only needed during interaction processing
					String parentInteractionClassName = new String();
					if (((Element) child).getFirstChild() != null) {
						handleInteractionClass.decode(child, this.ivct_rti, parentInteractionClassName, interactionHandleSet, parameterHandleDataTypeMap);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("tags")) {
					logger.trace("Got tags!");
					if (((Element) child).getFirstChild() != null) {
						// handleTags.decode(child);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("dataTypes")) {
					logger.trace("Got dataTypes!");
					if (((Element) child).getFirstChild() != null) {
						handleDataTypes.decode(child, hlaDataTypes);
					}
					continue;
				}
				// ---------------------------------------------------------------------------
				if (child.getNodeName().equals("notes")) {
					logger.trace("Got notes!");
					if (((Element) child).getFirstChild() != null) {
						// handleNotes.decode(child);
					}
					continue;
				}
			} catch (Exception e) {
				logger.error("DataTreeBuilder.buildData: " + e);
				return true;
			}
		}
		logger.trace("DataTreeBuilder.buildData: leave");
		return false;
	}
}
