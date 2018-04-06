package org.processmining.database.metamodel.dapoql

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale
import java.util.function.Function
import org.codehaus.groovy.runtime.callsite.MetaClassConstructorSite;
import org.deckfour.xes.model.XLog
import org.processmining.openslex.metamodel.AbstractAttDBElement
import org.processmining.openslex.metamodel.AbstractDBElement
import org.processmining.openslex.metamodel.AbstractDBElementWithAtts
import org.processmining.openslex.metamodel.AbstractDBElementWithValue
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
import org.processmining.openslex.utils.MMUtils

import groovy.lang.MetaClass;
import groovy.transform.TypeChecked;
import groovy.transform.CompileStatic;

@TypeChecked
@CompileStatic
class DAPOQLDSL extends Script {

	protected DAPOQLFunctionsGroovy dapoqlfunc = null;
	private SLEXMMStorageMetaModel storage = null;
	
	private static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final Boolean TRUE = Boolean.TRUE
	public static final Boolean FALSE = Boolean.FALSE
	
	protected void init(SLEXMMStorageMetaModel storage, DAPOQLFunctionsGroovy func) {
		this.storage = storage;
		this.dapoqlfunc = func;
	}
	
	@Override
	public Object run() {
		super.run();
	}
	
	private SLEXMMStorageMetaModel getStorage() {
		return this.storage;
	}
	
	class DAPOQLAttributeHolder {

		Object o = null;
		Class type = null;
				
		def getProperty(String propertyName) {
			
			def p = getPropertyRaw(propertyName);
			
			return p ? p : new String();
		}
		
		def getPropertyRaw(String propertyName) {
			
			if (AbstractDBElementWithAtts.class.isAssignableFrom(type)) {
				AbstractDBElementWithAtts ae = (AbstractDBElementWithAtts) o;
				AbstractDBElementWithValue atv = ae.getAttributeValue(propertyName);
				
				if (atv != null) {
					return atv.getValue()
				}
			}
			
			return null;
			
//			if (type == SLEXMMEvent.class) {
//				SLEXMMEvent e = (SLEXMMEvent) o;
//				HashMap<SLEXMMEventAttribute,SLEXMMEventAttributeValue> atValsMap = e.getAttributeValues();
//
//				for (SLEXMMEventAttribute eat: atValsMap.keySet()) {
//					if (eat.getName() == propertyName) {
//						return atValsMap.get(eat).getValue();
//					}
//				}
//			} else if (type == SLEXMMObjectVersion.class) {
//				SLEXMMObjectVersion ov = (SLEXMMObjectVersion) o;
//				HashMap<SLEXMMAttribute,SLEXMMAttributeValue> atValsMap = ov.getAttributeValues();
//
//				for (SLEXMMAttribute ovat: atValsMap.keySet()) {
//					if (ovat.getName() == propertyName) {
//						return atValsMap.get(ovat).getValue();
//					}
//				}
//			} else if (type == SLEXMMCase.class) {
//				SLEXMMCase c = (SLEXMMCase) o;
//				HashMap<SLEXMMCaseAttribute,SLEXMMCaseAttributeValue> atValsMap = c.getAttributeValues();
//
//				for (SLEXMMCaseAttribute cat: atValsMap.keySet()) {
//					if (cat.getName() == propertyName) {
//						return atValsMap.get(cat).getValue();
//					}
//				}
//			} else if (type == SLEXMMLog.class) {
//				SLEXMMLog l = (SLEXMMLog) o;
//				HashMap<SLEXMMLogAttribute,SLEXMMLogAttributeValue> atValsMap = l.getAttributeValues();
//
//				for (SLEXMMLogAttribute lat: atValsMap.keySet()) {
//					if (lat.getName() == propertyName) {
//						return atValsMap.get(lat).getValue();
//					}
//				}
//			}
		}
	}
	
	def QueryGroovyResult buildResult(DAPOQLSet set, Boolean withAttributes = false) {
		return dapoqlfunc.buildResult(set, withAttributes);
	}
	
	def QueryGroovyResult allDatamodels() {
		return buildResult(dapoqlfunc.getAllDatamodels());
	}
	
	def QueryGroovyResult allClasses() {
		return buildResult(dapoqlfunc.getAllClasses());
	}
	
	def QueryGroovyResult allAttributes() {
		return buildResult(dapoqlfunc.getAllAttributes());
	}
	
	def QueryGroovyResult allRelationships() {
		return buildResult(dapoqlfunc.getAllRelationships());
	}
	
	def QueryGroovyResult allObjects() {
		return buildResult(dapoqlfunc.getAllObjects());
	}
	
	def QueryGroovyResult allVersions(Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.getAllVersions(), withAttributes);
	}
	
	def QueryGroovyResult allRelations() {
		return buildResult(dapoqlfunc.getAllRelations());
	}
	
	def QueryGroovyResult allEvents(Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.getAllEvents(), withAttributes);
	}
	
	def QueryGroovyResult allActivityInstances() {
		return buildResult(dapoqlfunc.getAllActivityInstances());
	}
	
	def QueryGroovyResult allCases(Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.getAllCases(), withAttributes);
	}
	
	def QueryGroovyResult allLogs(Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.getAllLogs(), withAttributes);
	}
	
	def QueryGroovyResult allActivities() {
		return buildResult(dapoqlfunc.getAllActivities());
	}
	
	def QueryGroovyResult allProcesses() {
		return buildResult(dapoqlfunc.getAllProcesses());
	}
	
	def QueryGroovyResult versionsRelatedTo(QueryGroovyResult qr) throws Exception {
		if (qr.type != SLEXMMObjectVersion.class) {
			throw new Exception("Argument of versionsRelatedTo must be a set of versions");
		}
		return buildResult(dapoqlfunc.versionsRelatedTo(qr.getResult()));
	}
	
	def QueryGroovyResult datamodelsOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.datamodelsOf(qr.getResult()));
	}
	
	def QueryGroovyResult classesOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.classesOf(qr.getResult()));
	}
	
	def QueryGroovyResult attributesOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.attributesOf(qr.getResult()));
	}
	
	def QueryGroovyResult relationshipsOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.relationshipsOf(qr.getResult()));
	}
	
	def QueryGroovyResult objectsOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.objectsOf(qr.getResult()));
	}
	
	def QueryGroovyResult versionsOf(QueryGroovyResult qr, Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.versionsOf(qr.getResult()), withAttributes);
	}
	
	def QueryGroovyResult relationsOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.relationsOf(qr.getResult()));
	}
	
	def QueryGroovyResult eventsOf(QueryGroovyResult qr, Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.eventsOf(qr.getResult()), withAttributes);
	}
	
	def QueryGroovyResult activityInstancesOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.activityInstancesOf(qr.getResult()));
	}
	
	def QueryGroovyResult activitiesOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.activitiesOf(qr.getResult()));
	}
	
	def QueryGroovyResult casesOf(QueryGroovyResult qr, Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.casesOf(qr.getResult()), withAttributes);
	}
	
	def QueryGroovyResult logsOf(QueryGroovyResult qr, Boolean withAttributes = true) {
		return buildResult(dapoqlfunc.logsOf(qr.getResult()), withAttributes);
	}
	
	def QueryGroovyResult processesOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.processesOf(qr.getResult()));
	}

	def QueryGroovyResult periodsOf(QueryGroovyResult qr) {
		return buildResult(dapoqlfunc.periodsOf(qr.getResult()));
	}
	
	def SLEXMMPeriod globalPeriodOf(QueryGroovyResult qr) {
				
		if (qr.getType() == SLEXMMPeriod) {
			long startTimestamp = -1L;
			long endTimestamp = -2L;
			
			for (Object o: qr.getResult()) {
				SLEXMMPeriod po = (SLEXMMPeriod) o;
				startTimestamp = MMUtils.earliest(startTimestamp,po.getStart());
				endTimestamp = MMUtils.latest(endTimestamp,po.getEnd());
			}
			
			SLEXMMPeriod p = new SLEXMMPeriod(getStorage(), startTimestamp, endTimestamp);
			return p;
		} else {
			return globalPeriodOf(periodsOf(qr));
		}
	}
	
	def QueryGroovyResult s(args) {
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
			QueryGroovyResult qr = new QueryGroovyResult(type, getStorage(), dapoqlfunc);
			for (Object o: args) {
				qr.getResult().add((AbstractDBElement) o);
			}
			return qr;
		} else {
			throw new Exception("Items do not have the same type");
		}
	}
	
	def methodMissing(String name, args) {
		
		if (getMetaClass().respondsTo(this, name, QueryGroovyResult)) {

			if (!(args instanceof QueryGroovyResult)) {

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
					QueryGroovyResult qr = new QueryGroovyResult(type, getStorage(), dapoqlfunc);

					for (Object o: args) {
						qr.getResult().add((AbstractDBElement) o);
					}

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
		
		SLEXMMPeriod p = new SLEXMMPeriod(getStorage(), startTimestamp, endTimestamp);
		return p;
	}
	
	def SLEXMMPeriod createPeriod(long A, long B) {
		return new SLEXMMPeriod(getStorage(), A, B);
	}
	
	def SLEXMMPeriod createPeriod(long A) {
		return new SLEXMMPeriod(getStorage(), A, A);
	}
	
	def boolean before(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (MMUtils.before(a.getEnd(),b.getStart()));
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
		return (MMUtils.before(a.getStart(),b.getStart()) &&
			    MMUtils.after(a.getEnd(),b.getStart()) &&
				MMUtils.before(a.getEnd(),b.getEnd()));
	}
	
	def boolean overlapsInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return overlaps(b,a);
	}

	def boolean starts(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (a.getStart() == b.getStart() &&
			    MMUtils.before(a.getEnd(),b.getEnd()));
	}
	
	def boolean startsInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return starts(b,a);
	}
	
	def boolean during(SLEXMMPeriod a, SLEXMMPeriod b) {
		return ((MMUtils.after(a.getStart(),b.getStart()) &&
			     MMUtils.beforeOrEqual(a.getEnd(),b.getEnd())) ||
				(MMUtils.afterOrEqual(a.getStart(),b.getStart()) &&
			     MMUtils.before(a.getEnd(),b.getEnd())));		 
	}
	
	def boolean duringInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return during(b,a);
	}
	
	def boolean finishes(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (MMUtils.after(a.getStart(),b.getStart()) &&
			    a.getEnd() == b.getEnd());
	}
	
	def boolean finishesInv(SLEXMMPeriod a, SLEXMMPeriod b) {
		return finishes(b,a);
	}
	
	def boolean matches(SLEXMMPeriod a, SLEXMMPeriod b) {
		return (a.getStart() == b.getStart() && a.getEnd() == b.getEnd());
	}
	
	def void exportXLogsOf(QueryGroovyResult qr) {
		if (qr.getType() != SLEXMMLog.class &&
			qr.getType() != SLEXMMCase.class &&
			qr.getType() != SLEXMMActivityInstance.class &&
			qr.getType() != SLEXMMEvent.class) {
			throw new Exception("First argument of XLogsOf must be a set of logs, cases, activity instances, or events.");
		} else {
			// export one XLog per SLEXMMLog in the input 
			// export one XLog containing the cases in the input 
			// export one XLog containing the events in the input 
			// export one XLog containing the events of the activity instances in the input
			dapoqlfunc.exportXLogsOf(qr, null);
		}
	}
	
	def void exportXLogsOf(QueryGroovyResult qr, QueryGroovyResult evqr) {
		if (qr.getType() != SLEXMMLog.class &&
			qr.getType() != SLEXMMCase.class &&
			qr.getType() != SLEXMMActivityInstance.class &&
			qr.getType() != SLEXMMEvent.class) {
			throw new Exception("First argument of XLogsOf must be a set of logs, cases, activity instances, or events.");
		} else if (evqr.getType() != SLEXMMEvent.class) {
			throw new Exception("Second argument of XLogsOf must be a set of events.");
		} else {
			// export one XLog per SLEXMMLog in the first argument, including only the events in the second argument
			// export one XLog containing the cases in the first argument, including only the events in the second argument
			dapoqlfunc.exportXLogsOf(qr, evqr);
		}		
	}
	
}
