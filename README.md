# TS_HLA_EncodingRulesTester (Still work in progress)

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
