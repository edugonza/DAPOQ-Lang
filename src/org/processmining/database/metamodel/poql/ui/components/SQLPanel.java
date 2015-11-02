package org.processmining.database.metamodel.poql.ui.components;

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
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class SQLPanel extends JPanel {

	private int sqlTabCounter = 0;
	SLEXMMStorageMetaModel slxmm = null;
	
	private int incSQLQueryTabCounter() {
		sqlTabCounter++;
		return sqlTabCounter;
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
	
	public SQLPanel(SLEXMMStorageMetaModel mm) {
		
		this.slxmm = mm;
		
		JPanel sqlTab = this;
		
		sqlTab.setLayout(new BorderLayout(0, 0));

		JSplitPane sqlSplitPane = new JSplitPane();
		sqlSplitPane.setResizeWeight(0.5);
		sqlTab.add(sqlSplitPane);

		final JTabbedPane sqlQueryTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		sqlSplitPane.setRightComponent(sqlQueryTabbedPane);

		SQLQueryPanel sqlQueryRightPanel = new SQLQueryPanel(getMetaModel(),incSQLQueryTabCounter());
		
		sqlQueryTabbedPane.addTab("Query "+sqlQueryRightPanel.getId(), sqlQueryRightPanel);
		
		sqlQueryTabbedPane.addTab("+", new JLabel());
		sqlQueryTabbedPane.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            if (sqlQueryTabbedPane.getSelectedComponent() instanceof JLabel) {
					if (!e.isControlDown()) {
						int count = sqlQueryTabbedPane.getTabCount();
						SQLQueryPanel newTab = new SQLQueryPanel(getMetaModel(),incSQLQueryTabCounter());
						sqlQueryTabbedPane.add(newTab, count - 1);
						sqlQueryTabbedPane.setTitleAt(count - 1, "Query "
								+ newTab.getId());
						sqlQueryTabbedPane.setSelectedComponent(newTab);
					}
	            } else if (e.isControlDown()) {
	            	int selected = sqlQueryTabbedPane.getSelectedIndex();
	            	SQLQueryPanel sqlquerypanel = (SQLQueryPanel) sqlQueryTabbedPane.getComponentAt(selected);
	            	if (sqlquerypanel.isQueryRunning()) {
	            		AskYesNoDialog dialog = new AskYesNoDialog(sqlquerypanel, "A query is running in this Tab. Do you want to kill it?");
	            		if (dialog.showDialog()) {
	            			sqlquerypanel.killQuery();
	            			sqlQueryTabbedPane.remove(selected);
	            		}
	            	} else {
	            		sqlQueryTabbedPane.remove(selected);
	            	}
	            	
	            }
	        }
	        
	    });
		
		JTabbedPane diagramsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sqlSplitPane.setLeftComponent(diagramsTabbedPane);
		
		SLEXMMDataModel dm = getDataModel();
		//DataModel mm = getMetaModelDataModel(); // FIXME
		
		DiagramComponent dmDiagram = new DiagramComponent(null);
		DiagramComponent mmDiagram = new DiagramComponent(null);
		dmDiagram.setDataModel(dm);
		// mmDiagram.setDataModel(mm); // FIXME
		
		diagramsTabbedPane.addTab("MetaModel", mmDiagram);
		diagramsTabbedPane.addTab("DataModel", dmDiagram);
		
	}
}
