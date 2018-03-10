package org.processmining.database.metamodel.dapoql

import groovy.lang.Closure;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel

class QueryGroovyResult extends QueryResult {

	private DAPOQLFunctionsGroovy dapoqlfunc = null;
	
	QueryGroovyResult(Class<?> type, SLEXMMStorageMetaModel storage) {
		this(type,storage,null);
	}
	
	QueryGroovyResult(Class<?> type, SLEXMMStorageMetaModel storage, DAPOQLFunctionsGroovy dapoqlfunc) {
		super(type,storage);
		this.dapoqlfunc = dapoqlfunc;
	}
	
	def where(Closure body) {
		QueryGroovyResult qr = new QueryGroovyResult(getType(),getStorage(),dapoqlfunc);

		body.resolveStrategy = Closure.DELEGATE_ONLY;
		DAPOQLDelegate dapoqldelegate = new DAPOQLDelegate();
		body.delegate = dapoqldelegate;
		dapoqldelegate.type = qr.getType();
		dapoqldelegate.dapoqlfunc = dapoqlfunc;

		result.each {
			dapoqldelegate.o = it;
			if (body(it) == true) {
				qr.getResult().add(it);
			}
		}
		return qr;
	}

	def union(QueryGroovyResult qrB) {
		QueryGroovyResult qr = new QueryGroovyResult(getType(),getStorage());
		
		for (Integer i: this.getResult().getIdsSet()) {
			qr.getResult().getIdsSet().add(i);
		}

		for (Integer i: qrB.getResult().getIdsSet()) {
			qr.getResult().getIdsSet().add(i);
		}

		return qr;
	}

	def excluding(QueryGroovyResult qrB) {
		QueryGroovyResult qr = new QueryGroovyResult(getType(),getStorage());

		for (Integer i: this.getResult().getIdsSet()) {
			qr.getResult().getIdsSet().add(i);
		}

		for (Integer i: qrB.getResult().getIdsSet()) {
			qr.getResult().getIdsSet().remove(i);
		}
		
		return qr;
	}

	def intersection(QueryGroovyResult qrB) {
		QueryGroovyResult qrAux = new QueryGroovyResult(getType(),getStorage());

		for (Integer i: this.getResult().getIdsSet()) {
			qrAux.getResult().getIdsSet().add(i);
		}

		for (Integer i: qrB.getResult().getIdsSet()) {
			qrAux.getResult().getIdsSet().remove(i);
		}

		QueryGroovyResult qr = new QueryGroovyResult(getType(),getStorage());
		
		for (Integer i: this.getResult().getIdsSet()) {
			qr.getResult().getIdsSet().add(i);
		}
		
		for (Integer i: qrAux.getResult().getIdsSet()) {
			qr.getResult().getIdsSet().remove(i);
		}
		
		return qr;
	}
}