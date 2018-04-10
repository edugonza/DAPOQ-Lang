package org.processmining.database.metamodel.dapoql.test.benchmark;


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

public class DAPOQLIntegrationTest {

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
	public void getThings() throws Exception {
		String[] elements = new String[] {
			"Datamodels",
			"Relationships",
			"Attributes",
			"Objects",
			"Versions",
			"Relations",
			"Events",
			"ActivityInstances",
			"Cases",
			"Logs",
			"Activities",
			"Processes"
		};
		
		String[] elementsOf = new String[]
		{
			"datamodels",
			"relationships",
			"attributes",
			"objects",
			"versions",
			"relations",
			"events",
			"activityInstances",
			"cases",
			"logs",
			"activities",
			"processes",
			"periods",
			"globalPeriod"
		};
		
		for (int i = 0; i < elements.length; i++) {
			String e = elements[i];
			System.out.println("all"+e);
			QueryResult qrA = runner.executeQuery(mm, "all"+e+"()", null);
			DAPOQLVariable var = new DAPOQLVariable("a", qrA.getType(), qrA.getResult());
			Set<DAPOQLVariable> vars = new HashSet<>();
			vars.add(var);
			for (String eOf: elementsOf) {
				System.out.println(eOf+"Of all"+e);
				runner.executeQuery(mm, eOf+"Of(a)", vars);
			}
		}
	}
	
	@AfterClass
	public static void close() {
		mm.disconnect();
	}

}
