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
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.FederateHandleNotKnown;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InvalidAttributeHandle;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidParameterHandle;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;

/**
 * This class holds common result information for interaction
 * parameters and object attributes
 *
 * @author mul (Fraunhofer IOSB)
 */
class ResultInfo {
	private int correctCount = 0;
	private int incorrectCount = 0;
	private String text = null;
	ResultInfo() {
		this.correctCount = 0;
		this.incorrectCount = 0;
	}

	/**
	 * Stores the result data
	 *
	 * @param correct true is PASSED, false is FAILED
	 * @param text the result verdict text
	 */
	void addInfo(final boolean correct, final String text) {
		// Manage the count of correct/incorrect
		if (correct) {
			this.correctCount += 1;
		} else {
			this.incorrectCount += 1;
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

	/**
	 * Gets the number of correct encodings parsed
	 *
	 * @return the number of correct encodings
	 */
	int getCorrectCount() {
		return this.correctCount;
	}
	
	/**
	 * Gets the number of incorrect encodings parsed
	 *
	 * @return the number of incorrect encodings
	 */
	int getIncorrectCount() {
		return this.incorrectCount;
	}

	/**
	 * Get the text associated with the result verdict
	 *
	 * @return the text
	 */
	String getText() {
		return this.text;
	}
}

/**
 * This class adds attribute specific result information
 *
 * @author mul (Fraunhofer IOSB)
 */
class ResultInfoAttribute extends ResultInfo{
	private FederateHandle federateHandle;
	private String federateName;
	private boolean gotFederateName = false;
	void addOwner() {
	}

	/**
	 * Store the federate handle
	 *
	 * @param federateHandle
	 */
	void addFederateHandle(final FederateHandle federateHandle) {
		this.federateHandle = federateHandle;
	}

	/**
	 * Store the federate name
	 *
	 * @param federateName
	 */
	void addFederateName(final String federateName) {
		this.federateName = federateName;
		this.gotFederateName = true;
	}

	/**
	 * Get the federate handle
	 *
	 * @return the federate handle
	 */
	FederateHandle getFederateHandle() {
		return this.federateHandle;
	}

	/**
	 * Get the federate name
	 *
	 * @return the federate name
	 */
	String getFederateName() {
		return this.federateName;
	}

	/**
	 * Check if the federate name is known
	 *
	 * @return whether the federate name is known
	 */
	boolean haveFederateName() {
		return this.gotFederateName;
	}
}

/**
 * @author mul (Fraunhofer IOSB)
 */
public class EncodingRulesTesterBaseModel extends IVCT_BaseModel {
	private boolean                                        errorOccurred = false;
	private String                                         errorText = new String("Encoding error found");

	private int correct = 0;
	private int incorrect = 0;
    private IVCT_RTIambassador                             ivct_rti;
    private IVCT_TcParam ivct_TcParam;
    private Logger                                         logger;
    private Map<InteractionClassHandle, Set<ParameterHandle>> interactionClassHandleMap = new HashMap<InteractionClassHandle, Set<ParameterHandle>>();
    private Map<ParameterHandle, String> parameterHandleDataTypeMap = new HashMap<ParameterHandle, String>();
    private Map<ObjectClassHandle, ObjectClassData> objectClassAttributeHandleMap = new HashMap<ObjectClassHandle, ObjectClassData>();
	private final Map<InteractionClassHandle, Map<ParameterHandle, ResultInfo>> interactionParameterResultsmap = new HashMap<InteractionClassHandle, Map<ParameterHandle, ResultInfo>>();
	private final Map<ObjectInstanceHandle, Map<AttributeHandle, ResultInfoAttribute>> objectAttributeResultsmap = new HashMap<ObjectInstanceHandle, Map<AttributeHandle, ResultInfoAttribute>>();
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
        this.ivct_TcParam = ivct_TcParam;
        logger.warn("DISPLAY SOME HELPER VERSION NUMBER TO CONFIRM VERSION USED: 2018-12-19T10:10");
    }

    /**
     * @return returns whether all interactions have been checked
     */
    public boolean getWhetherAllInteractionsChecked() {
		for (Map.Entry<InteractionClassHandle, Map<ParameterHandle, ResultInfo>> entry : this.interactionParameterResultsmap.entrySet()) {
			Map<ParameterHandle, ResultInfo> atts = entry.getValue();
			for(Map.Entry<ParameterHandle, ResultInfo> parRes :  atts.entrySet()) {
				if (parRes.getValue().getCorrectCount() == 0 && parRes.getValue().getIncorrectCount() == 0) {
					return false;
				}
			}
		}
    	return true;
    }

    /**
     * @return returns whether all attribute have been checked detected
     */
    public boolean getWhetherAllAttibutesChecked() {
		for (Map.Entry<ObjectInstanceHandle, Map<AttributeHandle, ResultInfoAttribute>> entry : this.objectAttributeResultsmap.entrySet()) {
			Map<AttributeHandle, ResultInfoAttribute> atts = entry.getValue();
			for(Map.Entry<AttributeHandle, ResultInfoAttribute> attRes :  atts.entrySet()) {
				if (attRes.getValue().getCorrectCount() == 0 && attRes.getValue().getIncorrectCount() == 0) {
					return false;
				}
			}
		}
    	return true;
    }

    /**
     * @return returns whether an error was detected
     */
    public boolean getErrorOccurred() {
    	return this.errorOccurred;
    }

    /**
     * @return returns the error text
     */
    public String getErrorText() {
    	return this.errorText;
    }

    /**
     * @return returns the failed count
     */
    public int getCorrect() {
    	return this.correct;
    }

    /**
     * @return returns the inconclusive count
     */
    public int getIncorrect() {
    	return this.incorrect;
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
     * Subscribe interactions and object attributes
     *
     * @throws TcInconclusive for errors
     */
    public void init() throws TcInconclusive {
    	// Read SOM files and process them.
    	processSOM();
    	Boolean b = new Boolean(false);

        // Subscribe interactions
    	this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe interactions");
		try {
			for (Map.Entry<InteractionClassHandle, Set<ParameterHandle>> entry : this.interactionClassHandleMap.entrySet()) {
				this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe " + this.ivct_rti.getInteractionClassName(entry.getKey()));
				this.ivct_rti.subscribeInteractionClass(entry.getKey());

                Map<ParameterHandle, ResultInfo> parameterResultMap = this.interactionParameterResultsmap.get(entry.getKey());
                if (parameterResultMap == null) {
                    // Interaction not managed - create all elements
                    Set<ParameterHandle> phs = interactionClassHandleMap.get(entry.getKey());
                    if (phs != null) {
                        Map<ParameterHandle, ResultInfo> tmpParameterResultMap = new HashMap<ParameterHandle, ResultInfo>();
                        for (ParameterHandle parameterHandle : phs) {
                            ResultInfo resultInfo = new ResultInfo();
                            tmpParameterResultMap.put(parameterHandle, resultInfo);
                        }
                        this.interactionParameterResultsmap.put(entry.getKey(), tmpParameterResultMap);
                        this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: A size " + this.interactionParameterResultsmap.size());
                    }
                }
			}
		}
		catch (FederateServiceInvocationsAreBeingReportedViaMOM e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: FederateServiceInvocationsAreBeingReportedViaMOM");
		}
		catch (InteractionClassNotDefined e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: InteractionClassNotDefined");
		}
		catch (SaveInProgress e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: SaveInProgress");
		}
		catch (RestoreInProgress e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: RestoreInProgress");
		}
		catch (FederateNotExecutionMember e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: FederateNotExecutionMember");
		}
		catch (NotConnected e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: NotConnected");
		}
		catch (RTIinternalError e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: RTIinternalError");
		}
		catch (InvalidInteractionClassHandle e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: exception: InvalidInteractionClassHandle");
		}

        // Subscribe object attributes
		int maxClassLevelDepth = 0;
		for (Map.Entry<ObjectClassHandle, ObjectClassData> entry : this.objectClassAttributeHandleMap.entrySet()) {
			ObjectClassData objectClassData = entry.getValue();
			if (objectClassData.classLevelDepth > maxClassLevelDepth) {
				maxClassLevelDepth = objectClassData.classLevelDepth;
			}
		}
		this.logger.trace("EncodingRulesTesterBaseModel.init: subscribe object attributes");
		try {
			for (int ind = maxClassLevelDepth; ind > 0; ind--) {
				for (Map.Entry<ObjectClassHandle, ObjectClassData> entry : this.objectClassAttributeHandleMap.entrySet()) {
					if (entry.getValue().classLevelDepth != ind) {
						continue;
					}
					this.logger.debug("EncodingRulesTesterBaseModel.init: subscribe " + this.ivct_rti.getObjectClassName(entry.getKey()));
					AttributeHandleSet ahs = entry.getValue().attributeHandleSet;
					for (AttributeHandle att : ahs) {
						this.logger.debug("EncodingRulesTesterBaseModel.init: attribute " + this.ivct_rti.getAttributeName(entry.getKey(), att));
					}
					this.ivct_rti.subscribeObjectClassAttributes(entry.getKey(), entry.getValue().attributeHandleSet);
				}
			}
		}
		catch (AttributeNotDefined e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: AttributeNotDefined");
		}
		catch (ObjectClassNotDefined e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: ObjectClassNotDefined");
		}
		catch (SaveInProgress e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: SaveInProgress");
		}
		catch (RestoreInProgress e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: RestoreInProgress");
		}
		catch (FederateNotExecutionMember e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: FederateNotExecutionMember");
		}
		catch (NotConnected e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: NotConnected");
		}
		catch (RTIinternalError e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception: RTIinternalError");
		}
		catch (InvalidObjectClassHandle e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: cannot subscribe object attributes: exception InvalidObjectClassHandle");
		} catch (InvalidAttributeHandle e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // requestAttributeValueUpdate
		this.logger.trace("EncodingRulesTesterBaseModel.init: requestAttributeValueUpdate");
		try {
			for (Map.Entry<ObjectClassHandle, ObjectClassData> entry : this.objectClassAttributeHandleMap.entrySet()) {
				this.logger.debug("EncodingRulesTesterBaseModel.init: subscribe " + this.ivct_rti.getObjectClassName(entry.getKey()));
				AttributeHandleSet ahs = entry.getValue().attributeHandleSet;
				for (AttributeHandle att : ahs) {
					this.logger.debug("EncodingRulesTesterBaseModel.init: attribute " + this.ivct_rti.getAttributeName(entry.getKey(), att));
				}
				this.ivct_rti.requestAttributeValueUpdate(entry.getKey(), entry.getValue().attributeHandleSet, null);
			}
		}
		catch (AttributeNotDefined e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception: AttributeNotDefined");
		}
		catch (InvalidAttributeHandle e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception: InvalidAttributeHandle");
		}
		catch (InvalidObjectClassHandle e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception InvalidObjectClassHandle");
		}
		catch (ObjectClassNotDefined e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception ObjectClassNotDefined");
		}
		catch (SaveInProgress e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception SaveInProgress");
		}
		catch (RestoreInProgress e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception RestoreInProgress");
		}
		catch (FederateNotExecutionMember e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception: FederateNotExecutionMember");
		}
		catch (NotConnected e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception: NotConnected");
		}
		catch (RTIinternalError e) {
			this.logger.error("EncodingRulesTesterBaseModel.init: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.init: init: exception: RTIinternalError");
		}
	}

    /**
     * Read the SOM files and build up an internal data cache to use within this
     * library.
     * 
     * @throws TcInconclusive for errors
     */
    private void processSOM() throws TcInconclusive {
        URL[] somUrls = this.ivct_TcParam.getUrls();

		try {
			DataTreeBuilder dataTreeBuilder = new DataTreeBuilder(this.ivct_rti, this.hlaDataTypes, this.interactionClassHandleMap, this.parameterHandleDataTypeMap, this.objectClassAttributeHandleMap, this.attributeHandleDataTypeMap);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			for (int i = 0; i < somUrls.length; i++) {
				Document document = builder.parse(somUrls[i].toString());
				Element elem = document.getDocumentElement();
				if (dataTreeBuilder.buildData(elem)) {
		            throw new TcInconclusive("EncodingRulesTesterBaseModel.processSOM: error in dataTreeBuilder.buildData");
				}
			}
			this.logger.debug("processSOM attributeHandle: BEFORE");
			for (Map.Entry<AttributeHandle, String> entry : this.attributeHandleDataTypeMap.entrySet() ) {
				this.logger.debug("processSOM attributeHandle: " + entry.getKey() + " type: " + entry.getValue());
			}
			this.logger.debug("processSOM attributeHandle: AFTER");
		}
		catch (FactoryConfigurationError e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.processSOM: exception: FactoryConfigurationError");
		} 
		catch (ParserConfigurationException e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.processSOM: exception: ParserConfigurationException");
		}
		catch (SAXException e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.processSOM: exception: SAXException");
		} 
		catch (IOException e) {
			this.logger.error("EncodingRulesTesterBaseModel.processSOM: exception: " + e);
            throw new TcInconclusive("EncodingRulesTesterBaseModel.processSOM: exception: IOException");
		}
    }

    /**
     * @param in byte value to be displayed as string
     * @return the string value corresponding to the byte value
     */
    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    /**
     * A function to deal with all the possibilities of adding to a map within a map.
     * 
     * @param theInteraction the HLA interaction handle
     * @param theParameter the HLA parameter handle
     * @param b whether the test was positive
     * @param text the text message
     */
    private void addParameterResult(final InteractionClassHandle theInteraction, final ParameterHandle theParameter, final boolean b, final String text) {
    	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: enter");
    	/*
    	 * Check if interaction already managed
    	 */
    	Map<ParameterHandle, ResultInfo> parameterResultMap = this.interactionParameterResultsmap.get(theInteraction);
    	if (parameterResultMap == null) {
        	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: A");
    		// Interaction not managed - create all elements
        	ResultInfo resultInfo = new ResultInfo();
        	resultInfo.addInfo(b, text);
        	Map<ParameterHandle, ResultInfo> tmpParameterResultMap = new HashMap<ParameterHandle, ResultInfo>();
        	tmpParameterResultMap.put(theParameter, resultInfo);
        	this.interactionParameterResultsmap.put(theInteraction, tmpParameterResultMap);
        	this.logger.trace("EncodingRulesTesterBaseModel.addParameterResult: A size " + this.interactionParameterResultsmap.size());
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

    /**
     * @param stringBuilder provided by caller to be logged by one log call
     * Print the result data for all interactions parameters
     */
    public void printParameterResults(StringBuilder stringBuilder) {
        stringBuilder.append("\n\nInteraction Parameter Summary \n");
        if (this.interactionParameterResultsmap.isEmpty()) {
            stringBuilder.append("- No Results -\n");
            return;
        }
    	String interactionClassName = null;
    	for (Map.Entry<InteractionClassHandle, Map<ParameterHandle, ResultInfo>> entryInteraction : this.interactionParameterResultsmap.entrySet()) {
    		try {
				interactionClassName = this.ivct_rti.getInteractionClassName(entryInteraction.getKey());
			} catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
                this.logger.error("EncodingRulesTesterBaseModel.printParameterResults: " + e);
				continue;
			}
            Map<ParameterHandle, ResultInfo> val = entryInteraction.getValue();
            if (val.size() > 0) {
                stringBuilder.append("INTERACTION: " + interactionClassName + "\n");
            } else {
                continue;
            }
    		String parameterName = null;
    		for (Map.Entry<ParameterHandle, ResultInfo> entryParameter : entryInteraction.getValue().entrySet()) {
    			try {
					parameterName = this.ivct_rti.getParameterName(entryInteraction.getKey(), entryParameter.getKey());
				} catch (InteractionParameterNotDefined | InvalidParameterHandle | InvalidInteractionClassHandle
						| FederateNotExecutionMember | NotConnected | RTIinternalError e) {
                    this.logger.error("EncodingRulesTesterBaseModel.printParameterResults: " + e);
				}
                if (entryParameter.getValue().getIncorrectCount() == 0) {
                    stringBuilder.append("    PARAMETER: " + parameterName + " CORRECT: " + entryParameter.getValue().getCorrectCount() + " INCORRECT: " + entryParameter.getValue().getIncorrectCount());
                } else {
                    stringBuilder.append("    PARAMETER: " + parameterName + " CORRECT: " + entryParameter.getValue().getCorrectCount() + " INCORRECT: " + entryParameter.getValue().getIncorrectCount() + " TEXT: " + entryParameter.getValue().getText());
                }
                stringBuilder.append("\n");
            }
    	}
    }

    /**
     * @param interactionClass the interaction class
     * @param parameterHandle the parameter handle
     * @param b byte field containing attribute data
     * @param errorBool whether to use logger error (true) or debug (false)
     */
    private void displayReceiveParameterValuesMessage(final InteractionClassHandle interactionClass, final ParameterHandle parameterHandle, final byte b[], final boolean errorBool) {
        String interactionName = null;
        String parameterName = null;
        try {
            interactionName = this.ivct_rti.getInteractionClassName(interactionClass);
            parameterName = this.ivct_rti.getParameterName(interactionClass, parameterHandle);
        } catch (InvalidInteractionClassHandle | FederateNotExecutionMember | NotConnected | RTIinternalError | InteractionParameterNotDefined | InvalidParameterHandle e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        String sNames = new String("Interaction: " + interactionName + " Parameter: " + parameterName);
        String sIDs = new String("Interaction class handle: " + interactionClass + " Parameter handle: " + parameterHandle);
        String sBytes = new String("Parameter value bytes: " + bytesToHex(b));
        String s = new String();
        s = "\n"
        + sNames + "\n"
        + sIDs + "\n"
        + sBytes;
        if (errorBool) {
            this.logger.error(s);
        } else {
            this.logger.debug(s);
        }
    }

    /**
     * @param interactionClass specify the interaction class
     * @param theParameters specify the parameter handles and values
     */
    private void doReceiveInteraction(final InteractionClassHandle interactionClass, final ParameterHandleValueMap theParameters) {
        this.logger.debug("EncodingRulesTesterBaseModel.doReceiveInteraction: enter");

        for (Map.Entry<ParameterHandle, byte[]> entry : theParameters.entrySet()) {
            ParameterHandle ph = entry.getKey();
            this.logger.debug("EncodingRulesTesterBaseModel.doReceiveInteraction:  GOT parameter " + ph);
            String dataTypeName = this.parameterHandleDataTypeMap.get(ph);
            if (dataTypeName == null) {
                this.logger.error("EncodingRulesTesterBaseModel.doReceiveInteraction cannot get data type for: " + ph);
                continue;
            }
            this.logger.debug("EncodingRulesTesterBaseModel.doReceiveInteraction: GOT receiveInteraction " + dataTypeName);
            HlaDataType hdt = this.hlaDataTypes.dataTypeMap.get(dataTypeName);
            if (hdt == null) {
                this.logger.error("EncodingRulesTesterBaseModel.doReceiveInteraction cannot get data type: " + dataTypeName);
                continue;
            }
            byte b[] = theParameters.get(ph);
            if (b == null) {
                // Empty buffer allowed for interactions.
                continue;
            }
            this.logger.debug("EncodingRulesTesterBaseModel.doReceiveInteraction: length " + b.length);
//            for (int i = 0; i < b.length; i++) {
//                this.logger.trace("EncodingRulesTesterBaseModel.doReceiveInteraction: byte " + b[i]);
//            }
            try {
                if (b.length != 0) {
                    displayReceiveParameterValuesMessage(interactionClass, ph, b, true);
                    int calculatedLength = hdt.testBuffer(entry.getValue(), 0, this.hlaDataTypes);
                    if (calculatedLength != entry.getValue().length) {
                        String error = "TEST BUFFER INCORRECT: overall length caculation: " + calculatedLength + " Buffer length: " + entry.getValue().length + "\n";
                        this.logger.error(error);
                        this.errorOccurred = true;
                        addParameterResult(interactionClass, ph, false, error);
//                        displayReceiveParameterValuesMessage(interactionClass, entry.getKey(), b, true);
                        this.incorrect += 1;
                    } else {
                        String ok = "TEST BUFFER CORRECT\n";
                        this.logger.warn(ok);
                        addParameterResult(interactionClass, ph, true, ok);
//                        displayReceiveParameterValuesMessage(interactionClass, entry.getKey(), b, false);
                        this.correct += 1;
                    }
                } else {
                    this.logger.warn("EncodingRulesTesterBaseModel.doReceiveInteraction: buffer length zero");
                }
			} catch (EncodingRulesException e) {
				String error = "TEST BUFFER INCORRECT: " + e.getMessage() + "\n";
				this.logger.error(error);
	            this.errorOccurred = true;
				addParameterResult(interactionClass, ph, false, error);
//	            displayReceiveParameterValuesMessage(interactionClass, entry.getKey(), b, true);
				this.incorrect += 1;
			}
        }
        this.logger.debug("EncodingRulesTesterBaseModel.doReceiveInteraction: leave");
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
        this.logger.debug("EncodingRulesTesterBaseModel.discoverObjectInstance: objectName: " + objectName);

        /*
         * Check if object already managed
         */
        Map<AttributeHandle, ResultInfoAttribute> attributeResultMap = this.objectAttributeResultsmap.get(theObject);
        if (attributeResultMap == null) {
            // Object not managed yet - create all elements
            Map<AttributeHandle, ResultInfoAttribute> tmpAttributeResultMap = new HashMap<AttributeHandle, ResultInfoAttribute>();

            ObjectClassData tmpObjectClassData = this.objectClassAttributeHandleMap.get(theObjectClass);
            if (tmpObjectClassData != null) {
                AttributeHandleSet ahs = tmpObjectClassData.attributeHandleSet;
                for (AttributeHandle att : ahs) {
                    ResultInfoAttribute tmpResultInfo = new ResultInfoAttribute();
                    tmpAttributeResultMap.put(att, tmpResultInfo);
                }
                this.objectAttributeResultsmap.put(theObject, tmpAttributeResultMap);
                } else {
                // Object rediscovered after localDeleteObjectInstance - add only not-yet-managed attributes
                ObjectClassData objectClassData = this.objectClassAttributeHandleMap.get(theObjectClass);
                if (objectClassData == null) {
                    this.logger.trace("EncodingRulesTesterBaseModel.discoverObjectInstance: cannot get object class data: " + theObjectClass);
                    return;
                }
                AttributeHandleSet ahs = objectClassData.attributeHandleSet;
                if (ahs != null) {
                    for (AttributeHandle att : ahs) {
                    ResultInfoAttribute resultInfoAttribute = attributeResultMap.get(att);
                        if (resultInfoAttribute == null) {
                            ResultInfoAttribute tmpResultInfo = new ResultInfoAttribute();
                            attributeResultMap.put(att, tmpResultInfo);
                        }
                    }
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObjectInstance(final ObjectInstanceHandle theObject, final byte[] userSuppliedTag, final OrderType sentOrdering, final FederateAmbassador.SupplementalRemoveInfo removeInfo) {
        // Do not remove any data collected.
    }

    /**
     * A function to deal with all the possibilities of adding to a map within a map.
     * 
     * @param theObject the HLA object handle
     * @param theAttribute the HLA attribute handle
     * @param b whether the test was positive
     * @param text the text message
     */
    private void addAttributeResult(final ObjectInstanceHandle theObject, final AttributeHandle theAttribute, final boolean b, final String text) {
    	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: enter");
        ResultInfoAttribute tmpResultInfo;

    	/*
    	 * Check if object already managed
    	 */
        Map<AttributeHandle, ResultInfoAttribute> attributeResultMap = this.objectAttributeResultsmap.get(theObject);
    	if (attributeResultMap == null) {
        	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: A");
    		// Object not managed - create all elements
            tmpResultInfo = new ResultInfoAttribute();
            tmpResultInfo.addInfo(b, text);
            Map<AttributeHandle, ResultInfoAttribute> tmpAttributeResultMap = new HashMap<AttributeHandle, ResultInfoAttribute>();
            tmpAttributeResultMap.put(theAttribute, tmpResultInfo);
            this.objectAttributeResultsmap.put(theObject, tmpAttributeResultMap);
        	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: A size " + this.objectAttributeResultsmap.size());
    	} else {
        	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: B");
    		// Object is already managed
            tmpResultInfo = attributeResultMap.get(theAttribute);
    		/*
    		 * Check if attribute is already managed
    		 */
    		if (tmpResultInfo == null) {
            	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: C");
    			// Attribute not managed - create result info
                tmpResultInfo = new ResultInfoAttribute();
                tmpResultInfo.addInfo(b, text);
                attributeResultMap.put(theAttribute, tmpResultInfo);
    		} else {
            	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: D");
    			// Attribute is already managed - update.
    			tmpResultInfo.addInfo(b, text);
    		}
    	}
        if (b == false) {
            try {
                this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: AA");
                if (tmpResultInfo.haveFederateName() == false) {
                    this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: AAA");
                    this.ivct_rti.queryAttributeOwnership(theObject, theAttribute);
                    this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: BBB");
                }
				this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: BB");
            } catch (AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress | RestoreInProgress
                    | FederateNotExecutionMember | NotConnected | RTIinternalError e) {
                this.logger.error("EncodingRulesTesterBaseModel.addAttributeResult: " + e);
            }
        }
    	this.logger.trace("EncodingRulesTesterBaseModel.addAttributeResult: leave");
    }

    public void printResults() throws TcInconclusive {
    	final StringBuilder stringBuilder = new StringBuilder();

        this.printAttributeResults(stringBuilder);
        if (getWhetherAllAttibutesChecked() == false) {
            stringBuilder.append("\nHave not seen all required attributes\n");
        }

        this.printParameterResults(stringBuilder);
        if (getWhetherAllInteractionsChecked() == false) {
            stringBuilder.append("\nHave not seen all required parameters\n");
        }

        int correct = getCorrect();
        int incorrect = getIncorrect();
        stringBuilder.append("\n\nVerdict Summary:\nCORRECT: " + correct + " INCORRECT: " + incorrect + "\n");

        this.logger.info(stringBuilder.toString());
    }

    private void printAttributeResults(StringBuilder stringBuilder) {
        stringBuilder.append("\n\nObject Attribute Summary \n");
        if (this.objectAttributeResultsmap.isEmpty()) {
            stringBuilder.append("- No Results -\n");
            this.logger.trace(stringBuilder.toString());
            return;
        }
    	String objectName = null;
    	ObjectClassHandle objectClassHandle;
        for (Map.Entry<ObjectInstanceHandle, Map<AttributeHandle, ResultInfoAttribute>> entryObject : this.objectAttributeResultsmap.entrySet()) {
    		try {
    			objectName = this.ivct_rti.getObjectInstanceName(entryObject.getKey());
        		objectClassHandle = this.ivct_rti.getKnownObjectClassHandle(entryObject.getKey());
			} catch (FederateNotExecutionMember | NotConnected | RTIinternalError | ObjectInstanceNotKnown e) {
                this.logger.error("EncodingRulesTesterBaseModel.printAttributeResults: " + e);
				continue;
			}
    		String attributeName = null;
            for (Map.Entry<AttributeHandle, ResultInfoAttribute> entryAttribute : entryObject.getValue().entrySet()) {
    			try {
					attributeName = this.ivct_rti.getAttributeName(objectClassHandle, entryAttribute.getKey());
				} catch (AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle
						| FederateNotExecutionMember | NotConnected | RTIinternalError e) {
                    this.logger.error("EncodingRulesTesterBaseModel.printAttributeResults: " + e);
				}
                if (entryAttribute.getValue().getCorrectCount() + entryAttribute.getValue().getIncorrectCount() > 0) {
                    stringBuilder.append("OBJECT: " + objectName + " ATTRIBUTE: " + attributeName + " CORRECT: " + entryAttribute.getValue().getCorrectCount() + " INCORRECT: " + entryAttribute.getValue().getIncorrectCount());
                } else {
                    stringBuilder.append("OBJECT: " + objectName + " ATTRIBUTE: " + attributeName + " NOT UPDATED");
                }
                if (entryAttribute.getValue().getIncorrectCount() > 0) {
					stringBuilder.append(" TEXT: " + entryAttribute.getValue().getText() + " Federate: " + entryAttribute.getValue().getFederateName());
                }
                stringBuilder.append("\n");
    		}
    	}
    }

    /**
     * @param theObjecttheObject the object instance handle
     * @param attributeHandletheObject the attribute handle
     * @param b byte field containing attribute data
     * @param errorBool whether to use logger error (true) or debug (false)
     */
    private void displayReflectAttributeValuesMessage(final ObjectInstanceHandle theObject, final AttributeHandle attributeHandle, final byte b[], final boolean errorBool) {
        String attributeName = null;
        String knownObjectClass = null;
        String objectName = null;
        try {
            objectName = this.ivct_rti.getObjectInstanceName(theObject);
            ObjectClassHandle knownObjectClassHandle = this.ivct_rti.getKnownObjectClassHandle(theObject);
            knownObjectClass = this.ivct_rti.getObjectClassName(knownObjectClassHandle);
            attributeName = this.ivct_rti.getAttributeName(knownObjectClassHandle, attributeHandle);
        } catch (ObjectInstanceNotKnown | FederateNotExecutionMember | NotConnected | RTIinternalError | AttributeNotDefined | InvalidAttributeHandle | InvalidObjectClassHandle e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        String sNames = new String("Object name: " + objectName + " Known object class: " + knownObjectClass + " Attribute name: " + attributeName);
        String sIDs = new String("Object handle: " + theObject + " Attribute handle: " + attributeHandle);
        String sBytes = new String("Attribute value bytes: " + bytesToHex(b));
        String s = new String();
        s = "\n"
        + sNames + "\n"
        + sIDs + "\n"
        + sBytes;
        if (errorBool) {
            this.logger.error(s);
        } else {
            this.logger.debug(s);
        }
    }

    /**
     * @param theObject the object instance handle
     * @param theAttributes the map of attribute handle / value
     */
    private void doReflectAttributeValues(final ObjectInstanceHandle theObject, final AttributeHandleValueMap theAttributes) {
        this.logger.debug("EncodingRulesTesterBaseModel.doReflectAttributeValues: enter");

        for (Map.Entry<AttributeHandle, byte[]> entry : theAttributes.entrySet()) {
            AttributeHandle ah = entry.getKey();
            this.logger.debug("EncodingRulesTesterBaseModel.doReflectAttributeValues: GOT attribute " + ah);
            String dataTypeName = this.attributeHandleDataTypeMap.get(ah);
            if (dataTypeName == null) {
                this.logger.error("EncodingRulesTesterBaseModel.doReflectAttributeValues cannot get data type for name: " + ah);
                continue;
            }
            this.logger.debug("EncodingRulesTesterBaseModel.doReflectAttributeValues: GOT reflectAttributeValues " + dataTypeName);

            HlaDataType hdt = this.hlaDataTypes.dataTypeMap.get(dataTypeName);
            if (hdt == null) {
                this.logger.error("EncodingRulesTesterBaseModel.doReflectAttributeValues cannot get data type: " + dataTypeName);
                continue;
            }
            byte b[] = theAttributes.get(ah);
            if (b == null) {
                this.logger.error("EncodingRulesTesterBaseModel.doReflectAttributeValues: no data reflected");
                continue;
            }
            this.logger.debug("EncodingRulesTesterBaseModel.doReflectAttributeValues: length " + b.length);
            try {
                if (b.length != 0) {
	            displayReflectAttributeValuesMessage(theObject, ah, b, true);
            	int calculatedLength = hdt.testBuffer(entry.getValue(), 0, this.hlaDataTypes);
				if (calculatedLength != entry.getValue().length) {
					String error = "TEST BUFFER INCORRECT: overall length calculation: " + calculatedLength + " Buffer length: " + entry.getValue().length + "\n";
					this.logger.error(error);
		            this.errorOccurred = true;
		            addAttributeResult(theObject, ah, false, error);
		            this.incorrect += 1;
				} else {
					String ok = "TEST BUFFER CORRECT\n";
					this.logger.warn(ok);
		            addAttributeResult(theObject, ah, true, ok);
		            this.correct += 1;
				}
                }  else {
                    this.logger.error("EncodingRulesTesterBaseModel.doReflectAttributeValues: buffer length ZERO");
                }
			} catch (EncodingRulesException e) {
				String error = "TEST BUFFER INCORRECT: " + e.getMessage() + "\n";
				this.logger.error(error);
	            this.errorOccurred = true;
	            addAttributeResult(theObject, ah, false, error);
	            this.incorrect += 1;
			}
        }
        this.logger.debug("EncodingRulesTesterBaseModel.doReflectAttributeValues: leave");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void informAttributeOwnership (
            ObjectInstanceHandle theObject,
            AttributeHandle      theAttribute,
            FederateHandle       theOwner)
                    throws FederateInternalError {
        this.logger.trace("EncodingRulesTesterBaseModel.informAttributeOwnership: ENTER");
        Map<AttributeHandle, ResultInfoAttribute> attributeResultInfoAttribute = this.objectAttributeResultsmap.get(theObject);
        if (attributeResultInfoAttribute != null) {
            ResultInfoAttribute resultInfoAttribute = attributeResultInfoAttribute.get(theAttribute);
            if (resultInfoAttribute != null) {
                resultInfoAttribute.addFederateHandle(theOwner);
                try {
                    this.logger.trace("EncodingRulesTesterBaseModel.informAttributeOwnership: BEFORE");
                    resultInfoAttribute.addFederateName(this.ivct_rti.getFederateName(theOwner));
                    this.logger.trace("EncodingRulesTesterBaseModel.informAttributeOwnership: AFTER");
				} catch (InvalidFederateHandle | FederateHandleNotKnown | FederateNotExecutionMember | NotConnected
						| RTIinternalError e) {
                    this.logger.error("EncodingRulesTesterBaseModel.informAttributeOwnership: " + e);
				}
            }
        }
        this.logger.trace("EncodingRulesTesterBaseModel.informAttributeOwnership: LEAVE");
    }
}
