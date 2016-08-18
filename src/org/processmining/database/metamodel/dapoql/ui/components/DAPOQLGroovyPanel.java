package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMDataModelResultSet;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class DAPOQLGroovyPanel extends JPanel {

	private int poqlTabCounter = 0;
	private SLEXMMStorageMetaModel slxmm = null;
	
	private int incPOQLQueryTabCounter() {
		poqlTabCounter++;
		return poqlTabCounter;
	}
	
	private SLEXMMStorageMetaModel getMetaModel() {
		return this.slxmm;
	}
	
	private SLEXMMDataModel getDataModel() {
		SLEXMMDataModelResultSet dmrset = getMetaModel().getDataModels();

		SLEXMMDataModel dm = dmrset.getNext();

		if (dm != null) {
			return dm;
		} else {
			return null;
		}
	}
	
	public DAPOQLGroovyPanel(SLEXMMStorageMetaModel mm) {
		
		this.slxmm = mm;
		
		JPanel poqlTab = this;
		
		poqlTab.setLayout(new BorderLayout(0, 0));

		JSplitPane poqlSplitPane = new JSplitPane();
		poqlSplitPane.setResizeWeight(0.5);
		poqlTab.add(poqlSplitPane);

		final JTabbedPane poqlQueryTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		poqlSplitPane.setRightComponent(poqlQueryTabbedPane);

		DAPOQLGroovyQueryPanel poqlQueryRightPanel = new DAPOQLGroovyQueryPanel(getMetaModel(),incPOQLQueryTabCounter());
		
		poqlQueryTabbedPane.addTab("Query "+poqlQueryRightPanel.getId(), poqlQueryRightPanel);
		
		poqlQueryTabbedPane.addTab("+", new JLabel());
		poqlQueryTabbedPane.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            if (poqlQueryTabbedPane.getSelectedComponent() instanceof JLabel) {
					if (!e.isControlDown()) {
						int count = poqlQueryTabbedPane.getTabCount();
						DAPOQLGroovyQueryPanel newTab = new DAPOQLGroovyQueryPanel(getMetaModel(),incPOQLQueryTabCounter());
						poqlQueryTabbedPane.add(newTab, count - 1);
						poqlQueryTabbedPane.setTitleAt(count - 1, "Query "
								+ newTab.getId());
						poqlQueryTabbedPane.setSelectedComponent(newTab);
					}
	            } else if (e.isControlDown()) {
	            	int selected = poqlQueryTabbedPane.getSelectedIndex();
	            	DAPOQLGroovyQueryPanel poqlquerypanel = (DAPOQLGroovyQueryPanel) poqlQueryTabbedPane.getComponentAt(selected);
	            	if (poqlquerypanel.isQueryRunning()) {
	            		AskYesNoDialog dialog = new AskYesNoDialog(poqlquerypanel, "A query is running in this Tab. Do you want to kill it?");
	            		if (dialog.showDialog()) {
	            			poqlquerypanel.killQuery();
	            			poqlQueryTabbedPane.remove(selected);
	            		}
	            	} else {
	            		poqlQueryTabbedPane.remove(selected);
	            	}
	            }
	        }
	        
	    });
		
		JTabbedPane diagramsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		poqlSplitPane.setLeftComponent(diagramsTabbedPane);
		
		SLEXMMDataModel dm = getDataModel();
		//DataModel mm = getMetaModelDataModel(); // FIXME
		
		DiagramComponent dmDiagram = new DiagramComponent(null);
		DiagramComponent mmDiagram = new DiagramComponent(null);
		dmDiagram.setDataModel(dm);
		//mmDiagram.setDataModel(mm); // FIXME
		
		diagramsTabbedPane.addTab("MetaModel", mmDiagram);
		diagramsTabbedPane.addTab("DataModel", dmDiagram);
		
	}
}
