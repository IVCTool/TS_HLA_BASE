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

public class HlaDataSimpleType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataSimpleType.class);
	/**
	 * The number of octets in the datatype
	 */
	protected int dataSize;
	
	/**
	 * the basic datatype name underlying the simple datatype
	 */
	String elementType;

	/**
	 * 
	 * @param dataTypeName the data type name
	 * @param hlaDataBasicType the underlying basic type
	 */
	public HlaDataSimpleType(final String dataTypeName, final HlaDataBasicType hlaDataBasicType) {
		this.dataTypeName = dataTypeName;
		this.elementType = hlaDataBasicType.dataTypeName;
		this.dataSize = hlaDataBasicType.getDataSize();
		this.alignment = calcAlignment(dataSize);
	}

	public boolean equalTo(HlaDataSimpleType other) {
		boolean result;
		result = (this.dataTypeName.equals(other.dataTypeName) && this.elementType.equals(other.elementType));
		return result;
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
		if (currentPosition + this.dataSize > buffer.length) {
			String errorMessageString = "HlaDataSimpleType: testBuffer: current position: " + currentPosition + " plus field value length : " + this.dataSize + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return currentPosition + dataSize;
	}
}
