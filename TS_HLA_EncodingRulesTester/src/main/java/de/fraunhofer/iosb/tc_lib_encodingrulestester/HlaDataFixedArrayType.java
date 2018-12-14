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

public class HlaDataFixedArrayType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataBasicType.class);
	/**
	 * The number of octets in the datatype
	 */
	private int dataSize;

	/**
	 * For fixed arrays
	 */
	private int cardinality;

	/**
	 * 
	 */
	String elementType;

	/**
	 * @param dataTypeName data type name
	 * @param elementType the element type
	 * @param dataSize the data size
	 * @param dataSizeFixed if the data size is fixed
	 * @param cardinality the cardinality
	 */
	public HlaDataFixedArrayType(final String dataTypeName, final String elementType, final int dataSize, final boolean dataSizeFixed, final int cardinality) {
		this.dataTypeName = dataTypeName;
		this.elementType = elementType;
		this.dataSize = dataSize;
		this.dataSizeFixed = dataSizeFixed;
		this.alignment = calcAlignment(dataSize);
		this.cardinality = cardinality;
	}

	public boolean equalTo(HlaDataFixedArrayType other) {
		if ((this.dataTypeName.equals(other.dataTypeName) && this.elementType.equals(other.elementType)) == false) {
			return false;
		}
		if (this.dataSizeFixed != other.dataSizeFixed) {
			return false;
		}
		if (this.cardinality != other.cardinality) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @throws EncodingRulesException
	 */
	public int getAlignment(final HlaDataTypes dataTypes) throws EncodingRulesException {
		HlaDataType hlaDataType = dataTypes.dataTypeMap.get(elementType);
		if (hlaDataType == null) {
			String errorMessageString = "HlaDataFixedArrayType: getAlignment: cannot find data element type: " + elementType;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return hlaDataType.getAlignment(dataTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getDataSizeFixed() {
		return dataSizeFixed;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getDataSize() {
		return dataSize * cardinality;
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
		if (dataSizeFixed) {
			myCurrentPosition += cardinality * dataSize;
		} else {
			HlaDataType hlaDataType = dataTypes.dataTypeMap.get(elementType);
			if (hlaDataType == null) {
				String errorMessageString = "HlaDataFixedArrayType: testBuffer: current position: " + currentPosition + " cannot find data element type " + elementType;
				logger.error(errorMessageString);
				throw new EncodingRulesException(errorMessageString);
			}
			myCurrentPosition = hlaDataType.testBuffer(buffer, myCurrentPosition, dataTypes);
		}
		if (myCurrentPosition > buffer.length) {
			String errorMessageString = "HlaDataFixedArrayType: testBuffer: currentPosition: " + currentPosition + " calculated total buffer length : " + myCurrentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return myCurrentPosition;
	}
}
