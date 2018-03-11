package org.processmining.database.metamodel.dapoql.ui.components;

import java.util.List;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGElement;
import com.kitfox.svg.SVGElementException;
import com.kitfox.svg.SVGRoot;
import com.kitfox.svg.animation.AnimationElement;
import com.kitfox.svg.xml.StyleAttribute;

public class ExtendedDotPanel extends DotPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5191962299852004580L;

	public ExtendedDotPanel(Dot dot) {
		super(dot);
	}

	@Override
	public void changeDot(Dot dot, boolean resetView) {
		super.changeDot(dot, resetView);
		
		SVGDiagram svgDiag = getSVG();
		SVGRoot root = svgDiag.getRoot();

		recursiveChangeFontSize(root);

		changeDot(dot, svgDiag, true);
	}
	
	private void recursiveChangeFontSize(SVGElement root) {
		@SuppressWarnings("unchecked")
		List<SVGElement> listEls = root.getChildren(null);

		for (SVGElement element : listEls) {

			recursiveChangeFontSize(element);

			if (element.getTagName().equalsIgnoreCase("text")) {
				try {
					if (element.hasAttribute("font-size", AnimationElement.AT_XML)) {
						int newsize = 8;
						int size = 16;
						StyleAttribute atval = element.getPresAbsolute("font-size");
						try {
							size = atval.getIntValue();
						} catch (Exception e) {
							size = 16;
						}
						if (size > 6) {
							newsize = size - 6;
						}
						element.setAttribute("font-size", AnimationElement.AT_XML, String.valueOf(newsize));
					} else {
						element.setAttribute("font-size", AnimationElement.AT_XML, "8");
					}
				} catch (SVGElementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
