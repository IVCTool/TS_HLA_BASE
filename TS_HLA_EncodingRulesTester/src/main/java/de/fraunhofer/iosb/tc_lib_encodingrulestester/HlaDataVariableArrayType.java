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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HlaDataVariableArrayType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataVariableArrayType.class);
    /**
     *
     */
    private static boolean useRPRv2_0 = false;
	/**
	 * The number of octets in the datatype
	 */
	int dataSize;

	// The encoding value from the SOM/FOM file.
	String encoding;

	/**
	 * 
	 */
	String elementType;

	/**
	 * 
	 * @param dataTypeName the data type name
	 * @param hlaDataTypeElement the data type element
	 * @param encoding the encoding value from FOM / SOM
	 */
	public HlaDataVariableArrayType(final String dataTypeName, final HlaDataType hlaDataTypeElement, final String encoding) {
		this.dataTypeName = dataTypeName;
		if (hlaDataTypeElement != null) {
			this.elementType = hlaDataTypeElement.dataTypeName;
			this.dataSize = hlaDataTypeElement.getDataSize();
		}
		this.dataSizeFixed = false;
		this.alignment = calcAlignment(dataSize);
		this.encoding = encoding;
	}

	public static void setRPRv2_0() {
		useRPRv2_0 = true;
	}

	public boolean equalTo(HlaDataVariableArrayType other) {
		if ((this.dataTypeName.equals(other.dataTypeName) && this.elementType.equals(other.elementType)) == false) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) throws EncodingRulesException {
		HlaDataType hlaDataType = dataTypes.dataTypeMap.get(elementType);
		if (hlaDataType == null) {
			String errorMessageString = "HlaDataVariableArrayType: getAlignment: cannot find data element type: " + elementType;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return hlaDataType.getAlignment(dataTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getDataSizeFixed() {
		return this.dataSizeFixed;
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
	 *
	 * @param hlaDataTypeElement the data type
	 */
	public void setDataType(final HlaDataType hlaDataTypeElement) {
		this.elementType = hlaDataTypeElement.dataTypeName;
		this.dataSize = hlaDataTypeElement.getDataSize();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int testBuffer(final byte[] buffer, final int currentPosition, final HlaDataTypes dataTypes) throws EncodingRulesException {
		if (encoding.equals("HLAvariableArray") == false) {
			if (useRPRv2_0) {
				int len = buffer.length;
				for (int i = 0; i < len; i++) {
					if (buffer[currentPosition + i] == 0) {
						return currentPosition + i + 1;
					}
				}
			}
			else {
				String errorMessageString = "HlaDataVariableArrayType: testBuffer: user defined encoding not supported: " + encoding;
				throw new EncodingRulesException(errorMessageString);
			}
		}
		int myCurrentPosition = currentPosition;
		int lengthValue = 0;
		lengthValue += buffer[myCurrentPosition];
		lengthValue <<= 8;
		lengthValue += buffer[myCurrentPosition + 1];
		lengthValue <<= 8;
		lengthValue += buffer[myCurrentPosition + 2];
		lengthValue <<= 8;
		lengthValue += buffer[myCurrentPosition + 3];
		HlaDataType elementDataType = dataTypes.dataTypeMap.get(elementType);
		if (elementDataType == null) {
			String errorMessageString = "HlaDataVariableArrayType: testBuffer: current position: " + currentPosition + " cannot find data element type: " + elementType;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		myCurrentPosition += 4;
		if (myCurrentPosition + lengthValue > buffer.length) {
			String errorMessageString = "HlaDataVariableArrayType: testBuffer: current position: " + currentPosition + " field value length: " + myCurrentPosition + lengthValue + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		if (elementDataType.getDataSizeFixed()) {
			myCurrentPosition += calcPaddingBytes(myCurrentPosition, elementDataType.dataTypeName, dataTypes);
			myCurrentPosition += lengthValue * elementDataType.getDataSize() + (lengthValue - 1) * (this.alignment - dataSize);
		} else {
			for (int i = 0; i < lengthValue; i++) {
				myCurrentPosition += calcPaddingBytes(myCurrentPosition, elementDataType.dataTypeName, dataTypes);
				myCurrentPosition = elementDataType.testBuffer(buffer, myCurrentPosition, dataTypes);
			}
		}
		if (myCurrentPosition > buffer.length) {
			String errorMessageString = "HlaDataVariableArrayType: testBuffer: current position: " + currentPosition + " calculated total buffer length: " + myCurrentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return myCurrentPosition;
	}
}
