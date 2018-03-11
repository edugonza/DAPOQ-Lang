package org.processmining.database.metamodel.dapoql.test.benchmark;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import com.google.common.base.Predicate;

public class RunBenchmarkTest {

	public File outputFile = null;
	String filename = "metamodel-RL.slexmm";
	String path = "./data/";
		
	public String[][] loadBenchmark() throws Exception {
		
		Predicate<String> dapoqlPred = new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				 return input.endsWith(".dapoql");
			}
		};
		
		Predicate<String> sqlPred = new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return input.endsWith(".sql");
			}
		};
		
		String package_name = "org.processmining.database.metamodel.dapoql.test.benchmark.resources";
		Set<String> dapoqlFiles = new Reflections(package_name,
				new ResourcesScanner()).getResources(dapoqlPred);
		Set<String> sqlFiles = new Reflections(package_name,
				new ResourcesScanner()).getResources(sqlPred);
		
		int n_dapoql = dapoqlFiles.size();
		int n_sql = sqlFiles.size();
		
		if (n_dapoql != n_sql) {
			throw new Exception("DAPOQL and SQL benchmark files are not even.");
		}
		
		String benchmarkQueries[][] = new String[n_dapoql][3];
		
		int i = 0;
		for (String df: dapoqlFiles) {
			String prefix = df.substring(0, df.length()-".dapoql".length());
			String sf = prefix.concat(".sql");
			InputStream dfin = this.getClass().getResourceAsStream("/"+df);
			InputStream sfin = this.getClass().getResourceAsStream("/"+sf);
			if (dfin == null) {
				throw new Exception("DAPOQL benchmark file missing: "+df);
			}
			if (sfin == null) {
				throw new Exception("SQL benchmark file missing: "+sf);
			}
			benchmarkQueries[i][0] = prefix;
			benchmarkQueries[i][1] = IOUtils.toString(dfin, Charset.defaultCharset());
			benchmarkQueries[i][2] = IOUtils.toString(sfin, Charset.defaultCharset());
			i++;
		}
		
		return benchmarkQueries;
	}
	
	@Test
	public void queryingBenchmark() throws Exception {
		String benchmarkQueries[][] = loadBenchmark();
		
		File datafile = new File(path + File.separator + filename);
		if (!datafile.exists()) {
			throw new Exception("Data does not exist: "+datafile.toString());
		}
		SLEXMMStorageMetaModel mm = new SLEXMMStorageMetaModelImpl(path, filename, true);
		
		Duration[][] benchmarkDurations = new Duration[benchmarkQueries.length][2]; 
		
		Set<DAPOQLVariable> vars = null;
		for (int i = 0; i < benchmarkQueries.length; i++) {
			String[] qset = benchmarkQueries[i];
			System.out.println("Query Set "+i+": "+qset[0]);
			ComparisonCase cc = new ComparisonCase(qset[1], qset[2]);
			benchmarkDurations[i] = cc.runCase(mm, vars);
		}
	}
	
	public static void main(String[] args) {
		File outputFile = new File("benchmark/results.csv");
		RunBenchmarkTest rbt = new RunBenchmarkTest();
		rbt.outputFile = outputFile;
		rbt.filename = "metamodel-RL.slexmm";
		rbt.path = "./data/";
		try {
			rbt.queryingBenchmark();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
