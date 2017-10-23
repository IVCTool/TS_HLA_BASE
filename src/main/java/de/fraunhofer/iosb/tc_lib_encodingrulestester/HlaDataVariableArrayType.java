package de.fraunhofer.iosb.ivct;

public class HlaDataVariableArrayType extends HlaDataType {
	/**
	 * The number of octets in the datatype
	 */
	int dataSize;

	/**
	 * 
	 */
	String elementType;

	/**
	 * 
	 * @param dataTypeName
	 * @param hlaDataTypeElement
	 */
	public HlaDataVariableArrayType(final String dataTypeName, final HlaDataType hlaDataTypeElement) {
		this.dataTypeName = dataTypeName;
		this.elementType = hlaDataTypeElement.dataTypeName;
		this.dataSizeFixed = false;
		this.dataSize = hlaDataTypeElement.getDataSize();
		this.alignment = calcAlignment(dataSize);
	}

	public boolean equalTo(HlaDataVariableArrayType other) {
		if ((this.dataTypeName.equals(other.dataTypeName) && this.elementType.equals(other.elementType)) == false) {
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
		return this.dataSizeFixed;
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
		int lengthValue = 0;
		lengthValue += buffer[myCurrentPosition];
		lengthValue <<= 8;
		lengthValue += buffer[myCurrentPosition + 1];
		lengthValue <<= 8;
		lengthValue += buffer[myCurrentPosition + 2];
		lengthValue <<= 8;
		lengthValue += buffer[myCurrentPosition + 3];
		HlaDataType elementDataType = dataTypes.dataTypeMap.get(elementType);
		myCurrentPosition += 4;
		if (elementDataType.getDataSizeFixed()) {
			myCurrentPosition += calcPaddingBytes(myCurrentPosition, elementDataType.dataTypeName, dataTypes);
			myCurrentPosition += lengthValue * elementDataType.getDataSize() + (lengthValue - 1) * (this.alignment - dataSize);
		} else {
			for (int i = 0; i < lengthValue; i++) {
				myCurrentPosition += calcPaddingBytes(myCurrentPosition, elementDataType.dataTypeName, dataTypes);
				myCurrentPosition = elementDataType.testBuffer(buffer, myCurrentPosition, dataTypes);
			}
		}
		return myCurrentPosition;
	}
}
