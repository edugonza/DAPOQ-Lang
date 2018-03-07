package org.processmining.database.metamodel.dapoql.test.benchmark;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.processmining.database.metamodel.dapoql.DAPOQLRunnerGroovy;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.database.metamodel.dapoql.QueryResult;
import org.processmining.openslex.metamodel.SLEXMMAbstractDatabaseObject;
import org.processmining.openslex.metamodel.SLEXMMSQLResult;
import org.processmining.openslex.metamodel.SLEXMMSQLResultSet;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class ComparisonCase {
	
	private String dapoqlQuery = "";
	private String sqlQuery = "";
	
	public ComparisonCase(String dapoqlQuery, String sqlQuery) {
		setDapoqlQuery(dapoqlQuery);
		setSqlQuery(sqlQuery);
	}
	
	public String getDapoqlQuery() {
		return dapoqlQuery;
	}
	
	public void setDapoqlQuery(String dapoqlQuery) {
		this.dapoqlQuery = dapoqlQuery;
	}
	
	public String getSqlQuery() {
		return sqlQuery;
	}
	
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
	
	private void reconnect(SLEXMMStorageMetaModel mm) {
		try {
			mm.getClass().getMethod("reconnect").invoke(mm);
		} catch (Exception e) {
			
		}
	}
	
	public Duration[] runCase(SLEXMMStorageMetaModel mm, Set<DAPOQLVariable> vars) throws Exception {
		Duration dur[] = new Duration[2];
		
		reconnect(mm);
		
		DAPOQLRunnerGroovy runner = new DAPOQLRunnerGroovy();
		HashSet<Integer> dapoqlResultIds = new HashSet<>();
		Instant startDapoql = Instant.now();
		QueryResult outDapoql = runner.executeQuery(mm, this.dapoqlQuery, vars);
		int i = 0;
		for (Object o: outDapoql.result) {
			// Just to consume the data iterating and making sure all the resultset is retrieved
			if (o instanceof SLEXMMAbstractDatabaseObject) {
				dapoqlResultIds.add(((SLEXMMAbstractDatabaseObject) o).getId());
			}
			i++;
		}
		Instant endDapoql = Instant.now();
		
		dur[0] = Duration.between(startDapoql, endDapoql);
		
		System.out.println("DAPOQL:"+dur[0]);
		
		reconnect(mm);
		
		HashSet<Integer> sqlResultIds = new HashSet<>();
		Instant startSQL = Instant.now();
		SLEXMMSQLResultSet outSQL = mm.executeSQL(this.sqlQuery);
		SLEXMMSQLResult r = null;
		int colId = -1;
		int k = 0;
		while (colId < 0 && k < outSQL.getColumnNames().length) {
			if (outSQL.getColumnNames()[k].equals("id")) {
				colId = k;
			} else {
				k++;
			}
		}
		int j = 0;
		while ((r = outSQL.getNext()) != null) {
			// Just to consume the data iterating and making sure all the resultset is retrieved
			if (colId >= 0) {
				sqlResultIds.add(Integer.valueOf(r.getValues()[colId]));
			}
			j++;
		}
		outSQL.close();
		Instant endSQL = Instant.now();
		
		dur[1] = Duration.between(startSQL, endSQL);
		
		System.out.println("SQL   :"+dur[1]);
		
		reconnect(mm);
		
		assert dapoqlResultIds.equals(sqlResultIds);
		
		return dur;
	}
}
