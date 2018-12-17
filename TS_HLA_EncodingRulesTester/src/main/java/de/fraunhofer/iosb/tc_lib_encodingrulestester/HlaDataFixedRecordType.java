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

import java.util.LinkedList;
import java.util.List;
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
	private Map<String, String> fields;
	private List<String> fieldNamesOrdered;

	//
	public HlaDataFixedRecordType(final String dataTypeName, final boolean dataSizeFixed) {
		this.dataTypeName = dataTypeName;
		this.dataSizeFixed = dataSizeFixed;
		// Has to be calculated based on alignment of fields
		this.alignment = 0;
		fields = new LinkedHashMap<String, String>();
	}
	
	//
	public HlaDataFixedRecordType(final String dataTypeName, final List<String> fieldNamesOrdered, final Map<String, String> fields, final boolean dataSizeFixed) {
		this.dataTypeName = dataTypeName;
		this.dataSizeFixed = dataSizeFixed;
		// Has to be calculated based on alignment of fields
		this.alignment = 0;
		this.fieldNamesOrdered = fieldNamesOrdered;
		this.fields = fields;
	}

	public boolean equalTo(HlaDataFixedRecordType other) {
		if (fields.size() != other.fields.size()) {
			return false;
		}
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			String othersValue = other.fields.get(entry.getKey());
			if (othersValue == null) {
				return false;
			}
			if (entry.getValue().equals(othersValue)) {
				continue;
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param theName the field name
	 * @param theType the field type
	 */
	public void addField(String theName, String theType) {
		fields.put(theName, theType);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) throws EncodingRulesException {
		int ret = 0;
		int testVal = 0;
		HlaDataType myHlaDataType = null;
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			myHlaDataType = dataTypes.dataTypeMap.get(entry.getValue());
			if (myHlaDataType == null) {
				String errorMessageString = "HlaDataFixedRecordType: getAlignment: cannot find data element type: " + entry.getValue();
				logger.error(errorMessageString);
				throw new EncodingRulesException(errorMessageString);
			}
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
		for (String fieldName : fieldNamesOrdered) {
			String fieldType = fields.get(fieldName);
			if (fieldType == null) {
				String errorMessageString = "HlaDataFixedRecordType: testBuffer: current position: " + currentPosition + " calculated total buffer length : " + myCurrentPosition + " cannot get fieldName: " + fieldName;
				logger.error(errorMessageString);
				throw new EncodingRulesException(errorMessageString);
			}
			HlaDataType hlaDataType = dataTypes.dataTypeMap.get(fieldType);
			if (hlaDataType == null) {
				String errorMessageString = "HlaDataFixedRecordType: testBuffer: current position: " + currentPosition + " calculated total buffer length : " + myCurrentPosition + " cannot get data type: " + fieldType;
				logger.error(errorMessageString);
				throw new EncodingRulesException(errorMessageString);
			}
			int alignment = hlaDataType.getAlignment(dataTypes);
			myCurrentPosition += HlaDataType.calcPaddingBytes(myCurrentPosition, alignment);
			myCurrentPosition = hlaDataType.testBuffer(buffer, myCurrentPosition, dataTypes);
		}
		if (myCurrentPosition > buffer.length) {
			String errorMessageString = "HlaDataFixedRecordType: testBuffer: current position: " + currentPosition + " calculated total buffer length : " + myCurrentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return myCurrentPosition;
	}
}
