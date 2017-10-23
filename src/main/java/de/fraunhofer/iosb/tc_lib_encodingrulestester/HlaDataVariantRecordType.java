package de.fraunhofer.iosb.ivct;

import java.util.LinkedHashMap;
import java.util.Map;

public class HlaDataVariantRecordType extends HlaDataType {
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
	 * @param dataTypeName
	 * @param discriminantName
	 * @param discriminantType
	 * @param alternativeMap
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
	 * @param other
	 * @return
	 */
	public boolean equalTo(HlaDataVariantRecordType other) {
		if (this.dataTypeName.equals(other.dataTypeName) == false) {
			System.out.println("HlaDataVariantRecordType data name inconsistency: " + dataTypeName + " IGNORED");
			return false;
		}
		if (this.discriminantName.equals(other.discriminantName) == false) {
			System.out.println("HlaDataVariantRecordType discriminantName inconsistency: " + discriminantName + " IGNORED");
			return false;
		}
		if (this.discriminantType.equals(other.discriminantType) == false) {
			System.out.println("HlaDataVariantRecordType discriminantType inconsistency: " + discriminantType + " IGNORED");
			return false;
		}
		
		if (alternativeMap.size() != other.alternativeMap.size()) {
			return false;
		}
		// TODO
		// alternativeMap equality
		for (Map.Entry<String, AlternativeStringPair> entry : alternativeMap.entrySet()) {
			AlternativeStringPair alternativeStringPair = entry.getValue();
			AlternativeStringPair tmpAlternativeStringPair = other.alternativeMap.get(entry.getKey());
			if (alternativeStringPair.equalTo(tmpAlternativeStringPair) == false) {
				System.out.println("HlaDataVariantRecordType alternativeMap inconsistency: " + entry.getKey() + " IGNORED");
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getAlignment(final HlaDataTypes dataTypes) {
		int ret = 0;
		HlaDataType myHlaDataType = dataTypes.dataTypeMap.get(discriminantType);
		if (myHlaDataType != null) {
			ret = myHlaDataType.getAlignment(dataTypes);
		}
		return ret;
	}

	/**
	 * Get the maximum alignment of the alternative types
	 */
	public int getAlternativeAlignment(final HlaDataTypes dataTypes) {
		int ret = 0;
		int testVal = 0;
		HlaDataType myHlaDataType = null;
		for (Map.Entry<String, AlternativeStringPair> entry : alternativeMap.entrySet()) {
			AlternativeStringPair asp = entry.getValue();
			if (asp.classType.contentEquals("NA")) {
				continue;
			}
			myHlaDataType = dataTypes.dataTypeMap.get(asp.classType);
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
		if (hlaDataType instanceof HlaDataEnumType) {
			hlaDataEnumType = (HlaDataEnumType) hlaDataType;
		} else {
			throw new EncodingRulesException("HlaDataVariantRecordType: unknown discriminant enum " + hlaDataType.dataTypeName);
		}
		String basicType = hlaDataEnumType.getElementTypeName();
		long discriminantValue = decodeInteger(buffer, currentPosition, basicType);
		myCurrentPosition += hlaDataEnumType.dataSize;

		/*
		 * Get the value based on the enum defined type
		 */
		String s = hlaDataEnumType.getEnumString(discriminantValue);
		AlternativeStringPair alt = alternativeMap.get(s);
		HlaDataType hlaDataTypeTmp;
		hlaDataTypeTmp = dataTypes.dataTypeMap.get(alt.classType);

		/*
		 * Get alignment of the alternatives
		 */
		int myAlignment = getAlternativeAlignment(dataTypes);
		myCurrentPosition += calcPaddingBytes(myCurrentPosition, myAlignment);
		myCurrentPosition = hlaDataTypeTmp.testBuffer(buffer, myCurrentPosition, dataTypes);
		return myCurrentPosition;
	}
}
