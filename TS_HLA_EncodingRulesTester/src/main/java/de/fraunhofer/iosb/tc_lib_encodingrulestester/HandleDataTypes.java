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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.tc_lib_encodingrulestester.AlternativeStringPair;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataBasicType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataEnumType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataFixedArrayType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataFixedRecordType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataTypes;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataVariableArrayType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataVariantRecordType;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.HlaDataSimpleType;

public class HandleDataTypes {
    private static Logger logger = LoggerFactory.getLogger(HandleDataTypes.class);
    private Map <String, String> missingTypes = new HashMap<String, String>();
	HlaDataTypes hlaDataTypes;

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeBasicData(Node theSelectedNode) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotSize = false;
		boolean gotEndian = false;
		String nameStr = null;
		boolean bigEndian = false;
		int size = 0;

		/*
		 * Loop for all data for one basicDataType
		 */
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("HandleDataTypes.decodeBasicData: BasicDataName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("size")) {
				if (((Element) child).getFirstChild() != null) {
					String textStr = ((Element) child).getFirstChild().getNodeValue();
					size = Integer.parseInt(textStr);
					gotSize = true;
					logger.trace("HandleDataTypes.decodeBasicData: BasicDataSize: " + size);
				}
				continue;
			}
			if (child.getNodeName().equals("endian")) {
				if (((Element) child).getFirstChild() != null) {
					String textStr = ((Element) child).getFirstChild().getNodeValue();
					if (textStr.equalsIgnoreCase("Big")) {
						bigEndian = true;
					}
					gotEndian = true;
					logger.trace("HandleDataTypes.decodeBasicData: BasicDataEndian: " + bigEndian);
				}
				continue;
			}
		}

		/*
		 * All elements found
		 */
		if (gotName && gotSize && gotEndian) {
			HlaDataBasicType hlaDataTypeBasic0 = new HlaDataBasicType(nameStr, size, bigEndian);
			/*
			 * Check if we already have the dataType
			 */
			HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(nameStr);
			if (tmpHlaDataType == null) {
				hlaDataTypes.dataTypeMap.put(nameStr, hlaDataTypeBasic0);
			} else {
				if (tmpHlaDataType instanceof HlaDataBasicType) {
					HlaDataBasicType tmpType = (HlaDataBasicType) tmpHlaDataType;
					if (tmpType.equalTo(hlaDataTypeBasic0)) {
						logger.info("HandleDataTypes.decodeBasicData: EQUAL DATA TYPE: " + nameStr + " IGNORED");
					} else {
						logger.info("HandleDataTypes.decodeBasicData: UNEQUAL DATA TYPE: " + nameStr + " NOT MERGED");
					}
				} else {
					logger.info("HandleDataTypes.decodeBasicData: UNEQUAL DATA TYPES: " + nameStr + " NOT MERGED");
				}
			}
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("HandleDataTypes.decodeBasicData: missing name");
		}
		if (gotSize == false) {
			logger.error("HandleDataTypes.decodeBasicData: missing size");
		}
		if (gotEndian == false) {
			logger.error("HandleDataTypes.decodeBasicData: missing endian");
		}

		throw new EncodingRulesException("decodeBasicData: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeBasicDataTypes(Node theSelectedNode) throws EncodingRulesException {
		String textPointer = null;

		/*
		 * Loop for all basicDataTypes
		 */
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					textPointer = ((Element) child).getFirstChild().getNodeValue();
					logger.trace("basicDataName: " + textPointer);
				}
				continue;
			}
			if (child.getNodeName().equals("basicData")) {
				decodeBasicData(child);
				continue;
			}
		}
		return;
	}
	
	private HlaDataBasicType checkAddBasicDefault(final String basicTypeName) throws EncodingRulesException {
		HlaDataBasicType hlaDataTypeBasic0 = null;
		HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(basicTypeName);

		if (tmpHlaDataType == null) {
			int size = HlaDataType.getBasicDataSizeBits(basicTypeName);
			if (size != 0) {
				boolean bigEndian = HlaDataType.getBigEndian(basicTypeName);
				hlaDataTypeBasic0 = new HlaDataBasicType(basicTypeName, size, bigEndian);
				hlaDataTypes.dataTypeMap.put(basicTypeName, hlaDataTypeBasic0);
			} else {
				throw new EncodingRulesException("checkAddBasicDefault: unknown basicDataType: " + basicTypeName + " NOT MERGED");
			}
		} else {
			if (tmpHlaDataType instanceof HlaDataBasicType) {
				hlaDataTypeBasic0 = (HlaDataBasicType) tmpHlaDataType;
			}
		}

		/*
		 * If the representation basicDataType not found, do not create the simpleDataType
		 */
		if (hlaDataTypeBasic0 == null) {
			throw new EncodingRulesException("checkAddBasicDefault: cannot get basicDataType: " + basicTypeName + " NOT MERGED");
		}

		return hlaDataTypeBasic0;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeSimpleData(Node theSelectedNode) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotRepresentation = false;
		String nameStr = null;
		String representationStr = null;
		HlaDataBasicType hlaDataTypeBasic0 = null;
		HlaDataSimpleType hlaDataSimpleType0 = null;

		/*
		 * Loop for all data for one simpleDataType
		 */
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("SimpleDataName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("representation")) {
				if (((Element) child).getFirstChild() != null) {
					representationStr = ((Element) child).getFirstChild().getNodeValue();
					gotRepresentation = true;
					logger.trace("SimpleDataRepresentation: " + representationStr);
				}
				continue;
			}
		}

		/*
		 * All elements found
		 */
		if (gotName && gotRepresentation) {
			/*
			 * Check if we already have the basicDataType, if not add a standard basicDataType
			 */
			hlaDataTypeBasic0 = checkAddBasicDefault(representationStr);

			/*
			 * Create the simpleDataType
			 */
			hlaDataSimpleType0 = new HlaDataSimpleType(nameStr, hlaDataTypeBasic0);

			/*
			 * Check if we already have the dataType
			 */
			HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(nameStr);
			if (tmpHlaDataType == null) {
				hlaDataTypes.dataTypeMap.put(nameStr, hlaDataSimpleType0);
			} else {
				if (tmpHlaDataType instanceof HlaDataSimpleType) {
					HlaDataSimpleType tmpType = (HlaDataSimpleType) tmpHlaDataType;
					if (tmpType.equalTo(hlaDataSimpleType0)) {
						logger.info("EQUAL DATA TYPE: " + nameStr + " IGNORED");
					} else {
						logger.info("UNEQUAL DATA TYPE: " + nameStr + " NOT MERGED");
					}
				}
			}
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("HandleDataTypes.decodeSimpleData: missing name");
		}
		if (gotRepresentation == false) {
			logger.error("HandleDataTypes.decodeSimpleData: missing representation");
		}

		throw new EncodingRulesException("decodeSimpleData: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeSimpleDataTypes(Node theSelectedNode) throws EncodingRulesException {
		String textPointer = null;

		/*
		 * Loop for all simpleDataTypes
		 */
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					textPointer = ((Element) child).getFirstChild().getNodeValue();
					logger.trace("simpleDataName: " + textPointer);
				}
				continue;
			}
			if (child.getNodeName().equals("simpleData")) {
				decodeSimpleData(child);
				continue;
			}
		}
		return;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @param enumValueMap map of enum values
	 * @throws EncodingRulesException
	 */
	private void decodeEnumerator(final Node theSelectedNode, final Map<Long, String> enumValueMap) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotValue = false;
		String nameStr = null;
		String valueStr = null;

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("EnumeratorName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("value")) {
				if (((Element) child).getFirstChild() != null) {
					valueStr = ((Element) child).getFirstChild().getNodeValue();
					gotValue = true;
					logger.trace("EnumeratorValue: " + valueStr);
				}
				continue;
			}
		}
		
		if (gotName && gotValue) {
			long l = Long.parseLong(valueStr);
			enumValueMap.put(l, nameStr);
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("decodeEnumerator: missing name");
		}
		if (gotValue == false) {
			logger.error("decodeEnumerator: missing value");
		}

		throw new EncodingRulesException("decodeEnumerator: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeEnumeratedData(Node theSelectedNode) throws EncodingRulesException {
		boolean gotEnumerator = false;
		boolean gotName = false;
		boolean gotRepresentation = false;
		String nameStr = null;
		String representationStr = null;
		HlaDataBasicType hlaDataTypeBasic0 = null;
		HlaDataEnumType hlaDataTypeEnumerated0 = null;
		Map<Long, String> enumValueMap = new LinkedHashMap<Long, String>();


		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("HandleDataTypes.decodeEnumeratedData: EnumeratedName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("representation")) {
				if (((Element) child).getFirstChild() != null) {
					representationStr = ((Element) child).getFirstChild().getNodeValue();
					gotRepresentation = true;
					logger.trace("HandleDataTypes.decodeEnumeratedData: EnumeratedRepresentation: " + representationStr);
				}
				continue;
			}
			if (child.getNodeName().equals("enumerator")) {
				decodeEnumerator(child, enumValueMap);
				gotEnumerator = true;
				continue;
			}
		}

		/*
		 * All elements found
		 */
		if (gotName && gotRepresentation && gotEnumerator) {
			/*
			 * Check if we already have the basicDataType, if not add a standard basicDataType
			 */
			hlaDataTypeBasic0 = checkAddBasicDefault(representationStr);

			/*
			 * Create the enumDataType
			 */
			hlaDataTypeEnumerated0 = new HlaDataEnumType(nameStr, hlaDataTypeBasic0, enumValueMap);

			/*
			 * Check if we already have the dataType
			 */
			HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(nameStr);
			if (tmpHlaDataType == null) {
				hlaDataTypes.dataTypeMap.put(nameStr, hlaDataTypeEnumerated0);
			} else {
				if (tmpHlaDataType instanceof HlaDataEnumType) {
					HlaDataEnumType tmpType = (HlaDataEnumType) tmpHlaDataType;
					if (tmpType.equalTo(hlaDataTypeEnumerated0)) {
						logger.info("HandleDataTypes.decodeEnumeratedData: EQUAL DATA TYPE: " + nameStr + " IGNORED");
					} else {
						logger.info("HandleDataTypes.decodeEnumeratedData: UNEQUAL DATA TYPE: " + nameStr + " NOT MERGED");
					}
				}
			}
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("HandleDataTypes.decodeEnumeratedData: missing name");
		}
		if (gotRepresentation == false) {
			logger.error("HandleDataTypes.decodeEnumeratedData: missing representation");
		}
		if (gotEnumerator == false) {
			logger.error("HandleDataTypes.decodeEnumeratedData: missing enumerator");
		}

		throw new EncodingRulesException("decodeEnumeratedData: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeEnumeratedDataTypes(Node theSelectedNode) throws EncodingRulesException {
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("enumeratedData")) {
				decodeEnumeratedData(child);
				continue;
			}
		}
		return;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeArrayData(Node theSelectedNode) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotDataType = false;
		boolean gotCardinality = false;
		int cardinality = 0;
		String nameStr = null;
		String dataTypeStr = null;
		String cardinalityStr = null;

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("ArrayDataName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("dataType")) {
				if (((Element) child).getFirstChild() != null) {
					dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
					gotDataType = true;
					logger.trace("ArrayDataType: " + dataTypeStr);
				}
				continue;
			}
			if (child.getNodeName().equals("cardinality")) {
				if (((Element) child).getFirstChild() != null) {
					cardinalityStr = ((Element) child).getFirstChild().getNodeValue();
					try {
						cardinality = Integer.parseInt(cardinalityStr);
					}
					catch(NumberFormatException e) {
						cardinality = 0;
					}
					gotCardinality = true;
					logger.trace("ArrayDataCardinality: " + cardinalityStr);
				}
				continue;
			}
		}
		
		/*
		 * Process the dataType
		 */
		if (gotName && gotDataType && gotCardinality) {
			HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(nameStr);
			HlaDataType tmpHlaDataTypeElement = hlaDataTypes.dataTypeMap.get(dataTypeStr);

			
			
			// To update later
			if (tmpHlaDataTypeElement == null) {
				missingTypes.put(nameStr, dataTypeStr);
				return;
			}

			if (cardinalityStr.equals("Dynamic")) {
				HlaDataVariableArrayType hlaDataTypeVariableArray = new HlaDataVariableArrayType(nameStr, tmpHlaDataTypeElement);
				if (tmpHlaDataType == null) {
					hlaDataTypes.dataTypeMap.put(nameStr, hlaDataTypeVariableArray);
				} else {
					if (tmpHlaDataType instanceof HlaDataVariableArrayType) {
						HlaDataVariableArrayType hlaDataVariableArrayType = (HlaDataVariableArrayType) tmpHlaDataType;
						if (hlaDataVariableArrayType.equalTo(hlaDataTypeVariableArray)) {
							logger.trace("HandleDataTypes.decodeFixedRecordData: EQUAL DATA TYPE: " + nameStr + " IGNORED");
						} else {
							logger.trace("HandleDataTypes.decodeFixedRecordData: UNEQUAL DATA TYPE: " + nameStr + " NOT MERGED");
						}
					} else {
						logger.trace("HandleDataTypes.decodeFixedRecordData: UNEQUAL DATA TYPES: " + nameStr + " NOT MERGED");
					}
				}
			} else {
				HlaDataFixedArrayType hlaDataTypeFixedArray = new HlaDataFixedArrayType(nameStr, dataTypeStr, tmpHlaDataTypeElement.getDataSize(), true, cardinality);
				if (tmpHlaDataType == null) {
					hlaDataTypes.dataTypeMap.put(nameStr, hlaDataTypeFixedArray);
				} else {
					if (tmpHlaDataType instanceof HlaDataFixedArrayType) {
						HlaDataFixedArrayType hlaDataVariableArrayType = (HlaDataFixedArrayType) tmpHlaDataType;
						if (hlaDataVariableArrayType.equalTo(hlaDataVariableArrayType)) {
							logger.trace("HandleDataTypes.decodeFixedRecordData: EQUAL DATA TYPE: " + nameStr + " IGNORED");
						} else {
							logger.trace("HandleDataTypes.decodeFixedRecordData: UNEQUAL DATA TYPE: " + nameStr + " NOT MERGED");
						}
					} else {
						logger.trace("HandleDataTypes.decodeFixedRecordData: UNEQUAL DATA TYPES: " + nameStr + " NOT MERGED");
					}
				}
			}
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("HandleDataTypes.decodeArrayData: missing name");
		}
		if (gotDataType == false) {
			logger.error("HandleDataTypes.decodeArrayData: missing dataType");
		}
		if (gotCardinality == false) {
			logger.error("HandleDataTypes.decodeArrayData: missing cardinality");
		}

		throw new EncodingRulesException("decodeArrayData: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeArrayDataTypes(Node theSelectedNode) throws EncodingRulesException {
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("arrayData")) {
				decodeArrayData(child);
				continue;
			}
		}
		return;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @param fields the name / data type map
	 * @throws EncodingRulesException
	 */
	private void decodeField(final Node theSelectedNode, List<String> fieldNames,  Map<String, String> fields) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotDataType = false;
		String nameStr = null;
		String dataTypeStr = null;

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					fieldNames.add(nameStr);
					gotName = true;
					logger.trace("FieldName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("dataType")) {
				if (((Element) child).getFirstChild() != null) {
					dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
					gotDataType = true;
					logger.trace("FieldDataType: " + dataTypeStr);
				}
				continue;
			}
		}
		
		/*
		 * Check if we already have the dataType
		 */
		HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(nameStr);
		if (tmpHlaDataType != null) {
			throw new EncodingRulesException("decodeField dataType not found: " + nameStr);
		}
		
		if (gotName && gotDataType) {
			String s = fields.get(nameStr);
			if (s == null) {
				fields.put(nameStr, dataTypeStr);
			} else {
				if (s.equals(dataTypeStr) == false) {
					throw new EncodingRulesException("HandleDataTypes.decodeField: duplicate field key: " + nameStr);
				}
			}
		}
		return;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeFixedRecordData(Node theSelectedNode) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotEncoding = false;
		boolean gotField = false;
		String nameStr = null;
		String textPointer = null;
		List<String> fieldNames = new LinkedList<String>();
		Map<String, String> fields = new HashMap<String, String>();

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("FixedRecordDataName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("encoding")) {
				if (((Element) child).getFirstChild() != null) {
					textPointer = ((Element) child).getFirstChild().getNodeValue();
					gotEncoding = true;
					logger.trace("FixedRecordDataEncoding: " + textPointer);
				}
				continue;
			}
			if (child.getNodeName().equals("field")) {
				decodeField(child, fieldNames, fields);
				gotField = true;
				continue;
			}
		}

		if (gotName && gotEncoding && gotField) {
			HlaDataFixedRecordType hlaDataTypeFixedRecord = new HlaDataFixedRecordType(nameStr, fieldNames, fields, false);

			HlaDataFixedRecordType tmpHlaDataType = (HlaDataFixedRecordType) hlaDataTypes.dataTypeMap.get(nameStr);
			if (tmpHlaDataType == null) {
				hlaDataTypes.dataTypeMap.put(nameStr, hlaDataTypeFixedRecord);
			} else {
				if (tmpHlaDataType.equalTo(hlaDataTypeFixedRecord)) {
					logger.trace("HandleDataTypes.decodeFixedRecordData: EQUAL DATA TYPE: " + nameStr + " IGNORED");
				} else {
					logger.trace("HandleDataTypes.decodeFixedRecordData: EQUAL DATA TYPE: " + nameStr + " NOT MERGED");
				}
			}
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("HandleDataTypes.decodeFixedRecordData: missing name");
		}
		if (gotEncoding == false) {
			logger.error("HandleDataTypes.decodeFixedRecordData: missing encoding");
		}
		if (gotField == false) {
			logger.error("HandleDataTypes.decodeFixedRecordData: missing field");
		}

		throw new EncodingRulesException("decodeFixedRecordData: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeFixedRecordDataTypes(Node theSelectedNode) throws EncodingRulesException {
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("fixedRecordData")) {
				decodeFixedRecordData(child);
				continue;
			}
		}
		return;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @param alternativeMap map of alternative name / dataType
	 * @throws EncodingRulesException
	 */
	private void decodeAlternative(Node theSelectedNode, Map<String, AlternativeStringPair> alternativeMap) throws EncodingRulesException {
		boolean gotEnumerator = false;
		boolean gotName = false;
		boolean gotDataType = false;
		String enumeratorStr = null;
		String nameStr = null;
		String dataTypeStr = null;

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("enumerator")) {
				if (((Element) child).getFirstChild() != null) {
					enumeratorStr = ((Element) child).getFirstChild().getNodeValue();
					gotEnumerator = true;
					logger.trace("AlternativeEnumerator: " + enumeratorStr);
				}
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("AlternativeName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("dataType")) {
				if (((Element) child).getFirstChild() != null) {
					dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
					gotDataType = true;
					logger.trace("AlternativeDataType: " + dataTypeStr);
				}
				continue;
			}
		}

		if (gotEnumerator && gotName && gotDataType) {
			AlternativeStringPair alternativeStringPair = new AlternativeStringPair(nameStr, dataTypeStr);
			AlternativeStringPair tmpAlternativeStringPair = alternativeMap.get(enumeratorStr);
			if (tmpAlternativeStringPair != null) {
				if (alternativeStringPair.equalTo(tmpAlternativeStringPair) == false) {
					throw new EncodingRulesException("decodeAlternative: alternativeStringPair enumeratorStr differ IGNORED");				}
			}
			alternativeMap.put(enumeratorStr, alternativeStringPair);
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotEnumerator == false) {
			logger.error("decodeAlternative: missing enumerator");
		}
		if (gotName == false) {
			logger.error("decodeAlternative: missing name");
		}
		if (gotDataType == false) {
			logger.error("decodeAlternative: missing dataType");
		}

		throw new EncodingRulesException("decodeAlternative: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeVariantRecordData(Node theSelectedNode) throws EncodingRulesException {
		boolean gotName = false;
		boolean gotDiscriminant = false;
		boolean gotDataType = false;
		boolean gotAlternative = false;
		Map<String, AlternativeStringPair> alternativeMap = new HashMap<String, AlternativeStringPair>();
		String nameStr = null;
		String discriminantStr = null;
		String dataTypeStr = null;

		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("name")) {
				if (((Element) child).getFirstChild() != null) {
					nameStr = ((Element) child).getFirstChild().getNodeValue();
					gotName = true;
					logger.trace("VariantRecordName: " + nameStr);
				}
				continue;
			}
			if (child.getNodeName().equals("discriminant")) {
				if (((Element) child).getFirstChild() != null) {
					discriminantStr = ((Element) child).getFirstChild().getNodeValue();
					gotDiscriminant = true;
					logger.trace("VariantRecordDiscriminant: " + discriminantStr);
				}
				continue;
			}
			if (child.getNodeName().equals("dataType")) {
				if (((Element) child).getFirstChild() != null) {
					dataTypeStr = ((Element) child).getFirstChild().getNodeValue();
					gotDataType = true;
					logger.trace("VariantRecordDataType: " + dataTypeStr);
				}
				continue;
			}
			if (child.getNodeName().equals("alternative")) {
				decodeAlternative(child, alternativeMap);
				gotAlternative = true;
				continue;
			}
		}
		
		if (gotName && gotDiscriminant && gotDataType && gotAlternative) {
			HlaDataVariantRecordType hlaDataTypeVariantRecord = new HlaDataVariantRecordType(nameStr, discriminantStr, dataTypeStr, alternativeMap);
			/*
			 * Check if we already have the dataType
			 */
			HlaDataType tmpHlaDataType = hlaDataTypes.dataTypeMap.get(nameStr);
			if (tmpHlaDataType == null) {
				hlaDataTypes.dataTypeMap.put(nameStr, hlaDataTypeVariantRecord);
			} else {
				if (tmpHlaDataType instanceof HlaDataVariantRecordType) {
					HlaDataVariantRecordType tmpType = (HlaDataVariantRecordType) tmpHlaDataType;
					if (tmpType.equalTo(hlaDataTypeVariantRecord)) {
						logger.trace("HandleDataTypes.decodeVariantRecordData: EQUAL DATA TYPE: " + nameStr + " IGNORED");
					} else {
						logger.trace("HandleDataTypes.decodeVariantRecordData: UNEQUAL DATA TYPE: " + nameStr + " NOT MERGED");
					}
				} else {
					logger.trace("HandleDataTypes.decodeVariantRecordData: UNEQUAL DATA TYPES: " + nameStr + " NOT MERGED");
				}
			}
			return;
		}

		/*
		 * Incomplete data
		 */
		if (gotName == false) {
			logger.error("HandleDataTypes.decodeVariantRecordData: missing name");
		}
		if (gotDiscriminant == false) {
			logger.error("HandleDataTypes.decodeVariantRecordData: missing discriminant");
		}
		if (gotDataType == false) {
			logger.error("HandleDataTypes.decodeVariantRecordData: missing dataType");
		}
		if (gotAlternative == false) {
			logger.error("HandleDataTypes.decodeVariantRecordData: missing alternative");
		}

		throw new EncodingRulesException("decodeVariantRecordData: missing data");
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @throws EncodingRulesException
	 */
	private void decodeVariantRecordDataTypes(Node theSelectedNode) throws EncodingRulesException {
		for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (child.getNodeName().equals("variantRecordData")) {
				decodeVariantRecordData(child);
				continue;
			}
		}
		return;
	}

	/**
	 * 
	 * @param theSelectedNode the Xerces node at this level
	 * @param hlaDataTypes
	 * @return
	 */
	boolean decode(final Node theSelectedNode, final HlaDataTypes hlaDataTypes) throws EncodingRulesException {
		String textPointer = null;

		this.hlaDataTypes = hlaDataTypes;
		if (theSelectedNode.getNodeType() == Node.ELEMENT_NODE) {
			textPointer = theSelectedNode.getNodeName();
			logger.trace("decode: " + textPointer);
		}
		try {
			// Need basicDataRepresentations first, all others refer to these
			for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				textPointer = child.getNodeName();
				if (child.getNodeName().equals("basicDataRepresentations")) {
					logger.trace("Got basicDataRepresentations!");
					decodeBasicDataTypes(child);
					continue;
				}
			}
			// Need simpleDataTypes and enumeratedDataTypes next, refer to basicDataRepresentations only and
			// are building stones for more complex types
			for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (child.getNodeName().equals("simpleDataTypes")) {
					logger.trace("Got simpleDataTypes!");
					decodeSimpleDataTypes(child);
					continue;
				}
				if (child.getNodeName().equals("enumeratedDataTypes")) {
					logger.trace("Got enumeratedDataTypes!");
					decodeEnumeratedDataTypes(child);
					continue;
				}
			}
			// All others refer to above in some manner
			for (Node child = theSelectedNode.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (child.getNodeName().equals("arrayDataTypes")) {
					logger.trace("Got arrayDataTypes!");
					decodeArrayDataTypes(child);
					continue;
				}
				if (child.getNodeName().equals("fixedRecordDataTypes")) {
					logger.trace("Got fixedRecordDataTypes!");
					decodeFixedRecordDataTypes(child);
					continue;
				}
				if (child.getNodeName().equals("variantRecordDataTypes")) {
					logger.trace("Got variantRecordDataTypes!");
					decodeVariantRecordDataTypes(child);
					continue;
				}
			}
		} catch (EncodingRulesException e) {
			logger.error("HandleDataTypes.decode: error: " + e);
			return true;
		}

		// Add any missing basic types
		addBasicType("HLAinteger64BE");
		addBasicType("HLAfloat64BE");
		addBasicType("HLAoctetPairBE");
		addBasicType("HLAoctet");

		// Add any missing simple types
		addSimpleType("HLAASCIIchar", "HLAoctet");
		addSimpleType("HLAunicodeChar", "HLAoctetPairBE");
		addSimpleType("HLAbyte", "HLAoctet");
		addSimpleType("HLAinteger64Time", "HLAinteger64BE");
		addSimpleType("HLAfloat64Time", "HLAfloat64BE");

		// Add any FOM/SOM missing types
		for (Map.Entry<String, String> entry : this.missingTypes.entrySet()) {
			HlaDataType hlaDataType = this.hlaDataTypes.dataTypeMap.get(entry.getValue());
			if (hlaDataType == null) {
				System.out.println("HandleDataTypes.decode missing data type: " + entry.getValue());
			} else {
				this.hlaDataTypes.dataTypeMap.put(entry.getKey() , hlaDataType);
			}
		}
		return false;
	}

	/**
	 * A method to add basic types not found in the FOM/SOM
	 *
	 * @param basicTypeName the name of the basic type to be created
	 */
	private void addBasicType(final String basicTypeName) {
		HlaDataType hlaDataType = this.hlaDataTypes.dataTypeMap.get(basicTypeName);
		if (hlaDataType == null) {
			HlaDataBasicType hlaDataBasicType = new HlaDataBasicType(basicTypeName, HlaDataType.getBasicDataSizeBits(basicTypeName), HlaDataType.getBigEndian(basicTypeName));
			this.hlaDataTypes.dataTypeMap.put(basicTypeName, hlaDataBasicType);
		}
	}

	/**
	 * A method to add simple types not found in the FOM/SOM
	 *
	 * @param simpleTypeName the name of the simple type
	 * @param basicTypeName the name of the underlying basic type
	 * @throws EncodingRulesException when an error is detected
	 */
	private void addSimpleType(final String simpleTypeName, final String basicTypeName) throws EncodingRulesException {
		HlaDataType hlaDataTypeHLAASCIIchar = this.hlaDataTypes.dataTypeMap.get(simpleTypeName);
		if (hlaDataTypeHLAASCIIchar == null) {
			HlaDataType hlaDataTypeHLAoctet = this.hlaDataTypes.dataTypeMap.get(basicTypeName);
			if (hlaDataTypeHLAoctet == null) {
				throw new EncodingRulesException("HandleDataTypes.addSimpleType: basicTypeName not found: " + basicTypeName);
			}
			HlaDataSimpleType hlaDataSimpleTypeHLAASCIIchar = new HlaDataSimpleType(simpleTypeName, (HlaDataBasicType) hlaDataTypeHLAoctet);
			this.hlaDataTypes.dataTypeMap.put(simpleTypeName, hlaDataSimpleTypeHLAASCIIchar);
		}
	}
}
