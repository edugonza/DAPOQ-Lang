package org.processmining.database.metamodel.dapoql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.processmining.database.metamodel.dapoql.lite.SuggestionsResult;
import org.processmining.database.metamodel.dapoql.lite.SuggestionsResult;
import org.processmining.database.metamodel.dapoql.lite.dapoqlLexer;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ProgContext;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class DAPOQLRunnerGroovy {

	SLEXMMStorageMetaModel slxmm = null;
	
	public void cancel() {
		if (slxmm != null) {
			slxmm.abort();
		}
	}
	
	public SuggestionsResult executeQueryForSuggestions(String query, Set<DAPOQLVariable> vars) {

		System.out.println("Executing query: "+query);
		long start_time = System.currentTimeMillis();
		System.out.println("Start time: "+start_time);
		
		dapoqlParser parser = null;
		
		try {
			
			ANTLRInputStream input = new ANTLRInputStream(query);

			dapoqlLexer lexer = new dapoqlLexer(input);

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			parser = new dapoqlParser(tokens);
			parser.dapoql.setMetaModel(null);
			parser.dapoql.setCheckerMode(true);
			parser.dapoql.setVocabulary(dapoqlLexer.VOCABULARY);
        
			if (vars != null) {
				for (DAPOQLVariable v : vars) {
					parser.dapoql.createVariable(v.getName(), v.getType(), new HashMap<Object,HashSet<Integer>>());
				}
			}
			
			ProgContext progC = parser.prog(); // begin parsing at rule 'prog'
			System.out.println(progC.toStringTree(parser)); // print LISP-style tree
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
        
        long end_time = System.currentTimeMillis();
        double total_time = (double) end_time - start_time;
        double total_time_secs = total_time / 1000.0;
        double total_time_mins = total_time_secs / 60.0;
        System.out.println("End time: "+end_time);
		System.out.println("Total time (millis): "+total_time);
		System.out.println("Total time (seconds): "+total_time_secs);
		System.out.println("Total time (minutes): "+total_time_mins);
		
		List<String> suggestions = parser.dapoql.getSuggestions();
		Token offendedToken = parser.dapoql.getOffendingToken();
		
		SuggestionsResult result = new SuggestionsResult();
		result.suggestions = suggestions;
		
		if (offendedToken == null) {
			result.initOffendingToken = query.length();
			result.endOffendingToken = query.length()-1;
		} else {
			if (offendedToken.getCharPositionInLine() != offendedToken.getStartIndex()) {
				System.err.println("WARNING!");
			}
			
			result.initOffendingToken = offendedToken.getStartIndex();
			result.endOffendingToken = offendedToken.getStopIndex();
		}
		
        return result;
	}
	
	public QueryResult executeQuery(SLEXMMStorageMetaModel slxmm, String query, Set<DAPOQLVariable> vars) throws Exception {
		this.slxmm = slxmm;

		System.out.println("Executing query: "+query);
		long start_time = System.currentTimeMillis();
		System.out.println("Start time: "+start_time);
				
		QueryResult qres = new QueryResult();
//		dapoqlParser parser = null;
//
//		try {
//			ANTLRInputStream input = new ANTLRInputStream(query);
//			dapoqlLexer lexer = new dapoqlLexer(input);
//			parser = new dapoqlParser(new CommonTokenStream(lexer));
//
//			parser.dapoql = new DAPOQLFunctions();
//			parser.dapoql.setMetaModel(slxmm);
//			parser.dapoql.setVocabulary(lexer.getVocabulary());
//			
//			if (vars != null) {
//				for (DAPOQLVariable v : vars) {
//					parser.dapoql.createVariable(v.getName(), v.getType(), v.getValue());
//				}
//			}
//
//			ParseTree tree = parser.prog();
//			DAPOQLBaseVisitor visitor = new DAPOQLBaseVisitor(parser.dapoql);
//			DAPOQLValue v = visitor.visit(tree);
//
//			qres.mapResult = v.result;
//			qres.result = v.result.keySet();
//			qres.type = v.type;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}

		try {
			CompilerConfiguration config = new CompilerConfiguration();
			config.setScriptBaseClass("org.processmining.database.metamodel.dapoql.DAPOQLDSL");
			Binding binding = new Binding();
			GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding, config);
			Script script = shell.parse(query);
			script.invokeMethod("init", slxmm);
			
			//script.setProperty("slxmm", slxmm);
			Object result = script.run();
			
//			DAPOQLDSL architecture = new DAPOQLDSL();
//			architecture.init(slxmm);
//			Object result = architecture.runDAPOQLDSL(query);
			
			if (result instanceof QueryResult) {
				qres = (QueryResult) result;
			} else if (result instanceof String) {
				throw new Exception((String) result);
			}
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		long end_time = System.currentTimeMillis();
		double total_time = (double) end_time - start_time;
		double total_time_secs = total_time / 1000.0;
		double total_time_mins = total_time_secs / 60.0;
		System.out.println("End time: " + end_time);
		System.out.println("Total time (millis): " + total_time);
		System.out.println("Total time (seconds): " + total_time_secs);
		System.out.println("Total time (minutes): " + total_time_mins);

		return qres;
	}
	
}
