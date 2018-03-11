package org.processmining.database.metamodel.dapoql.ui.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;

public class DateRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3036933261008989820L;
	private SimpleDateFormat sdfNewValue = new SimpleDateFormat("EE MMM dd hh:mm:ss z yyyy");
	private String valueToString = "";

	@Override
	public void setValue(Object value) {
	    if ((value != null)) {
//			String stringFormat = value.toString();
//			try {
//				dateValue = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse(stringFormat);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
	    	
	    	if (value instanceof Date) {
	    		valueToString = sdfNewValue.format(value);
	    	} else {
	    		valueToString = "";
	    	}
	        value = valueToString;
	    }
	    super.setValue(value);
	}
}