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

import org.slf4j.Logger;

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


/**
 * @author mul (Fraunhofer IOSB)
 */
public class TC0002 extends AbstractTestCase {
    private String                              federateName                   = "IVCT";
    FederateHandle                              federateHandle;

    // Build test case parameters to use
    static EncodingRulesTesterTcParam              encodingRulesTesterTcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador           ivct_rti;
    static EncodingRulesTesterBaseModel            encodingRulesTesterBaseModel;

    static IVCT_LoggingFederateAmbassador ivct_LoggingFederateAmbassador;

    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive {
        encodingRulesTesterTcParam              = new EncodingRulesTesterTcParam(tcParamJson);
    	ivct_rti             = IVCT_RTI_Factory.getIVCT_RTI(logger);
    	encodingRulesTesterBaseModel          = new EncodingRulesTesterBaseModel(logger, ivct_rti, encodingRulesTesterTcParam);
		encodingRulesTesterBaseModel.setRPRv2_0();
    	ivct_LoggingFederateAmbassador = new IVCT_LoggingFederateAmbassador(encodingRulesTesterBaseModel, logger);
    	return encodingRulesTesterBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
        String s = new String();
        s = "\n"
        +   "---------------------------------------------------------------------\n"
        +   "TEST PURPOSE: Test if a federate correctly encodes the attribute and parameter data\n"
        +   "TEST PURPOSE: fields\n"
        +   "TEST PURPOSE: Use the SOM files to discover which classes the federate publishes and\n"
        +   "TEST PURPOSE: subscribe to these. For each attribute or parameter data received, use\n"
        +   "TEST PURPOSE: the OMT encoding rules to test if the buffer is consistent to the OMT\n"
        +   "TEST PURPOSE: encoding rules: length, padding and enumerator values.\n"
        +   "TEST PURPOSE: This does not imply correctness of the data item values\n"
        +   "---------------------------------------------------------------------\n";

        logger.info(s);
    }

    public void displayOperatorInstructions(final Logger logger) throws TcInconclusive {
        String s = new String();
        s = "\n"
        +   "---------------------------------------------------------------------\n"
        +   "OPERATOR INSTRUCTIONS: \n"
        +   "1. Start the test federate "
        +   getSutFederateName()
        +   " before the confirmation\n"
        +   "2. The federate should run for the full duration of the tests\n"
        +   "---------------------------------------------------------------------\n";

        logger.info(s);
		try {
			sendOperatorRequest(s);
		} catch (InterruptedException e) {
            logger.info("Exception: sendOperatorRequest: " + e);
		}
    }

    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive {

        // Notify the operator
        displayOperatorInstructions(logger);

        // Initiate rti
        this.federateHandle = encodingRulesTesterBaseModel.initiateRti(this.federateName, ivct_LoggingFederateAmbassador);

        // Do the necessary calls to get handles and subscribe to the classes published by the federate
        encodingRulesTesterBaseModel.init();
    }


    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed {

    	long sleepTime = encodingRulesTesterTcParam.getTestTimeWait() / 10;
    	// Allow time to work and test some reflect/receive values.
    	for (int i = 0; i < 10; i++) {
    		if (encodingRulesTesterBaseModel.sleepFor(sleepTime)) {
    			throw new TcInconclusive("sleepFor problem");
    		}
            sendTcStatus ("running", i*10);
    	}

        encodingRulesTesterBaseModel.printResults();

        // Errors are found asynchronously.
        if (encodingRulesTesterBaseModel.getErrorOccurred()) {
            throw new TcFailed(encodingRulesTesterBaseModel.getErrorText());
        }

        // If not all attributes and parameters were checked, will not be passed.
        if ((encodingRulesTesterBaseModel.getWhetherAllAttibutesChecked() == false) && (encodingRulesTesterBaseModel.getWhetherAllInteractionsChecked() == false)) {
            throw new TcInconclusive("Have not seen all required attributes and parameters");
        }

        // If not all attributes were checked, will not be passed.
        if (encodingRulesTesterBaseModel.getWhetherAllAttibutesChecked() == false) {
            throw new TcInconclusive("Have not seen all required attributes");
        }

        // If not all interactions were checked, will not be passed.
        if (encodingRulesTesterBaseModel.getWhetherAllInteractionsChecked() == false) {
            throw new TcInconclusive("Have not seen all required parameters");
        }
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive {
        // Terminate rti
        encodingRulesTesterBaseModel.terminateRti();
    }
}
