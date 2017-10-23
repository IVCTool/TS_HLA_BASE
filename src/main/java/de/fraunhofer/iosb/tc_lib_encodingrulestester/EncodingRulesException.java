package de.fraunhofer.iosb.tc_lib_encodingrulestester;

public class EncodingRulesException extends Exception {
	/*
	 * Special logic to identify versions
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param msg the exception text
	 */
	public EncodingRulesException(String msg){
		super(msg);
	}
}
