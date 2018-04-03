package org.processmining.database.metamodel.dapoql.test.benchmark;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.processmining.database.metamodel.dapoql.DAPOQLRunnerGroovy;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.database.metamodel.dapoql.QueryResult;
import org.processmining.openslex.metamodel.AbstractDBElement;
import org.processmining.openslex.metamodel.SLEXMMSQLResult;
import org.processmining.openslex.metamodel.SLEXMMSQLResultSet;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class ComparisonCase {
	
	private String dapoqlQuery = "";
	private String sqlQuery = "";
	Logger logger = Logger.getLogger(this.getClass().getName());
	
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
	
	private void reconnect(SLEXMMStorageMetaModel mm) throws Exception {
		mm.reconnect();
	}
	
	public static HashSet<Integer> runDAPOQL(SLEXMMStorageMetaModel mm, Set<DAPOQLVariable> vars, String query) throws Exception {
		HashSet<Integer> dapoqlResultIds = new HashSet<>();
		DAPOQLRunnerGroovy runner = new DAPOQLRunnerGroovy();
		Instant startDapoql = Instant.now();
		QueryResult outDapoql = runner.executeQuery(mm, query, vars);
		for (Object o : outDapoql.getResult()) {
			// Just to consume the data iterating and making sure all the resultset is
			// retrieved
			if (o instanceof AbstractDBElement) {
				dapoqlResultIds.add(((AbstractDBElement) o).getId());
			}
		}
		Instant endDapoql = Instant.now();
		Duration.between(startDapoql, endDapoql);
		return dapoqlResultIds;
	}
	
	public static HashSet<Integer> runSQL(SLEXMMStorageMetaModel mm, String query) throws Exception {
		HashSet<Integer> sqlResultIds = new HashSet<>();
		
		SLEXMMSQLResultSet outSQL = mm.executeSQL(query);
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
		while ((r = outSQL.getNext()) != null) {
			// Just to consume the data iterating and making sure all the resultset is retrieved
			if (colId >= 0) {
				sqlResultIds.add(Integer.valueOf(r.getValues()[colId]));
			}
		}
		outSQL.close();
		
		return sqlResultIds;
	}
	
	public Duration[] runCase(SLEXMMStorageMetaModel mm, Set<DAPOQLVariable> vars) throws Exception {
		Duration dur[] = new Duration[2];
				
		reconnect(mm);
		
		HashSet<Integer> dapoqlResultIds = new HashSet<>();
		
		if (getDapoqlQuery() != null) {

			Instant startDapoql = Instant.now();
			dapoqlResultIds = runDAPOQL(mm, vars, getDapoqlQuery());
			Instant endDapoql = Instant.now();

			dur[0] = Duration.between(startDapoql, endDapoql);

			logger.info("DAPOQL:" + dur[0]);

			reconnect(mm);

		}
		
		HashSet<Integer> sqlResultIds = new HashSet<>();
		
		if (getSqlQuery() != null) {
			Instant startSQL = Instant.now();
			sqlResultIds = runSQL(mm, getSqlQuery());
			Instant endSQL = Instant.now();
		
			dur[1] = Duration.between(startSQL, endSQL);
		
			logger.info("SQL   :"+dur[1]);
		
			reconnect(mm);
		}
		
		if (getDapoqlQuery() != null && getSqlQuery() != null) {
			assert dapoqlResultIds.equals(sqlResultIds);
		}
		
		return dur;
	}
}
