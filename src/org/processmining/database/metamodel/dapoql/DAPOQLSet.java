package org.processmining.database.metamodel.dapoql;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.processmining.openslex.metamodel.AbstractDBElement;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class DAPOQLSet implements Iterable<AbstractDBElement> {

	private Set<Integer> set = null;
	private SLEXMMStorageMetaModel storage = null;
	private Class<?> c = null;
	private boolean attsFetched = false;
	
	public DAPOQLSet(SLEXMMStorageMetaModel storage, Class<?> c) {
		this.storage = storage;
		this.c = c;
		this.set = new HashSet<>();
		this.attsFetched = false;
	}
	
	public boolean attributesFetched() {
		return this.attsFetched;
	}
	
	public void setAttributesFetched(boolean fetched) {
		this.attsFetched = fetched;
	}
	
	@Override
	public Iterator<AbstractDBElement> iterator() {
		return new DAPOQLSetIterator(this.storage, this.set, c);
	}
	
	public Class<?> getType() {
		return c;
	}
	
	public Set<Integer> getIdsSet() {
		return this.set;
	}
	
	public void add(AbstractDBElement o) {
		set.add(o.getId());
	}
	
	public void set(Set<Integer> set) {
		this.set = set;
	}
	
	public Collection<Object> getObjSet() {
		HashSet<Object> objset = new HashSet<>();
		for (Object o : this) {
			objset.add(o);
		}
		return objset;
	}
	
	public DAPOQLSet intersection(DAPOQLSet bset) {
		DAPOQLSet inter = new DAPOQLSet(this.storage, this.getType());
		
		if (this.getType() == bset.getType()) {
			if (this.getIdsSet() != null && bset.getIdsSet() != null) {
				if (!this.getIdsSet().isEmpty() && !bset.getIdsSet().isEmpty()) {
					HashSet<Integer> auxset = new HashSet<>();
					auxset.addAll(this.getIdsSet());
					auxset.removeAll(bset.getIdsSet());
					
					HashSet<Integer> auxset2 = new HashSet<>();
					auxset2.addAll(this.getIdsSet());
					auxset2.removeAll(auxset);
					
					inter.set(auxset2);
				}
			}
		}
		
		return inter;
	}
	
	public DAPOQLSet union(DAPOQLSet bset) {
		DAPOQLSet un = new DAPOQLSet(this.storage, this.getType());
		
		if (this.getType() == bset.getType()) {
			if (this.getIdsSet() != null) {
				un.getIdsSet().addAll(this.getIdsSet());
			}
			if (bset.getIdsSet() != null) {
				un.getIdsSet().addAll(bset.getIdsSet());
			}
		}
		
		return un;
	}
	
	public DAPOQLSet excluding(DAPOQLSet bset) {
		DAPOQLSet ex = new DAPOQLSet(this.storage, this.getType());
		
		if (this.getType() == bset.getType()) {
			if (this.getIdsSet() != null) {
				ex.getIdsSet().addAll(this.getIdsSet());
			}
			if (bset.getIdsSet() != null) {
				ex.getIdsSet().removeAll(bset.getIdsSet());
			}
		}
		
		return ex;
	}
}
