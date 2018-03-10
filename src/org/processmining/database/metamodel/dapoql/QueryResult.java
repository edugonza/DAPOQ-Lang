package org.processmining.database.metamodel.dapoql;

import java.util.Set;

import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class QueryResult {

	private SLEXMMStorageMetaModel storage = null;
	private DAPOQLSet result = null;
	private Class<?> type = null;
	
	public QueryResult(Class<?> type, SLEXMMStorageMetaModel storage) {
		this.storage = storage;
		this.type = type;
		this.result = new DAPOQLSet(storage, type);
	}
	
	protected SLEXMMStorageMetaModel getStorage() {
		return this.storage;
	}
	
	public Class<?> getType() {
		return this.type;
	}
	
	public void setResult(DAPOQLSet result) {
		this.result = result;
	}
	
	public void setResult(Set<Integer> result) {
		this.result = new DAPOQLSet(this.storage, this.type);
		this.result.set(result);
	}
	
	public DAPOQLSet getResult() {
		return this.result;
	}
	
}