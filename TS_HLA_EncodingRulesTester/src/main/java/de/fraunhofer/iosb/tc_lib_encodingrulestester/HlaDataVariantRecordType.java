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

public class HlaDataVariantRecordType extends HlaDataType {
    private static Logger logger = LoggerFactory.getLogger(HlaDataVariantRecordType.class);
	/**
	 * The number of octets in the datatype
	 */
	protected int dataSize;
	

	/**
	 *  For variant records
	 * Discriminant
	 */
	protected String discriminantName;
	protected String discriminantType;

	/**
	 *  <Enum <alternative name, alternative type>>
	 */
	Map<String, AlternativeStringPair> alternativeMap;


	/**
	 * 
	 * @param dataTypeName the data type name
	 * @param discriminantName the discriminant name
	 * @param discriminantType the discriminant type
	 * @param alternativeMap the alternative map
	 */
	public HlaDataVariantRecordType(final String dataTypeName, final String discriminantName, final String discriminantType, final Map<String, AlternativeStringPair> alternativeMap) {
		this.dataTypeName = dataTypeName;
		//TODO
		this.dataSizeFixed = false;
		this.discriminantName = discriminantName;
		this.discriminantType = discriminantType;
		this.alternativeMap = alternativeMap;
	}
	
	/**
	 * 
	 * @param other the other variant record data type
	 * @return true if equal, else false
	 */
	public boolean equalTo(HlaDataVariantRecordType other) {
		if (this.dataTypeName.equals(other.dataTypeName) == false) {
			logger.info("HlaDataVariantRecordType data name inconsistency: " + dataTypeName + " IGNORED");
			return false;
		}
		if (this.discriminantName.equals(other.discriminantName) == false) {
			logger.info("HlaDataVariantRecordType discriminantName inconsistency: " + discriminantName + " IGNORED");
			return false;
		}
		if (this.discriminantType.equals(other.discriminantType) == false) {
			logger.info("HlaDataVariantRecordType discriminantType inconsistency: " + discriminantType + " IGNORED");
			return false;
		}
		
		if (alternativeMap.size() != other.alternativeMap.size()) {
			return false;
		}
		// TODO
		// alternativeMap equality
		for (Map.Entry<String, AlternativeStringPair> entry : alternativeMap.entrySet()) {
			AlternativeStringPair alternativeStringPair = entry.getValue();
			String k = entry.getKey();
			AlternativeStringPair tmpAlternativeStringPair = other.alternativeMap.get(k);
			if (tmpAlternativeStringPair == null) {
				logger.error("HlaDataVariantRecordType.equalTo: cannot find alternative string pair for: " + k);
				continue;
			}
			if (alternativeStringPair.equalTo(tmpAlternativeStringPair) == false) {
				logger.info("HlaDataVariantRecordType alternativeMap inconsistency: " + entry.getKey() + " IGNORED");
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) throws EncodingRulesException {
		int ret = 0;
		HlaDataType myHlaDataType = dataTypes.dataTypeMap.get(discriminantType);
		if (myHlaDataType != null) {
			ret = myHlaDataType.getAlignment(dataTypes);
		}
		return ret;
	}

	/**
	 * Get the maximum alignment of the alternative types
	 * @param dataTypes the map of data types
	 * @return the alignment value
	 * @throws EncodingRulesException exception with text
	 */
	public int getAlternativeAlignment(final HlaDataTypes dataTypes) throws EncodingRulesException {
		int ret = 0;
		int testVal = 0;
		HlaDataType myHlaDataType = null;
		for (Map.Entry<String, AlternativeStringPair> entry : alternativeMap.entrySet()) {
			AlternativeStringPair asp = entry.getValue();
			if (asp.classType.contentEquals("NA")) {
				continue;
			}
			myHlaDataType = dataTypes.dataTypeMap.get(asp.classType);
			if (myHlaDataType == null) {
				String errorMessageString = "HlaDataVariantRecordType: getAlternativeAlignment: cannot find alternative string pair data element type: " + asp.classType;
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
		/*
		 * Get the enum discriminant
		 */
		int myCurrentPosition = currentPosition;
		HlaDataEnumType hlaDataEnumType;
		HlaDataType hlaDataType = dataTypes.dataTypeMap.get(discriminantType);
		if (hlaDataType == null) {
			String errorMessageString = "HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " cannot find discriminantType: " + discriminantType;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		if (hlaDataType instanceof HlaDataEnumType) {
			hlaDataEnumType = (HlaDataEnumType) hlaDataType;
		} else {
			throw new EncodingRulesException("HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " unknown discriminant enum " + hlaDataType.dataTypeName);
		}
		String basicType = hlaDataEnumType.getElementTypeName();
		long discriminantValue = decodeInteger(buffer, currentPosition, basicType);
		myCurrentPosition += hlaDataEnumType.dataSize;
		if (myCurrentPosition > buffer.length) {
			String errorMessageString = "HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " calculated total buffer length : " + myCurrentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}

		/*
		 * Get the value based on the enum defined type
		 */
		String s = hlaDataEnumType.getEnumString(discriminantValue);
		AlternativeStringPair alt = alternativeMap.get(s);
		if (alt == null) {
			String errorMessageString = "HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " discriminantValue leads to null pointer in alternativeMap";
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		HlaDataType hlaDataTypeTmp;
		hlaDataTypeTmp = dataTypes.dataTypeMap.get(alt.classType);
		if (hlaDataTypeTmp == null) {
			String errorMessageString = "HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " cannot get alternative class type " + alt.classType;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}

		/*
		 * Get alignment of the alternatives
		 */
		int myAlignment = getAlternativeAlignment(dataTypes);
		myCurrentPosition += calcPaddingBytes(myCurrentPosition, myAlignment);
		if (myCurrentPosition > buffer.length) {
			String errorMessageString = "HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " calculated alignment position : " + myCurrentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		myCurrentPosition = hlaDataTypeTmp.testBuffer(buffer, myCurrentPosition, dataTypes);
		if (myCurrentPosition > buffer.length) {
			String errorMessageString = "HlaDataVariantRecordType: testBuffer: current position: " + currentPosition + " calculated total value length : " + myCurrentPosition + " exceeds buffer length: " + buffer.length;
			logger.error(errorMessageString);
			throw new EncodingRulesException(errorMessageString);
		}
		return myCurrentPosition;
	}
}
