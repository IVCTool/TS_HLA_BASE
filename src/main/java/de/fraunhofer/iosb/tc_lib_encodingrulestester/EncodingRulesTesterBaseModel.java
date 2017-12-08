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
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
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
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InvalidAttributeHandle;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidParameterHandle;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;

class ResultInfo {
	private int correctCount = 0;
	private int incorrectCount = 0;
	private String text = null;
	ResultInfo() {
		correctCount = 0;
		incorrectCount = 0;
	}
	
	void addInfo(final boolean correct, final String text) {
		// Manage the count of correct/incorrect
		if (correct) {
			correctCount += 1;
		} else {
			incorrectCount += 1;
		}
		// Store first text message, only overwrite if incorrect
		if (this.text == null) {
			this.text = text;
		} else {
			if (correct == false) {
				this.text = text;
			}
		}
	}

	int getCorrectCount() {
		return this.correctCount;
	}
	
	int getIncorrectCount() {
		return this.incorrectCount;
	}
	
	String getText() {
		return this.text;
	}
}

/**
 * @author mul (Fraunhofer IOSB)
 */
public class EncodingRulesTesterBaseModel extends IVCT_BaseModel {
	private boolean                                        errorOccurred = false;
	private String                                         errorText = new String("Encoding error found");

	private boolean                                        receivedReflect = false;
	private int correct = 0;
	private int incorrect = 0;
    private EncoderFactory                                 _encoderFactory;
    private IVCT_RTIambassador                             ivct_rti;
    private IVCT_TcParam ivct_TcParam;
    private Logger                                         logger;
    private ParameterHandle                                parameterIdSender;
    private ParameterHandle                                parameterIdText;
    private Set<InteractionClassHandle> interactionClassHandleSet = new HashSet<InteractionClassHandle>();
    private Map<ParameterHandle, String> parameterHandleDataTypeMap = new HashMap<ParameterHandle, String>();
    private Map<ObjectClassHandle, AttributeHandleSet> objectClassAttributeHandleMap = new HashMap<ObjectClassHandle, AttributeHandleSet>();
	private final Map<InteractionClassHandle, Map<ParameterHandle, ResultInfo>> interactionParameterResultsmap = new HashMap<InteractionClassHandle, Map<ParameterHandle, ResultInfo>>();
	private final Map<ObjectInstanceHandle, Map<AttributeHandle, ResultInfo>> objectAttributeResultsmap = new HashMap<ObjectInstanceHandle, Map<AttributeHandle, ResultInfo>>();
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

    /**
     * @return returns whether an error was detected
     */
    public boolean getErrorOccurred() {
    	return errorOccurred;
    }

    /**
     * @return returns the error text
     */
    public String getErrorText() {
    	return errorText;
    }

    /**
     * @return returns the failed count
     */
    public int getCorrect() {
    	return correct;
    }

    /**
     * @return returns the inconclusive count
     */
    public int getIncorrect() {
    	return incorrect;
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
     * @throws TcInconclusive 
     */
    public boolean init() throws TcInconclusive {
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
			ex1.printStackTrace();
            return true;
		} catch (InvalidInteractionClassHandle e) {
			e.printStackTrace();
            return true;
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
     * @throws TcInconclusive 
     */
    private boolean processSOM() throws TcInconclusive {
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
     * A function to deal with all the possibilities of adding to a map within a map.
     * 
     * @param theInteraction the HLA interaction handle
     * @param theParameter the HLA parameter handle
     * @param b whether the test was positive
     * @param text the text message
     */
    void addParameterResult(final InteractionClassHandle theInteraction, final ParameterHandle theParameter, final boolean b, final String text) {
    	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: enter");
    	/*
    	 * Check if interaction already managed
    	 */
    	Map<ParameterHandle, ResultInfo> parameterResultMap = interactionParameterResultsmap.get(theInteraction);
    	if (parameterResultMap == null) {
        	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: A");
    		// Interaction not managed - create all elements
        	ResultInfo resultInfo = new ResultInfo();
        	resultInfo.addInfo(b, text);
        	Map<ParameterHandle, ResultInfo> tmpParameterResultMap = new HashMap<ParameterHandle, ResultInfo>();
        	tmpParameterResultMap.put(theParameter, resultInfo);
        	interactionParameterResultsmap.put(theInteraction, tmpParameterResultMap);
        	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: A size " + interactionParameterResultsmap.size());
    	} else {
        	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: B");
    		// Interaction is already managed
    		ResultInfo tmpResultInfo = parameterResultMap.get(theParameter);
    		/*
    		 * Check if parameter is already managed
    		 */
    		if (tmpResultInfo == null) {
            	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: C");
    			// Parameter not managed - create result info
            	ResultInfo resultInfo = new ResultInfo();
            	resultInfo.addInfo(b, text);
            	parameterResultMap.put(theParameter, resultInfo);
    		} else {
            	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: D");
    			// Parameter is already managed - update.
    			tmpResultInfo.addInfo(b, text);
    		}
    	}
    	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: leave");
    }
    
    public void printParameterResults() {
    	final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\nInteraction Parameter Summary\n");
        if (interactionParameterResultsmap.isEmpty()) {
            stringBuilder.append("- No Results -\n");
        	this.logger.info(stringBuilder.toString());
            return;
        }
    	String interactionClassName = null;
    	for (Map.Entry<InteractionClassHandle, Map<ParameterHandle, ResultInfo>> entryInteraction : interactionParameterResultsmap.entrySet()) {
    		try {
				interactionClassName = ivct_rti.getInteractionClassName(entryInteraction.getKey());
			} catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
    		String parameterName = null;
    		for (Map.Entry<ParameterHandle, ResultInfo> entryParameter : entryInteraction.getValue().entrySet()) {
    			try {
					parameterName = ivct_rti.getParameterName(entryInteraction.getKey(), entryParameter.getKey());
				} catch (InteractionParameterNotDefined | InvalidParameterHandle | InvalidInteractionClassHandle
						| FederateNotExecutionMember | NotConnected | RTIinternalError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			stringBuilder.append("INTERACTION: " + interactionClassName + " PARAMETER: " + parameterName + " CORRECT: " + entryParameter.getValue().getCorrectCount() + " INCORRECT: " + entryParameter.getValue().getIncorrectCount() + " TEXT: " + entryParameter.getValue().getText() + "\n");
    		}
    	}
    	this.logger.info(stringBuilder.toString());
    }

    /**
     * @param interactionClass specify the interaction class
     * @param theParameters specify the parameter handles and values
     */
    private void doReceiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters) {
    	this.logger.info("EncodingRulesTesterBaseModel.doReceiveInteraction: enter");
        for (Map.Entry<ParameterHandle, byte[]> entry : theParameters.entrySet()) {
        	this.logger.info("EncodingRulesTesterBaseModel.doReceiveInteraction:  GOT parameter " + entry.getKey());
        	this.logger.info("EncodingRulesTesterBaseModel.doReceiveInteraction: GOT receiveInteraction " + this.parameterHandleDataTypeMap.get(entry.getKey()));
            HlaDataType hdt = this.hlaDataTypes.dataTypeMap.get(this.parameterHandleDataTypeMap.get(entry.getKey()));
            byte b[] = theParameters.get(entry.getKey());
            this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction: length " + b.length);
            for (int i = 0; i < b.length; i++) {
                this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction: byte " + b[i]);
            }
            try {
            	int calculatedLength = hdt.testBuffer(entry.getValue(), 0, hlaDataTypes);
				if (calculatedLength != entry.getValue().length) {
					String error = "TEST BUFFER INCORRECT: overall length caculation: " + calculatedLength + " Buffer length: " + entry.getValue().length;
					this.logger.info(error);
		            errorOccurred = true;
		            addParameterResult(interactionClass, entry.getKey(), false, error);
		            incorrect += 1;
				} else {
					String ok = "TEST BUFFER CORRECT";
					this.logger.info(ok);
					addParameterResult(interactionClass, entry.getKey(), true, ok);
					correct += 1;
				}
			} catch (EncodingRulesException e) {
				String error = "TEST BUFFER INCORRECT: " + e.getMessage();
				this.logger.info(error);
	            errorOccurred = true;
				addParameterResult(interactionClass, entry.getKey(), false, error);
	            incorrect += 1;
			}
        }
    	this.logger.info("EncodingRulesTesterBaseModel.doReceiveInteraction: leave");
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
     * A function to deal with all the possibilities of adding to a map within a map.
     * 
     * @param theObject the HLA object handle
     * @param theAttribute the HLA attribute handle
     * @param b whether the test was positive
     * @param text the text message
     */
    void addAttributeResult(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final boolean b, final String text) {
    	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: enter");
    	/*
    	 * Check if object already managed
    	 */
    	Map<AttributeHandle, ResultInfo> attributeResultMap = objectAttributeResultsmap.get(theObject);
    	if (attributeResultMap == null) {
        	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: A");
    		// Object not managed - create all elements
        	ResultInfo resultInfo = new ResultInfo();
        	resultInfo.addInfo(b, text);
        	Map<AttributeHandle, ResultInfo> tmpAttributeResultMap = new HashMap<AttributeHandle, ResultInfo>();
        	tmpAttributeResultMap.put(theAttribute, resultInfo);
        	objectAttributeResultsmap.put(theObject, tmpAttributeResultMap);
        	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: A size " + objectAttributeResultsmap.size());
    	} else {
        	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: B");
    		// Object is already managed
    		ResultInfo tmpResultInfo = attributeResultMap.get(theAttribute);
    		/*
    		 * Check if attribute is already managed
    		 */
    		if (tmpResultInfo == null) {
            	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: C");
    			// Attribute not managed - create result info
            	ResultInfo resultInfo = new ResultInfo();
            	resultInfo.addInfo(b, text);
            	attributeResultMap.put(theAttribute, resultInfo);
    		} else {
            	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: D");
    			// Attribute is already managed - update.
    			tmpResultInfo.addInfo(b, text);
    		}
    	}
    	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: leave");
    }
    
    public void printAttributeResults() {
    	final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\nObject Attribute Summary\n");
        if (objectAttributeResultsmap.isEmpty()) {
            stringBuilder.append("- No Results -\n");
        	this.logger.info(stringBuilder.toString());
            return;
        }
    	String objectName = null;
    	String objectClassName = null;
    	ObjectClassHandle objectClassHandle;
    	for (Map.Entry<ObjectInstanceHandle, Map<AttributeHandle, ResultInfo>> entryObject : objectAttributeResultsmap.entrySet()) {
    		try {
    			objectName = ivct_rti.getObjectInstanceName(entryObject.getKey());
        		objectClassHandle = ivct_rti.getKnownObjectClassHandle(entryObject.getKey());
				objectClassName = ivct_rti.getObjectClassName(objectClassHandle);
			} catch (InvalidObjectClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError | ObjectInstanceNotKnown e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
    		String attributeName = null;
    		for (Map.Entry<AttributeHandle, ResultInfo> entryAttribute : entryObject.getValue().entrySet()) {
    			try {
					attributeName = ivct_rti.getAttributeName(objectClassHandle, entryAttribute.getKey());
				} catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle
						| FederateNotExecutionMember | NotConnected | RTIinternalError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			stringBuilder.append("OBJECT: " + objectName + " ATTRIBUTE: " + attributeName + " CORRECT: " + entryAttribute.getValue().getCorrectCount() + " INCORRECT: " + entryAttribute.getValue().getIncorrectCount() + " TEXT: " + entryAttribute.getValue().getText() + "\n");
    		}
    	}
    	this.logger.info(stringBuilder.toString());
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    public void doReflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes) {
    	this.logger.info("EncodingRulesTesterBaseModel.doReflectAttributeValues: enter");
        for (Map.Entry<AttributeHandle, byte[]> entry : theAttributes.entrySet()) {
        	this.logger.info("EncodingRulesTesterBaseModel.doReflectAttributeValues: GOT attribute " + entry.getKey());
        	this.logger.info("EncodingRulesTesterBaseModel.doReflectAttributeValues: GOT reflectAttributeValues " + this.attributeHandleDataTypeMap.get(entry.getKey()));
            HlaDataType hdt = this.hlaDataTypes.dataTypeMap.get(this.attributeHandleDataTypeMap.get(entry.getKey()));
            byte b[] = theAttributes.get(entry.getKey());
            this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: length " + b.length);
            for (int i = 0; i < b.length; i++) {
                this.logger.trace("EncodingRulesTesterBaseModel.doReflectAttributeValues: byte " + b[i]);
            }
            try {
            	int calculatedLength = hdt.testBuffer(entry.getValue(), 0, hlaDataTypes);
				if (calculatedLength != entry.getValue().length) {
					String error = "TEST BUFFER INCORRECT: overall length caculation: " + calculatedLength + " Buffer length: " + entry.getValue().length;
					this.logger.info(error);
		            errorOccurred = true;
		            addAttributeResult(theObject, entry.getKey(), false, error);
		            incorrect += 1;
				} else {
					String ok = "TEST BUFFER CORRECT";
					this.logger.info(ok);
		            addAttributeResult(theObject, entry.getKey(), true, ok);
					correct += 1;
				}
			} catch (EncodingRulesException e) {
				String error = "TEST BUFFER INCORRECT: " + e.getMessage();
				this.logger.info(error);
	            errorOccurred = true;
	            addAttributeResult(theObject, entry.getKey(), false, error);
	            incorrect += 1;
			}
        }
    	this.logger.info("EncodingRulesTesterBaseModel.doReflectAttributeValues: leave");
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
