package org.processmining.database.metamodel.dapoql.test.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class RunBenchmarkTest {

	private File outputFile = null;
	private static final String DEFAULT_FILENAME = "metamodel-RL.slexmm";
	private static final String DEFAULT_PATH = "./data/";
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	private <T> List<T> toList(Stream<T> st) {
		ArrayList<T> list = new ArrayList<>();
		Iterator<T> it = st.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
	
	public String[][] loadBenchmark(String queriespath) throws Exception {
		
		Predicate<Path> dapoqlPred = new Predicate<Path>() {
			@Override
			public boolean test(Path input) {
				return input.toString().endsWith(".dapoql");
			}
		};
		
		Predicate<Path> sqlPred = new Predicate<Path>() {
			@Override
			public boolean test(Path input) {
				return input.toString().endsWith(".sql");
			}
		};
		
		String package_name = "org.processmining.database.metamodel.dapoql.test.benchmark.resources";
		Path path = Paths.get(queriespath);
		List<Path> dapoqlFiles = Files.walk(path).filter(dapoqlPred).collect(Collectors.toList());
		List<Path> sqlFiles = Files.walk(path).filter(sqlPred).collect(Collectors.toList());
		
		int n_dapoql = (int) dapoqlFiles.size();
		int n_sql = (int) sqlFiles.size();
		
		if (n_dapoql != n_sql) {
			logger.warning("DAPOQL and SQL benchmark files are not even.");
		}
		
		String benchmarkQueries[][] = new String[n_dapoql][3];
		
		int i = 0;
		for (Path fp: dapoqlFiles) {
			String df = fp.toString();
			String prefix = df.substring(0, df.length()-".dapoql".length());
			String sf = prefix.concat(".sql");
			File dfin = new File(df);
			File sfin = new File(sf);
			benchmarkQueries[i][0] = prefix;
			if (!dfin.exists()) {
				logger.warning("DAPOQL benchmark file missing: "+df);
			} else {
				benchmarkQueries[i][1] = IOUtils.toString(new FileInputStream(dfin), Charset.defaultCharset());
			}
			if (!sfin.exists()) {
				logger.warning("SQL benchmark file missing: "+sf);
			} else {
				benchmarkQueries[i][2] = IOUtils.toString(new FileInputStream(sfin), Charset.defaultCharset());
			}
			i++;
		}
		
		return benchmarkQueries;
	}
	
	@Test
	public void queryingBenchmark(String datasetPath, String queriesPath) throws Exception {
		String benchmarkQueries[][] = loadBenchmark(queriesPath);
		
		File datafile = new File(datasetPath);
		if (!datafile.exists()) {
			throw new Exception("Data does not exist: "+datafile.toString());
		}
		String path = datafile.getParent();
		String filename = datafile.getName();
		SLEXMMStorageMetaModel mm = new SLEXMMStorageMetaModelImpl(path, filename, true);
		
		Duration[][] benchmarkDurations = new Duration[benchmarkQueries.length][2]; 
		
		Set<DAPOQLVariable> vars = null;
		for (int i = 0; i < benchmarkQueries.length; i++) {
			String[] qset = benchmarkQueries[i];
			logger.info("Query Set "+i+": "+qset[0]);
			ComparisonCase cc = new ComparisonCase(qset[1], qset[2]);
			benchmarkDurations[i] = cc.runCase(mm, vars);
		}
	}
	
	public static void main(String[] args) {
		
		File outputFile = new File("benchmark/results-RL.csv");
		RunBenchmarkTest rbt = new RunBenchmarkTest();
		rbt.outputFile = outputFile;
		try {
			rbt.queryingBenchmark("./data/metamodel-SAP.slexmm","./benchmark/queries-Filter-periods-SAP");
		} catch (Exception e) {
			rbt.logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
