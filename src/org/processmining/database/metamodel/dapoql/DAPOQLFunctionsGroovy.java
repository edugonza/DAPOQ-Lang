package org.processmining.database.metamodel.dapoql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.processmining.openslex.metamodel.AbstractDBElement;
import org.processmining.openslex.metamodel.AbstractDBElementWithAtts;
import org.processmining.openslex.metamodel.AbstractRSetElement;
import org.processmining.openslex.metamodel.AbstractRSetWithAtts;
import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMAttributeValue;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMLog;
import org.processmining.openslex.metamodel.SLEXMMObject;
import org.processmining.openslex.metamodel.SLEXMMObjectVersion;
import org.processmining.openslex.metamodel.SLEXMMObjectVersionResultSet;
import org.processmining.openslex.metamodel.SLEXMMPeriod;
import org.processmining.openslex.metamodel.SLEXMMPeriodResultSet;
import org.processmining.openslex.metamodel.SLEXMMProcess;
import org.processmining.openslex.metamodel.SLEXMMRelation;
import org.processmining.openslex.metamodel.SLEXMMRelationship;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class DAPOQLFunctionsGroovy {

	private SLEXMMStorageMetaModel slxmm = null;

	private static final int MAX_IDS_ARRAY_SIZE = 40000;

	public DAPOQLFunctionsGroovy(SLEXMMStorageMetaModel strg) {
		this.slxmm = strg;
		initMapFunctions();
	}
	
	public boolean filterChangedOperation(SLEXMMObjectVersion ov, SLEXMMAttribute slxAtt, String v, String valueFrom,
			String valueTo) {

		SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForObject(ov.getObjectId());
		SLEXMMObjectVersion ova = null;
		SLEXMMObjectVersion ovb = null;

		while ((ovb = ovrset.getNext()) != null) {
			if (ovb.getId() == ov.getId()) {
				break;
			} else {
				ova = ovb;
			}
		}

		if (ova != null) {
			SLEXMMAttributeValue prevAtV = ova.getAttributeValues().get(slxAtt);
			String prevV = prevAtV.getValue();
			if (valueFrom != null) {
				if (!prevV.equals(valueFrom)) {
					return false;
				}
			}
			if (valueTo != null) {
				if (!v.equals(valueTo)) {
					return false;
				}
			}
			if (prevV.equals(v)) {
				return false;
			}
		} else {
			// ov was already the first Object Version for this object. We
			// cannot decide what changed.
			return false;
		}

		return true;
	}

	private SLEXMMStorageMetaModel getStorage() {
		return this.slxmm;
	}	
	
	private HashMap<Class<?>,HashMap<Class<?>,Function<int[],AbstractRSetElement<?>>>> mapFunctions;
	private HashMap<Class<?>,Function<SLEXMMPeriod,AbstractRSetElement<?>>> mapPeriodFunctions;
	
	private void initMapFunctions() {
		mapPeriodFunctions = new HashMap<Class<?>,Function<SLEXMMPeriod,AbstractRSetElement<?>>>();
		mapPeriodFunctions.put(SLEXMMObject.class, getStorage()::getObjectsForPeriod);
		mapPeriodFunctions.put(SLEXMMEvent.class, getStorage()::getEventsForPeriod);
		mapPeriodFunctions.put(SLEXMMAttribute.class, getStorage()::getAttributesForPeriod);
		mapPeriodFunctions.put(SLEXMMClass.class, getStorage()::getClassesForPeriod);
		mapPeriodFunctions.put(SLEXMMDataModel.class, getStorage()::getDatamodelsForPeriod);
		mapPeriodFunctions.put(SLEXMMActivityInstance.class, getStorage()::getActivityInstancesForPeriod);
		mapPeriodFunctions.put(SLEXMMActivity.class, getStorage()::getActivitiesForPeriod);
		mapPeriodFunctions.put(SLEXMMProcess.class, getStorage()::getProcessesForPeriod);
		mapPeriodFunctions.put(SLEXMMCase.class, getStorage()::getCasesForPeriod);
		mapPeriodFunctions.put(SLEXMMLog.class, getStorage()::getLogsForPeriod);
		mapPeriodFunctions.put(SLEXMMObjectVersion.class, getStorage()::getVersionsForPeriod);
		mapPeriodFunctions.put(SLEXMMRelationship.class, getStorage()::getRelationshipsForPeriod);
		mapPeriodFunctions.put(SLEXMMRelation.class, getStorage()::getRelationsForPeriod);
		
		mapFunctions = new HashMap<Class<?>, HashMap<Class<?>,Function<int[],AbstractRSetElement<?>>>>();
		HashMap<Class<?>,Function<int[],AbstractRSetElement<?>>> funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, null);
		funcs.put(SLEXMMEvent.class, getStorage()::getObjectsForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getObjectsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getObjectsForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getObjectsForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getObjectsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getObjectsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getObjectsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getObjectsForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getObjectsForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getObjectsForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getObjectsForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getObjectsForRelations);
		mapFunctions.put(SLEXMMObject.class,funcs);
		
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getEventsForObjects);
		funcs.put(SLEXMMEvent.class, null);
		funcs.put(SLEXMMAttribute.class, getStorage()::getEventsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getEventsForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getEventsForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getEventsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getEventsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getEventsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getEventsForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getEventsForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getEventsForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getEventsForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getEventsForRelations);
		mapFunctions.put(SLEXMMEvent.class,funcs);
		
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getAttributesForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getAttributesForEvents);
		funcs.put(SLEXMMAttribute.class, null);
		funcs.put(SLEXMMClass.class, getStorage()::getAttributesForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getAttributesForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getAttributesForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getAttributesForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getAttributesForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getAttributesForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getAttributesForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getAttributesForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getAttributesForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getAttributesForRelations);
		mapFunctions.put(SLEXMMAttribute.class,funcs);
		
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getClassesForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getClassesForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getClassesForAttributes);
		funcs.put(SLEXMMClass.class, null);
		funcs.put(SLEXMMDataModel.class, getStorage()::getClassesForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getClassesForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getClassesForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getClassesForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getClassesForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getClassesForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getClassesForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getClassesForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getClassesForRelations);
		mapFunctions.put(SLEXMMClass.class,funcs);
		
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getDatamodelsForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getDatamodelsForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getDatamodelsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getDatamodelsForClasses);
		funcs.put(SLEXMMDataModel.class, null);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getDatamodelsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getDatamodelsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getDatamodelsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getDatamodelsForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getDatamodelsForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getDatamodelsForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getDatamodelsForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getDatamodelsForRelations);
		mapFunctions.put(SLEXMMDataModel.class,funcs);
		
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getActivityInstancesForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getActivityInstancesForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getActivityInstancesForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getActivityInstancesForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getActivityInstancesForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, null);
		funcs.put(SLEXMMActivity.class, getStorage()::getActivityInstancesForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getActivityInstancesForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getActivityInstancesForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getActivityInstancesForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getActivityInstancesForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getActivityInstancesForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getActivityInstancesForRelations);
		mapFunctions.put(SLEXMMActivityInstance.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getActivitiesForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getActivitiesForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getActivitiesForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getActivitiesForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getActivitiesForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getActivitiesForActivityInstances);
		funcs.put(SLEXMMActivity.class, null);
		funcs.put(SLEXMMProcess.class, getStorage()::getActivitiesForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getActivitiesForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getActivitiesForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getActivitiesForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getActivitiesForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getActivitiesForRelations);
		mapFunctions.put(SLEXMMActivity.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getProcessesForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getProcessesForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getProcessesForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getProcessesForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getProcessesForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getProcessesForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getProcessesForActivities);
		funcs.put(SLEXMMProcess.class, null);
		funcs.put(SLEXMMCase.class, getStorage()::getProcessesForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getProcessesForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getProcessesForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getProcessesForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getProcessesForRelations);
		mapFunctions.put(SLEXMMProcess.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getCasesForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getCasesForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getCasesForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getCasesForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getCasesForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getCasesForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getCasesForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getCasesForProcesses);
		funcs.put(SLEXMMCase.class, null);
		funcs.put(SLEXMMLog.class, getStorage()::getCasesForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getCasesForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getCasesForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getCasesForRelations);
		mapFunctions.put(SLEXMMCase.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getLogsForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getLogsForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getLogsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getLogsForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getLogsForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getLogsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getLogsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getLogsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getLogsForCases);
		funcs.put(SLEXMMLog.class, null);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getLogsForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getLogsForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getLogsForRelations);
		mapFunctions.put(SLEXMMLog.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getObjectVersionsForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getObjectVersionsForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getObjectVersionsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getObjectVersionsForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getVersionsForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getObjectVersionsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getObjectVersionsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getVersionsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getObjectVersionsForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getVersionsForLogs);
		funcs.put(SLEXMMObjectVersion.class, null);
		funcs.put(SLEXMMRelationship.class, getStorage()::getObjectVersionsForRelationships);
		funcs.put(SLEXMMRelation.class, getStorage()::getObjectVersionsForRelations);
		mapFunctions.put(SLEXMMObjectVersion.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getRelationshipsForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getRelationshipsForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getRelationshipsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getRelationshipsForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getRelationshipsForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getRelationshipsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getRelationshipsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getRelationshipsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getRelationshipsForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getRelationshipsForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getRelationshipsForObjectVersions);
		funcs.put(SLEXMMRelationship.class, null);
		funcs.put(SLEXMMRelation.class, getStorage()::getRelationshipsForRelations);
		mapFunctions.put(SLEXMMRelationship.class,funcs);
				
		funcs = new HashMap<>();
		funcs.put(SLEXMMObject.class, getStorage()::getRelationsForObjects);
		funcs.put(SLEXMMEvent.class, getStorage()::getRelationsForEvents);
		funcs.put(SLEXMMAttribute.class, getStorage()::getRelationsForAttributes);
		funcs.put(SLEXMMClass.class, getStorage()::getRelationsForClasses);
		funcs.put(SLEXMMDataModel.class, getStorage()::getRelationsForDatamodels);
		funcs.put(SLEXMMActivityInstance.class, getStorage()::getRelationsForActivityInstances);
		funcs.put(SLEXMMActivity.class, getStorage()::getRelationsForActivities);
		funcs.put(SLEXMMProcess.class, getStorage()::getRelationsForProcesses);
		funcs.put(SLEXMMCase.class, getStorage()::getRelationsForCases);
		funcs.put(SLEXMMLog.class, getStorage()::getRelationsForLogs);
		funcs.put(SLEXMMObjectVersion.class, getStorage()::getRelationsForObjectVersions);
		funcs.put(SLEXMMRelationship.class, getStorage()::getRelationsForRelationships);
		funcs.put(SLEXMMRelation.class, null);
		mapFunctions.put(SLEXMMRelation.class,funcs);
		
		HashMap<Class<?>,Function<int[],AbstractRSetElement<?>>> periodFunctions = new HashMap<>();
		periodFunctions.put(SLEXMMObject.class, getStorage()::getPeriodsForObjects);
		periodFunctions.put(SLEXMMEvent.class, getStorage()::getPeriodsForEvents);
		periodFunctions.put(SLEXMMAttribute.class, getStorage()::getPeriodsForAttributes);
		periodFunctions.put(SLEXMMClass.class, getStorage()::getPeriodsForClasses);
		periodFunctions.put(SLEXMMDataModel.class, getStorage()::getPeriodsForDatamodels);
		periodFunctions.put(SLEXMMActivityInstance.class, getStorage()::getPeriodsForActivityInstances);
		periodFunctions.put(SLEXMMActivity.class, getStorage()::getPeriodsForActivities);
		periodFunctions.put(SLEXMMProcess.class, getStorage()::getPeriodsForProcesses);
		periodFunctions.put(SLEXMMCase.class, getStorage()::getPeriodsForCases);
		periodFunctions.put(SLEXMMLog.class, getStorage()::getPeriodsForLogs);
		periodFunctions.put(SLEXMMObjectVersion.class, getStorage()::getPeriodsForVersions);
		periodFunctions.put(SLEXMMRelationship.class, getStorage()::getPeriodsForRelationships);
		periodFunctions.put(SLEXMMRelation.class, getStorage()::getPeriodsForRelations);
		mapFunctions.put(SLEXMMPeriod.class,periodFunctions);
    }
	
	public DAPOQLSet objectsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMObject.class);
	}
	
	public DAPOQLSet casesOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMCase.class);
	}
	
	public DAPOQLSet eventsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMEvent.class);
	}
	
	public DAPOQLSet attributesOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMAttribute.class);
	}
	
	public DAPOQLSet classesOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMClass.class);
	}
	
	public DAPOQLSet datamodelsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMDataModel.class);
	}
	
	public DAPOQLSet activityInstancesOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMActivityInstance.class);
	}
	
	public DAPOQLSet activitiesOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMActivity.class);
	}
	
	public DAPOQLSet processesOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMProcess.class);
	}
	
	public DAPOQLSet logsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMLog.class);
	}
	
	public DAPOQLSet versionsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMObjectVersion.class);
	}
	
	public DAPOQLSet relationshipsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMRelationship.class);
	}
	
	public DAPOQLSet relationsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMRelation.class);
	}
	
	public DAPOQLSet periodsOf(DAPOQLSet list) throws Exception {
		return ElementsOf(list, SLEXMMPeriod.class);
	}
	
	public DAPOQLSet versionsRelatedTo(DAPOQLSet list) throws Exception {
		Class<?> type = list.getType();
		
		if (type == SLEXMMObjectVersion.class) {
			return ElementsOf(list, SLEXMMObjectVersion.class, getStorage()::getVersionsRelatedToObjectVersions, null);
		} else {
			throw new Exception("Wrong input type");
		}
	}
	
	public DAPOQLSet getAll(Class<?> targetType, Supplier<AbstractRSetElement<?>> s) {
		DAPOQLSet list = new DAPOQLSet(getStorage(), targetType);
		
		AbstractRSetElement<?> elrset = s.get();
		AbstractDBElement el = null;
		while ((el = (AbstractDBElement) elrset.getNext()) != null) {
			list.add(el);
		}
		return list;
	}
	
	public DAPOQLSet getAllWithAttributes(Class<?> targetType, Supplier<AbstractRSetWithAtts<?,?,?>> s) {
		DAPOQLSet list = new DAPOQLSet(getStorage(), targetType);
		
		AbstractRSetWithAtts<?,?,?> elrset = s.get();
		AbstractDBElementWithAtts<?,?> el = null;
		while ((el = (AbstractDBElementWithAtts<?,?>) elrset.getNextWithAttributes()) != null) {
			list.add(el);
		}
		list.setAttributesFetched(true);
		return list;
	}
	
	public DAPOQLSet ElementsOfPeriod(DAPOQLSet list, Class<?> targetType, Function<SLEXMMPeriod, AbstractRSetElement<?>> f) throws Exception {

		DAPOQLSet listResult = new DAPOQLSet(getStorage(), targetType);
		
		for (Object o : list.getObjSet()) {
			SLEXMMPeriod p = (SLEXMMPeriod) o;
			AbstractRSetElement<?> elrset = f.apply(p);
			AbstractDBElement el = null;
			while ((el = (AbstractDBElement) elrset.getNext()) != null) {
				listResult.add(el);
			}
		}
		
		return listResult;
	}

	public DAPOQLSet ElementsOf(DAPOQLSet list, Class<?> targetType) throws Exception {
		return ElementsOf(list, targetType, null, null);
	}

	public DAPOQLSet ElementsOf(DAPOQLSet list, Class<?> targetType, Function<int[], AbstractRSetElement<?>> fi,
			Function<int[], AbstractRSetWithAtts<?,?,?>> fiwatt) throws Exception {

		Class<?> type = list.getType();
		
		if (fi == null && fiwatt == null) {
			if (type == targetType) {
				return list;
			} else if (type == SLEXMMPeriod.class) {
				Function<SLEXMMPeriod,AbstractRSetElement<?>> fp = mapPeriodFunctions.get(targetType);
				return ElementsOfPeriod(list, targetType, fp);
			}
		}
		
		DAPOQLSet listResult = new DAPOQLSet(getStorage(), targetType);
		
		Function<int[],AbstractRSetElement<?>> f = fi;
		if (fi == null && fiwatt == null) {
			f = mapFunctions.get(targetType).get(type);
		}
		
		int[][] ids = getArrayIds(list.getIdsSet(), type);
		for (int i = 0; i < ids.length; i++) {
			if (fiwatt != null) {
				AbstractRSetWithAtts<?,?,?> elrset = null;
				elrset = fiwatt.apply(ids[i]);
				AbstractDBElementWithAtts<?,?> el = null;
				while ((el = (AbstractDBElementWithAtts<?,?>) elrset.getNextWithAttributes()) != null) {
					listResult.add(el);
				}
				listResult.setAttributesFetched(true);
			} else {
				AbstractRSetElement<?> elrset = null;
				elrset = f.apply(ids[i]);
				AbstractDBElement el = null;
				while ((el = (AbstractDBElement) elrset.getNext()) != null) {
					listResult.add(el);
				}
			}
		}
		
		return listResult;
	}
	
	private int[][] getArrayIds(Set<Integer> list, Class<?> type) {
		Iterator<Integer> it = list.iterator();
		int remaining = list.size();
		int numArrays = (int) Math.ceil(((float) remaining / (float) MAX_IDS_ARRAY_SIZE));
		int[][] idsArrays = new int[numArrays][];
		for (int a = 0; a < numArrays; a++) {
			int size = 0;
			if (remaining > MAX_IDS_ARRAY_SIZE) {
				remaining -= MAX_IDS_ARRAY_SIZE;
				size = MAX_IDS_ARRAY_SIZE;
			} else {
				size = remaining;
				remaining = 0;
			}
			idsArrays[a] = new int[size];
			int[] ids = idsArrays[a];

			for (int i = 0; i < size; i++) {
				Integer oid = it.next();
				ids[i] = oid;
			}
		}

		return idsArrays;
	}
	
	
	public DAPOQLSet getAllObjects() {
		return getAll(SLEXMMObject.class, getStorage()::getObjects);
	}
	
	public DAPOQLSet getAllCases() {
		return getAllWithAttributes(SLEXMMCase.class, getStorage()::getAllCasesAndAttributeValues);
		//return getAll(SLEXMMCase.class, getStorage()::getCases);
	}
	
	public DAPOQLSet getAllEvents() {
		return getAllWithAttributes(SLEXMMEvent.class, getStorage()::getAllEventsAndAttributeValues);
		//return getAll(SLEXMMEvent.class, getStorage()::getEvents);
	}
	
	public DAPOQLSet getAllVersions() {
		return getAllWithAttributes(SLEXMMObjectVersion.class, getStorage()::getAllVersionsAndAttributeValues);
		//return getAll(SLEXMMObjectVersion.class, getStorage()::getObjectVersions);
	}
	
	public DAPOQLSet getAllActivities() {
		return getAll(SLEXMMActivity.class, getStorage()::getActivities);
	}
	
	public DAPOQLSet getAllClasses() {
		return getAll(SLEXMMClass.class, getStorage()::getClasses);
	}
	
	public DAPOQLSet getAllRelations() {
		return getAll(SLEXMMRelation.class, getStorage()::getRelations);
	}
	
	public DAPOQLSet getAllRelationships() {
		return getAll(SLEXMMRelationship.class, getStorage()::getRelationshipsRS);
	}
	
	public DAPOQLSet getAllAttributes() {
		return getAll(SLEXMMAttribute.class, getStorage()::getAttributes);
	}

	public DAPOQLSet getAllDatamodels() {
		return getAll(SLEXMMDataModel.class, getStorage()::getDataModels);
	}

	public DAPOQLSet getAllProcesses() {
		return getAll(SLEXMMProcess.class, getStorage()::getProcesses);
	}

	public DAPOQLSet getAllLogs() {
		return getAllWithAttributes(SLEXMMLog.class, getStorage()::getAllLogsAndAttributeValues);
		//return getAll(SLEXMMLog.class, getStorage()::getLogs);
	}
	
	public DAPOQLSet getAllActivityInstances() {
		return getAll(SLEXMMActivityInstance.class, getStorage()::getActivityInstances);
	}

}
