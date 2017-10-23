package de.fraunhofer.iosb.tc_lib_encodingrulestester;

public class HlaDataBasicType extends HlaDataType {
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
	 */
	public HlaDataBasicType(final String dataTypeName, final int dataSizeBits, final boolean bigEndian) {
		this.dataTypeName = dataTypeName;
		// Always work in octets
		this.dataSize = dataSizeBits / 8;
		this.bigEndian = bigEndian;
		this.alignment = calcAlignment(dataSize);
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
		return dataSize;
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
		return currentPosition + dataSize;
	}
}
