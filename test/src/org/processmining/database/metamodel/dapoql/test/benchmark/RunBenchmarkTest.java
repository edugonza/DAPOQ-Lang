package org.processmining.database.metamodel.dapoql.test.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class RunBenchmarkTest {

	private BufferedWriter bw = null;
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
	
	public static String readFile(String path) throws Exception {
		String query = null;
		
		File fd = new File(path);
		
		if (fd.exists()) {
			query = IOUtils.toString(new FileInputStream(fd), Charset.defaultCharset());
		} else {
			throw new Exception();
		}
		
		return query;
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
		
	private void writeSingleOut(Duration dur, String qname, String dbname, boolean isDapoql, boolean diskcache) throws IOException {
		if (this.bw != null) {
			bw.write(System.currentTimeMillis()+","+dbname+","+diskcache+","+qname+","+isDapoql+","+dur.toMillis()+"\n");
		}
	}
	
	private void writeOutHeader() throws IOException {
		if (this.bw != null) {
			bw.write("ts,DB,diskcache,QueryName,isDapoql,duration\n");
		}
	}
	
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
	
	public void singleBenchmark(String datasetPath, boolean diskcache, String queryPath, boolean queryIsDapoql,
			String outputPath, boolean printConsole, String printFile) throws Exception {
		if (outputPath != null) {
			File outputFile = new File(outputPath);
			boolean writeheader = !outputFile.exists();
			this.bw = new BufferedWriter(new FileWriter(outputFile,true));
			if (writeheader) {
				writeOutHeader();
			}
		}
		
		File datafile = new File(datasetPath);
		if (!datafile.exists()) {
			throw new Exception("Data does not exist: "+datafile.toString());
		}
		String path = datafile.getParent();
		String filename = datafile.getName();
		SLEXMMStorageMetaModel mm = new SLEXMMStorageMetaModelImpl(path, filename, diskcache);
		
		Duration duration = null;
		HashSet<Integer> ids = null;
		
		String query = readFile(queryPath);
		
		if (queryIsDapoql) {
			Set<DAPOQLVariable> vars = null;
			logger.info("Query DAPOQL");
			Instant start = Instant.now();
			ids = ComparisonCase.runDAPOQL(mm, vars, query);
			Instant end = Instant.now();
			duration = Duration.between(start, end);
		} else {
			logger.info("Query SQL");
			Instant start = Instant.now();
			ids = ComparisonCase.runSQL(mm, query);
			Instant end = Instant.now();
			duration = Duration.between(start, end);
		}
		
		if (this.bw != null) {
			writeSingleOut(duration, queryPath, datasetPath, queryIsDapoql, diskcache);
			this.bw.flush();
			this.bw.close();
		}
		
		if (printConsole) {
			System.out.print("\n[ ");
			for (Integer i: ids) {
				System.out.print(i+", ");
			}
			System.out.print(" ]\n");
		}
		if (printFile != null) {
			File pf = new File(printFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(pf));
			for (Integer i: ids) {
				bw.write(i+"\n");
			}
			bw.flush();
			bw.close();
		}
	}
	
	@Test
	public void test_benchmark() {
		String mmPath = "./data/metamodel-RL.slexmm";
		String queriesPath = "./benchmark/queries-RL";
		String outputPath = null;
		try {
			this.queryingBenchmark(mmPath,queriesPath);
		} catch (Exception e) {
			this.logger.severe(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		Options options = new Options();
		options.addRequiredOption("db", "database", true, "OpenSLEX file to query");
		options.addOption("mc", "mem-cache", false, "Disable disk-based cache for DAPOQ-Lang queries and use memory instead");
		OptionGroup querygroup = new OptionGroup();
		querygroup.addOption(new Option("dpf", "dapoql-file", true, "DAPOQ-Lang query file"));
		querygroup.addOption(new Option("sqlf", "sql-file", true, "SQL query file"));
		options.addOptionGroup(querygroup);
		options.addOption("o", "output", true, "Output CSV file to append the query time");
		options.addOption("p", "print", false, "Pring query output to console");
		options.addOption("pf", "print-file", true, "Write query output to file");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		
		String queryPath = null;
		String mmPath = null;
		String outputPath = null;
		boolean printconsole = false;
		String printFile = null;
		boolean isDapoql = false;
		boolean diskcache = false;
		try {
			cmd = parser.parse(options, args);
			mmPath = cmd.getOptionValue("db");
			printconsole = cmd.hasOption("p");
			diskcache = !cmd.hasOption("mc");
			if (cmd.hasOption("pf")) {
				printFile = cmd.getOptionValue("pf");
			}
			if (cmd.hasOption("o")) {
				outputPath = cmd.getOptionValue("o");
			}
			if (cmd.hasOption("dpf")) {
				isDapoql = true;
				queryPath = cmd.getOptionValue("dpf");
			} else if (cmd.hasOption("sqlf")) {
				diskcache = false;
				isDapoql = false;
				queryPath = cmd.getOptionValue("sqlf");
			} else {
				throw new ParseException("Query option missing");
			}
		} catch (ParseException e1) {
			System.err.println(e1.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "RunBenchmarkTest", options );
			System.exit(1);
		}
		
		RunBenchmarkTest rbt = new RunBenchmarkTest();
		try {
			rbt.singleBenchmark(mmPath,diskcache,queryPath,isDapoql,outputPath,printconsole,printFile);
		} catch (Exception e) {
			rbt.logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
