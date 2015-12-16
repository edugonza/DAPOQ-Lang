package org.processmining.database.metamodel.poql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class POQLVariable {
	private String name = null;
	private Class type = null;
	private HashMap<Object,HashSet<Integer>> value = null;
	
	public POQLVariable(String name, Class type, HashMap<Object,HashSet<Integer>> value) {
		setName(name);
		setType(type);
		setValue(value);
	}
	
	public HashMap<Object,HashSet<Integer>> getValue() {
		return value;
	}
	
	public void setValue(HashMap<Object,HashSet<Integer>> value) {
		this.value = value;
	}
	
	public Class getType() {
		return type;
	}
	
	public void setType(Class type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
