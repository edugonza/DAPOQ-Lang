package org.processmining.database.metamodel.dapoql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMActivityInstanceResultSet;
import org.processmining.openslex.metamodel.SLEXMMActivityResultSet;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMAttributeResultSet;
import org.processmining.openslex.metamodel.SLEXMMAttributeValue;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMCaseResultSet;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMClassResultSet;
import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMDataModelResultSet;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMEventResultSet;
import org.processmining.openslex.metamodel.SLEXMMLog;
import org.processmining.openslex.metamodel.SLEXMMLogResultSet;
import org.processmining.openslex.metamodel.SLEXMMObject;
import org.processmining.openslex.metamodel.SLEXMMObjectResultSet;
import org.processmining.openslex.metamodel.SLEXMMObjectVersion;
import org.processmining.openslex.metamodel.SLEXMMObjectVersionResultSet;
import org.processmining.openslex.metamodel.SLEXMMPeriod;
import org.processmining.openslex.metamodel.SLEXMMPeriodResultSet;
import org.processmining.openslex.metamodel.SLEXMMProcess;
import org.processmining.openslex.metamodel.SLEXMMProcessResultSet;
import org.processmining.openslex.metamodel.SLEXMMRelation;
import org.processmining.openslex.metamodel.SLEXMMRelationResultSet;
import org.processmining.openslex.metamodel.SLEXMMRelationship;
import org.processmining.openslex.metamodel.SLEXMMRelationshipResultSet;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class DAPOQLFunctionsGroovy {

	private SLEXMMStorageMetaModel slxmm = null;
	private boolean checkerMode = false;

//	public static final int ID_TYPE_ANY = 0;
//	public static final int ID_TYPE_OBJECT = 1;
//	public static final int ID_TYPE_EVENT = 2;
//	public static final int ID_TYPE_CLASS = 3;
//	public static final int ID_TYPE_VERSION = 4;
//	public static final int ID_TYPE_ACTIVITY = 5;
//	public static final int ID_TYPE_RELATION = 6;
//	public static final int ID_TYPE_RELATIONSHIP = 7;
//	public static final int ID_TYPE_ACTIVITY_INSTANCE = 8;
//	public static final int ID_TYPE_CASE = 9;
//	public static final int ID_TYPE_ATTRIBUTE = 10;
//	public static final int ID_TYPE_PERIOD = 11;
//	public static final int ID_TYPE_DATAMODEL = 12;
//	public static final int ID_TYPE_PROCESS = 13;
//	public static final int ID_TYPE_LOG = 14;

	private static final int MAX_IDS_ARRAY_SIZE = 40000;

//	public int typeToInt(Class<?> type) {
//
//		if (type == SLEXMMObject.class) {
//			return ID_TYPE_OBJECT;
//		} else if (type == SLEXMMEvent.class) {
//			return ID_TYPE_EVENT;
//		} else if (type == SLEXMMClass.class) {
//			return ID_TYPE_CLASS;
//		} else if (type == SLEXMMObjectVersion.class) {
//			return ID_TYPE_VERSION;
//		} else if (type == SLEXMMActivity.class) {
//			return ID_TYPE_ACTIVITY;
//		} else if (type == SLEXMMRelation.class) {
//			return ID_TYPE_RELATION;
//		} else if (type == SLEXMMRelationship.class) {
//			return ID_TYPE_RELATIONSHIP;
//		} else if (type == SLEXMMActivityInstance.class) {
//			return ID_TYPE_ACTIVITY_INSTANCE;
//		} else if (type == SLEXMMCase.class) {
//			return ID_TYPE_CASE;
//		} else if (type == SLEXMMAttribute.class) {
//			return ID_TYPE_ATTRIBUTE;
//		} else if (type == SLEXMMPeriod.class) {
//			return ID_TYPE_PERIOD;
//		} else if (type == SLEXMMDataModel.class) {
//			return ID_TYPE_DATAMODEL;
//		} else if (type == SLEXMMProcess.class) {
//			return ID_TYPE_PROCESS;
//		} else if (type == SLEXMMLog.class) {
//			return ID_TYPE_LOG;
//		} else {
//			return -1;
//		}
//
//	}

	public void setMetaModel(SLEXMMStorageMetaModel strg) {
		this.slxmm = strg;
	}

	public void setCheckerMode(boolean mode) {
		this.checkerMode = mode;
	}

	public boolean isCheckerModeEnabled() {
		return this.checkerMode;
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

	public HashMap<Object, HashSet<Integer>> objectsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {

		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			return list;
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForEvents(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForCases(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForActivities(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForClasses(ids[i]);
				SLEXMMObject slxo = null;

				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForRelationships(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForObjectVersions(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForRelations(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForActivityInstances(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForAttributes(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMObjectResultSet orset = slxmm.getObjectsForPeriod(p);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForDatamodels(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForLogs(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForProcesses(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> casesOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForObjects(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForEvents(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			return list;
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForActivities(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForClasses(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForRelationships(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForObjectVersions(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForRelations(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForActivityInstances(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForAttributes(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc, new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMCaseResultSet orset = slxmm.getCasesForPeriod(p);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet orset = slxmm.getCasesForDatamodels(ids[i]);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet orset = slxmm.getCasesForLogs(ids[i]);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet orset = slxmm.getCasesForProcesses(ids[i]);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> eventsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {

		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForObjects(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			return list;
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForCases(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForActivities(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForClasses(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForRelationships(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForObjectVersions(ids[i]);
				SLEXMMEvent e = null;

				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForRelations(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForActivityInstances(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForAttributes(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e, new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMEventResultSet orset = slxmm.getEventsForPeriod(p);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet orset = slxmm.getEventsForDatamodels(ids[i]);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet orset = slxmm.getEventsForLogs(ids[i]);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet orset = slxmm.getEventsForProcesses(ids[i]);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;

	}

	public HashMap<Object, HashSet<Integer>> versionsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForObjects(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForEvents(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForCases(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForActivities(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForClasses(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForRelationships(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			return list;
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForRelations(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForActivityInstances(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForAttributes(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForPeriod(p);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForDatamodels(ids[i]);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForLogs(ids[i]);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForProcesses(ids[i]);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> activitiesOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForObjects(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForEvents(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForCases(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			return list;
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForClasses(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForRelationships(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForObjectVersions(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForRelations(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForActivityInstances(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForAttributes(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov, new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForPeriod(p);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForDatamodels(ids[i]);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForLogs(ids[i]);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForProcesses(ids[i]);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> classesOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForObjects(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForEvents(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForCases(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForActivities(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			return list;
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForRelationships(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForObjectVersions(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForRelations(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForActivityInstances(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForAttributes(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c, new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMClassResultSet orset = slxmm.getClassesForPeriod(p);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet orset = slxmm.getClassesForDatamodels(ids[i]);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet orset = slxmm.getClassesForLogs(ids[i]);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet orset = slxmm.getClassesForProcesses(ids[i]);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> relationsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForObjects(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForEvents(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { //
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForCases(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { //
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForActivities(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForClasses(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForRelationships(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForObjectVersions(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			return list;
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForActivityInstances(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForAttributes(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r, new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMRelationResultSet orset = slxmm.getRelationsForPeriod(p);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet orset = slxmm.getRelationsForDatamodels(ids[i]);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet orset = slxmm.getRelationsForLogs(ids[i]);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet orset = slxmm.getRelationsForProcesses(ids[i]);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> relationshipsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForObjects(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForEvents(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForCases(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForActivities(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForClasses(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			return list;
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForObjectVersions(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForRelations(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForActivityInstances(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForAttributes(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs, new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForPeriod(p);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForDatamodels(ids[i]);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForLogs(ids[i]);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForProcesses(ids[i]);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> activityInstancesOf(HashMap<Object, HashSet<Integer>> list,
			Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForObjects(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForEvents(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForCases(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForActivities(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForClasses(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForRelationships(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForObjectVersions(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForRelations(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			return list;
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForAttributes(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai, new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForPeriod(p);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForDatamodels(ids[i]);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForLogs(ids[i]);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForProcesses(ids[i]);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> attributesOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForObjects(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForEvents(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForCases(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForActivities(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForClasses(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForRelationships(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForObjectVersions(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForRelations(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForActivityInstances(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			return list;
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForPeriod(p);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForDatamodels(ids[i]);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForLogs(ids[i]);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForProcesses(ids[i]);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> datamodelsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForObjects(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForEvents(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForCases(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForActivities(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForClasses(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForRelationships(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForObjectVersions(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForRelations(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForActivityInstances(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForAttributes(ids[i]);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForPeriod(p);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			return list;
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForLogs(ids[i]);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForProcesses(ids[i]);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> processesOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForObjects(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForEvents(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForCases(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForActivities(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForClasses(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForRelationships(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForObjectVersions(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForRelations(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForActivityInstances(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet orset = slxmm.getProcessesForAttributes(ids[i]);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMProcessResultSet orset = slxmm.getProcessesForPeriod(p);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet orset = slxmm.getProcessesForDatamodels(ids[i]);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet orset = slxmm.getProcessesForLogs(ids[i]);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			return list;
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> logsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForObjects(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForEvents(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForCases(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForActivities(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForClasses(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForRelationships(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForObjectVersions(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForRelations(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForActivityInstances(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at, new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet orset = slxmm.getLogsForAttributes(ids[i]);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMLogResultSet orset = slxmm.getLogsForPeriod(p);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet orset = slxmm.getLogsForDatamodels(ids[i]);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			return list;
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet orset = slxmm.getLogsForProcesses(ids[i]);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	public HashMap<Object, HashSet<Integer>> periodsOf(HashMap<Object, HashSet<Integer>> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> listResult = new HashMap<>();

		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForObjects(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForEvents(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForCases(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForActivities(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForClasses(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForRelationships(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForVersions(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForRelations(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForActivityInstances(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForAttributes(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p, new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			return list;
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet orset = slxmm.getPeriodsForDatamodels(ids[i]);
				SLEXMMPeriod slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet orset = slxmm.getPeriodsForLogs(ids[i]);
				SLEXMMPeriod slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(), type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet orset = slxmm.getPeriodsForProcesses(ids[i]);
				SLEXMMPeriod slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo, new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return listResult;
	}

	private int[][] getArrayIds(Set<Object> list, Class<?> type) {
		Iterator<Object> it = list.iterator();
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

			if (type == SLEXMMObject.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMObject ob = (SLEXMMObject) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMEvent.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMEvent ob = (SLEXMMEvent) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMCase.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMCase ob = (SLEXMMCase) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMActivity.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMActivity ob = (SLEXMMActivity) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMClass.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMClass ob = (SLEXMMClass) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMRelationship.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMRelationship ob = (SLEXMMRelationship) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMObjectVersion.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMObjectVersion ob = (SLEXMMObjectVersion) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMRelation.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMRelation ob = (SLEXMMRelation) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMActivityInstance.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMActivityInstance ob = (SLEXMMActivityInstance) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMAttribute.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMAttribute ob = (SLEXMMAttribute) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMDataModel.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMDataModel ob = (SLEXMMDataModel) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMProcess.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMProcess ob = (SLEXMMProcess) it.next();
					ids[i] = ob.getId();
				}
			} else if (type == SLEXMMLog.class) {
				for (int i = 0; i < size; i++) {
					SLEXMMLog ob = (SLEXMMLog) it.next();
					ids[i] = ob.getId();
				}
			} else {
				// ERROR
				System.err.println("Unknown type");
			}
		}

		return idsArrays;
	}

	public HashMap<Object, HashSet<Integer>> versionsRelatedTo(Set<Object> list, Class<?> type) {
		HashMap<Object, HashSet<Integer>> setResult = new HashMap<>();

		if (type == SLEXMMObjectVersion.class) {
			int[] ids = new int[list.size()];
			int i = 0;
			for (Object o : list) {
				SLEXMMObjectVersion ob = (SLEXMMObjectVersion) o;
				ids[i] = ob.getId();
				i++;
			}

			SLEXMMObjectVersionResultSet ovrset = slxmm.getVersionsRelatedToObjectVersions(ids);

			SLEXMMObjectVersion ov = null;
			while ((ov = ovrset.getNext()) != null) {
				Integer originId = ovrset.getOriginId();
				if (!setResult.containsKey(ov)) {
					setResult.put(ov, new HashSet<Integer>());
				}
				setResult.get(ov).add(originId);
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return setResult;
	}

	public HashMap<Object, HashSet<Integer>> getAllObjects() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMObjectResultSet orset = slxmm.getObjects();
			SLEXMMObject o = null;
			while ((o = orset.getNext()) != null) {
				list.put(o, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllCases() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMCaseResultSet crset = slxmm.getCases();
			SLEXMMCase c = null;
			while ((c = crset.getNext()) != null) {
				list.put(c, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllEvents() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMEventResultSet erset = slxmm.getEvents();
			SLEXMMEvent e = null;
			while ((e = erset.getNext()) != null) {
				list.put(e, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllVersions() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersions();
			SLEXMMObjectVersion ov = null;
			while ((ov = ovrset.getNext()) != null) {
				list.put(ov, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllActivities() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMActivityResultSet acrset = slxmm.getActivities();
			SLEXMMActivity act = null;
			while ((act = acrset.getNext()) != null) {
				list.put(act, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllClasses() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMClassResultSet crset = slxmm.getClasses();
			SLEXMMClass cl = null;
			while ((cl = crset.getNext()) != null) {
				list.put(cl, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllRelations() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMRelationResultSet rrset = slxmm.getRelations();
			SLEXMMRelation r = null;
			while ((r = rrset.getNext()) != null) {
				list.put(r, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllRelationships() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			for (SLEXMMRelationship rs : slxmm.getRelationships()) {
				list.put(rs, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllActivityInstances() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstances();
			SLEXMMActivityInstance ai = null;
			while ((ai = airset.getNext()) != null) {
				list.put(ai, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllAttributes() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMAttributeResultSet arset = slxmm.getAttributes();
			SLEXMMAttribute at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllDatamodels() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMDataModelResultSet arset = slxmm.getDataModels();
			SLEXMMDataModel at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllProcesses() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMProcessResultSet arset = slxmm.getProcesses();
			SLEXMMProcess at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at, null);
			}
		}
		return list;
	}

	public HashMap<Object, HashSet<Integer>> getAllLogs() {
		HashMap<Object, HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMLogResultSet arset = slxmm.getLogs();
			SLEXMMLog at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at, null);
			}
		}
		return list;
	}

//	public HashMap<Object, HashSet<Integer>> concurrentWith(HashMap<Object, HashSet<Integer>> vals, Class<?> type) {
//		HashMap<Object, HashSet<Integer>> result = new HashMap<>();
//
//		HashMap<Object, HashSet<Integer>> periodsMap = periodsOf(vals, type);
//
//		for (Object p : periodsMap.keySet()) {
//
//			HashMap<Object, HashSet<Integer>> inputMap = new HashMap<>();
//			inputMap.put(p, periodsMap.get(p));
//			HashMap<Object, HashSet<Integer>> concurrentSet = null;
//
//			if (type == SLEXMMActivity.class) {
//				concurrentSet = activitiesOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMActivityInstance.class) {
//				concurrentSet = activityInstancesOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMAttribute.class) {
//				concurrentSet = attributesOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMCase.class) {
//				concurrentSet = casesOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMClass.class) {
//				concurrentSet = classesOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMEvent.class) {
//				concurrentSet = eventsOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMObject.class) {
//				concurrentSet = objectsOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMObjectVersion.class) {
//				concurrentSet = versionsOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMRelation.class) {
//				concurrentSet = relationsOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMRelationship.class) {
//				concurrentSet = relationshipsOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMDataModel.class) {
//				concurrentSet = datamodelsOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMProcess.class) {
//				concurrentSet = processesOf(inputMap, SLEXMMPeriod.class);
//			} else if (type == SLEXMMLog.class) {
//				concurrentSet = logsOf(inputMap, SLEXMMPeriod.class);
//			} else {
//				System.err.println("Unknown type");
//				break;
//			}
//
//			for (Object o : concurrentSet.keySet()) {
//				if (!result.containsKey(o)) {
//					result.put(o, new HashSet<Integer>());
//				}
//				result.get(o).addAll(periodsMap.get(p));
//			}
//
//		}
//		return result;
//	}
//
//	public HashMap<Object, HashSet<Integer>> getScopeOf(int scope, HashMap<Object, HashSet<Integer>> val,
//			Class<?> type) {
//		HashMap<Object, HashSet<Integer>> result = null;
//
//		switch (scope) {
//		case ID_TYPE_ACTIVITY:
//			result = activitiesOf(val, type);
//			break;
//		case ID_TYPE_ACTIVITY_INSTANCE:
//			result = activityInstancesOf(val, type);
//			break;
//		case ID_TYPE_ATTRIBUTE:
//			result = attributesOf(val, type);
//			break;
//		case ID_TYPE_CASE:
//			result = casesOf(val, type);
//			break;
//		case ID_TYPE_CLASS:
//			result = classesOf(val, type);
//			break;
//		case ID_TYPE_EVENT:
//			result = eventsOf(val, type);
//			break;
//		case ID_TYPE_OBJECT:
//			result = objectsOf(val, type);
//			break;
//		case ID_TYPE_RELATION:
//			result = relationsOf(val, type);
//			break;
//		case ID_TYPE_RELATIONSHIP:
//			result = relationshipsOf(val, type);
//			break;
//		case ID_TYPE_VERSION:
//			result = versionsOf(val, type);
//			break;
//		case ID_TYPE_DATAMODEL:
//			result = datamodelsOf(val, type);
//			break;
//		case ID_TYPE_PROCESS:
//			result = processesOf(val, type);
//			break;
//		case ID_TYPE_LOG:
//			result = logsOf(val, type);
//			break;
//		default:
//			break;
//		}
//
//		return result;
//	}

}
