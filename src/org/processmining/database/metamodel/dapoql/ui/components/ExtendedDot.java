package org.processmining.database.metamodel.dapoql.ui.components;

import java.util.HashMap;

import org.processmining.plugins.graphviz.dot.Dot;

public class ExtendedDot extends Dot {
	
	private String stringValue = null;
	private HashMap<String,String> graphOptions = new HashMap<>();
	
	@Override
	public void setStringValue(String stringValue) {
		super.setStringValue(stringValue);
		this.stringValue = stringValue;
	}
	
	public void setGraphOption(String key, String value) {
		graphOptions.put(key, value);
	}
	
	@Override
	public String toString() {
		if (stringValue != null) {
			return stringValue;
		}

		StringBuilder result = new StringBuilder();
		result.append("digraph G {\n");

		if (isKeepOrderingOfChildren() || !graphOptions.isEmpty()) {
			result.append("graph [");
			boolean comma = false;
			if (isKeepOrderingOfChildren()) {
				result.append("ordering=\"out\"");
				comma = true;
			}
			for (String key : graphOptions.keySet()) {
				if (comma) {
					result.append(", ");
				}
				result.append(key);
				result.append("=\""+graphOptions.get(key)+"\"");
				comma = true;
			}
			
			result.append("];\n");
		}

		for (String key : getOptionKeySet()) {
			result.append(key + "=\"" + getOption(key) + "\";\n");
		}

		contentToString(result);

		result.append("}");

		return result.toString();
	}
}
