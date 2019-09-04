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

package nato.ivct.etc.fr.tc_lib_cs_verification;

import java.io.File;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.exceptions.FederateInternalError;
import nato.ivct.etc.fr.fctt_common.configuration.controller.validation.FCTTFilesCheck;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Constant;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Environment;
import org.slf4j.Logger;

/**
 * @author FRANCE (DGA/Capgemini)
 */
public class CS_Verification_BaseModel extends IVCT_BaseModel {

	private Logger					logger;
	private CS_Verification_TcParam	tcParams;
	private FCTTFilesCheck			filesValidator;
	private File					resultFile;

	/**
     * @param logger reference to a logger
     * @param ivct_rti reference to the RTI ambassador
     * @param CsVerificationTcParam linked parameters
	 * @param sutName SuT name
     */
    public CS_Verification_BaseModel(final Logger logger, final IVCT_RTIambassador ivct_rti, final CS_Verification_TcParam CsVerificationTcParam, String sutName) {

        super(ivct_rti, logger, CsVerificationTcParam);
		this.logger = logger;
		this.tcParams = CsVerificationTcParam;
		this.filesValidator = new FCTTFilesCheck(logger,CsVerificationTcParam.getResultDir(),sutName);

    	// Generate result files
		String fileName = "CS_Verification_report_" + FCTT_Environment.getDateForFileName() + FCTT_Constant.REPORT_FILE_NAME_EX;
		resultFile = new File(CsVerificationTcParam.getResultDir() + File.separator + fileName);
    }


    /**
	 * Check the FOM and SOM files.
	 * @return True if the FOM and SOM files are valid, false if not
	 */
	public boolean validateFomSomFiles() {

		// Check files and write result file
		return filesValidator.checkFiles(tcParams.getFomFiles(),tcParams.getSomFiles(),resultFile);
	}


	/**
     * {@inheritDoc}
     */
    @Override
    public void terminateRti() {
    	// Do nothing
    	// Supersedes IVCT_BaseModel.terminateRti to
    	// avoid a crash on null ivct_rti dereferencing !
    }


	/**
     * {@inheritDoc}
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator) {
    	// No RTI connection
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
    	// No RTI connection
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
    	// No RTI connection
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
    	// No RTI connection
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName) throws FederateInternalError {
    	// No RTI connection
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final FederateAmbassador.SupplementalRemoveInfo removeInfo) {
    	// No RTI connection
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
    	// No RTI connection
    }


    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
    	// No RTI connection
    }


    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
    	// No RTI connection
    }

}
