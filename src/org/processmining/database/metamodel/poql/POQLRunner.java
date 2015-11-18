package org.processmining.database.metamodel.poql;

import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.tree.ParseTree;
import org.processmining.database.metamodel.poql.poqlParser.ProgContext;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class POQLRunner {

	SLEXMMStorageMetaModel slxmm = null;
	
	public void cancel() {
		if (slxmm != null) {
			slxmm.abort();
		}
	}
	
	public SuggestionsResult executeQueryForSuggestions(String query) {

		System.out.println("Executing query: "+query);
		long start_time = System.currentTimeMillis();
		System.out.println("Start time: "+start_time);
		
		poqlParser parser = null;
		
		try {
			
			ANTLRInputStream input = new ANTLRInputStream(query);

			poqlLexer lexer = new poqlLexer(input);

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			parser = new poqlParser(tokens);
			parser.poql.setMetaModel(null);
			parser.poql.setCheckerMode(true);
			parser.poql.setVocabulary(poqlLexer.VOCABULARY);
        
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
		
		List<String> suggestions = parser.poql.getSuggestions();
		Token offendedToken = parser.poql.getOffendingToken();
		
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
	
	public QueryResult executeQuery(SLEXMMStorageMetaModel slxmm, String query) throws Exception {
		this.slxmm = slxmm;

		System.out.println("Executing query: "+query);
		long start_time = System.currentTimeMillis();
		System.out.println("Start time: "+start_time);
		
		QueryResult qres = new QueryResult();
		poqlParser parser = null;

		try {
			ANTLRInputStream input = new ANTLRInputStream(query);
			poqlLexer lexer = new poqlLexer(input);
			parser = new poqlParser(new CommonTokenStream(lexer));

			parser.poql = new POQLFunctions();
			parser.poql.setMetaModel(slxmm);
			parser.poql.setVocabulary(lexer.getVocabulary());

			ParseTree tree = parser.prog();
			POQLBaseVisitor visitor = new POQLBaseVisitor(parser.poql);
			POQLValue v = visitor.visit(tree);

			qres.result = v.result;
			qres.type = v.type;

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
