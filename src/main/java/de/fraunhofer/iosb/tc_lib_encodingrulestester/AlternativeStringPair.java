package de.fraunhofer.iosb.tc_lib_encodingrulestester;

public class AlternativeStringPair {
	protected String nameString;
	protected String classType;
	/**
	 * 
	 * @param nameString name of the datatype 
	 * @param classType type of the datatype
	 */
	public AlternativeStringPair(final String nameString, final String classType) {
		this.nameString = nameString;
		this.classType = classType;
	}

	/**
	 * 
	 * @param other the object to be compared to
	 * @return true if the fields are equal 
	 */
	public boolean equalTo(final AlternativeStringPair other) {
		if (nameString.equals(other.nameString) == false) {
			return false;
		}
		if (classType.equals(other.classType) == false) {
			return false;
		}
		return true;
	}
}
