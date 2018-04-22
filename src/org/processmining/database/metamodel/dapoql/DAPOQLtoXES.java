package org.processmining.database.metamodel.dapoql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.processmining.openslex.metamodel.AbstractAttDBElement;
import org.processmining.openslex.metamodel.AbstractDBElement;
import org.processmining.openslex.metamodel.AbstractDBElementWithValue;
import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMLog;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class DAPOQLtoXES {
	
	private static XAttributeMap createAttributeMap(XFactory xfact, Map<AbstractAttDBElement, AbstractDBElementWithValue> map) {
		XAttributeMap xmap = xfact.createAttributeMap();
		for (AbstractAttDBElement at : map.keySet()) {
			XAttribute xat = xfact.createAttributeLiteral(at.getName(), map.get(at).getValue(), null);
			xmap.put(at.getName(), xat);
		}
		
		return xmap;
	}
	
	private static XEvent exportEvent(XFactory xfact, DAPOQLFunctionsGroovy func, SLEXMMEvent slxev,
			Map<Integer, SLEXMMActivityInstance> mapAI,
			Map<Integer, SLEXMMActivity> mapAct) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		XAttributeMap xmap = createAttributeMap(xfact, (Map) slxev.getAttributeValues());
		XEvent e = xfact.createEvent(xmap);
		XLifecycleExtension.instance().assignTransition(e, slxev.getLifecycle());
		XTimeExtension.instance().assignTimestamp(e, slxev.getTimestamp());
		XOrganizationalExtension.instance().assignResource(e, slxev.getResource());
		XIdentityExtension.instance().assignID(e,
				new XID(slxev.getClazzId(),slxev.getId()));
		int aiId = slxev.getActivityInstanceId();
		SLEXMMActivityInstance ai = mapAI.get(aiId);
		int actId = ai.getActivityId();
		SLEXMMActivity act = mapAct.get(actId);
		XConceptExtension.instance().assignName(e, act.getName());
		return e;
	}
	
	private static class EventTimeComparator implements Comparator<Integer> {

		private SLEXMMStorageMetaModel storage = null;
		
		public EventTimeComparator(SLEXMMStorageMetaModel storage) {
			this.storage = storage;
		}
		
		@Override
		public int compare(Integer evIdA, Integer evIdB) {
			SLEXMMEvent evA = this.storage.getFromCache(SLEXMMEvent.class, evIdA);
			SLEXMMEvent evB = this.storage.getFromCache(SLEXMMEvent.class, evIdB);
			long tsA = evA.getTimestamp();
			long tsB = evB.getTimestamp();
			return Long.compare(tsA, tsB);
		}
		
	}
	
	private static XTrace exportTrace(XFactory xfact, DAPOQLFunctionsGroovy func, SLEXMMCase slxcase,
			DAPOQLSet evset,
			Map<Integer, SLEXMMActivityInstance> mapAI,
			Map<Integer, SLEXMMActivity> mapAct,
			boolean prefetched) throws Exception {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		XAttributeMap xmap = createAttributeMap(xfact, (Map) slxcase.getAttributeValues());
		XTrace t = xfact.createTrace(xmap);
		XIdentityExtension.instance().assignID(t,
				new XID(slxcase.getClazzId(),slxcase.getId()));
		XConceptExtension.instance().assignName(t, slxcase.getName());
		DAPOQLSet set = new DAPOQLSet(func.getStorage(),SLEXMMCase.class);
		set.add(slxcase);
		DAPOQLSet res = null;
		
		if (prefetched) {
			res = func.eventsOf(set);
		} else {
			res = func.buildResult(func.eventsOf(set)).getResult();
		}
		
		if (evset != null) {
			res = res.intersection(evset);
		}
		
		ArrayList<Integer> orderedEvents = new ArrayList<>(res.getIdsSet());
		
		Collections.sort(orderedEvents, new EventTimeComparator(slxcase.getStorage()));
		
		for (Integer elId : orderedEvents) {
			SLEXMMEvent slxev = (SLEXMMEvent) slxcase.getStorage().getFromCache(SLEXMMEvent.class, elId);
			XEvent ev = exportEvent(xfact,func,slxev,mapAI,mapAct);
			t.add(ev);
		}
		return t;
	}
	
	private static Map<Integer,SLEXMMActivityInstance> getActInstancesMap(DAPOQLFunctionsGroovy func, DAPOQLSet set) throws Exception {
		HashMap<Integer,SLEXMMActivityInstance> map = new HashMap<>();
		for (AbstractDBElement aiEl : set) {
			SLEXMMActivityInstance ai = (SLEXMMActivityInstance) aiEl;
			map.put(ai.getId(), ai);
		}
		return map;
	}
	
	private static Map<Integer,SLEXMMActivity> getActivitiesMap(DAPOQLFunctionsGroovy func, DAPOQLSet set) throws Exception {
		HashMap<Integer,SLEXMMActivity> map = new HashMap<>();
		for (AbstractDBElement actEl : set) {
			SLEXMMActivity act = (SLEXMMActivity) actEl;
			map.put(act.getId(), act);
		}
		return map;
	}
	
	private static DAPOQLSet getActInstances(DAPOQLFunctionsGroovy func, DAPOQLSet set) throws Exception {
		QueryGroovyResult resAI = func.buildResult(func.activityInstancesOf(set));
		return resAI.getResult();
	}
	
	private static DAPOQLSet getActivities(DAPOQLFunctionsGroovy func, DAPOQLSet set) throws Exception {
		QueryGroovyResult resAct = func.buildResult(func.activitiesOf(set));
		return resAct.getResult();
	}
	
	private static XLog exportLog(XFactory xfact, DAPOQLFunctionsGroovy func, SLEXMMLog slxlog,
			DAPOQLSet evset,
			Map<Integer, SLEXMMActivityInstance> mapAI,
			Map<Integer, SLEXMMActivity> mapAct,
			boolean prefetched) throws Exception {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		XAttributeMap xmap = createAttributeMap(xfact, (Map) slxlog.getAttributeValues());
		XLog log = xfact.createLog(xmap);
		XIdentityExtension.instance().assignID(log,
				new XID(slxlog.getClazzId(),slxlog.getId()));
		XConceptExtension.instance().assignName(log, slxlog.getName());
		
		DAPOQLSet set = new DAPOQLSet(func.getStorage(),SLEXMMLog.class);
		set.add(slxlog);
		
		DAPOQLSet res = null;
		if (prefetched) {
			res = func.casesOf(set);
		} else {
			res = func.buildResult(func.casesOf(set)).getResult();
		}
		for (AbstractDBElement el : res) {
			SLEXMMCase slxcase = (SLEXMMCase) el;
			XTrace t = exportTrace(xfact,func,slxcase,evset,mapAI,mapAct,prefetched);
			log.add(t);
		}
		return log;
	}
	
	private static void serializeLog(XLog log, XSerializer serializer, String name, File parent) throws IOException {
		File logfd = new File(parent, name);
		FileOutputStream out = new FileOutputStream(logfd); 
		serializer.serialize(log, out);
	}
	
	private static void prefetch(DAPOQLFunctionsGroovy func, DAPOQLSet set, DAPOQLSet evset) throws Exception {
		DAPOQLSet fetched_set = set;
		DAPOQLSet fetched_evset = evset;
		if (evset != null && !evset.attributesFetched()) {
			QueryResult res = func.buildResult(evset);
			fetched_evset = res.getResult();
		}
		if (!set.attributesFetched()) {
			if (set.getType() != SLEXMMEvent.class || evset == null) {
				QueryResult res = func.buildResult(set);
				fetched_set = res.getResult();
			}
		}
		if (set.getType() == SLEXMMLog.class) {
			QueryResult cres = func.buildResult(func.casesOf(fetched_set));
			prefetch(func,cres.getResult(),fetched_evset);
		} else if (set.getType() == SLEXMMCase.class) {
			func.buildResult(func.eventsOf(fetched_set));
		} else if (set.getType() == SLEXMMEvent.class) {
			// Done
		}
	}
	
	public static void exportLogs(DAPOQLFunctionsGroovy func, DAPOQLSet res, DAPOQLSet evres, String logpath) throws Exception {
		File f = new File(logpath);
		if (!f.exists()) {
			f.mkdirs();
		} else {
			if (!f.isDirectory()) {
				throw new Exception("Provided log path is not a directory.");
			}
		}
		
		XFactory xfact = new XFactoryBufferedImpl();
		XSerializer xeserial = new XesXmlGZIPSerializer();
		
		if (res.getType() == SLEXMMLog.class) {
			prefetch(func,res,evres);
			
			DAPOQLSet aiSet = getActInstances(func,res);
			Map<Integer, SLEXMMActivityInstance> mapAI = getActInstancesMap(func,aiSet);
			DAPOQLSet actSet = getActivities(func,aiSet);
			Map<Integer, SLEXMMActivity> mapAct = getActivitiesMap(func,actSet);
			
			for (AbstractDBElement logEl : res) {
				SLEXMMLog slxlog = (SLEXMMLog) logEl;
				XLog log = exportLog(xfact, func, slxlog, evres,mapAI,mapAct,true);
				String logfilename = slxlog.getId()+"_"+slxlog.getName()+".xes.gz";
				
				serializeLog(log, xeserial, logfilename, f);
			}
		} else if (res.getType() == SLEXMMCase.class) {
			prefetch(func,res,evres);
			XLog log = xfact.createLog();
			
			DAPOQLSet aiSet = getActInstances(func,res);
			Map<Integer, SLEXMMActivityInstance> mapAI = getActInstancesMap(func,aiSet);
			DAPOQLSet actSet = getActivities(func,aiSet);
			Map<Integer, SLEXMMActivity> mapAct = getActivitiesMap(func,actSet);
			for (AbstractDBElement caseEl : res) {
				XTrace trace = exportTrace(xfact, func, (SLEXMMCase) caseEl, evres, mapAI,mapAct,true);
				log.add(trace);
			}
			
			String logfilename = "log-from-cases_"+System.currentTimeMillis()+".xes.gz";
			
			serializeLog(log, xeserial, logfilename, f);
		} else if (res.getType() == SLEXMMEvent.class) {
			prefetch(func,res,evres);
			XLog log = xfact.createLog();
			XTrace trace = xfact.createTrace();
			log.add(trace);
			
			DAPOQLSet evset = res;
			if (evres != null) {
				evset = evres;
			}
			
			DAPOQLSet aiSet = getActInstances(func,evset);
			Map<Integer, SLEXMMActivityInstance> mapAI = getActInstancesMap(func,aiSet);
			DAPOQLSet actSet = getActivities(func,aiSet);
			Map<Integer, SLEXMMActivity> mapAct = getActivitiesMap(func,actSet);
			
			for (AbstractDBElement evEl : evset) {
				XEvent event = exportEvent(xfact, func, (SLEXMMEvent) evEl, mapAI, mapAct);
				trace.add(event);
			}
			
			String logfilename = "log-from-events_"+System.currentTimeMillis()+".xes.gz";
			
			serializeLog(log, xeserial, logfilename, f);
		} else if (res.getType() == SLEXMMActivityInstance.class) {
			DAPOQLSet evRes = func.eventsOf(res);
			exportLogs(func, evRes, evres, logpath);
		} else {
			throw new Exception("The result cannot be exported as XES logs if it is not"
					+ " a set of logs, cases, events or activity instances.");
		}
	}
	
}
