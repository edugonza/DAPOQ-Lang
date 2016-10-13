package org.processmining.database.metamodel.dapoql

import groovy.lang.Closure;

class QueryGroovyResult extends QueryResult {
		
		DAPOQLFunctionsGroovy dapoqlfunc = null;
	
		def where(Closure body) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.result = new HashSet<>();
			qr.mapResult = new HashMap<>();
			qr.type = this.type;
			
			body.resolveStrategy = Closure.DELEGATE_ONLY;
			DAPOQLDelegate dapoqldelegate = new DAPOQLDelegate();
			body.delegate = dapoqldelegate;
			dapoqldelegate.type = qr.type;
			dapoqldelegate.dapoqlfunc = dapoqlfunc;
			
			result.each {
				dapoqldelegate.o = it;
				if (body(it) == true) {
					qr.result.add(it);
					qr.mapResult.put(it,this.mapResult.get(it));
				}
			}
			return qr;
		}
		
		def union(QueryGroovyResult qrB) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.mapResult = new HashMap<>();
			
			for (Object o: this.result) {
				qr.mapResult.put(o,this.mapResult.get(o));
			}
			
			for (Object o: qrB.result) {
				qr.mapResult.put(o,qrB.mapResult.get(o));
			}
			
			qr.type = this.type;
			qr.result = qr.mapResult.keySet();
			return qr;
		}
		
		def excluding(QueryGroovyResult qrB) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.mapResult = new HashMap<>();
			
			for (Object o : this.result) {
				qr.mapResult.put(o,this.mapResult.get(o));
			}
			
			for (Object o : qrB.result) {
				qr.mapResult.remove(o);
			}
			
			qr.type = this.type;
			qr.result = qr.mapResult.keySet();
			return qr;
		}
		
		def intersection(QueryGroovyResult qrB) {
			QueryGroovyResult qrAux = new QueryGroovyResult();
			
			qrAux.result = new HashSet<>();
			qrAux.result.addAll(this.result);
			qrAux.type = this.type;
			qrAux.result.removeAll(qrB.result);
						
			QueryGroovyResult qr = new QueryGroovyResult();
			
			qr.mapResult = new HashMap<>();
			
			for (Object o : this.result) {
				qr.mapResult.put(o,this.mapResult.get(o));
			}
			
			for (Object o : qrAux.result) {
				qr.mapResult.remove(o);
			}
			
			qr.type = this.type;
			qr.result = qr.mapResult.keySet();
			
			return qr;
		}
	}