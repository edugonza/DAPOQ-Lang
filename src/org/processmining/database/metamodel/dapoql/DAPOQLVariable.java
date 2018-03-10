package org.processmining.database.metamodel.dapoql;

public class DAPOQLVariable {
	private String name = null;
	private Class<?> type = null;
	//private ResultMap value = null;
	private DAPOQLSet value = null;
	
	public DAPOQLVariable(String name, Class<?> type, DAPOQLSet value) {
		setName(name);
		setType(type);
		setValue(value);
	}
	
	public DAPOQLSet getValue() {
		return value;
	}
	
	public void setValue(DAPOQLSet value) {
		this.value = value;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
