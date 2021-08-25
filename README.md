[![gradle-publish](https://github.com/IVCTool/TS_HLA_BASE/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/IVCTool/TS_HLA_BASE/actions/workflows/gradle-publish.yml)

# TS_HLA_BASE

The Test suite HLA_BASE is a multi-test-suite for all basic HLA test suite described below.

## TS_HLA_EncodingRulesTester

This test system will test whether a System Under Test (SUT) correctly encodes the data types used in interaction parameters or object attributes.

For all interaction parameters or object attributes with the sharing value of Publish or PublishSubscribe in the SOM file of the SUT a subscribe will be made. For each value received, will check if the data type is correctly encoded.

The rules are defined in the IEEE Object Model Template (OMT) Specification. The structure of the buffer in terms of lengths and enum values will be checked.

The program logic is as follows:

1) Read SUT SOM file(s) using the merge rules defined in the OMT Specification
2) Subscribe to all interactions and object attributes with the sharing value of Publish or PublishSubscribe
3) For the published parameters and attributes, build up two maps <parameterHandle, dataTypeName> and <attributeHandle, dataTypeName>
4) Build a map of data types <dataTypeName, dataTypeTester>
5) When an interaction parameter is received use the <parameterHandle, dataTypeName> map to find the dataTypeName in <dataTypeName, dataTypeTester> and check the buffer received using the dataTypeTester
6) When an object attribute is received use the <attributeHandle, dataTypeName> map  to find the dataTypeName in <dataTypeName, dataTypeTester> and check the buffer received using the dataTypeTester
7) After a given time, stop the test case and report on the values received and checked and a list of parameters or attributes expected but not received

The interoperability requirements associated with this test case are as follows:

* ["IR-SOM-0017"]: "SuT shall encode all updated attribute values according to CS/SOM", associated test case is de.fraunhofer.iosb.tc_encodingrulestester.TC0001"
* ["IR-SOM-0018"]:	"SuT shall encode all sent interaction class parameters according to CS/SOM", associated test case is 	"de.fraunhofer.iosb.tc_encodingrulestester.TC0001"


## TS_HLA_Services

This test case verifies that the federate uses only the services specified in its SOM.

The interoperability requirements associated with this test case are as follows:
* [IR-SOM-0019]:	SuT shall implement/use all HLA services as described as implemented/used in CS/SOM
* [IR-SOM-0020]:	SuT shall not implement/use any HLA service that is not described as implemented/used in CS/SOM

derrived from https://github.com/MSG134/TS_HLA_Services

## TS_HLA_Object

This test case verifies that the federate sends and receives only the objects and interactions specified in its SOM.

The interoperability requirements associated with this test case are as follows:
* [IR-SOM-0011]:	SuT shall register at least one object instance for each published object class
* [IR-SOM-0012]:	SuT shall discover object instances for all object classes with attributes defined as subscribed
* [IR-SOM-0013]:	SuT shall update attribute values for each published object class attribute
* [IR-SOM-0014]:	SuT shall reflect attribute values for each subscribed object class attribute
* [IR-SOM-0015]:	SuT shall send at least one interaction for each published interaction class
* [IR-SOM-0016]:	SuT shall receive interactions for each subscribed interaction class

derrived from https://github.com/MSG134/TS_HLA_Object

## TS_HLA_Declaration

This test case verifies that the federate only publishes and subscribes objects, interactions and attributes specified in its SOM.

The interoperability requirements associated with this test case are as follows:
* [IR-SOM-0003]:	SuT shall publish all object class attributes defined as published in CS/SOM
* [IR-SOM-0004]:	SuT shall not publish any object class attribute that is not defined as published in CS/SOM
* [IR-SOM-0005]:	SuT shall publish all interaction classes defined as published is CS/SOM
* [IR-SOM-0006]:	SuT shall not publish any interaction class that is not defined as published is CS/SOM
* [IR-SOM-0007]:	SuT shall subscribe to all object class attributes defined as subscribed in CS/SOM
* [IR-SOM-0008]:	SuT shall not subscribe to any object class attribute that is not defined as subscribed in CS/SOM
* [IR-SOM-0009]:	SuT shall subscribe to all interaction classes defined as subscribed in CS/SOM
* [IR-SOM-0010]:	SuT shall not subscribe to any interaction class that is not defined as subscribed in CS/SOM

derrived from https://github.com/MSG134/TS_HLA_Declaration

## TS_HLA_Verification

This test case is equivalent to the "FCTT_NG" configuration verification step. The role of this test case is to check:
* The existence of SOM and FOM files
* The validity of SOM and FOM files
* Internal and overall consistency of SOM and FOM files with respect to HLA standard

The interoperability requirements associated with this test case are as follows:
* [IR-DOC-0001]:	SuT interoperability capabilities shall be documented in a Conformance Statement including a SOM and a FOM with a minimum set of supporting FOM modules
* [IR-SOM-0001]:	SuT CS/SOM shall be valid
* [IR-SOM-0002]:	SuT CS/SOM shall be consistent

derrived from https://github.com/MSG134/TS_CS_Verification

## LICENCE

Copyright 2019 NATO/OTAN

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
