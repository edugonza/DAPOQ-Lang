package org.processmining.database.metamodel.dapoql

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale

import org.codehaus.groovy.runtime.callsite.MetaClassConstructorSite;
import org.junit.internal.runners.statements.InvokeMethod;
import org.processmining.openslex.metamodel.SLEXMMActivity
import org.processmining.openslex.metamodel.SLEXMMActivityInstance
import org.processmining.openslex.metamodel.SLEXMMAttribute
import org.processmining.openslex.metamodel.SLEXMMAttributeValue
import org.processmining.openslex.metamodel.SLEXMMCase
import org.processmining.openslex.metamodel.SLEXMMCaseAttribute
import org.processmining.openslex.metamodel.SLEXMMCaseAttributeValue
import org.processmining.openslex.metamodel.SLEXMMCaseResultSet
import org.processmining.openslex.metamodel.SLEXMMClass
import org.processmining.openslex.metamodel.SLEXMMDataModel
import org.processmining.openslex.metamodel.SLEXMMEvent
import org.processmining.openslex.metamodel.SLEXMMEventAttribute
import org.processmining.openslex.metamodel.SLEXMMEventAttributeValue
import org.processmining.openslex.metamodel.SLEXMMEventResultSet
import org.processmining.openslex.metamodel.SLEXMMLog
import org.processmining.openslex.metamodel.SLEXMMLogAttribute
import org.processmining.openslex.metamodel.SLEXMMLogAttributeValue
import org.processmining.openslex.metamodel.SLEXMMLogResultSet
import org.processmining.openslex.metamodel.SLEXMMObject
import org.processmining.openslex.metamodel.SLEXMMObjectVersion
import org.processmining.openslex.metamodel.SLEXMMObjectVersionResultSet
import org.processmining.openslex.metamodel.SLEXMMPeriod;
import org.processmining.openslex.metamodel.SLEXMMProcess
import org.processmining.openslex.metamodel.SLEXMMRelation
import org.processmining.openslex.metamodel.SLEXMMRelationship
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel
import org.processmining.openslex.metamodel.SLEXMMUtils;

import groovy.lang.MetaClass;;

class DAPOQLDSL extends Script {

	private DAPOQLFunctionsGroovy dapoqlfunc = null;
	private SLEXMMStorageMetaModel slxmm = null;
	
	private static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	protected void init(SLEXMMStorageMetaModel slxmm) {
		this.slxmm = slxmm;
		this.dapoqlfunc = new DAPOQLFunctionsGroovy();
		this.dapoqlfunc.setMetaModel(slxmm);
	}
	
	@Override
	public Object run() {
		super.run();
	}
	
	class DAPOQLAttributeHolder {

		Object o = null;
		Class type = null;
				
		def getProperty(String propertyName) {
			
			if (type == SLEXMMEvent.class) {
				SLEXMMEvent e = (SLEXMMEvent) o;
				HashMap<SLEXMMEventAttribute,SLEXMMEventAttributeValue> atValsMap = e.getAttributeValues();

				for (SLEXMMEventAttribute eat: atValsMap.keySet()) {
					if (eat.getName() == propertyName) {
						return atValsMap.get(eat).getValue();
					}
				}
			} else if (type == SLEXMMObjectVersion.class) {
				SLEXMMObjectVersion ov = (SLEXMMObjectVersion) o;
				HashMap<SLEXMMAttribute,SLEXMMAttributeValue> atValsMap = ov.getAttributeValues();

				for (SLEXMMAttribute ovat: atValsMap.keySet()) {
					if (ovat.getName() == propertyName) {
						return atValsMap.get(ovat).getValue();
					}
				}
			} else if (type == SLEXMMCase.class) {
				SLEXMMCase c = (SLEXMMCase) o;
				HashMap<SLEXMMCaseAttribute,SLEXMMCaseAttributeValue> atValsMap = c.getAttributeValues();

				for (SLEXMMCaseAttribute cat: atValsMap.keySet()) {
					if (cat.getName() == propertyName) {
						return atValsMap.get(cat).getValue();
					}
				}
			} else if (type == SLEXMMLog.class) {
				SLEXMMLog l = (SLEXMMLog) o;
				HashMap<SLEXMMLogAttribute,SLEXMMLogAttributeValue> atValsMap = l.getAttributeValues();

				for (SLEXMMLogAttribute lat: atValsMap.keySet()) {
					if (lat.getName() == propertyName) {
						return atValsMap.get(lat).getValue();
					}
				}
			}
			
			return null;
		}
	}
	
	class DAPOQLDelegate {
		
		Object o = null;
		Class type = null;
				
		def has(p) {
			return p ? true: false;
		}
		
		def getProperty(String propertyName) {
			if (propertyName == "at") {
				DAPOQLAttributeHolder dapoqlAtHolder = new DAPOQLAttributeHolder();
				dapoqlAtHolder.o = o;
				dapoqlAtHolder.type = type;
				return dapoqlAtHolder;
			} else {
				return o.getProperties().get(propertyName);
			}
		}
		
		def changed(Object p, String a, String b) {
//			if (p instanceof SLEXMMObjectVersion) {
//				
//			} else {
//				return false;
//			}
			// TODO
		}
	}
	
	class QueryGroovyResult extends QueryResult {
		
		def where(Closure body) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.result = new HashSet<>();
			qr.mapResult = new HashMap<>();
			qr.type = this.type;
			
			body.resolveStrategy = Closure.DELEGATE_ONLY;
			DAPOQLDelegate dapoqldelegate = new DAPOQLDelegate();
			body.delegate = dapoqldelegate;
			dapoqldelegate.type = qr.type;
			
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
	
	private QueryGroovyResult buildResult(HashMap<Object, HashSet<Integer>> map, Class type) {
		QueryGroovyResult qr = new QueryGroovyResult();
		
		qr.mapResult = map;
		qr.result = map.keySet();
		qr.type = type;
		
		if (type == SLEXMMEvent) {
			qr.mapResult = new HashMap<>();
			SLEXMMEventResultSet erset = slxmm.getEventsAndAttributeValues(qr.result);
			SLEXMMEvent e = null;
			while ((e = erset.getNextWithAttributes()) != null) {
				qr.mapResult.put(e,map.get(e));
			}
			qr.result = qr.mapResult.keySet();
			
		} else if (type == SLEXMMObjectVersion) {
			qr.mapResult = new HashMap<>();
			SLEXMMObjectVersionResultSet erset = slxmm.getVersionsAndAttributeValues(qr.result);
			SLEXMMObjectVersion e = null;
			while ((e = erset.getNextWithAttributes()) != null) {
				qr.mapResult.put(e,map.get(e));
			}
			qr.result = qr.mapResult.keySet();
		} else if (type == SLEXMMCase) {
			qr.mapResult = new HashMap<>();
			SLEXMMCaseResultSet crset = slxmm.getCasesAndAttributeValues(qr.result);
			SLEXMMCase c = null;
			while ((c = crset.getNextWithAttributes()) != null) {
				qr.mapResult.put(c,map.get(c));
			}
			qr.result = qr.mapResult.keySet();
		} else if (type == SLEXMMLog) {
			qr.mapResult = new HashMap<>();
			SLEXMMLogResultSet lrset = slxmm.getLogsAndAttributeValues(qr.result);
			SLEXMMLog log = null;
			while ((log = lrset.getNextWithAttributes()) != null) {
				qr.mapResult.put(log,map.get(log));
			}
			qr.result = qr.mapResult.keySet();
		}
		return qr;
	}
	
	def QueryGroovyResult allDatamodels() {
		return buildResult(dapoqlfunc.getAllDatamodels(),SLEXMMDataModel.class);
	}
	
	def QueryResult allClasses() {
		return buildResult(dapoqlfunc.getAllClasses(),SLEXMMClass.class);
	}
	
	def QueryResult allAttributes() {
		return buildResult(dapoqlfunc.getAllAttributes(),SLEXMMAttribute.class);
	}
	
	def QueryResult allRelationships() {
		return buildResult(dapoqlfunc.getAllRelationships(),SLEXMMRelationship.class);
	}
	
	def QueryResult allObjects() {
		return buildResult(dapoqlfunc.getAllObjects(),SLEXMMObject.class);
	}
	
	def QueryResult allVersions() {
		return buildResult(dapoqlfunc.getAllVersions(),SLEXMMObjectVersion.class);
	}
	
	def QueryResult allRelations() {
		return buildResult(dapoqlfunc.getAllRelations(),SLEXMMRelation.class);
	}
	
	def QueryResult allEvents() {
		return buildResult(dapoqlfunc.getAllEvents(),SLEXMMEvent.class);
	}
	
	def QueryResult allActivityInstances() {
		return buildResult(dapoqlfunc.getAllActivityInstances(),SLEXMMActivityInstance.class);
	}
	
	def QueryResult allCases() {
		return buildResult(dapoqlfunc.getAllCases(),SLEXMMCase.class);
	}
	
	def QueryResult allLogs() {
		return buildResult(dapoqlfunc.getAllLogs(),SLEXMMLog.class);
	}
	
	def QueryResult allActivities() {
		return buildResult(dapoqlfunc.getAllActivities(),SLEXMMActivity.class);
	}
	
	def QueryResult allProcesses() {
		return buildResult(dapoqlfunc.getAllProcesses(),SLEXMMProcess.class);
	}
	
	def QueryResult versionsRelatedTo(QueryResult qr) throws Exception {
		if (qr.type != SLEXMMObjectVersion.class) {
			throw new Exception("Argument of versionsRelatedTo must be a set of versions");
		}
		return buildResult(dapoqlfunc.versionsRelatedTo(qr.result,qr.type),SLEXMMObjectVersion.class);
	}
	
	def QueryResult datamodelsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.datamodelsOf(qr.mapResult, qr.type),SLEXMMDataModel.class);
	}
	
	def QueryResult classesOf(QueryResult qr) {
		return buildResult(dapoqlfunc.classesOf(qr.mapResult, qr.type),SLEXMMClass.class);
	}
	
	def QueryResult attributesOf(QueryResult qr) {
		return buildResult(dapoqlfunc.attributesOf(qr.mapResult, qr.type),SLEXMMAttribute.class);
	}
	
	def QueryResult relationshipsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.relationshipsOf(qr.mapResult, qr.type),SLEXMMRelationship.class);
	}
	
	def QueryResult objectsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.objectsOf(qr.mapResult, qr.type),SLEXMMObject.class);
	}
	
	def QueryResult versionsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.versionsOf(qr.mapResult, qr.type),SLEXMMObjectVersion.class);
	}
	
	def QueryResult relationsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.relationsOf(qr.mapResult, qr.type),SLEXMMRelation.class);
	}
	
	def QueryResult eventsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.eventsOf(qr.mapResult, qr.type),SLEXMMEvent.class);
	}
	
	def QueryResult activityInstancesOf(QueryResult qr) {
		return buildResult(dapoqlfunc.activityInstancesOf(qr.mapResult, qr.type),SLEXMMActivityInstance.class);
	}
	
	def QueryResult activitiesOf(QueryResult qr) {
		return buildResult(dapoqlfunc.activitiesOf(qr.mapResult, qr.type),SLEXMMActivity.class);
	}
	
	def QueryResult casesOf(QueryResult qr) {
		return buildResult(dapoqlfunc.casesOf(qr.mapResult, qr.type),SLEXMMCase.class);
	}
	
	def QueryResult logsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.logsOf(qr.mapResult, qr.type),SLEXMMLog.class);
	}
	
	def QueryResult processesOf(QueryResult qr) {
		return buildResult(dapoqlfunc.processesOf(qr.mapResult, qr.type),SLEXMMProcess.class);
	}

	def QueryResult periodsOf(QueryResult qr) {
		return buildResult(dapoqlfunc.periodsOf(qr.mapResult,qr.type),SLEXMMPeriod.class);
	}
	
	def SLEXMMPeriod globalPeriod(QueryResult qr) {
				
		if (qr.type == SLEXMMPeriod) {
			long startTimestamp = -1L;
			long endTimestamp = -2L;
			
			for (SLEXMMPeriod po: qr.result) {
				startTimestamp = SLEXMMUtils.earliest(startTimestamp,po.getStart());
				endTimestamp = SLEXMMUtils.latest(endTimestamp,po.getEnd());
			}
			
			SLEXMMPeriod p = new SLEXMMPeriod(startTimestamp, endTimestamp);
			return p;
		} else {
			return globalPeriod(periodsOf(qr));
		}
	}
	
	def methodMissing(String name, args) {
		
		if (getMetaClass().respondsTo(this, name, QueryResult)) {

			if (!(args instanceof QueryResult)) {

				boolean equalType = true;
				Class type = null;

				for (Object o: args) {
					if (type == null) {
						type = o.getClass();
					}
					if (!(equalType && o.getClass() == type)) {
						equalType = false;
					}
				}

				if (equalType) {
					QueryResult qr = new QueryResult();
					qr.type = type;
					qr.mapResult = new HashMap<>();

					for (Object o: args) {
						qr.mapResult.put(o,new ArrayList<>());
					}

					qr.result = qr.mapResult.keySet();
					return invokeMethod(name,qr);
				}
			}
		}
		
        throw new MissingMethodException(name, this.class, args);
    }
	
	def SLEXMMPeriod createPeriod(String start, String end) {
		return createPeriod(start,end,DEFAULT_TIMESTAMP_FORMAT);
	}
	
	def SLEXMMPeriod createPeriod(String start, String end, String format) {
		long startTimestamp = 0L;
		long endTimestamp = 0L;
		
		DateFormat dformat = new SimpleDateFormat(format, Locale.ENGLISH);
		
		Date dateStart = null;
		Date dateEnd = null;
		try {
			dateStart = dformat.parse(start);
			dateEnd = dformat.parse(end);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Date format not valid");
		}
		
		startTimestamp = dateStart.getTime();
		endTimestamp = dateEnd.getTime();
		
		SLEXMMPeriod p = new SLEXMMPeriod(startTimestamp, endTimestamp);
		return p;
	}
	
	def boolean before(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (SLEXMMUtils.before(a.getEnd(),b.getStart()));
	}
	
	def boolean after(SLEXMMPeriod a, SLEXMMPeriod b) {
		return before(b,a);
	}
	
	def boolean meets(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (a.getEnd() == b.getStart());
	}
	
	def boolean meetsInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return meets(b,a);
	}

	def boolean overlaps(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (SLEXMMUtils.before(a.getStart(),b.getStart()) &&
			    SLEXMMUtils.after(a.getEnd(),b.getStart()) &&
				SLEXMMUtils.before(a.getEnd(),b.getEnd()));
	}
	
	def boolean overlapsInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return overlaps(b,a);
	}

	def boolean starts(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (a.getStart() == b.getStart() &&
			    SLEXMMUtils.before(a.getEnd(),b.getEnd()));
	}
	
	def boolean startsInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return starts(b,a);
	}
	
	def boolean during(SLEXMMPeriod a, SLEXMMPeriod b) {
		return ((SLEXMMUtils.after(a.getStart(),b.getStart()) &&
			     SLEXMMUtils.beforeOrEqual(a.getEnd(),b.getEnd())) ||
				(SLEXMMUtils.afterOrEqual(a.getStart(),b.getStart()) &&
			     SLEXMMUtils.before(a.getEnd(),b.getEnd())));		 
	}
	
	def boolean duringInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return during(b,a);
	}
	
	def boolean finishes(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (SLEXMMUtils.after(a.getStart(),b.getStart()) &&
			    a.getEnd() == b.getEnd());
	}
	
	def boolean finishesInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return finishes(b,a);
	}
	
	def boolean matches(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (a.getStart() == b.getStart() && a.getEnd() == b.getEnd());
	}
	
}
