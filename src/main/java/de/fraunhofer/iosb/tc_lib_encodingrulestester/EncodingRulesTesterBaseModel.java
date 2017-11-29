/*
Copyright 2017, Johannes Mulder (Fraunhofer IOSB)

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

package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.FederateHandleNotKnown;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;


/**
 * @author mul (Fraunhofer IOSB)
 */
public class EncodingRulesTesterBaseModel extends IVCT_BaseModel {
	private boolean                                        errorOccurred = false;
	private String                                         errorText = new String("Encoding error found");

	private boolean                                        receivedReflect = false;
    private EncoderFactory                                 _encoderFactory;
    private IVCT_RTIambassador                             ivct_rti;
    private IVCT_TcParam ivct_TcParam;
    private Logger                                         logger;
    private ParameterHandle                                parameterIdSender;
    private ParameterHandle                                parameterIdText;
    private Set<InteractionClassHandle> interactionClassHandleSet = new HashSet<InteractionClassHandle>();
    private Map<ParameterHandle, String> parameterHandleDataTypeMap = new HashMap<ParameterHandle, String>();
    private Map<ObjectClassHandle, AttributeHandleSet> objectClassAttributeHandleMap = new HashMap<ObjectClassHandle, AttributeHandleSet>();
	private Map<AttributeHandle, String> attributeHandleDataTypeMap = new HashMap<AttributeHandle, String>();
	// FOM/SOM data types
	private HlaDataTypes hlaDataTypes = new HlaDataTypes();

    /**
     * @param logger reference to a logger
     * @param ivct_rti reference to the RTI ambassador
     * @param ivct_TcParam ivct_TcParam
     */
    public EncodingRulesTesterBaseModel(final Logger logger, final IVCT_RTIambassador ivct_rti, final IVCT_TcParam ivct_TcParam) {
        super(ivct_rti, logger, ivct_TcParam);
        this.logger = logger;
        this.ivct_rti = ivct_rti;
        this._encoderFactory = ivct_rti.getEncoderFactory();
        this.ivct_TcParam = ivct_TcParam;
    }

    public boolean getErrorOccured() {
    	return errorOccurred;
    }

    public String getErrorText() {
    	return errorText;
    }

    /**
     * @param federateHandle the federate handle
     * @return the federate name or null
     */
    public String getFederateName(final FederateHandle federateHandle) {
        try {
            return this.ivct_rti.getFederateName(federateHandle);
        }
        catch (InvalidFederateHandle | FederateHandleNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * @return false if a message received, true otherwise
     */
    public boolean getReflectMessageStatus() {
        for (int j = 0; j < 100; j++) {
            if (this.receivedReflect) {
                return false;
            }
            try {
                Thread.sleep(20);
            }
            catch (final InterruptedException ex) {
                continue;
            }
        }
        return true;
    }

    /**
     * @return the parameter id text received
     */
    public ParameterHandle getParameterIdText() {
        return this.parameterIdText;
    }

    /**
     * @return the parameter id text received
     */
    public ParameterHandle getParameterIdSender() {
        return this.parameterIdSender;
    }


    /**
     * {@inheritDoc}
     */
    public void connect(final FederateAmbassador federateReference, final CallbackModel callbackModel, final String localSettingsDesignator) {
        try {
            this.ivct_rti.connect(federateReference, callbackModel, localSettingsDesignator);
        }
        catch (ConnectionFailed | InvalidLocalSettingsDesignator | UnsupportedCallbackModel | AlreadyConnected | CallNotAllowedFromWithinCallback | RTIinternalError ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }


    /**
     * @param sleepTime time to sleep
     * @return true means problem, false is ok
     */
    public boolean sleepFor(final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        }
        catch (final InterruptedException ex) {
            return true;
        }

        return false;
    }


    /**
     * @return true means error, false means correct
     */
    public boolean init() {
    	// Read SOM files and process them.
    	processSOM();

        // Subscribe interactions
    	this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe interactions");
		try {
			for (InteractionClassHandle ich : interactionClassHandleSet) {
				this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe " + this.ivct_rti.getInteractionClassName(ich));
				this.ivct_rti.subscribeInteractionClass(ich);
			}
		}
		catch (FederateServiceInvocationsAreBeingReportedViaMOM | InteractionClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError ex1) {
			this.logger.error("EncodingRulesTesterBaseModel.init: cannot subcribe interaction");
			// TODO Auto-generated catch block
			ex1.printStackTrace();
		} catch (InvalidInteractionClassHandle e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Subscribe object attributes
	this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe object attributes");
        try {
        	for (Map.Entry<ObjectClassHandle, AttributeHandleSet> entry : objectClassAttributeHandleMap.entrySet()) {
        		this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe " + this.ivct_rti.getObjectClassName(entry.getKey()));
                this.ivct_rti.subscribeObjectClassAttributes(entry.getKey(), entry.getValue());
        	}
        }
        catch (AttributeNotDefined | ObjectClassNotDefined | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError | InvalidObjectClassHandle ex) {
            this.logger.error("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes " + ex);
            return true;
        }

        return false;
    }

    /**
     * 
     * @return true means error occurred
     */
    private boolean processSOM() {
        URL[] somUrls = this.ivct_TcParam.getUrls();

		try {
			DataTreeBuilder dataTreeBuilder = new DataTreeBuilder(this.ivct_rti, this.hlaDataTypes, this.interactionClassHandleSet, this.parameterHandleDataTypeMap, this.objectClassAttributeHandleMap, this.attributeHandleDataTypeMap);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			for (int i = 0; i < somUrls.length; i++) {
				Document document = builder.parse(somUrls[i].toString());
				Element elem = document.getDocumentElement();
				if (dataTreeBuilder.buildData(elem)) {
					return true;
				}
			}
		}
		catch (FactoryConfigurationError e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: unable to get a document builder factory");
            return true;
		} 
		catch (ParserConfigurationException e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: unable to configure parser");
            return true;
		}
		catch (SAXException e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: parsing error");
            return true;
		} 
		catch (IOException e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: i/o error");
            return true;
		}
		
		return false;
    }

    /**
     * @param interactionClass specify the interaction class
     * @param theParameters specify the parameter handles and values
     */
    private void doReceiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters) {
    	this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction: enter");
        for (Map.Entry<ParameterHandle, byte[]> entry : theParameters.entrySet()) {
        	this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction:  GOT parameter " + entry.getKey());
            HlaDataType hdt = this.hlaDataTypes.dataTypeMap.get(this.parameterHandleDataTypeMap.get(entry.getKey()));
            byte b[] = theParameters.get(entry.getKey());
            this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction: length " + b.length);
            try {
				if (hdt.testBuffer(entry.getValue(), 0, hlaDataTypes) != entry.getValue().length) {
		            System.out.println("TEST BUFFER FAILED");
		            errorOccurred = true;
				} else {
		            System.out.println("TEST BUFFER PASSED");
				}
			} catch (EncodingRulesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction: leave");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        this.doReceiveInteraction(interactionClass, theParameters);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        this.doReceiveInteraction(interactionClass, theParameters);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        this.doReceiveInteraction(interactionClass, theParameters);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void discoverObjectInstance(final ObjectInstanceHandle theObject, final ObjectClassHandle theObjectClass, final String objectName) throws FederateInternalError {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final FederateAmbassador.SupplementalRemoveInfo removeInfo) {

    }


    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    public void doReflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes) {
    	this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: enter");
        for (Map.Entry<AttributeHandle, byte[]> entry : theAttributes.entrySet()) {
        	this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: GOT attribute " + entry.getKey());
        	this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: GOT reflectAttributeValues " + this.attributeHandleDataTypeMap.get(entry.getKey()));
            HlaDataType hdt = this.hlaDataTypes.dataTypeMap.get(this.attributeHandleDataTypeMap.get(entry.getKey()));
            byte b[] = theAttributes.get(entry.getKey());
            this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: length " + b.length);
            try {
				if (hdt.testBuffer(entry.getValue(), 0, hlaDataTypes) != entry.getValue().length) {
		            System.out.println("TEST BUFFER FAILED");
		            errorOccurred = true;
				} else {
		            System.out.println("TEST BUFFER PASSED");
				}
			} catch (EncodingRulesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: leave");
    }


    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        this.doReflectAttributeValues(theObject, theAttributes);
    }


    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        this.doReflectAttributeValues(theObject, theAttributes);
    }


    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    @Override
    public void reflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes, final byte[] userSuppliedTag, final OrderType sentOrdering, final TransportationTypeHandle theTransport, final LogicalTime theTime, final OrderType receivedOrdering, final MessageRetractionHandle retractionHandle, final SupplementalReflectInfo reflectInfo) throws FederateInternalError {
        this.doReflectAttributeValues(theObject, theAttributes);
    }

}
