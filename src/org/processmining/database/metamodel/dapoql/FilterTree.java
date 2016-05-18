package org.processmining.database.metamodel.dapoql;

public class FilterTree {
	
	public static final int OPERATOR_EQUAL = 1;
	public static final int OPERATOR_DIFFERENT = 2;
	public static final int OPERATOR_GREATER_THAN = 3;
	public static final int OPERATOR_SMALLER_THAN = 4;
	public static final int OPERATOR_EQUAL_OR_GREATER_THAN = 5;
	public static final int OPERATOR_EQUAL_OR_SMALLER_THAN = 6;
	public static final int OPERATOR_CONTAINS = 7;
	public static final int OPERATOR_CHANGED = 8;
	
	public static final int NODE_AND = 1;
	public static final int NODE_OR = 2;
	public static final int NODE_NOT = 3;
	public static final int NODE_TERMINAL = 4;
		
	int node = 0;
	FilterTree leftChild = null;
	FilterTree rightChild = null;
	String key = null;
	int keyId = -1;
	String value = null;
	String valueFrom = null;
	String valueTo = null;
	int operator = 0;
	boolean att = false;
	
	public FilterTree getLeft() {
		return leftChild;
	}
	
	public FilterTree getRight() {
		return rightChild;
	}
	
	public String getKey() {
		return key;
	}
	
	public int getKeyId() {
		return keyId;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getValueFrom() {
		return valueFrom;
	}
	
	public String getValueTo() {
		return valueTo;
	}
	
	public int getOperator() {
		return operator;
	}
	
	public int getNode() {
		return node;
	}
	
	public boolean isAttribute() {
		return att;
	}
	
	public boolean isTerminal() {
		return node == NODE_TERMINAL;
	}
	
	public boolean isNot() {
		return node == NODE_NOT;
	}
	
	public boolean isAnd() {
		return node == NODE_AND;
	}
	
	public boolean isOr() {
		return node == NODE_OR;
	}
	
	public boolean isEqual() {
		return isTerminal() && operator == OPERATOR_EQUAL;
	}
	
	public boolean isDifferent() {
		return isTerminal() && operator == OPERATOR_DIFFERENT;
	}
	
	public boolean isGreater() {
		return isTerminal() && operator == OPERATOR_GREATER_THAN;
	}
	
	public boolean isSmaller() {
		return isTerminal() && operator == OPERATOR_SMALLER_THAN;
	}
	
	public boolean isEqualOrGreater() {
		return isTerminal() && operator == OPERATOR_EQUAL_OR_GREATER_THAN;
	}
	
	public boolean isEqualOrSmaller() {
		return isTerminal() && operator == OPERATOR_EQUAL_OR_SMALLER_THAN;
	}
	
	public boolean isChanged() {
		return isTerminal() && operator == OPERATOR_CHANGED;
	}
	
}
