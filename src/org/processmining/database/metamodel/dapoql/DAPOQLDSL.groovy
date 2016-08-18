package org.processmining.database.metamodel.dapoql

import java.util.HashMap;
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap.ForEachEntryTask

import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMDataModel
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMLog;
import org.processmining.openslex.metamodel.SLEXMMObject;
import org.processmining.openslex.metamodel.SLEXMMObjectVersion;
import org.processmining.openslex.metamodel.SLEXMMProcess;
import org.processmining.openslex.metamodel.SLEXMMRelation;
import org.processmining.openslex.metamodel.SLEXMMRelationship;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

class DAPOQLDSL extends Script {

	private DAPOQLFunctionsGroovy dapoqlfunc = null;
	
	protected void init(SLEXMMStorageMetaModel slxmm) {
		this.dapoqlfunc = new DAPOQLFunctionsGroovy();
		this.dapoqlfunc.setMetaModel(slxmm);
	}
	
	@Override
	public Object run() {
		super.run();
	}
	
	class QueryGroovyResult extends QueryResult {
		
		def where(Closure body) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.result = new HashSet<>();
			qr.mapResult = new HashMap<>(); // FIXME
			qr.type = this.type;
			
			result.each {
				if (body(it) == true) {
					qr.result.add(it);
				}
			}
			return qr;
		}
		
		def union(QueryGroovyResult qrB) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.result = new HashSet<>();
			qr.result.addAll(this.result);
			qr.type = this.type;
			qr.result.addAll(qrB.result);
			return qr;
		}
		
		def excluding(QueryGroovyResult qrB) {
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.result = new HashSet<>();
			qr.result.addAll(this.result);
			qr.type = this.type;
			qr.result.removeAll(qrB.result);
			return qr;
		}
		
		def intersection(QueryGroovyResult qrB) {
			QueryGroovyResult qrAux = new QueryGroovyResult();
			qrAux.result = new HashSet<>();
			qrAux.result.addAll(this.result);
			qrAux.type = this.type;
			qrAux.result.removeAll(qrB.result);
			
			QueryGroovyResult qr = new QueryGroovyResult();
			qr.result = new HashSet<>();
			qr.result.addAll(this.result);
			qr.type = this.type;
			qr.result.removeAll(qrAux.result);
			return qr;
		}
	}
	
	private QueryGroovyResult buildResult(HashMap<Object, HashSet<Integer>> map, Class type) {
		QueryGroovyResult qr = new QueryGroovyResult();
		
		qr.mapResult = map;
		qr.result = map.keySet();
		qr.type = type;
		
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
	
}
