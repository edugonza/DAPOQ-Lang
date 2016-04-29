package org.processmining.database.metamodel.poql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class POQLValue {
	
	public HashMap<Object,HashSet<Integer>> result;
	public Class<?> type;
	public FilterTree filterTree;
	public int nodeType = 0;
	public int keyId = 0;
	public String keyStr = null;
	public boolean isAttribute = false;
	public int operator = 0;
	public String operatorValue = null;
	public String changedFrom = null;
	public String changedTo = null;
	public int setOperator = 0;
	public boolean conditionBoolean = false;
	public int scope = 0;
	public POQLTimestamp timestamp;
}
