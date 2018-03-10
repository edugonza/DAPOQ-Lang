package org.processmining.database.metamodel.dapoql;

import java.util.Set;
import java.util.Iterator;

import org.processmining.openslex.metamodel.AbstractDBElement;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class DAPOQLSetIterator implements Iterator<AbstractDBElement> {

	private Set<Integer> set = null;
	private SLEXMMStorageMetaModel storage = null;
	private Class<?> c = null;
	private Iterator<Integer> it = null;
	
	public DAPOQLSetIterator(SLEXMMStorageMetaModel storage, Set<Integer> set, Class<?> c) {
		this.storage = storage;
		this.c = c;
		this.set = set;
		this.it = this.set.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public AbstractDBElement next() {
		return storage.getFromCache(c,it.next());
	}

}
