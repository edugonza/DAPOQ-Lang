package org.processmining.database.metamodel.dapoql;


import java.util.Set;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
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
	
	public QueryResult executeQuery(SLEXMMStorageMetaModel slxmm, String query, Set<DAPOQLVariable> vars) throws Exception {
		this.slxmm = slxmm;

//		System.out.println("Executing query: "+query);
//		long start_time = System.currentTimeMillis();
//		System.out.println("Start time: "+start_time);
				
		QueryResult qres = null;

		try {
			CompilerConfiguration config = new CompilerConfiguration();
			config.setScriptBaseClass(DAPOQLDSL.class.getName());
			ImportCustomizer icz = new ImportCustomizer();
			icz.addImport("Duration", "java.time.Duration");
			config.addCompilationCustomizers(icz);
			Binding binding = new Binding();
			if (vars != null) {
				for (DAPOQLVariable v : vars) {
					QueryGroovyResult vr = new QueryGroovyResult(v.getType(), slxmm);
					vr.setResult(v.getValue());
					binding.setVariable(v.getName(), vr);
				}
			}
			
			GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding, config);
			Script script = shell.parse(query);
			script.invokeMethod("init", slxmm);
			Object result = script.run();
			
			if (result instanceof QueryResult) {
				qres = (QueryResult) result;
			} else if (result instanceof String) {
				throw new Exception((String) result);
			}
			
			//System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
//		long end_time = System.currentTimeMillis();
//		double total_time = (double) end_time - start_time;
//		double total_time_secs = total_time / 1000.0;
//		double total_time_mins = total_time_secs / 60.0;
//		System.out.println("End time: " + end_time);
//		System.out.println("Total time (millis): " + total_time);
//		System.out.println("Total time (seconds): " + total_time_secs);
//		System.out.println("Total time (minutes): " + total_time_mins);

		return qres;
	}
	
}
