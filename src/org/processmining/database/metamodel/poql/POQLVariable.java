package org.processmining.database.metamodel.poql;

import java.util.Set;

public class POQLVariable {
	private String name = null;
	private Class type = null;
	private Set<Object> value = null;
	private int level = 0;
	
	public POQLVariable(int level, String name, Class type, Set<Object> value) {
		setName(name);
		setType(type);
		setValue(value);
		setLevel(level);
	}
	
	public Set<Object> getValue() {
		return value;
	}
	
	public void setValue(Set<Object> value) {
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
