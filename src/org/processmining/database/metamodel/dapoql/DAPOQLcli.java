package org.processmining.database.metamodel.dapoql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class DAPOQLcli {

	public static void main(String[] args) {
		
		Options options = new Options();
		options.addRequiredOption("m", "metamodel", true, "Meta model file to query");
		OptionGroup opg = new OptionGroup();
		opg.addOption(new Option("q", "query", true, "DAPOQ-Lang query to execute"));
		opg.addOption(new Option("qf", "query-file", true, "DAPOQ-Lang query file to execute"));
		options.addOptionGroup(opg);
		options.addOption("nd", false, "Disable disk cache for querying");
		options.addOption("e", true, "Set the path where to save any generated XES log");
		options.addOption("p", false, "Print output");
		options.addOption("h", "help", false, "Show help");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
			System.err.println(e1.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "dapoql-cli", options );
			System.exit(1);
		}
		
		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "dapoql-cli", options );
			System.exit(0);
		}
		
		String mmFile = cmd.getOptionValue("m");
		boolean diskCache = !cmd.hasOption("nd");
		String query = cmd.getOptionValue("q");
		String queryFile = cmd.getOptionValue("qf");
		boolean exportLog = cmd.hasOption("e");
		String logpath = cmd.getOptionValue("e");
		boolean printOutput = cmd.hasOption("p");
		
		SLEXMMStorageMetaModel mm = null;
		
		Path fp = Paths.get(mmFile);
		Path folder = fp.getParent();
		String filename = fp.getFileName().toString();
		String path = folder.toString();
		try {
			mm = new SLEXMMStorageMetaModelImpl(path, filename, diskCache);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mm == null) {
				System.exit(1);
			}
		}
		
		if (query == null) {
			File qf = new File(queryFile);
			try {
				if (qf.exists()) {
					BufferedReader br = new BufferedReader(new FileReader(qf));
					StringBuilder sb = new StringBuilder();
					while (br.ready()) {
						sb.append(br.readLine());
						sb.append('\n');
					}
					query = sb.toString();
				} else {
					throw new Exception("Query file not available.");
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "dapoql-cli", options );
				System.exit(1);
			}
		}
		
		DAPOQLRunnerGroovy runner = new DAPOQLRunnerGroovy();
		try {
			if (exportLog) {
				runner.setOutputLogPath(logpath);
			}
			QueryResult outDapoql = runner.executeQuery(mm, query, null);
			if (printOutput) {
				System.out.println("Class: "+outDapoql.getResult().getType());
				System.out.println(outDapoql.getResult().getIdsSet());
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
