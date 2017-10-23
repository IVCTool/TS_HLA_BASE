package de.fraunhofer.iosb.ivct;

import java.util.LinkedHashMap;
import java.util.Map;


public class HlaDataEnumType extends HlaDataType {
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
	private void testEnumValue(final byte[] buffer, final int currentPosition) throws EncodingRulesException {
		long testVal = decodeInteger(buffer, currentPosition, elementType);
		String s = enumValueMap.get(testVal);
		if (s == null) {
			throw new EncodingRulesException("Enum value unknown: " + testVal);
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
		testEnumValue(buffer, myCurrentPosition);
		myCurrentPosition += dataSize;
		return myCurrentPosition;
	}
}
