package org.processmining.database.metamodel.dapoql.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.processmining.database.metamodel.dapoql.DAPOQLRunnerGroovy;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.database.metamodel.dapoql.QueryResult;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class DAPOQLTest {

	static SLEXMMStorageMetaModel mm;
	static String path = "data/";
	static String filename = "metamodel-RL.slexmm";
	DAPOQLRunnerGroovy runner;
	
	@BeforeClass
	public static void initAll() throws Exception {
		mm = new SLEXMMStorageMetaModelImpl(path, filename, true);
	}
	
	@Before
	public void init() {
		runner = new DAPOQLRunnerGroovy();
	}
	
	@Test
	public void getEvents() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allEvents()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
	
	@Test
	public void getVersions() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allVersions()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
	
	@Test
	public void getCases() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allCases()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
	
	@Test
	public void getLogs() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allLogs()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
	
	@Test
	public void getClasses() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allClasses()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
	
	@Test
	public void getActivities() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allActivities()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
	
	@Test
	public void getProcesses() throws Exception {
		QueryResult out = runner.executeQuery(mm, "allProcesses()", null);
		if (out == null) {
			fail("Output was null");
		}
	}
		
	@AfterClass
	public static void close() {
		mm.disconnect();
	}

}
