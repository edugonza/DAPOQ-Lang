package org.processmining.database.metamodel.dapoql

import org.processmining.database.metamodel.dapoql.DAPOQLDSL.DAPOQLAttributeHolder
import org.processmining.openslex.metamodel.SLEXMMAttribute
import org.processmining.openslex.metamodel.SLEXMMAttributeValue
import org.processmining.openslex.metamodel.SLEXMMObjectVersion


class DAPOQLDelegate {

	Object o = null;
	Class type = null;
	DAPOQLFunctionsGroovy dapoqlfunc = null;
	
	def has(p) {
		return p ? true: false;
	}

	def getProperty(String propertyName) {
		if (propertyName == "at") {
			DAPOQLAttributeHolder dapoqlAtHolder = new DAPOQLAttributeHolder();
			dapoqlAtHolder.o = o;
			dapoqlAtHolder.type = type;
			return dapoqlAtHolder;
		} else {
			return o.getProperties().get(propertyName);
		}
	}

	def changed(Map args) {

		String attributeName = args.get("at");
		String fromV = args.get("from");
		String toV = args.get("to");

		if (attributeName != null) {

			if (attributeName.startsWith("at.")) {
				attributeName = attributeName.substring(3);
			}

			if ( type == SLEXMMObjectVersion && has(getProperty("at").getProperty(attributeName))) {
				SLEXMMObjectVersion ov = ((SLEXMMObjectVersion) o);
				SLEXMMAttributeValue atV = ov.getAttributeValue(attributeName);
				SLEXMMAttribute atmm = ov.getAttribute(attributeName);
				return this.dapoqlfunc.filterChangedOperation(ov,atmm,atV.getValue(),fromV,toV);
			}
		}

		return false;
	}
}