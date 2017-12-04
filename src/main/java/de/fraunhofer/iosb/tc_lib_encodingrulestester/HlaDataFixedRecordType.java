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

public class HlaDataFixedRecordType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataFixedRecordType.class);
	/**
	 * The number of octets in the datatype
	 */
	protected int dataSize;
	
	/**
	 *  For records
	 * <Field name, Type Name> 
	 */
	Map<String, String> fields;

	//
	public HlaDataFixedRecordType(final String dataTypeName, final boolean dataSizeFixed) {
		this.dataTypeName = dataTypeName;
		this.dataSizeFixed = dataSizeFixed;
		// Has to be calculated based on alignment of fields
		this.alignment = 0;
		fields = new LinkedHashMap<String, String>();
	}
	
	//
	public HlaDataFixedRecordType(final String dataTypeName, final Map<String, String> fields, final boolean dataSizeFixed) {
		this.dataTypeName = dataTypeName;
		this.dataSizeFixed = dataSizeFixed;
		// Has to be calculated based on alignment of fields
		this.alignment = 0;
		this.fields = fields;
	}
	
	public boolean equalTo(HlaDataFixedRecordType other) {
		if (fields.size() != other.fields.size()) {
			return false;
		}
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			if (entry.getValue().equals(other.fields.get(entry.getKey()))) {
				continue;
			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param theName
	 * @param theType
	 */
	public void addField(String theName, String theType) {
		fields.put(theName, theType);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) {
		int ret = 0;
		int testVal = 0;
		HlaDataType myHlaDataType = null;
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			myHlaDataType = dataTypes.dataTypeMap.get(entry.getValue());
			testVal = myHlaDataType.getAlignment(dataTypes);
			if (testVal > ret) {
				ret = testVal;
			}
		}
		return ret;
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
		return dataSize;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getElementTypeName() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int testBuffer(final byte[] buffer, final int currentPosition, final HlaDataTypes dataTypes) throws EncodingRulesException {
		int myCurrentPosition = currentPosition;
		for (Map.Entry<String, String> field : fields.entrySet()) {
			HlaDataType hlaDataType = dataTypes.dataTypeMap.get(field.getValue());
			myCurrentPosition += calcPaddingBytes(myCurrentPosition, "HLAinteger32BE", dataTypes);
			myCurrentPosition = hlaDataType.testBuffer(buffer, myCurrentPosition, dataTypes);
		}
		if (currentPosition > buffer.length) {
			String errorMessageString = "HlaDataFixedRecordType: testBuffer: calculated value length : " + currentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return myCurrentPosition;
	}
}
