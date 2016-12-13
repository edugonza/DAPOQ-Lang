package org.processmining.database.metamodel.dapoql.ui.components;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class DAPOQLGroovyCompletionMetaData {

	private static DAPOQLGroovyCompletionMetaData _instance = null;

	private static final String AUTOCOMPLETE_DESCRIPTIONS_RESOURCE =
			"/org/processmining/database/metamodel/dapoql/resources/descriptions.json";
	private static final String AUTOCOMPLETE_SUMMARIES_RESOURCE =
			"/org/processmining/database/metamodel/dapoql/resources/summaries.json";

	private HashMap<String,String> descriptionMap = null;
	private HashMap<String,String> summaryMap = null;
	
	private DAPOQLGroovyCompletionMetaData() {
		init();
	}

	private void init() {
		InputStream descIS = this.getClass().getResourceAsStream(AUTOCOMPLETE_DESCRIPTIONS_RESOURCE);
		InputStream summIS = this.getClass().getResourceAsStream(AUTOCOMPLETE_SUMMARIES_RESOURCE);
		
		JsonReader descReader = new JsonReader(new InputStreamReader(descIS));
		JsonReader summReader = new JsonReader(new InputStreamReader(summIS));
		
		Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		Gson gson = new Gson();
		descriptionMap = gson.fromJson(descReader, type);
		summaryMap = gson.fromJson(summReader, type);
		
	}

	public static DAPOQLGroovyCompletionMetaData getInstance() {
		if (_instance == null) {
			_instance = new DAPOQLGroovyCompletionMetaData();
		}
		return _instance;
	}
	
	public Map<String,String> getDescriptionMap() {
		return getInstance().descriptionMap;
	}

	public Map<String,String> getSummaryMap() {
		return getInstance().summaryMap;
	}
	
}
