/*
Copyright 2017, FRANCE (DGA/Capgemini)

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

package nato.ivct.etc.fr.tc_cs_verification;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcFailed;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import hla.rti1516e.FederateHandle;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.TextInternationalization;
import nato.ivct.etc.fr.tc_lib_cs_verification.CS_Verification_BaseModel;
import nato.ivct.etc.fr.tc_lib_cs_verification.CS_Verification_TcParam;

import org.slf4j.Logger;
import java.io.File;

/**
 * @author FRANCE (DGA/Capgemini)
 */
public class TC_001_Files_Check extends AbstractTestCase {
    FederateHandle                              TcFederateHandle = null;
    private String                              TcFederateName = "IVCT_CS_Verification";	// Not used (no RTI connection)

    // Build test case parameters to use
    static CS_Verification_TcParam              CsVerificationTcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador           ivct_rti = null;	// Not used (no RTI connection)
    static CS_Verification_BaseModel            CsVerificationBaseModel;
    

    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive {
        
    	File currentDir = new File("").getAbsoluteFile();
    	logger.debug("pwd = " + currentDir.getAbsolutePath());
    	logger.debug("Locale = " + System.getProperty("user.language") + "_" + System.getProperty("user.country"));

    	try {
	    	CsVerificationTcParam   = new CS_Verification_TcParam(tcParamJson);
	    	CsVerificationBaseModel = new CS_Verification_BaseModel(logger, ivct_rti, CsVerificationTcParam, getSutName());
    	}
    	catch(Exception ex) {
    		logger.error(ex.getMessage());
    		logger.error(TextInternationalization.getString("etc_fra.noInstanciation"));
    	}
    	return CsVerificationBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
    	
    	// Build purpose text
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FCTT_Constant.REPORT_FILE_SEPARATOR);
        stringBuilder.append(TextInternationalization.getString("etc_fra.purpose")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("csverification.FomSomExistence")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("csverification.FomSomParsing")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("csverification.FomSomSharing")); stringBuilder.append("\n");
        stringBuilder.append(TextInternationalization.getString("csverification.FomSomRules")); stringBuilder.append("\n");
        final String testPurpose = stringBuilder.toString();
        logger.info(testPurpose);
    }


    @Override
    protected void preambleAction(final Logger logger) throws TcInconclusive {

        // Initiate rti : no RTI connection
    	logger.info(TextInternationalization.getString("csverification.noConnection"));
    	logger.info(FCTT_Constant.REPORT_FILE_SEPARATOR);
    }


    @Override
    protected void performTest(final Logger logger) throws TcInconclusive, TcFailed {

    	// Check result directory
    	String resultFileName = CsVerificationTcParam.getResultDir();
        File resultFile = new File(resultFileName);
        if (!resultFile.exists())
            throw new TcInconclusive(String.format(TextInternationalization.getString("etc_fra.resultDirError"),resultFileName));

        // Check FOM/SOM files
        if (CsVerificationBaseModel.validateFomSomFiles() == false)
        	throw new TcFailed(TextInternationalization.getString("csverification.invalidFomSom"));
    	
    	logger.info(FCTT_Constant.REPORT_FILE_SEPARATOR);
    }


    @Override
    protected void postambleAction(final Logger logger) throws TcInconclusive {
    	
        // Terminate rti : no RTI connection
    	logger.info(TextInternationalization.getString("csverification.noDeconnection"));
    	logger.info(FCTT_Constant.REPORT_FILE_SEPARATOR);
    }
}
