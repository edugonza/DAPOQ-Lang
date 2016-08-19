package org.processmining.database.metamodel.dapoql.lite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMActivityInstanceResultSet;
import org.processmining.openslex.metamodel.SLEXMMActivityResultSet;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMAttributeResultSet;
import org.processmining.openslex.metamodel.SLEXMMAttributeValue;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMCaseAttribute;
import org.processmining.openslex.metamodel.SLEXMMCaseAttributeValue;
import org.processmining.openslex.metamodel.SLEXMMCaseResultSet;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMClassResultSet;
import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMDataModelResultSet;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMEventAttribute;
import org.processmining.openslex.metamodel.SLEXMMEventAttributeResultSet;
import org.processmining.openslex.metamodel.SLEXMMEventAttributeValue;
import org.processmining.openslex.metamodel.SLEXMMEventResultSet;
import org.processmining.openslex.metamodel.SLEXMMLog;
import org.processmining.openslex.metamodel.SLEXMMLogAttribute;
import org.processmining.openslex.metamodel.SLEXMMLogAttributeValue;
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

public class DAPOQLFunctions {

	private SLEXMMStorageMetaModel slxmm = null;
	private boolean checkerMode = false;
	private List<String> suggestions = null;
	private Token offendingToken = null;
	private Vocabulary vocabulary = null;
	private HashMap<String,DAPOQLVariable> variablesMap = new HashMap<>();
	
	public static final int ID_TYPE_ANY = 0;
	public static final int ID_TYPE_OBJECT = 1;
	public static final int ID_TYPE_EVENT = 2;
	public static final int ID_TYPE_CLASS = 3;
	public static final int ID_TYPE_VERSION = 4;
	public static final int ID_TYPE_ACTIVITY = 5;
	public static final int ID_TYPE_RELATION = 6;
	public static final int ID_TYPE_RELATIONSHIP = 7;
	public static final int ID_TYPE_ACTIVITY_INSTANCE = 8;
	public static final int ID_TYPE_CASE = 9;
	public static final int ID_TYPE_ATTRIBUTE = 10;
	public static final int ID_TYPE_PERIOD = 11;
	public static final int ID_TYPE_DATAMODEL = 12;
	public static final int ID_TYPE_PROCESS = 13;
	public static final int ID_TYPE_LOG = 14;
	
	private static final int MAX_IDS_ARRAY_SIZE = 40000;

	public boolean checkVariable(dapoqlParser parser, int type) {
		DAPOQLVariable var = findVariable(parser.getTokenStream().LT(1).getText());
		
		if (var != null) {
			if (typeToInt(var.getType()) == type) {
				return true;
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public int typeToInt(Class<?> type) {
		
		if (type == SLEXMMObject.class) {
			return ID_TYPE_OBJECT;
		} else if (type == SLEXMMEvent.class) {
			return ID_TYPE_EVENT;
		} else if (type == SLEXMMClass.class) {
			return ID_TYPE_CLASS;
		} else if (type == SLEXMMObjectVersion.class) {
			return ID_TYPE_VERSION;
		} else if (type == SLEXMMActivity.class) {
			return ID_TYPE_ACTIVITY;
		} else if (type == SLEXMMRelation.class) {
			return ID_TYPE_RELATION;
		} else if (type == SLEXMMRelationship.class) {
			return ID_TYPE_RELATIONSHIP;
		} else if (type == SLEXMMActivityInstance.class) {
			return ID_TYPE_ACTIVITY_INSTANCE;
		} else if (type == SLEXMMCase.class) {
			return ID_TYPE_CASE;
		} else if (type == SLEXMMAttribute.class) {
			return ID_TYPE_ATTRIBUTE;
		} else if (type == SLEXMMPeriod.class) {
			return ID_TYPE_PERIOD;
		} else if (type == SLEXMMDataModel.class) {
			return ID_TYPE_DATAMODEL;
		} else if (type == SLEXMMProcess.class) {
			return ID_TYPE_PROCESS;
		} else if (type == SLEXMMLog.class) {
			return ID_TYPE_LOG;
		} else {
			return -1;
		}
		
	}
	
	public void setOffendingToken(Token offendingToken) {
		this.offendingToken = offendingToken;
	}
	
	public void setCheckerMode(boolean mode) {
		this.checkerMode = mode;
	}

	public boolean isCheckerModeEnabled() {
		return this.checkerMode;
	}

	public void setMetaModel(SLEXMMStorageMetaModel strg) {
		this.slxmm = strg;
	}

	public DAPOQLVariable findVariable(String name) {
		DAPOQLVariable var = null;
		
		if (variablesMap != null) {
			var = variablesMap.get(name);
		}				
		
		return var;
	}
	
	public void removeVariable(String name) {
		
		DAPOQLVariable var = findVariable(name);
		
		if (var == null) {
			System.err.println("Variable "+name+" does not exist"); // Throw exception?
			return;
		}
		
		if (variablesMap != null) {
			variablesMap.remove(name);
		}
		
	}
	
	public DAPOQLVariable createVariable(String name, Class<?> type, HashMap<Object,HashSet<Integer>> value) {
		
		if (findVariable(name) != null) {
			System.err.println("Variable "+name+" already defined."); // Throw exception?
			return null;
		}
		
		DAPOQLVariable var = new DAPOQLVariable(name, type, value);
		
		if (variablesMap == null) {
			variablesMap = new HashMap<>();
		}
		
		variablesMap.put(var.getName(), var);
		
		return var;
	}
	
	public Set<Object> set_operation(int op, Set<Object> listA, Set<Object> listB, Class<?> type) {
		HashSet<Object> resultList = new HashSet<>();
		
		if (op == dapoqlParser.UNION) {
			resultList.addAll(listA);
			resultList.addAll(listB);
		} else if (op == dapoqlParser.EXCLUDING) {
			resultList.addAll(listA);
			resultList.removeAll(listB);
		} else if (op == dapoqlParser.INTERSECTION) {
			HashSet<Object> intersectionSet = new HashSet<>();
			intersectionSet.addAll(listA);
			for (Object o: listB) {
				if (intersectionSet.contains(o)) {
					resultList.add(o);
				}
			}
		} else {
			return listA;
		}
		
		return resultList;
	}
	
	public Set<Object> filterTerminal(Set<Object> list, Class<?> type,
			FilterTree condition) {
		HashSet<Object> filteredList = new HashSet<>();

		if (type == SLEXMMObject.class) {
			for (Object o : list) {
				SLEXMMObject ob = (SLEXMMObject) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Object");
					return list;
				} else if (condition.getKeyId() == (dapoqlParser.CLASS_ID)) {
					v = String.valueOf(ob.getClassId());
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMDataModel.class) {
			for (Object o : list) {
				SLEXMMDataModel ob = (SLEXMMDataModel) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Datamodel");
					return list;
				} else if (condition.getKeyId() == (dapoqlParser.NAME)) {
					v = String.valueOf(ob.getName());
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMProcess.class) {
			for (Object o : list) {
				SLEXMMProcess ob = (SLEXMMProcess) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Process");
					return list;
				} else if (condition.getKeyId() == (dapoqlParser.NAME)) {
					v = String.valueOf(ob.getName());
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMLog.class) {
			for (Object o : list) {
				SLEXMMLog ob = (SLEXMMLog) o;
				String v = null;
				SLEXMMLogAttribute slxAtt = null;
				if (condition.isAttribute()) {
					HashMap<SLEXMMLogAttribute, SLEXMMLogAttributeValue> attsMap = ob
							.getAttributeValues();
					slxAtt = null;

					for (SLEXMMLogAttribute at : attsMap.keySet()) {
						if (at.getName().equals(condition.getKey())) {
							slxAtt = at;
							break;
						}
					}

					if (slxAtt != null) {
						SLEXMMLogAttributeValue slxAttVal = attsMap.get(slxAtt);
						if (slxAttVal != null) {
							v = slxAttVal.getValue();
						}
					}
					
				} else if (condition.getKeyId() == (dapoqlParser.NAME)) {
					v = String.valueOf(ob.getName());
				} else if (condition.getKeyId() == (dapoqlParser.PROCESS_ID)) {
					v = String.valueOf(ob.getProcessId());
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMObjectVersion.class) {
			for (Object o : list) {
				SLEXMMObjectVersion ob = (SLEXMMObjectVersion) o;
				String v = null;
				SLEXMMAttribute slxAtt = null;
				if (condition.isAttribute()) {
					HashMap<SLEXMMAttribute, SLEXMMAttributeValue> attsMap = ob
							.getAttributeValues();
					slxAtt = null;

					for (SLEXMMAttribute at : attsMap.keySet()) {
						if (at.getName().equals(condition.getKey())) {
							slxAtt = at;
							break;
						}
					}

					if (slxAtt != null) {
						SLEXMMAttributeValue slxAttVal = attsMap.get(slxAtt);
						if (slxAttVal != null) {
							v = slxAttVal.getValue();
						}
					}

				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.OBJECT_ID) {
					v = String.valueOf(ob.getObjectId());
				} else if (condition.getKeyId() == dapoqlParser.START_TIMESTAMP) {
					v = String.valueOf(ob.getStartTimestamp());
				} else if (condition.getKeyId() == dapoqlParser.END_TIMESTAMP) {
					v = String.valueOf(ob.getEndTimestamp());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (condition.isChanged()) {
					if (slxAtt != null) {
						if (filterChangedOperation(ob, slxAtt, v,
								condition.valueFrom, condition.valueTo)) {
							filteredList.add(o);
						}
					}
				} else if (v != null
						&& filterOperation(v, condition.value,
								condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMCase.class) {
			for (Object o : list) {
				SLEXMMCase ob = (SLEXMMCase) o;
				String v = null;
				SLEXMMCaseAttribute slxAtt = null;
				if (condition.isAttribute()) {
					HashMap<SLEXMMCaseAttribute, SLEXMMCaseAttributeValue> attsMap = ob
							.getAttributeValues();
					slxAtt = null;

					for (SLEXMMCaseAttribute at : attsMap.keySet()) {
						if (at.getName().equals(condition.getKey())) {
							slxAtt = at;
							break;
						}
					}

					if (slxAtt != null) {
						SLEXMMCaseAttributeValue slxAttVal = attsMap.get(slxAtt);
						if (slxAttVal != null) {
							v = slxAttVal.getValue();
						}
					}
					
				} else if (condition.getKeyId() == (dapoqlParser.NAME)) {
					v = String.valueOf(ob.getName());
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMEvent.class) {
			
			List<SLEXMMEventAttribute> slxAtts = new ArrayList<>();
			
			if (condition.isAttribute()) {
				SLEXMMEventAttributeResultSet earset = slxmm.getEventAttributes();
				SLEXMMEventAttribute ea = null;
				while ((ea = earset.getNext()) != null) {
					if (ea.getName().equals(condition.getKey())) {
						slxAtts.add(ea);
						break;
					}
				}
			}
			
			for (Object o : list) {
				SLEXMMEvent ob = (SLEXMMEvent) o;
				String v = null;
				if (condition.isAttribute()) {
					HashMap<SLEXMMEventAttribute, SLEXMMEventAttributeValue> attsMap = ob
							.getAttributeValues();
					
					if (!slxAtts.isEmpty()) {
						for (SLEXMMEventAttribute at: slxAtts) {
							SLEXMMEventAttributeValue slxAttVal = attsMap
								.get(at);
							if (slxAttVal != null) {
								v = slxAttVal.getValue();
								break;
							}
						}
					}

				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.ACTIVITY_INSTANCE_ID) {
					v = String.valueOf(ob.getActivityInstanceId());
				} else if (condition.getKeyId() == dapoqlParser.ORDERING) {
					v = String.valueOf(ob.getOrder());
				} else if (condition.getKeyId() == dapoqlParser.TIMESTAMP) {
					v = String.valueOf(ob.getTimestamp());
				} else if (condition.getKeyId() == dapoqlParser.LIFECYCLE) {
					v = String.valueOf(ob.getLifecycle());
				} else if (condition.getKeyId() == dapoqlParser.RESOURCE) {
					v = String.valueOf(ob.getResource());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (v != null
						&& filterOperation(v, condition.value,
								condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMClass.class) {
			for (Object o : list) {
				SLEXMMClass ob = (SLEXMMClass) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Class");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.DATAMODEL_ID) {
					v = String.valueOf(ob.getDataModelId());
				} else if (condition.getKeyId() == dapoqlParser.NAME) {
					v = String.valueOf(ob.getName());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (v != null
						&& filterOperation(v, condition.value,
								condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMActivity.class) {
			for (Object o : list) {
				SLEXMMActivity ob = (SLEXMMActivity) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Activity");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.NAME) {
					v = String.valueOf(ob.getName());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMActivityInstance.class) {
			for (Object o : list) {
				SLEXMMActivityInstance ob = (SLEXMMActivityInstance) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Activity Instance");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.ACTIVITY_ID) {
					v = String.valueOf(ob.getActivityId());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMRelation.class) {
			for (Object o : list) {
				SLEXMMRelation ob = (SLEXMMRelation) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Relation");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.SOURCE_OBJECT_VERSION_ID) {
					v = String.valueOf(ob.getSourceObjectVersionId());
				} else if (condition.getKeyId() == dapoqlParser.TARGET_OBJECT_VERSION_ID) {
					v = String.valueOf(ob.getTargetObjectVersionId());
				} else if (condition.getKeyId() == dapoqlParser.RELATIONSHIP_ID) {
					v = String.valueOf(ob.getRelationshipId());
				} else if (condition.getKeyId() == dapoqlParser.START_TIMESTAMP) {
					v = String.valueOf(ob.getStartTimestamp());
				} else if (condition.getKeyId() == dapoqlParser.END_TIMESTAMP) {
					v = String.valueOf(ob.getEndTimestamp());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMRelationship.class) {
			for (Object o : list) {
				SLEXMMRelationship ob = (SLEXMMRelationship) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Relationship");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.SOURCE) {
					v = String.valueOf(ob.getSourceClassId());
				} else if (condition.getKeyId() == dapoqlParser.TARGET) {
					v = String.valueOf(ob.getTargetClassId());
				} else if (condition.getKeyId() == dapoqlParser.NAME) {
					v = String.valueOf(ob.getName());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (filterOperation(v, condition.value, condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMAttribute.class) {
			for (Object o : list) {
				SLEXMMAttribute ob = (SLEXMMAttribute) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Attribute");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.ID) {
					v = String.valueOf(ob.getId());
				} else if (condition.getKeyId() == dapoqlParser.CLASS_ID) {
					v = String.valueOf(ob.getClassId());
				} else if (condition.getKeyId() == dapoqlParser.NAME) {
					v = String.valueOf(ob.getName());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (v != null
						&& filterOperation(v, condition.value,
								condition.operator)) {
					filteredList.add(o);
				}

			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o : list) {
				SLEXMMPeriod ob = (SLEXMMPeriod) o;
				String v = null;
				if (condition.isAttribute()) {
					// ERROR
					System.err.println("No attributes for type Period");
					return list;
				} else if (condition.getKeyId() == dapoqlParser.START) {
					v = String.valueOf(ob.getStart());
				} else if (condition.getKeyId() == dapoqlParser.END) {
					v = String.valueOf(ob.getEnd());
				} else {
					// ERROR
					System.err.println("Unknown key");
					return list;
				}

				if (v != null
						&& filterOperation(v, condition.value,
								condition.operator)) {
					filteredList.add(o);
				}

			}
		} else {
			// ERROR
			System.err.println("Unknown type");
			return list;
		}

		return filteredList;
	}

	public boolean filterChangedOperation(SLEXMMObjectVersion ov,
			SLEXMMAttribute slxAtt, String v, String valueFrom, String valueTo) {

		SLEXMMObjectVersionResultSet ovrset = slxmm
				.getObjectVersionsForObject(ov.getObjectId());
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

	public boolean filterOperation(String a, String b, int op) {
		switch (op) {
		case FilterTree.OPERATOR_CONTAINS:
			return (a.contains(b));
		case FilterTree.OPERATOR_DIFFERENT:
			return !a.equals(b);
		case FilterTree.OPERATOR_EQUAL:
			return a.equals(b);
		case FilterTree.OPERATOR_EQUAL_OR_GREATER_THAN:
			return a.compareTo(b) >= 0;
		case FilterTree.OPERATOR_EQUAL_OR_SMALLER_THAN:
			return a.compareTo(b) <= 0;
		case FilterTree.OPERATOR_GREATER_THAN:
			return a.compareTo(b) > 0;
		case FilterTree.OPERATOR_SMALLER_THAN:
			return a.compareTo(b) < 0;
		default:
			return false;
		}
	}

	public Set<Object> filter(Set<Object> list, Class<?> type,
			FilterTree conditions) {
		HashSet<Object> filteredList = new HashSet<>();

		if (conditions.isTerminal()) {
			// Filter terminal
			return filterTerminal(list, type, conditions);
		} else if (conditions.isNot()) {
			// Filter NOT
			for (Object o : list) {
				if (filter(new HashSet<>(Arrays.asList(o)), type, conditions.leftChild)
						.isEmpty()) {
					filteredList.add(o);
				}
			}
		} else if (conditions.isAnd()) {
			// Filter AND
			for (Object o : list) {
				if (!filter(new HashSet<>(Arrays.asList(o)), type, conditions.leftChild)
						.isEmpty()) {
					if (!filter(new HashSet<>(Arrays.asList(o)), type, conditions.rightChild)
							.isEmpty()) {
						filteredList.add(o);
					}
				}
			}
		} else if (conditions.isOr()) {
			// Filter OR
			for (Object o : list) {
				if (filter(new HashSet<>(Arrays.asList(o)), type, conditions.leftChild)
						.isEmpty()) {
					if (!filter(new HashSet<>(Arrays.asList(o)), type, conditions.rightChild)
							.isEmpty()) {
						filteredList.add(o);
					}
				} else {
					filteredList.add(o);
				}
			}
		} else {
			// ERROR
			System.err.println("Unknown Filter node");
			return list;
		}

		return filteredList;
	}

	public FilterTree createNotNode(FilterTree tree) {
		FilterTree notNode = new FilterTree();
		notNode.node = FilterTree.NODE_NOT;
		notNode.leftChild = tree;
		return notNode;
	}

	public FilterTree createNode(FilterTree left, FilterTree right, int operator) {
		if (operator == FilterTree.NODE_AND) {
			return createAndNode(left, right);
		} else if (operator == FilterTree.NODE_OR) {
			return createOrNode(left, right);
		} else if (operator == FilterTree.NODE_NOT) {
			return createNotNode(left);
		} else {
			// ERROR
			System.err.println("Unknown Node Type");
			return null;
		}
	}

	public FilterTree createAndNode(FilterTree left, FilterTree right) {
		FilterTree andNode = new FilterTree();
		andNode.node = FilterTree.NODE_AND;
		andNode.leftChild = left;
		andNode.rightChild = right;
		return andNode;
	}

	public FilterTree createOrNode(FilterTree left, FilterTree right) {
		FilterTree orNode = new FilterTree();
		orNode.node = FilterTree.NODE_OR;
		orNode.leftChild = left;
		orNode.rightChild = right;
		return orNode;
	}

	public FilterTree createChangedTerminalFilter(String key, String from,
			String to) {
		FilterTree node = new FilterTree();
		node.node = FilterTree.NODE_TERMINAL;
		node.operator = FilterTree.OPERATOR_CHANGED;
		node.key = key;
		node.valueFrom = from;
		node.valueTo = to;
		node.att = true;
		return node;
	}

	public FilterTree createTerminalFilter(int id, String key, String value,
			int operator, boolean att) {
		FilterTree node = new FilterTree();
		node.node = FilterTree.NODE_TERMINAL;
		node.operator = operator;
		node.key = key;
		node.keyId = id;
		node.value = value;
		node.att = att;
		return node;
	}

	public HashMap<Object,HashSet<Integer>> objectsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			return list;
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForEvents(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForCases(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForActivities(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForClasses(ids[i]);
				SLEXMMObject slxo = null;

				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForRelationships(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForObjectVersions(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForRelations(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForActivityInstances(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForAttributes(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMObjectResultSet orset = slxmm.getObjectsForPeriod(p);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForDatamodels(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForLogs(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectResultSet orset = slxmm.getObjectsForProcesses(ids[i]);
				SLEXMMObject slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> casesOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForObjects(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForEvents(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			return list;
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForActivities(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForClasses(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForRelationships(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForObjectVersions(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForRelations(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForActivityInstances(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet crset = slxmm.getCasesForAttributes(ids[i]);
				SLEXMMCase slxc = null;
				while ((slxc = crset.getNext()) != null) {
					if (!listResult.containsKey(slxc)) {
						listResult.put(slxc,new HashSet<Integer>());
					}
					listResult.get(slxc).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMCaseResultSet orset = slxmm.getCasesForPeriod(p);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet orset = slxmm.getCasesForDatamodels(ids[i]);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet orset = slxmm.getCasesForLogs(ids[i]);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMCaseResultSet orset = slxmm.getCasesForProcesses(ids[i]);
				SLEXMMCase slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> eventsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForObjects(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			return list;
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForCases(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForActivities(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForClasses(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForRelationships(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForObjectVersions(ids[i]);
				SLEXMMEvent e = null;

				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForRelations(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForActivityInstances(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet erset = slxmm.getEventsForAttributes(ids[i]);
				SLEXMMEvent e = null;
				while ((e = erset.getNext()) != null) {
					if (!listResult.containsKey(e)) {
						listResult.put(e,new HashSet<Integer>());
					}
					listResult.get(e).add(erset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMEventResultSet orset = slxmm.getEventsForPeriod(p);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet orset = slxmm.getEventsForDatamodels(ids[i]);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet orset = slxmm.getEventsForLogs(ids[i]);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMEventResultSet orset = slxmm.getEventsForProcesses(ids[i]);
				SLEXMMEvent slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> versionsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
		
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm
						.getObjectVersionsForObjects(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForEvents(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForCases(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForActivities(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForClasses(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForRelationships(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			return list;
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForRelations(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForActivityInstances(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersionsForAttributes(ids[i]);
				SLEXMMObjectVersion ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForPeriod(p);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForDatamodels(ids[i]);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForLogs(ids[i]);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMObjectVersionResultSet orset = slxmm.getVersionsForProcesses(ids[i]);
				SLEXMMObjectVersion slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> activitiesOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForObjects(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForEvents(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForCases(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			return list;
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForClasses(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForRelationships(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForObjectVersions(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForRelations(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForActivityInstances(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet ovrset = slxmm.getActivitiesForAttributes(ids[i]);
				SLEXMMActivity ov = null;
				while ((ov = ovrset.getNext()) != null) {
					if (!listResult.containsKey(ov)) {
						listResult.put(ov,new HashSet<Integer>());
					}
					listResult.get(ov).add(ovrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForPeriod(p);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForDatamodels(ids[i]);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForLogs(ids[i]);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityResultSet orset = slxmm.getActivitiesForProcesses(ids[i]);
				SLEXMMActivity slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> classesOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForObjects(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForEvents(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForCases(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForActivities(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			return list;
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForRelationships(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForObjectVersions(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForRelations(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForActivityInstances(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet crset = slxmm.getClassesForAttributes(ids[i]);
				SLEXMMClass c = null;
				while ((c = crset.getNext()) != null) {
					if (!listResult.containsKey(c)) {
						listResult.put(c,new HashSet<Integer>());
					}
					listResult.get(c).add(crset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMClassResultSet orset = slxmm.getClassesForPeriod(p);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet orset = slxmm.getClassesForDatamodels(ids[i]);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet orset = slxmm.getClassesForLogs(ids[i]);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMClassResultSet orset = slxmm.getClassesForProcesses(ids[i]);
				SLEXMMClass slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> relationsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForObjects(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForEvents(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { // 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForCases(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { // 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForActivities(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForClasses(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForRelationships(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForObjectVersions(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			return list;
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForActivityInstances(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet rrset = slxmm.getRelationsForAttributes(ids[i]);
				SLEXMMRelation r = null;
				while ((r = rrset.getNext()) != null) {
					if (!listResult.containsKey(r)) {
						listResult.put(r,new HashSet<Integer>());
					}
					listResult.get(r).add(rrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMRelationResultSet orset = slxmm.getRelationsForPeriod(p);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet orset = slxmm.getRelationsForDatamodels(ids[i]);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet orset = slxmm.getRelationsForLogs(ids[i]);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationResultSet orset = slxmm.getRelationsForProcesses(ids[i]);
				SLEXMMRelation slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> relationshipsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForObjects(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForEvents(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForCases(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForActivities(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForClasses(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) { 
			return list;
		} else if (type == SLEXMMObjectVersion.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForObjectVersions(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForRelations(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForActivityInstances(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet rsrset = slxmm.getRelationshipsForAttributes(ids[i]);
				SLEXMMRelationship rs = null;
				while ((rs = rsrset.getNext()) != null) {
					if (!listResult.containsKey(rs)) {
						listResult.put(rs,new HashSet<Integer>());
					}
					listResult.get(rs).add(rsrset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForPeriod(p);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForDatamodels(ids[i]);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForLogs(ids[i]);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMRelationshipResultSet orset = slxmm.getRelationshipsForProcesses(ids[i]);
				SLEXMMRelationship slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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

	public HashMap<Object,HashSet<Integer>> activityInstancesOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForObjects(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForEvents(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForCases(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForActivities(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForClasses(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForRelationships(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForObjectVersions(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForRelations(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			return list;
		} else if (type == SLEXMMAttribute.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstancesForAttributes(ids[i]);
				SLEXMMActivityInstance ai = null;
				while ((ai = airset.getNext()) != null) {
					if (!listResult.containsKey(ai)) {
						listResult.put(ai,new HashSet<Integer>());
					}
					listResult.get(ai).add(airset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForPeriod(p);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForDatamodels(ids[i]);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForLogs(ids[i]);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMActivityInstanceResultSet orset = slxmm.getActivityInstancesForProcesses(ids[i]);
				SLEXMMActivityInstance slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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
	
	public HashMap<Object,HashSet<Integer>> attributesOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForObjects(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForEvents(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForCases(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForActivities(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForClasses(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForRelationships(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForObjectVersions(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForRelations(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet atrset = slxmm.getAttributesForActivityInstances(ids[i]);
				SLEXMMAttribute at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			return list;
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForPeriod(p);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForDatamodels(ids[i]);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForLogs(ids[i]);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMAttributeResultSet orset = slxmm.getAttributesForProcesses(ids[i]);
				SLEXMMAttribute slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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
	
	public HashMap<Object,HashSet<Integer>> datamodelsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForObjects(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForEvents(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForCases(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForActivities(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForClasses(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForRelationships(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForObjectVersions(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForRelations(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet atrset = slxmm.getDatamodelsForActivityInstances(ids[i]);
				SLEXMMDataModel at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForAttributes(ids[i]);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForPeriod(p);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			return list;
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForLogs(ids[i]);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMDataModelResultSet orset = slxmm.getDatamodelsForProcesses(ids[i]);
				SLEXMMDataModel slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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
	
	public HashMap<Object,HashSet<Integer>> processesOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForObjects(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForEvents(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForCases(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForActivities(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForClasses(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForRelationships(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForObjectVersions(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForRelations(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet atrset = slxmm.getProcessesForActivityInstances(ids[i]);
				SLEXMMProcess at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet orset = slxmm.getProcessesForAttributes(ids[i]);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMProcessResultSet orset = slxmm.getProcessesForPeriod(p);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet orset = slxmm.getProcessesForDatamodels(ids[i]);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMProcessResultSet orset = slxmm.getProcessesForLogs(ids[i]);
				SLEXMMProcess slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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
	
	public HashMap<Object,HashSet<Integer>> logsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForObjects(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForEvents(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForCases(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForActivities(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForClasses(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForRelationships(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForObjectVersions(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForRelations(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) { 
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet atrset = slxmm.getLogsForActivityInstances(ids[i]);
				SLEXMMLog at = null;
				while ((at = atrset.getNext()) != null) {
					if (!listResult.containsKey(at)) {
						listResult.put(at,new HashSet<Integer>());
					}
					listResult.get(at).add(atrset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet orset = slxmm.getLogsForAttributes(ids[i]);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			for (Object o: list.keySet()) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;
				SLEXMMLogResultSet orset = slxmm.getLogsForPeriod(p);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet orset = slxmm.getLogsForDatamodels(ids[i]);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			return list;
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMLogResultSet orset = slxmm.getLogsForProcesses(ids[i]);
				SLEXMMLog slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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
	
	public HashMap<Object,HashSet<Integer>> periodsOf(HashMap<Object,HashSet<Integer>> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> listResult = new HashMap<>();
	 	
		if (type == SLEXMMObject.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForObjects(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMEvent.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForEvents(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMCase.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForCases(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivity.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForActivities(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMClass.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForClasses(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelationship.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForRelationships(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMObjectVersion.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForVersions(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMRelation.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForRelations(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMActivityInstance.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForActivityInstances(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMAttribute.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet prset = slxmm.getPeriodsForAttributes(ids[i]);
				SLEXMMPeriod p = null;
				while ((p = prset.getNext()) != null) {
					if (!listResult.containsKey(p)) {
						listResult.put(p,new HashSet<Integer>());
					}
					listResult.get(p).add(prset.getOriginId());
				}
			}
		} else if (type == SLEXMMPeriod.class) {
			return list;
		} else if (type == SLEXMMDataModel.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet orset = slxmm.getPeriodsForDatamodels(ids[i]);
				SLEXMMPeriod slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMLog.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet orset = slxmm.getPeriodsForLogs(ids[i]);
				SLEXMMPeriod slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
					}
					listResult.get(slxo).add(orset.getOriginId());
				}
			}
		} else if (type == SLEXMMProcess.class) {
			int[][] ids = getArrayIds(list.keySet(),type);
			for (int i = 0; i < ids.length; i++) {
				SLEXMMPeriodResultSet orset = slxmm.getPeriodsForProcesses(ids[i]);
				SLEXMMPeriod slxo = null;
				while ((slxo = orset.getNext()) != null) {
					if (!listResult.containsKey(slxo)) {
						listResult.put(slxo,new HashSet<Integer>());
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
		int numArrays = (int) Math.ceil(((float)remaining / (float)MAX_IDS_ARRAY_SIZE));
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

	public HashMap<Object,HashSet<Integer>> versionsRelatedTo(Set<Object> list, Class<?> type) {
		HashMap<Object,HashSet<Integer>> setResult = new HashMap<>();

		if (type == SLEXMMObjectVersion.class) {
			int[] ids = new int[list.size()];
			int i = 0;
			for (Object o : list) {
				SLEXMMObjectVersion ob = (SLEXMMObjectVersion) o;
				ids[i] = ob.getId();
				i++;
			}
			
			SLEXMMObjectVersionResultSet ovrset = slxmm
					.getVersionsRelatedToObjectVersions(ids);
			
			SLEXMMObjectVersion ov = null;
			while ((ov = ovrset.getNext()) != null) {
				Integer originId = ovrset.getOriginId();
				if (!setResult.containsKey(ov)) {
					setResult.put(ov,new HashSet<Integer>());
				}
				setResult.get(ov).add(originId);
			}
		} else {
			// ERROR
			System.err.println("Unknown type");
		}

		return setResult;
	}

	public HashMap<Object,HashSet<Integer>> getAllObjects() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMObjectResultSet orset = slxmm.getObjects();
			SLEXMMObject o = null;
			while ((o = orset.getNext()) != null) {
				list.put(o,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllCases() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMCaseResultSet crset = slxmm.getCases();
			SLEXMMCase c = null;
			while ((c = crset.getNext()) != null) {
				list.put(c,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllEvents() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMEventResultSet erset = slxmm.getEvents();
			SLEXMMEvent e = null;
			while ((e = erset.getNext()) != null) {
				list.put(e,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllVersions() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMObjectVersionResultSet ovrset = slxmm.getObjectVersions();
			SLEXMMObjectVersion ov = null;
			while ((ov = ovrset.getNext()) != null) {
				list.put(ov,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllActivities() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMActivityResultSet acrset = slxmm.getActivities();
			SLEXMMActivity act = null;
			while ((act = acrset.getNext()) != null) {
				list.put(act, null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllClasses() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMClassResultSet crset = slxmm.getClasses();
			SLEXMMClass cl = null;
			while ((cl = crset.getNext()) != null) {
				list.put(cl,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllRelations() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMRelationResultSet rrset = slxmm.getRelations();
			SLEXMMRelation r = null;
			while ((r = rrset.getNext()) != null) {
				list.put(r,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllRelationships() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			for (SLEXMMRelationship rs: slxmm.getRelationships()) {
				list.put(rs,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllActivityInstances() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMActivityInstanceResultSet airset = slxmm.getActivityInstances();
			SLEXMMActivityInstance ai = null;
			while ((ai = airset.getNext()) != null) {
				list.put(ai,null);
			}
		}
		return list;
	}

	public HashMap<Object,HashSet<Integer>> getAllAttributes() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMAttributeResultSet arset = slxmm.getAttributes();
			SLEXMMAttribute at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at,null);
			}
		}
		return list;
	}
	
	public HashMap<Object,HashSet<Integer>> getAllDatamodels() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMDataModelResultSet arset = slxmm.getDataModels();
			SLEXMMDataModel at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at,null);
			}
		}
		return list;
	}
	
	public HashMap<Object,HashSet<Integer>> getAllProcesses() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMProcessResultSet arset = slxmm.getProcesses();
			SLEXMMProcess at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at,null);
			}
		}
		return list;
	}
	
	public HashMap<Object,HashSet<Integer>> getAllLogs() {
		HashMap<Object,HashSet<Integer>> list = new HashMap<>();
		if (!isCheckerModeEnabled()) {
			SLEXMMLogResultSet arset = slxmm.getLogs();
			SLEXMMLog at = null;
			while ((at = arset.getNext()) != null) {
				list.put(at,null);
			}
		}
		return list;
	}
	
	public void computeSuggestions(Token offendingToken, Set<Integer> set) {
		List<String> suggestions = new ArrayList<>();
		for (Integer i : set) {
			if (i >= 0) {
				String name = vocabulary.getLiteralName(i);
				if (name == null) {
					name = vocabulary.getSymbolicName(i);
				} else {
					name = name.substring(1, name.length() - 1);
				}
				if (i == dapoqlParser.STRING) {
					name = "\"\"";
				} else if (i == dapoqlParser.IDATT) {
					name = "at.";
				} else if (i == dapoqlParser.EQUAL) {
					name = "==";
				} else if (i == dapoqlParser.EQUAL_OR_GREATER) {
					name = "=>";
				} else if (i == dapoqlParser.EQUAL_OR_SMALLER) {
					name = "=<";
				} else if (i == dapoqlParser.SMALLER) {
					name = "<";
				} else if (i == dapoqlParser.GREATER) {
					name = ">";
				} else if (i == dapoqlParser.DIFFERENT) {
					name = "<>";
				} else if (i == dapoqlParser.OPEN_PARENTHESIS) {
					name = "(";
				} else if (i == dapoqlParser.CLOSE_PARENTHESIS) {
					name = ")";
				} else if (i == dapoqlParser.END_STATEMENT) {
					name = ";";
				} else if (i == dapoqlParser.ASSIGNMENT_SIGN) {
					name = "=";
				} else if (i == dapoqlParser.VAR_NAME) {
					name = "_";
				} else if (i == dapoqlParser.COMMA) {
					name = ",";
				} else if (i == dapoqlParser.PLUS) {
					name = "+";
				} else if (i == dapoqlParser.MINUS) {
					name = "-";
				}
				suggestions.add(name);
			}
		}

		System.out.println(suggestions);
		this.suggestions = suggestions;
		this.offendingToken = offendingToken;
	}

	public List<String> getSuggestions() {
		return this.suggestions;
	}

	public Token getOffendingToken() {
		return this.offendingToken;
	}

	public void setVocabulary(Vocabulary vocabulary) {
		this.vocabulary = vocabulary;
	}

	public HashMap<Object, HashSet<Integer>> concurrentWith(HashMap<Object,HashSet<Integer>> vals,
			Class<?> type) {
		HashMap<Object,HashSet<Integer>> result = new HashMap<>();
		
		HashMap<Object,HashSet<Integer>> periodsMap = periodsOf(vals,type);
		
		for (Object p: periodsMap.keySet()) {
			
			HashMap<Object,HashSet<Integer>> inputMap = new HashMap<>();
			inputMap.put(p, periodsMap.get(p));
			HashMap<Object,HashSet<Integer>> concurrentSet = null;
			
			if (type == SLEXMMActivity.class) {
				concurrentSet = activitiesOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMActivityInstance.class) {
				concurrentSet = activityInstancesOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMAttribute.class) {
				concurrentSet = attributesOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMCase.class) {
				concurrentSet = casesOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMClass.class) {
				concurrentSet = classesOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMEvent.class) {
				concurrentSet = eventsOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMObject.class) {
				concurrentSet = objectsOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMObjectVersion.class) {
				concurrentSet = versionsOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMRelation.class) {
				concurrentSet = relationsOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMRelationship.class) {
				concurrentSet = relationshipsOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMDataModel.class) {
				concurrentSet = datamodelsOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMProcess.class) {
				concurrentSet = processesOf(inputMap, SLEXMMPeriod.class);
			} else if (type == SLEXMMLog.class) {
				concurrentSet = logsOf(inputMap, SLEXMMPeriod.class);
			} else {
				System.err.println("Unknown type");
				break;
			}
			
			for (Object o: concurrentSet.keySet()) {
				if (!result.containsKey(o)) {
					result.put(o, new HashSet<Integer>());
				}
				result.get(o).addAll(periodsMap.get(p));
			}
			
		}
		return result;
	}

	public HashMap<Object, HashSet<Integer>> getScopeOf(int scope,
			HashMap<Object,HashSet<Integer>> val, Class<?> type) {
		HashMap<Object,HashSet<Integer>> result = null;
		
		switch (scope) {
		case ID_TYPE_ACTIVITY:
			result = activitiesOf(val, type);
			break;
		case ID_TYPE_ACTIVITY_INSTANCE:
			result = activityInstancesOf(val, type);
			break;
		case ID_TYPE_ATTRIBUTE:
			result = attributesOf(val, type);
			break;
		case ID_TYPE_CASE:
			result = casesOf(val, type);
			break;
		case ID_TYPE_CLASS:
			result = classesOf(val, type);
			break;
		case ID_TYPE_EVENT:
			result = eventsOf(val, type);
			break;
		case ID_TYPE_OBJECT:
			result = objectsOf(val, type);
			break;
		case ID_TYPE_RELATION:
			result = relationsOf(val, type);
			break;
		case ID_TYPE_RELATIONSHIP:
			result = relationshipsOf(val, type);
			break;
		case ID_TYPE_VERSION:
			result = versionsOf(val, type);
			break;
		case ID_TYPE_DATAMODEL:
			result = datamodelsOf(val, type);
			break;
		case ID_TYPE_PROCESS:
			result = processesOf(val, type);
			break;
		case ID_TYPE_LOG:
			result = logsOf(val, type);
			break;
		default:
			break;
		}
		
		return result;
	}

}
