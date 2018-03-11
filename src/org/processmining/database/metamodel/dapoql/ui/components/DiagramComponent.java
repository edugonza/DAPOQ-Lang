package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMClassResultSet;
import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMRelationship;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.listeners.DotElementSelectionListener;

import com.kitfox.svg.SVGDiagram;

public class DiagramComponent extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5543858873672796408L;

	private HashMap<String, SLEXMMClass> nodeIdToClassMap = new HashMap<>();
	private HashMap<Integer, DotNode> classToNodeMap = new HashMap<>();

	private NodeSelectionHandler nodeSelectionHandler;

	private Dot dot = null;
	private ExtendedDotPanel dotpanel = null;
	private JScrollPane scrollPane = null;

	public DiagramComponent(NodeSelectionHandler handler) {
		super(new BorderLayout(0, 0));
		nodeSelectionHandler = handler;

		JButton maxButton = new JButton("Maximize");
		this.add(maxButton, BorderLayout.NORTH);

		maxButton.addActionListener(new MaxButtonListener());

		scrollPane = new JScrollPane();

		this.add(scrollPane, BorderLayout.CENTER);

		init();
	}

	private class MaxButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFrame win = new JFrame();
			win.setSize(300, 300);
			win.setLocationRelativeTo(DiagramComponent.this);
			win.add(dotpanel);
			win.setVisible(true);
			win.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowClosing(WindowEvent e) {
					scrollPane.setViewportView(dotpanel);
					DiagramComponent.this.revalidate();
				}

				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub
				}
			});
		}
	}

	private void init() {
		dot = new Dot();
		dotpanel = new ExtendedDotPanel(dot);
		scrollPane.setViewportView(dotpanel);
	}

	private class NodeElementSelectionListener implements DotElementSelectionListener {

		@Override
		public void selected(DotElement element, SVGDiagram image) {
			String id = element.getId();
			SLEXMMClass cl = nodeIdToClassMap.get(id);
			if (cl != null) {
				if (nodeSelectionHandler != null) {
					nodeSelectionHandler.run(cl);
				}
			}
		}

		@Override
		public void deselected(DotElement element, SVGDiagram image) {

		}

	}

	public void setDataModel(SLEXMMDataModel dm) {

		init();

		if (dm == null) {
			return;
		}

		dot.setGraphOption("splines", "spline");
		dot.setGraphOption("nodesep", "1");
		dot.setGraphOption("bgcolor", "gainsboro");

		SLEXMMStorageMetaModel mmstrg = dm.getStorage();

		nodeIdToClassMap = new HashMap<>();
		classToNodeMap = new HashMap<>();

		SLEXMMClassResultSet crset = mmstrg.getClassesForDatamodels(new int[] { dm.getId() });
		SLEXMMClass c = null;

		NodeElementSelectionListener listener = new NodeElementSelectionListener();

		while ((c = crset.getNext()) != null) {
			String name = c.getName() + " (" + c.getId() + ")";
			DotNode node = dot.addNode(name);
			node.setOption("style", "filled");
			node.setOption("shape", "none");
			node.setOption("fontname", "Monospaced");
			node.setOption("fontcolor", "black");
			node.setOption("fontsize", "16");
			node.setOption("margin", "0.02,0.02");
			node.setOption("fillcolor", "black");

			classToNodeMap.put(c.getId(), node);
			nodeIdToClassMap.put(node.getId(), c);
			node.addSelectionListener(listener);

			StringBuilder label = new StringBuilder(
					"<<TABLE><TR><TD bgcolor=\"blue\"><font color=\"white\">" + name + "</font></TD></TR>");

			List<SLEXMMAttribute> attrs = mmstrg.getListAttributesForClass(c);
			//int i = 1;
			for (SLEXMMAttribute at : attrs) {
				label.append("<TR><TD align=\"left\" bgcolor=\"white\" ><font color=\"black\"> (" + at.getId() + ") "
						+ at.getName() + "</font></TD></TR>");
				//i++;
			}

			label.append("</TABLE>>");
			node.setLabel(label.toString());
		}

		crset = mmstrg.getClassesForDatamodels(new int[] { dm.getId() });
		c = null;

		while ((c = crset.getNext()) != null) {
			List<SLEXMMRelationship> rels = mmstrg.getRelationshipsForClass(c);

			for (SLEXMMRelationship rel : rels) {

				DotNode source = classToNodeMap.get(rel.getSourceClassId());
				DotNode target = classToNodeMap.get(rel.getTargetClassId());

				if (rel.getSourceClassId() == c.getId() && target != null) {

					DotEdge edge = dot.addEdge(source, target, rel.getName() + " (" + rel.getId() + ")");
					edge.setOption("fontname", "Monospaced");
					edge.setOption("fontcolor", "red");
					edge.setOption("fontsize", "16");
					edge.setOption("margin", "1,0.055");
				}

			}
		}

		dotpanel.changeDot(dot, true);

		revalidate();

	}
}
