package de.fraunhofer.iosb.ivct;

import hla.rti1516e.AttributeHandleSet;

public class AttributeHandleSetFactoryImpl implements hla.rti1516e.AttributeHandleSetFactory {
	   /**
	    * return hla.rti1516.AttributeHandleSet newly created
	    */
	   public AttributeHandleSet create() {
		   return new AttributeHandleSetImpl();
	   }
}
