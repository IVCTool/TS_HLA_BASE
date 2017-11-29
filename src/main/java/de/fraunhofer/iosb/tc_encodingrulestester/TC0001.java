/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_encodingrulestester;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_LoggingFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.EncodingRulesTesterBaseModel;
import de.fraunhofer.iosb.tc_lib_encodingrulestester.EncodingRulesTesterTcParam;
import hla.rti1516e.FederateHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mul (Fraunhofer IOSB)
 */
public class TC0001 extends AbstractTestCase {
    private String                              federateName                   = "IVCT";
    FederateHandle                              federateHandle;

    // Build test case parameters to use
    static EncodingRulesTesterTcParam              encodingRulesTesterTcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador           ivct_rti;
    static EncodingRulesTesterBaseModel            encodingRulesTesterBaseModel;

    static IVCT_LoggingFederateAmbassador ivct_LoggingFederateAmbassador;

    /**
     * @param args the parameter line arguments
     */
    public static void main(final String[] args) {
        Logger                       logger                         = LoggerFactory.getLogger(TC0001.class);
    	String paramJson = "{\"federationName\" : \"HelloWorld\", \"rtiHostName\" : \"localhost\",  \"rtiPort\" : \"8989\",  \"sutFederateName\" : \"A\"}";
        new TC0001().execute(paramJson, logger);
    }

    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive {
    	encodingRulesTesterTcParam              = new EncodingRulesTesterTcParam(tcParamJson);
    	ivct_rti             = IVCT_RTI_Factory.getIVCT_RTI(logger);
    	encodingRulesTesterBaseModel          = new EncodingRulesTesterBaseModel(logger, ivct_rti, encodingRulesTesterTcParam);
    	ivct_LoggingFederateAmbassador = new IVCT_LoggingFederateAmbassador(encodingRulesTesterBaseModel, logger);
    	return encodingRulesTesterBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        stringBuilder.append("TEST PURPOSE\n");
        stringBuilder.append("Test if a federate correctly encodes the attribute and parameter data\n");
        stringBuilder.append("fields\n");
        stringBuilder.append("Use the SOM files to discover which classes the federate publishes and\n");
        stringBuilder.append("subscribe to these. For each attribute or parameter data received, use\n");
        stringBuilder.append("the OMT encoding rules to test if the buffer is consistent to the OMT\n");
        stringBuilder.append("encoding rules: length, padding and enumerator values.\n");
        stringBuilder.append("This does not imply correctness of the data item values\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        final String testPurpose = stringBuilder.toString();

        logger.info(testPurpose);
    }


    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive {

        // Initiate rti
        this.federateHandle = encodingRulesTesterBaseModel.initiateRti(this.federateName, ivct_LoggingFederateAmbassador);

        // Do the necessary calls to get handles and subscribe to the classes published by the federate
        if (encodingRulesTesterBaseModel.init()) {
            throw new TcInconclusive("Cannot encoderTesterBaseModel.init()");
        }
    }


    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed {

        // Allow time to work and test some reflect/receive values.
        if (encodingRulesTesterBaseModel.sleepFor(encodingRulesTesterTcParam.getTestTimeWait())) {
            throw new TcInconclusive("sleepFor problem");
        }

        // Errors are found asynchronously.
        if (encodingRulesTesterBaseModel.getErrorOccured()) {
            throw new TcFailed(encodingRulesTesterBaseModel.getErrorText());
        }
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive {
        // Terminate rti
        encodingRulesTesterBaseModel.terminateRti();
    }
}
