package de.fraunhofer.iosb.ivct;

import java.net.URL;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleFactory;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.AttributeHandleValueMapFactory;
import hla.rti1516e.AttributeSetRegionSetPairList;
import hla.rti1516e.AttributeSetRegionSetPairListFactory;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.DimensionHandle;
import hla.rti1516e.DimensionHandleFactory;
import hla.rti1516e.DimensionHandleSet;
import hla.rti1516e.DimensionHandleSetFactory;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleFactory;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.FederateHandleSetFactory;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.InteractionClassHandleFactory;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeFactory;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.MessageRetractionReturn;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectClassHandleFactory;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ObjectInstanceHandleFactory;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleFactory;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.ParameterHandleValueMapFactory;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RangeBounds;
import hla.rti1516e.RegionHandle;
import hla.rti1516e.RegionHandleSet;
import hla.rti1516e.RegionHandleSetFactory;
import hla.rti1516e.ResignAction;
import hla.rti1516e.ServiceGroup;
import hla.rti1516e.TimeQueryReturn;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.TransportationTypeHandleFactory;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyDisabled;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyEnabled;
import hla.rti1516e.exceptions.AttributeAcquisitionWasNotRequested;
import hla.rti1516e.exceptions.AttributeAlreadyBeingAcquired;
import hla.rti1516e.exceptions.AttributeAlreadyBeingChanged;
import hla.rti1516e.exceptions.AttributeAlreadyBeingDivested;
import hla.rti1516e.exceptions.AttributeAlreadyOwned;
import hla.rti1516e.exceptions.AttributeDivestitureWasNotRequested;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.AttributeNotPublished;
import hla.rti1516e.exceptions.AttributeRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.AttributeRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.AttributeScopeAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.AttributeScopeAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.CouldNotOpenMIM;
import hla.rti1516e.exceptions.DeletePrivilegeNotHeld;
import hla.rti1516e.exceptions.DesignatorIsHLAstandardMIM;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.ErrorReadingMIM;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateHandleNotKnown;
import hla.rti1516e.exceptions.FederateHasNotBegunSave;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederateUnableToUseTime;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.IllegalName;
import hla.rti1516e.exceptions.InTimeAdvancingState;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InteractionClassAlreadyBeingChanged;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionClassNotPublished;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InteractionRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.InteractionRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.InvalidAttributeHandle;
import hla.rti1516e.exceptions.InvalidDimensionHandle;
import hla.rti1516e.exceptions.InvalidFederateHandle;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.exceptions.InvalidLookahead;
import hla.rti1516e.exceptions.InvalidMessageRetractionHandle;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidOrderName;
import hla.rti1516e.exceptions.InvalidOrderType;
import hla.rti1516e.exceptions.InvalidParameterHandle;
import hla.rti1516e.exceptions.InvalidRangeBound;
import hla.rti1516e.exceptions.InvalidRegion;
import hla.rti1516e.exceptions.InvalidRegionContext;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.InvalidServiceGroup;
import hla.rti1516e.exceptions.InvalidTransportationName;
import hla.rti1516e.exceptions.InvalidTransportationType;
import hla.rti1516e.exceptions.InvalidUpdateRateDesignator;
import hla.rti1516e.exceptions.LogicalTimeAlreadyPassed;
import hla.rti1516e.exceptions.MessageCanNoLongerBeRetracted;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NameSetWasEmpty;
import hla.rti1516e.exceptions.NoAcquisitionPending;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectClassRelevanceAdvisorySwitchIsOff;
import hla.rti1516e.exceptions.ObjectClassRelevanceAdvisorySwitchIsOn;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RegionDoesNotContainSpecifiedDimension;
import hla.rti1516e.exceptions.RegionInUseForUpdateOrSubscription;
import hla.rti1516e.exceptions.RegionNotCreatedByThisFederate;
import hla.rti1516e.exceptions.RequestForTimeConstrainedPending;
import hla.rti1516e.exceptions.RequestForTimeRegulationPending;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.RestoreNotInProgress;
import hla.rti1516e.exceptions.RestoreNotRequested;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SaveNotInProgress;
import hla.rti1516e.exceptions.SaveNotInitiated;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.TimeConstrainedAlreadyEnabled;
import hla.rti1516e.exceptions.TimeConstrainedIsNotEnabled;
import hla.rti1516e.exceptions.TimeRegulationAlreadyEnabled;
import hla.rti1516e.exceptions.TimeRegulationIsNotEnabled;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;

public class IVCT_NullRTIambassador implements RTIambassador {
    private Logger logger = LoggerFactory.getLogger(IVCT_NullRTIambassador.class);

    /**
     */
    public IVCT_NullRTIambassador() {
    }

////////////////////////////////////
//Federation Management Services //
////////////////////////////////////

// 4.2
public void connect(FederateAmbassador federateReference,
CallbackModel callbackModel,
String localSettingsDesignator)
throws
ConnectionFailed,
InvalidLocalSettingsDesignator,
UnsupportedCallbackModel,
AlreadyConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("connect not implemented");
}

// 4.2
public void connect(FederateAmbassador federateReference,
CallbackModel callbackModel)
throws
ConnectionFailed,
InvalidLocalSettingsDesignator,
UnsupportedCallbackModel,
AlreadyConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("connect not implemented");
}

// 4.3
public void disconnect()
throws
FederateIsExecutionMember,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("disconnect not implemented");
}

//4.5
public void createFederationExecution(String federationExecutionName,
    URL[] fomModules,
    URL mimModule,
    String logicalTimeImplementationName)
throws
CouldNotCreateLogicalTimeFactory,
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
ErrorReadingMIM,
CouldNotOpenMIM,
DesignatorIsHLAstandardMIM,
FederationExecutionAlreadyExists,
NotConnected,
RTIinternalError {
    this.logger.warn("createFederationExecution not implemented");
}

//4.5
public void createFederationExecution(String federationExecutionName,
    URL[] fomModules,
    String logicalTimeImplementationName)
throws
CouldNotCreateLogicalTimeFactory,
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
FederationExecutionAlreadyExists,
NotConnected,
RTIinternalError {
    this.logger.warn("createFederationExecution not implemented");
}

//4.5
public void createFederationExecution(String federationExecutionName,
    URL[] fomModules,
    URL mimModule)
throws
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
ErrorReadingMIM,
CouldNotOpenMIM,
DesignatorIsHLAstandardMIM,
FederationExecutionAlreadyExists,
NotConnected,
RTIinternalError {
    this.logger.warn("createFederationExecution not implemented");
}

//4.5
public void createFederationExecution(String federationExecutionName,
    URL[] fomModules)
throws
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
FederationExecutionAlreadyExists,
NotConnected,
RTIinternalError {
    this.logger.warn("createFederationExecution not implemented");
}

//4.5
public void createFederationExecution(String federationExecutionName,
    URL fomModule)
throws
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
FederationExecutionAlreadyExists,
NotConnected,
RTIinternalError {
    this.logger.warn("createFederationExecution not implemented");
}

//4.6
public void destroyFederationExecution(String federationExecutionName)
throws
FederatesCurrentlyJoined,
FederationExecutionDoesNotExist,
NotConnected,
RTIinternalError {
    this.logger.warn("destroyFederationExecution not implemented");
}

// 4.7
public void listFederationExecutions()
throws
NotConnected,
RTIinternalError {
    this.logger.warn("listFederationExecutions not implemented");
}

//4.9
public FederateHandle joinFederationExecution(String federateName,
            String federateType,
            String federationExecutionName,
            URL[] additionalFomModules)
throws
CouldNotCreateLogicalTimeFactory,
FederateNameAlreadyInUse,
FederationExecutionDoesNotExist,
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
SaveInProgress,
RestoreInProgress,
FederateAlreadyExecutionMember,
NotConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("joinFederationExecution not implemented");
    return null;
}

//4.9
public FederateHandle joinFederationExecution(String federateType,
            String federationExecutionName,
            URL[] additionalFomModules)
throws
CouldNotCreateLogicalTimeFactory,
FederationExecutionDoesNotExist,
InconsistentFDD,
ErrorReadingFDD,
CouldNotOpenFDD,
SaveInProgress,
RestoreInProgress,
FederateAlreadyExecutionMember,
NotConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("joinFederationExecution not implemented");
    return null;
}

//4.9
public FederateHandle joinFederationExecution(String federateName,
            String federateType,
            String federationExecutionName)
throws
CouldNotCreateLogicalTimeFactory,
FederateNameAlreadyInUse,
FederationExecutionDoesNotExist,
SaveInProgress,
RestoreInProgress,
FederateAlreadyExecutionMember,
NotConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("joinFederationExecution not implemented");
    return null;
}

//4.9
public FederateHandle joinFederationExecution(String federateType,
            String federationExecutionName)
throws
CouldNotCreateLogicalTimeFactory,
FederationExecutionDoesNotExist,
SaveInProgress,
RestoreInProgress,
FederateAlreadyExecutionMember,
NotConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("joinFederationExecution not implemented");
    return null;
}

//4.10
public void resignFederationExecution(ResignAction resignAction)
throws
InvalidResignAction,
OwnershipAcquisitionPending,
FederateOwnsAttributes,
FederateNotExecutionMember,
NotConnected,
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("resignFederationExecution not implemented");
}

//4.11
public void registerFederationSynchronizationPoint(String synchronizationPointLabel,
                 byte[] userSuppliedTag)
throws
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("registerFederationSynchronizationPoint not implemented");
}

//4.11
public void registerFederationSynchronizationPoint(String synchronizationPointLabel,
                 byte[] userSuppliedTag,
                 FederateHandleSet synchronizationSet)
throws
InvalidFederateHandle,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("registerFederationSynchronizationPoint not implemented");
}

//4.14
public void synchronizationPointAchieved(String synchronizationPointLabel)
throws
SynchronizationPointLabelNotAnnounced,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("synchronizationPointAchieved not implemented");
}

//4.14
public void synchronizationPointAchieved(String synchronizationPointLabel,
       boolean successIndicator)
throws
SynchronizationPointLabelNotAnnounced,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("synchronizationPointAchieved not implemented");
}

// 4.16
public void requestFederationSave(String label)
throws
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestFederationSave not implemented");
}

// 4.16
public void requestFederationSave(String label,
LogicalTime theTime)
throws
LogicalTimeAlreadyPassed,
InvalidLogicalTime,
FederateUnableToUseTime,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestFederationSave not implemented");
}

// 4.18
public void federateSaveBegun()
throws
SaveNotInitiated,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("federateSaveBegun not implemented");
}

// 4.19
public void federateSaveComplete()
throws
FederateHasNotBegunSave,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("federateSaveComplete not implemented");
}

// 4.19
public void federateSaveNotComplete()
throws
FederateHasNotBegunSave,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("federateSaveNotComplete not implemented");
}

// 4.21
public void abortFederationSave()
throws
SaveNotInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("abortFederationSave not implemented");
}

// 4.22
public void queryFederationSaveStatus()
throws
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryFederationSaveStatus not implemented");
}

// 4.24
public void requestFederationRestore(String label)
throws
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestFederationRestore not implemented");
}

// 4.28
public void federateRestoreComplete()
throws
RestoreNotRequested,
SaveInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("federateRestoreComplete not implemented");
}

// 4.28
public void federateRestoreNotComplete()
throws
RestoreNotRequested,
SaveInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("federateRestoreNotComplete not implemented");
}

// 4.30
public void abortFederationRestore()
throws
RestoreNotInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("abortFederationRestore not implemented");
}

// 4.31
public void queryFederationRestoreStatus()
throws
SaveInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryFederationRestoreStatus not implemented");
}


/////////////////////////////////////
//Declaration Management Services //
/////////////////////////////////////

// 5.2
public void publishObjectClassAttributes(ObjectClassHandle theClass,
       AttributeHandleSet attributeList)
throws
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("publishObjectClassAttributes not implemented");
}

// 5.3
public void unpublishObjectClass(ObjectClassHandle theClass)
throws
OwnershipAcquisitionPending,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unpublishObjectClass not implemented");
}

// 5.3
public void unpublishObjectClassAttributes(ObjectClassHandle theClass,
         AttributeHandleSet attributeList)
throws
OwnershipAcquisitionPending,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unpublishObjectClassAttributes not implemented");
}

// 5.4
public void publishInteractionClass(InteractionClassHandle theInteraction)
throws
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("publishInteractionClass not implemented");
}

// 5.5
public void unpublishInteractionClass(InteractionClassHandle theInteraction)
throws
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unpublishInteractionClass not implemented");
}

// 5.6
public void subscribeObjectClassAttributes(ObjectClassHandle theClass,
         AttributeHandleSet attributeList)
throws
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributes not implemented");
}

// 5.6
public void subscribeObjectClassAttributes(ObjectClassHandle theClass,
         AttributeHandleSet attributeList,
         String updateRateDesignator)
throws
AttributeNotDefined,
ObjectClassNotDefined,
InvalidUpdateRateDesignator,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributes not implemented");
}

// 5.6
public void subscribeObjectClassAttributesPassively(ObjectClassHandle theClass,
                  AttributeHandleSet attributeList)
throws
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributesPassively not implemented");
}

// 5.6
public void subscribeObjectClassAttributesPassively(ObjectClassHandle theClass,
                  AttributeHandleSet attributeList,
                  String updateRateDesignator)
throws
AttributeNotDefined,
ObjectClassNotDefined,
InvalidUpdateRateDesignator,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributesPassively not implemented");
}

// 5.7
public void unsubscribeObjectClass(ObjectClassHandle theClass)
throws
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unsubscribeObjectClass not implemented");
}

// 5.7
public void unsubscribeObjectClassAttributes(ObjectClassHandle theClass,
           AttributeHandleSet attributeList)
throws
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unsubscribeObjectClassAttributes not implemented");
}

// 5.8
public void subscribeInteractionClass(InteractionClassHandle theClass)
throws
FederateServiceInvocationsAreBeingReportedViaMOM,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeInteractionClass not implemented");
}

// 5.8
public void subscribeInteractionClassPassively(InteractionClassHandle theClass)
throws
FederateServiceInvocationsAreBeingReportedViaMOM,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeInteractionClassPassively not implemented");
}

// 5.9
public void unsubscribeInteractionClass(InteractionClassHandle theClass)
throws
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unsubscribeInteractionClass not implemented");
}

////////////////////////////////
//Object Management Services //
////////////////////////////////

// 6.2
public void reserveObjectInstanceName(String theObjectName)
throws
IllegalName,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("reserveObjectInstanceName not implemented");
}

// 6.4
public void releaseObjectInstanceName(String theObjectInstanceName)
throws
ObjectInstanceNameNotReserved,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("releaseObjectInstanceName not implemented");
}

// 6.5
public void reserveMultipleObjectInstanceName(Set<String> theObjectNames)
throws
IllegalName,
NameSetWasEmpty,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("reserveMultipleObjectInstanceName not implemented");
}

// 6.7
public void releaseMultipleObjectInstanceName(Set<String> theObjectNames)
throws
ObjectInstanceNameNotReserved,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("releaseMultipleObjectInstanceName not implemented");
}

// 6.8
public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass)
throws
ObjectClassNotPublished,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("registerObjectInstance not implemented");
    return null;
}

// 6.8
public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass,
                 String theObjectName)
throws
ObjectInstanceNameInUse,
ObjectInstanceNameNotReserved,
ObjectClassNotPublished,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("registerObjectInstance not implemented");
    return null;
}

// 6.10
public void updateAttributeValues(ObjectInstanceHandle theObject,
AttributeHandleValueMap theAttributes,
byte[] userSuppliedTag)
throws
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("updateAttributeValues not implemented");
}

// 6.10
public MessageRetractionReturn updateAttributeValues(ObjectInstanceHandle theObject,
                   AttributeHandleValueMap theAttributes,
                   byte[] userSuppliedTag,
                   LogicalTime theTime)
throws
InvalidLogicalTime,
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("updateAttributeValues not implemented");
    return null;
}

// 6.12
public void sendInteraction(InteractionClassHandle theInteraction,
ParameterHandleValueMap theParameters,
byte[] userSuppliedTag)
throws
InteractionClassNotPublished,
InteractionParameterNotDefined,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("sendInteraction not implemented");
}

// 6.12
public MessageRetractionReturn sendInteraction(InteractionClassHandle theInteraction,
             ParameterHandleValueMap theParameters,
             byte[] userSuppliedTag,
             LogicalTime theTime)
throws
InvalidLogicalTime,
InteractionClassNotPublished,
InteractionParameterNotDefined,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("sendInteraction not implemented");
    return null;
}

// 6.14
public void deleteObjectInstance(ObjectInstanceHandle objectHandle,
byte[] userSuppliedTag)
throws
DeletePrivilegeNotHeld,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("deleteObjectInstance not implemented");
}

// 6.14
public MessageRetractionReturn deleteObjectInstance(ObjectInstanceHandle objectHandle,
                  byte[] userSuppliedTag,
                  LogicalTime theTime)
throws
InvalidLogicalTime,
DeletePrivilegeNotHeld,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("deleteObjectInstance not implemented");
    return null;
}

// 6.16
public void localDeleteObjectInstance(ObjectInstanceHandle objectHandle)
throws
OwnershipAcquisitionPending,
FederateOwnsAttributes,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("localDeleteObjectInstance not implemented");
}

// 6.19
public void requestAttributeValueUpdate(ObjectInstanceHandle theObject,
      AttributeHandleSet theAttributes,
      byte[] userSuppliedTag)
throws
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestAttributeValueUpdate not implemented");
}

// 6.19
public void requestAttributeValueUpdate(ObjectClassHandle theClass,
      AttributeHandleSet theAttributes,
      byte[] userSuppliedTag)
throws
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestAttributeValueUpdate not implemented");
}

// 6.23
public void requestAttributeTransportationTypeChange(ObjectInstanceHandle theObject,
                   AttributeHandleSet theAttributes,
                   TransportationTypeHandle theType)
throws
AttributeAlreadyBeingChanged,
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
InvalidTransportationType,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestAttributeTransportationTypeChange not implemented");
}

// 6.25
public void queryAttributeTransportationType(ObjectInstanceHandle theObject,
           AttributeHandle theAttribute)
throws
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryAttributeTransportationType not implemented");
}

// 6.27
public void requestInteractionTransportationTypeChange(InteractionClassHandle theClass,
                     TransportationTypeHandle theType)
throws
InteractionClassAlreadyBeingChanged,
InteractionClassNotPublished,
InteractionClassNotDefined,
InvalidTransportationType,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestInteractionTransportationTypeChange not implemented");
}

// 6.29
public void queryInteractionTransportationType(FederateHandle theFederate,
             InteractionClassHandle theInteraction)
throws
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryInteractionTransportationType not implemented");
}

///////////////////////////////////
//Ownership Management Services //
///////////////////////////////////

// 7.2
public void unconditionalAttributeOwnershipDivestiture(ObjectInstanceHandle theObject,
                     AttributeHandleSet theAttributes)
throws
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unconditionalAttributeOwnershipDivestiture not implemented");
}

// 7.3
public void negotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle theObject,
                  AttributeHandleSet theAttributes,
                  byte[] userSuppliedTag)
throws
AttributeAlreadyBeingDivested,
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("negotiatedAttributeOwnershipDivestiture not implemented");
}

// 7.6
public void confirmDivestiture(ObjectInstanceHandle theObject,
AttributeHandleSet theAttributes,
byte[] userSuppliedTag)
throws
NoAcquisitionPending,
AttributeDivestitureWasNotRequested,
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("confirmDivestiture not implemented");
}

// 7.8
public void attributeOwnershipAcquisition(ObjectInstanceHandle theObject,
        AttributeHandleSet desiredAttributes,
        byte[] userSuppliedTag)
throws
AttributeNotPublished,
ObjectClassNotPublished,
FederateOwnsAttributes,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("attributeOwnershipAcquisition not implemented");
}

// 7.9
public void attributeOwnershipAcquisitionIfAvailable(ObjectInstanceHandle theObject,
                   AttributeHandleSet desiredAttributes)
throws
AttributeAlreadyBeingAcquired,
AttributeNotPublished,
ObjectClassNotPublished,
FederateOwnsAttributes,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("attributeOwnershipAcquisitionIfAvailable not implemented");
}

// 7.12
public void attributeOwnershipReleaseDenied(ObjectInstanceHandle theObject,
          AttributeHandleSet theAttributes)
throws
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("attributeOwnershipReleaseDenied not implemented");
}

// 7.13
public AttributeHandleSet attributeOwnershipDivestitureIfWanted(ObjectInstanceHandle theObject,
                              AttributeHandleSet theAttributes)
throws
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("attributeOwnershipDivestitureIfWanted not implemented");
    return null;
}

// 7.14
public void cancelNegotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle theObject,
                        AttributeHandleSet theAttributes)
throws
AttributeDivestitureWasNotRequested,
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("cancelNegotiatedAttributeOwnershipDivestiture not implemented");
}

// 7.15
public void cancelAttributeOwnershipAcquisition(ObjectInstanceHandle theObject,
              AttributeHandleSet theAttributes)
throws
AttributeAcquisitionWasNotRequested,
AttributeAlreadyOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("cancelAttributeOwnershipAcquisition not implemented");
}

// 7.17
public void queryAttributeOwnership(ObjectInstanceHandle theObject,
  AttributeHandle theAttribute)
throws
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryAttributeOwnership not implemented");
}

// 7.19
public boolean isAttributeOwnedByFederate(ObjectInstanceHandle theObject,
        AttributeHandle theAttribute)
throws
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("isAttributeOwnedByFederate not implemented");
    return false;
}

//////////////////////////////
//Time Management Services //
//////////////////////////////

// 8.2
public void enableTimeRegulation(LogicalTimeInterval theLookahead)
throws
InvalidLookahead,
InTimeAdvancingState,
RequestForTimeRegulationPending,
TimeRegulationAlreadyEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableTimeRegulation not implemented");
}

// 8.4
public void disableTimeRegulation()
throws
TimeRegulationIsNotEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableTimeRegulation not implemented");
}

// 8.5
public void enableTimeConstrained()
throws
InTimeAdvancingState,
RequestForTimeConstrainedPending,
TimeConstrainedAlreadyEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableTimeConstrained not implemented");
}

// 8.7
public void disableTimeConstrained()
throws
TimeConstrainedIsNotEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableTimeConstrained not implemented");
}

// 8.8
public void timeAdvanceRequest(LogicalTime theTime)
throws
LogicalTimeAlreadyPassed,
InvalidLogicalTime,
InTimeAdvancingState,
RequestForTimeRegulationPending,
RequestForTimeConstrainedPending,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("timeAdvanceRequest not implemented");
}

// 8.9
public void timeAdvanceRequestAvailable(LogicalTime theTime)
throws
LogicalTimeAlreadyPassed,
InvalidLogicalTime,
InTimeAdvancingState,
RequestForTimeRegulationPending,
RequestForTimeConstrainedPending,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("timeAdvanceRequestAvailable not implemented");
}

// 8.10
public void nextMessageRequest(LogicalTime theTime)
throws
LogicalTimeAlreadyPassed,
InvalidLogicalTime,
InTimeAdvancingState,
RequestForTimeRegulationPending,
RequestForTimeConstrainedPending,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("nextMessageRequest not implemented");
}

// 8.11
public void nextMessageRequestAvailable(LogicalTime theTime)
throws
LogicalTimeAlreadyPassed,
InvalidLogicalTime,
InTimeAdvancingState,
RequestForTimeRegulationPending,
RequestForTimeConstrainedPending,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("nextMessageRequestAvailable not implemented");
}

// 8.12
public void flushQueueRequest(LogicalTime theTime)
throws
LogicalTimeAlreadyPassed,
InvalidLogicalTime,
InTimeAdvancingState,
RequestForTimeRegulationPending,
RequestForTimeConstrainedPending,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("flushQueueRequest not implemented");
}

// 8.14
public void enableAsynchronousDelivery()
throws
AsynchronousDeliveryAlreadyEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableAsynchronousDelivery not implemented");
}

// 8.15
public void disableAsynchronousDelivery()
throws
AsynchronousDeliveryAlreadyDisabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableAsynchronousDelivery not implemented");
}

// 8.16
public TimeQueryReturn queryGALT()
throws
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryGALT not implemented");
    return null;
}

// 8.17
public LogicalTime queryLogicalTime()
throws
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryLogicalTime not implemented");
    return null;
}

// 8.18
public TimeQueryReturn queryLITS()
throws
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("queryLITS not implemented");
    return null;
}

// 8.19
public void modifyLookahead(LogicalTimeInterval theLookahead)
throws
InvalidLookahead,
InTimeAdvancingState,
TimeRegulationIsNotEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("modifyLookahead not implemented");
}

// 8.20
public LogicalTimeInterval queryLookahead()
throws
TimeRegulationIsNotEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("LogicalTimeInterval not implemented");
    return null;
}

// 8.21
public void retract(MessageRetractionHandle theHandle)
throws
MessageCanNoLongerBeRetracted,
InvalidMessageRetractionHandle,
TimeRegulationIsNotEnabled,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("retract not implemented");
}

// 8.23
public void changeAttributeOrderType(ObjectInstanceHandle theObject,
   AttributeHandleSet theAttributes,
   OrderType theType)
throws
AttributeNotOwned,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("changeAttributeOrderType not implemented");
}

// 8.24
public void changeInteractionOrderType(InteractionClassHandle theClass,
     OrderType theType)
throws
InteractionClassNotPublished,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("changeInteractionOrderType not implemented");
}

//////////////////////////////////
//Data Distribution Management //
//////////////////////////////////

// 9.2
public RegionHandle createRegion(DimensionHandleSet dimensions)
throws
InvalidDimensionHandle,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("createRegion not implemented");
    return null;
}

// 9.3
public void commitRegionModifications(RegionHandleSet regions)
throws
RegionNotCreatedByThisFederate,
InvalidRegion,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("commitRegionModifications not implemented");
}

// 9.4
public void deleteRegion(RegionHandle theRegion)
throws
RegionInUseForUpdateOrSubscription,
RegionNotCreatedByThisFederate,
InvalidRegion,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("deleteRegion not implemented");
}

//9.5
public ObjectInstanceHandle registerObjectInstanceWithRegions(ObjectClassHandle theClass,
                            AttributeSetRegionSetPairList attributesAndRegions)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotPublished,
ObjectClassNotPublished,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("registerObjectInstanceWithRegions not implemented");
    return null;
}

//9.5
public ObjectInstanceHandle registerObjectInstanceWithRegions(ObjectClassHandle theClass,
                            AttributeSetRegionSetPairList attributesAndRegions,
                            String theObject)
throws
ObjectInstanceNameInUse,
ObjectInstanceNameNotReserved,
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotPublished,
ObjectClassNotPublished,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("registerObjectInstanceWithRegions not implemented");
    return null;
}

// 9.6
public void associateRegionsForUpdates(ObjectInstanceHandle theObject,
     AttributeSetRegionSetPairList attributesAndRegions)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("associateRegionsForUpdates not implemented");
}

// 9.7
public void unassociateRegionsForUpdates(ObjectInstanceHandle theObject,
       AttributeSetRegionSetPairList attributesAndRegions)
throws
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectInstanceNotKnown,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unassociateRegionsForUpdates not implemented");
}

// 9.8
public void subscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
                    AttributeSetRegionSetPairList attributesAndRegions)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributesWithRegions not implemented");
}

// 9.8
public void subscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
                    AttributeSetRegionSetPairList attributesAndRegions,
                    String updateRateDesignator)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectClassNotDefined,
InvalidUpdateRateDesignator,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributesWithRegions not implemented");
}

// 9.8
public void subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle theClass,
                             AttributeSetRegionSetPairList attributesAndRegions)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributesPassivelyWithRegions not implemented");
}

// 9.8
public void subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle theClass,
                             AttributeSetRegionSetPairList attributesAndRegions,
                             String updateRateDesignator)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectClassNotDefined,
InvalidUpdateRateDesignator,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeObjectClassAttributesPassivelyWithRegions not implemented");
}

// 9.9
public void unsubscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
                      AttributeSetRegionSetPairList attributesAndRegions)
throws
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unsubscribeObjectClassAttributesWithRegions not implemented");
}

// 9.10
public void subscribeInteractionClassWithRegions(InteractionClassHandle theClass,
               RegionHandleSet regions)
throws
FederateServiceInvocationsAreBeingReportedViaMOM,
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeInteractionClassWithRegions not implemented");
}

// 9.10
public void subscribeInteractionClassPassivelyWithRegions(InteractionClassHandle theClass,
                        RegionHandleSet regions)
throws
FederateServiceInvocationsAreBeingReportedViaMOM,
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("subscribeInteractionClassPassivelyWithRegions not implemented");
}

// 9.11
public void unsubscribeInteractionClassWithRegions(InteractionClassHandle theClass,
                 RegionHandleSet regions)
throws
RegionNotCreatedByThisFederate,
InvalidRegion,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("unsubscribeInteractionClassWithRegions not implemented");
}

//9.12
public void sendInteractionWithRegions(InteractionClassHandle theInteraction,
     ParameterHandleValueMap theParameters,
     RegionHandleSet regions,
     byte[] userSuppliedTag)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
InteractionClassNotPublished,
InteractionParameterNotDefined,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("sendInteractionWithRegions not implemented");
}

//9.12
public MessageRetractionReturn sendInteractionWithRegions(InteractionClassHandle theInteraction,
                        ParameterHandleValueMap theParameters,
                        RegionHandleSet regions,
                        byte[] userSuppliedTag,
                        LogicalTime theTime)
throws
InvalidLogicalTime,
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
InteractionClassNotPublished,
InteractionParameterNotDefined,
InteractionClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("sendInteractionWithRegions not implemented");
    return null;
}

// 9.13
public void requestAttributeValueUpdateWithRegions(ObjectClassHandle theClass,
                 AttributeSetRegionSetPairList attributesAndRegions,
                 byte[] userSuppliedTag)
throws
InvalidRegionContext,
RegionNotCreatedByThisFederate,
InvalidRegion,
AttributeNotDefined,
ObjectClassNotDefined,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("requestAttributeValueUpdateWithRegions not implemented");
}

//////////////////////////
//RTI Support Services //
//////////////////////////

// 10.2
public ResignAction getAutomaticResignDirective()
throws
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getAutomaticResignDirective not implemented");
    return null;
}

// 10.3
public void setAutomaticResignDirective(ResignAction resignAction)
throws
InvalidResignAction,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("setAutomaticResignDirective not implemented");
}

// 10.4
public FederateHandle getFederateHandle(String theName)
throws
NameNotFound,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getFederateHandle not implemented");
    return null;
}

// 10.5
public String getFederateName(FederateHandle theHandle)
throws
InvalidFederateHandle,
FederateHandleNotKnown,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getFederateName not implemented");
    return null;
}

// 10.6
public ObjectClassHandle getObjectClassHandle(String theName)
throws
NameNotFound,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getObjectClassHandle not implemented");
    return null;
}

// 10.7
public String getObjectClassName(ObjectClassHandle theHandle)
throws
InvalidObjectClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getObjectClassName not implemented");
    return null;
}

// 10.8
public ObjectClassHandle getKnownObjectClassHandle(ObjectInstanceHandle theObject)
throws
ObjectInstanceNotKnown,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getKnownObjectClassHandle not implemented");
    return null;
}

// 10.9
public ObjectInstanceHandle getObjectInstanceHandle(String theName)
throws
ObjectInstanceNotKnown,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getObjectInstanceHandle not implemented");
    return null;
}

// 10.10
public String getObjectInstanceName(ObjectInstanceHandle theHandle)
throws
ObjectInstanceNotKnown,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getObjectInstanceName not implemented");
    return null;
}

// 10.11
public AttributeHandle getAttributeHandle(ObjectClassHandle whichClass,
        String theName)
throws
NameNotFound,
InvalidObjectClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getAttributeHandle not implemented");
    return null;
}

// 10.12
public String getAttributeName(ObjectClassHandle whichClass,
AttributeHandle theHandle)
throws
AttributeNotDefined,
InvalidAttributeHandle,
InvalidObjectClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getAttributeName not implemented");
    return null;
}

// 10.13
public double getUpdateRateValue(String updateRateDesignator)
throws
InvalidUpdateRateDesignator,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getUpdateRateValue not implemented");
    return 0.0;
}

// 10.14
public double getUpdateRateValueForAttribute(ObjectInstanceHandle theObject,
           AttributeHandle theAttribute)
throws
ObjectInstanceNotKnown,
AttributeNotDefined,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getUpdateRateValueForAttribute not implemented");
    return 0.0;
}

// 10.15
public InteractionClassHandle getInteractionClassHandle(String theName)
throws
NameNotFound,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getInteractionClassHandle not implemented");
    return null;
}

// 10.16
public String getInteractionClassName(InteractionClassHandle theHandle)
throws
InvalidInteractionClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getInteractionClassName not implemented");
    return null;
}

// 10.17
public ParameterHandle getParameterHandle(InteractionClassHandle whichClass,
        String theName)
throws
NameNotFound,
InvalidInteractionClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getParameterHandle not implemented");
    return null;
}

// 10.18
public String getParameterName(InteractionClassHandle whichClass,
ParameterHandle theHandle)
throws
InteractionParameterNotDefined,
InvalidParameterHandle,
InvalidInteractionClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getParameterName not implemented");
    return null;
}

// 10.19
public OrderType getOrderType(String theName)
throws
InvalidOrderName,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getOrderType not implemented");
    return null;
}

// 10.20
public String getOrderName(OrderType theType)
throws
InvalidOrderType,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getOrderName not implemented");
    return null;
}

// 10.21
public TransportationTypeHandle getTransportationTypeHandle(String theName)
throws
InvalidTransportationName,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getTransportationTypeHandle not implemented");
    return null;
}

// 10.22
public String getTransportationTypeName(TransportationTypeHandle theHandle)
throws
InvalidTransportationType,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getTransportationTypeName not implemented");
    return null;
}

// 10.23
public DimensionHandleSet getAvailableDimensionsForClassAttribute(ObjectClassHandle whichClass,
                                AttributeHandle theHandle)
throws
AttributeNotDefined,
InvalidAttributeHandle,
InvalidObjectClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getAvailableDimensionsForClassAttribute not implemented");
    return null;
}

// 10.24
public DimensionHandleSet getAvailableDimensionsForInteractionClass(InteractionClassHandle theHandle)
throws
InvalidInteractionClassHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getAvailableDimensionsForInteractionClass not implemented");
    return null;
}

// 10.25
public DimensionHandle getDimensionHandle(String theName)
throws
NameNotFound,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getDimensionHandle not implemented");
    return null;
}

// 10.26
public String getDimensionName(DimensionHandle theHandle)
throws
InvalidDimensionHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getDimensionName not implemented");
    return null;
}

// 10.27
public long getDimensionUpperBound(DimensionHandle theHandle)
throws
InvalidDimensionHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getDimensionUpperBound not implemented");
    return 0;
}

// 10.28
public DimensionHandleSet getDimensionHandleSet(RegionHandle region)
throws
InvalidRegion,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getDimensionHandleSet not implemented");
    return null;
}

// 10.29
public RangeBounds getRangeBounds(RegionHandle region,
DimensionHandle dimension)
throws
RegionDoesNotContainSpecifiedDimension,
InvalidRegion,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("getRangeBounds not implemented");
    return null;
}

// 10.30
public void setRangeBounds(RegionHandle region,
DimensionHandle dimension,
RangeBounds bounds)
throws
InvalidRangeBound,
RegionDoesNotContainSpecifiedDimension,
RegionNotCreatedByThisFederate,
InvalidRegion,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("setRangeBounds not implemented");
}

// 10.31
public long normalizeFederateHandle(FederateHandle federateHandle)
throws
InvalidFederateHandle,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("normalizeFederateHandle not implemented");
    return 0;
}

// 10.32
public long normalizeServiceGroup(ServiceGroup group)
throws
InvalidServiceGroup,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("normalizeServiceGroup not implemented");
    return 0;
}

// 10.33
public void enableObjectClassRelevanceAdvisorySwitch()
throws
ObjectClassRelevanceAdvisorySwitchIsOn,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableObjectClassRelevanceAdvisorySwitch not implemented");
}

// 10.34
public void disableObjectClassRelevanceAdvisorySwitch()
throws
ObjectClassRelevanceAdvisorySwitchIsOff,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableObjectClassRelevanceAdvisorySwitch not implemented");
}

// 10.35
public void enableAttributeRelevanceAdvisorySwitch()
throws
AttributeRelevanceAdvisorySwitchIsOn,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableAttributeRelevanceAdvisorySwitch not implemented");
}

// 10.36
public void disableAttributeRelevanceAdvisorySwitch()
throws
AttributeRelevanceAdvisorySwitchIsOff,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableAttributeRelevanceAdvisorySwitch not implemented");
}

// 10.37
public void enableAttributeScopeAdvisorySwitch()
throws
AttributeScopeAdvisorySwitchIsOn,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableAttributeScopeAdvisorySwitch not implemented");
}

// 10.38
public void disableAttributeScopeAdvisorySwitch()
throws
AttributeScopeAdvisorySwitchIsOff,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableAttributeScopeAdvisorySwitch not implemented");
}

// 10.39
public void enableInteractionRelevanceAdvisorySwitch()
throws
InteractionRelevanceAdvisorySwitchIsOn,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("enableInteractionRelevanceAdvisorySwitch not implemented");
}

// 10.40
public void disableInteractionRelevanceAdvisorySwitch()
throws
InteractionRelevanceAdvisorySwitchIsOff,
SaveInProgress,
RestoreInProgress,
FederateNotExecutionMember,
NotConnected,
RTIinternalError {
    this.logger.warn("disableInteractionRelevanceAdvisorySwitch not implemented");
}

// 10.41
public boolean evokeCallback(double approximateMinimumTimeInSeconds)
throws
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("evokeCallback not implemented");
    return false;
}

// 10.42
public boolean evokeMultipleCallbacks(double approximateMinimumTimeInSeconds,
    double approximateMaximumTimeInSeconds)
throws
CallNotAllowedFromWithinCallback,
RTIinternalError {
    this.logger.warn("evokeMultipleCallbacks not implemented");
    return false;
}

// 10.43
public void enableCallbacks()
throws
SaveInProgress,
RestoreInProgress,
RTIinternalError {
    this.logger.warn("enableCallbacks not implemented");
}

// 10.44
public void disableCallbacks()
throws
SaveInProgress,
RestoreInProgress,
RTIinternalError {
    this.logger.warn("disableCallbacks not implemented");
}

//API-specific services
public AttributeHandleFactory getAttributeHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getAttributeHandleFactory not implemented");
    return null;
}

public AttributeHandleSetFactory getAttributeHandleSetFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getAttributeHandleSetFactory not implemented");
    return null;
}

public AttributeHandleValueMapFactory getAttributeHandleValueMapFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getAttributeHandleValueMapFactory not implemented");
    return null;
}

public AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getAttributeSetRegionSetPairListFactory not implemented");
    return null;
}

public DimensionHandleFactory getDimensionHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getDimensionHandleFactory not implemented");
    return null;
}

public DimensionHandleSetFactory getDimensionHandleSetFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getDimensionHandleSetFactory not implemented");
    return null;
}

public FederateHandleFactory getFederateHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getFederateHandleFactory not implemented");
    return null;
}

public FederateHandleSetFactory getFederateHandleSetFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getFederateHandleSetFactory not implemented");
    return null;
}

public InteractionClassHandleFactory getInteractionClassHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getInteractionClassHandleFactory not implemented");
    return null;
}

public ObjectClassHandleFactory getObjectClassHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getObjectClassHandleFactory not implemented");
    return null;
}

public ObjectInstanceHandleFactory getObjectInstanceHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getObjectInstanceHandleFactory not implemented");
    return null;
}

public ParameterHandleFactory getParameterHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getParameterHandleFactory not implemented");
    return null;
}

public ParameterHandleValueMapFactory getParameterHandleValueMapFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getParameterHandleValueMapFactory not implemented");
    return null;
}

public RegionHandleSetFactory getRegionHandleSetFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getRegionHandleSetFactory not implemented");
    return null;
}

public TransportationTypeHandleFactory getTransportationTypeHandleFactory()
throws
FederateNotExecutionMember,
NotConnected {
    this.logger.warn("getTransportationTypeHandleFactory not implemented");
    return null;
}

public String getHLAversion() {
    this.logger.warn("getHLAversion not implemented");
    return null;
}

public LogicalTimeFactory getTimeFactory()
		throws
		FederateNotExecutionMember,
		NotConnected {
	this.logger.warn("getTimeFactory not implemented");
	return null;
}
}
