package de.fraunhofer.iosb.ivct;

public class HlaDataSimpleType extends HlaDataType {
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
	 * @param dataTypeName
	 * @param hlaDataBasicType
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
		return currentPosition + dataSize;
	}
}
