package de.fraunhofer.iosb.tc_lib_encodingrulestester;

public class HlaDataFixedArrayType extends HlaDataType {
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
	 *
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
	 */
	public int getAlignment(final HlaDataTypes dataTypes) {
		HlaDataType hlaDataType = dataTypes.dataTypeMap.get(elementType);
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
			myCurrentPosition = hlaDataType.testBuffer(buffer, myCurrentPosition, dataTypes);
		}
		return myCurrentPosition;
	}
}
