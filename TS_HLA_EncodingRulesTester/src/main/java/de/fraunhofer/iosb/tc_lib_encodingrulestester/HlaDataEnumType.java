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

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HlaDataEnumType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataEnumType.class);
	/**
	 * The number of octets in the datatype
	 */
	int dataSize;
	
	/**
	 * 
	 */
	private String elementType;

	/**
	 *  For enums, the key is the value extracted from the buffer.
	 *  If the key exists, the value is considered valid.
	 */
	private Map<Long, String> enumValueMap;
	
	/**
	 * 
	 * @param enumKey the key is the value extracted from the buffer.
	 * @return the enum value from FOM/SOM
	 */
	String getEnumString(final long enumKey) {
		return enumValueMap.get(enumKey);
	}
	
	/**
	 * @param buffer the HLA reflectAttributeValue or receiveInteraction parameter value
	 * @param currentPosition the starting position for the current evaluation
	 */
	private void testEnumValue(final byte[] buffer, final int currentPosition, final String fundamentalType) throws EncodingRulesException {
		long testVal = decodeInteger(buffer, currentPosition, fundamentalType);
		String s = enumValueMap.get(testVal);
		if (s == null) {
			throw new EncodingRulesException("Enum value unknown: " + currentPosition + " " + fundamentalType + " " + testVal);
		}
	}

	/**
	 * 
	 * @param dataTypeName the enumeration name from FOM/SOM
	 * @param hlaDataBasicType the mapped to datatype
	 */
	public HlaDataEnumType(final String dataTypeName, final HlaDataBasicType hlaDataBasicType) {
		this.dataTypeName = dataTypeName;
		this.elementType = hlaDataBasicType.dataTypeName;
		this.dataSize = hlaDataBasicType.getDataSize();
		this.alignment = calcAlignment(dataSize);
		enumValueMap = new LinkedHashMap<Long, String>();
	}
	
	/**
	 * 
	 * @param dataTypeName the enumeration name from FOM/SOM
	 * @param hlaDataBasicType the mapped to datatype
	 * @param enumValueMap the mapped to datatype
	 */
	public HlaDataEnumType(final String dataTypeName, final HlaDataBasicType hlaDataBasicType, final Map<Long, String> enumValueMap) {
		this.dataTypeName = dataTypeName;
		this.elementType = hlaDataBasicType.dataTypeName;
		this.dataSize = hlaDataBasicType.getDataSize();
		this.alignment = calcAlignment(dataSize);
		this.enumValueMap = enumValueMap;
	}
	
	public boolean equalTo(HlaDataEnumType other) {
		boolean result;
		result = (this.dataTypeName.equals(other.dataTypeName) && this.elementType.equals(other.elementType));
		return result;
	}

	/**
	 * 
	 * @param theKey the enumeration value from FOM/SOM
	 * @param theName the enumeration name from FOM/SOM
	 */
	public void addEnumValue(Long theKey, String theName) {
		enumValueMap.put(theKey, theName);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) {
		return alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getDataSizeFixed() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getDataSize() {
		return dataSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getElementTypeName() {
		return elementType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int testBuffer(final byte[] buffer, final int currentPosition, final HlaDataTypes dataTypes) throws EncodingRulesException {
		int myCurrentPosition = currentPosition;
		if (myCurrentPosition + this.dataSize > buffer.length) {
			String errorMessageString = "HlaDataEnumType: testBuffer: current position " + myCurrentPosition + " plus field value length : " + this.dataSize + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		HlaDataBasicType hlaDataType = (HlaDataBasicType) dataTypes.dataTypeMap.get(elementType);
		if (hlaDataType == null) {
			throw new EncodingRulesException("HlaDataEnumType.testBuffer: Type not found: " + elementType);
		}
		else {
			int elementTypeSize = hlaDataType.getDataSize();
			boolean b = hlaDataType.bigEndian;
			String s = getFundamentalType(elementTypeSize, b);
			testEnumValue(buffer, myCurrentPosition, s);
		}
		myCurrentPosition += dataSize;
		return myCurrentPosition;
	}
}
