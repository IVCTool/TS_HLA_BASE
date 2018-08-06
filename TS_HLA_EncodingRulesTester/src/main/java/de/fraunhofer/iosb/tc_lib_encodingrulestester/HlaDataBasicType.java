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

public class HlaDataBasicType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataBasicType.class);
	/**
	 * The number of octets in the datatype
	 */
	int dataSize;
	
	/**
	 * 
	 */
	boolean bigEndian;
	
	/**
	 * Set some values based on dataTypeName defined in OMT standard
	 *
	 * @param dataTypeName name of the data type
	 * @param dataSizeBits number of bits in the data type
	 * @param bigEndian whether data is big endian
	 */
	public HlaDataBasicType(final String dataTypeName, final int dataSizeBits, final boolean bigEndian) {
		this.dataTypeName = dataTypeName;
		// Always work in octets
		this.dataSize = dataSizeBits / 8;
		this.bigEndian = bigEndian;
		this.alignment = calcAlignment(this.dataSize);
	}

	public boolean equalTo(HlaDataBasicType other) {
		boolean result;
		result = (this.dataTypeName.equals(other.dataTypeName) && this.dataSize == other.dataSize);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) {
		return alignment;
	}

	/**
	 * @return whether dataSize is a fixed value
	 */
	public boolean getDataSizeFixed() {
		return true;
	}

	/**
	 * @return dataSize
	 */
	public int getDataSize() {
		return this.dataSize;
	}

	/**
	 * @return always return null since this is already the basic datatype level
	 */
	public String getElementTypeName() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int testBuffer(final byte[] buffer, final int currentPosition, final HlaDataTypes dataTypes) throws EncodingRulesException {
		if (currentPosition + this.dataSize > buffer.length) {
			String errorMessageString = "HlaDataBasicType: testBuffer: current position: " + currentPosition + " plus field value length : " + this.dataSize + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return currentPosition + this.dataSize;
	}
}
