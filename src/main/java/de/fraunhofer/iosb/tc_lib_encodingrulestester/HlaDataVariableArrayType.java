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
	 * The number of octets in the datatype
	 */
	int dataSize;

	/**
	 * 
	 */
	String elementType;

	/**
	 * 
	 * @param dataTypeName
	 * @param hlaDataTypeElement
	 */
	public HlaDataVariableArrayType(final String dataTypeName, final HlaDataType hlaDataTypeElement) {
		this.dataTypeName = dataTypeName;
		this.elementType = hlaDataTypeElement.dataTypeName;
		this.dataSizeFixed = false;
		this.dataSize = hlaDataTypeElement.getDataSize();
		this.alignment = calcAlignment(dataSize);
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
	public int getAlignment(final HlaDataTypes dataTypes) {
		HlaDataType hlaDataType = dataTypes.dataTypeMap.get(elementType);
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
	 * {@inheritDoc}
	 */
	public int testBuffer(final byte[] buffer, final int currentPosition, final HlaDataTypes dataTypes) throws EncodingRulesException {
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
		myCurrentPosition += 4;
		if (myCurrentPosition + lengthValue > buffer.length) {
			String errorMessageString = "HlaDataVariableArrayType: testBuffer: field value length : " + myCurrentPosition + lengthValue + " exceeds buffer length: " + buffer.length;
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
		if (currentPosition > buffer.length) {
			String errorMessageString = "HlaDataVariableArrayType: testBuffer: field value length : " + currentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return myCurrentPosition;
	}
}
