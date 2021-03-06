package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;

import org.processmining.database.metamodel.dapoql.DAPOQLRunnerGroovy;
import org.processmining.database.metamodel.dapoql.QueryResult;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class DAPOQLGroovyQueryPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5849304365480188323L;
	private int id = 0;
	private SLEXMMStorageMetaModel slxmm = null;
	
//	private JTable sqlResultTable = null;
//	private JTable detailsTable = null;
//	private JTextArea poqlQueryField = null;
	private DAPOQLGroovyQueryField queryPane = null;
	private JPanel resultsPanel = null;
	private JButton btnExecutePOQLQuery = null;
	
	private JProgressBar progressBar = null;
//	private JScrollPane scrollPane1 = null;
//	private JScrollPane scrollPane2 = null;
	
	private QueryThread queryThread = null;
	private boolean queryRunning = false;
	
	/**/
//	private static final String COMMIT_ACTION = "commit";
//	private static final String SHIFT_ACTION = "shift";
	
	private static final String EXECUTE_BUTTON_TEXT = "<html>Execute<br/>DAPOQL<br/>Query</html>";
	private static final String STOP_BUTTON_TEXT = "Stop DAPOQL Query";
	private static final String STOPPING_BUTTON_TEXT = "Stopping DAPOQL Query";
	
	public int getId() {
		return this.id;
	}
	
	public DAPOQLGroovyQueryPanel(SLEXMMStorageMetaModel mm, int id) {
		super();
		
		this.id = id;
		try {
			this.slxmm = new SLEXMMStorageMetaModelImpl(mm.getPath(),mm.getFilename());
		} catch (Exception e) {
			e.printStackTrace();
			this.slxmm = mm;
		}
		
		this.setLayout(new BorderLayout(0, 0));

		JPanel sqlQueryPanel = new JPanel();
		//this.add(sqlQueryPanel, BorderLayout.NORTH);

		queryPane = new DAPOQLGroovyQueryField();
		resultsPanel = new JPanel(new BorderLayout());
		
//		poqlQueryField = new JTextArea(5, 0);
//		JScrollPane scrollQueryPane = new JScrollPane(poqlQueryField);
		
		// Without this, cursor always leaves text field
//		poqlQueryField.setFocusTraversalKeysEnabled(false);
//		Autocomplete autoComplete = new Autocomplete(poqlQueryField, new ArrayList<String>());
//		poqlQueryField.getDocument().addDocumentListener(autoComplete);
		// Maps the tab key to the commit action, which finishes the
		// autocomplete
		// when given a suggestion
//		poqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("TAB"),SHIFT_ACTION);
//		poqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),COMMIT_ACTION);
//		poqlQueryField.getActionMap().put(COMMIT_ACTION,autoComplete.new CommitAction());
//		poqlQueryField.getActionMap().put(SHIFT_ACTION,autoComplete.new ShiftAction());

		btnExecutePOQLQuery = new JButton(EXECUTE_BUTTON_TEXT);
		sqlQueryPanel.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		
		sqlQueryPanel.add(queryPane, BorderLayout.CENTER);
		sqlQueryPanel.add(btnExecutePOQLQuery, BorderLayout.EAST);
		sqlQueryPanel.add(progressBar, BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setTopComponent(sqlQueryPanel);

//		scrollPane1 = new JScrollPane();
		//this.add(scrollPane1, BorderLayout.CENTER);
		
//		splitPane.setBottomComponent(scrollPane1);
		
		splitPane.setBottomComponent(resultsPanel);

//		sqlResultTable = new JTable();
//		sqlResultTable.setFillsViewportHeight(true);
//		scrollPane1.setViewportView(sqlResultTable);
		
//		scrollPane2 = new JScrollPane();
//		this.add(scrollPane2, BorderLayout.SOUTH);

//		detailsTable = new JTable();
//		detailsTable.setFillsViewportHeight(true);
//		scrollPane2.setViewportView(detailsTable);
//		scrollPane2.setPreferredSize(new Dimension(0, 250));
		
//		scrollPane2.setVisible(false);
		
		btnExecutePOQLQuery.addActionListener(new ExecutePOQLQueryAction());
		
	}
	
	private void setMessage(String msg) {
		JLabel msgLabel = new JLabel();
		msgLabel.setText(msg);
		resultsPanel.removeAll();
		resultsPanel.add(msgLabel);
	}
	
//	private void setTable(JTable table) {
//		scrollPane1.setViewportView(table);
//	}
	
//	private void setSelectionListener(final Class type) {
//		sqlResultTable.setSelectionModel(new DefaultListSelectionModel());
//		if (type == SLEXMMEvent.class) {
//			sqlResultTable.getSelectionModel().addListSelectionListener(new EventSelectionListener());
//		} else if (type == SLEXMMObjectVersion.class) {
//			sqlResultTable.getSelectionModel().addListSelectionListener(new ObjectVersionSelectionListener());
//		}
//	}
	
	public boolean isQueryRunning() {
		return this.queryRunning;
	}
	
	public void killQuery() {
		if (queryThread != null) {
			btnExecutePOQLQuery.setEnabled(false);
			btnExecutePOQLQuery.setText(STOPPING_BUTTON_TEXT);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					queryThread.stopThread();
					queryPane.setEnabled(true);
					progressBar.setIndeterminate(false);
					queryRunning = false;
					btnExecutePOQLQuery.setText(EXECUTE_BUTTON_TEXT);
					btnExecutePOQLQuery.setEnabled(true);
					try {
						slxmm = new SLEXMMStorageMetaModelImpl(slxmm.getPath(), slxmm.getFilename());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
//	private class EventSelectionListener implements ListSelectionListener {
//		@Override
//		public void valueChanged(ListSelectionEvent e) {
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					Integer selected = MetaModelTableUtils.getSelectedEvent(sqlResultTable);
//					if (selected != null) {
//						SLEXMMEvent ev = slxmm.getEventForId(selected);
//						try {
//							MetaModelTableUtils.setEventAttributesTableContent(detailsTable,
//									ev.getAttributeValues(),ev.getLifecycle(),ev.getResource(),
//									String.valueOf(ev.getTimestamp()));
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//					}
//				}
//			}).start();
//		}
//	}
	
//	private class ObjectVersionSelectionListener implements ListSelectionListener {
//		@Override
//		public void valueChanged(ListSelectionEvent e) {
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					Integer selected = MetaModelTableUtils.getSelectedEvent(sqlResultTable);
//					if (selected != null) {
//						HashMap<SLEXMMAttribute, SLEXMMAttributeValue> atts =
//								slxmm.getAttributeValuesForObjectVersion(selected);
//						try {
//							MetaModelTableUtils.setObjectVersionAttributesTableContent(detailsTable,atts);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//					}
//				}
//			}).start();
//		}
//	}
	
	public class ExecutePOQLQueryAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if (queryRunning) {
				
				killQuery();
				
			} else {
			
				QueryThread queryPOQLThread = new QueryThread();
			
				queryThread = queryPOQLThread;
			
				queryPOQLThread.start();
			}
		}
	}
	
	private class QueryThread extends Thread {
		
		DAPOQLRunnerGroovy runner = null;
		
		private void stopThread() {
			if (runner != null) {
				runner.cancel();
				this.interrupt();
			}
		}
		
		@Override
		public void run() {
			
			queryRunning = true;
			
			try {
				queryPane.setEnabled(false);
				//btnExecutePOQLQuery.setEnabled(false);
				btnExecutePOQLQuery.setText(STOP_BUTTON_TEXT);
				progressBar.setIndeterminate(true);
				String query = queryPane.getQuery();
				runner = new DAPOQLRunnerGroovy();
				QueryResult qr = runner.executeQuery(slxmm, query, null);
				
				resultsPanel.removeAll();
				resultsPanel.add(new DAPOQLResultsPanel(slxmm, qr.getType(), qr.getResult()));
//				setTable(sqlResultTable);
//				setSelectionListener(qr.type);
//				scrollPane2.setVisible(false);
//				
//				if (qr.type == SLEXMMObject.class) {
//					MetaModelTableUtils.setObjectsTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMObjectVersion.class) {
//					MetaModelTableUtils.setObjectVersionsTableContent(sqlResultTable, qr.result);
//					scrollPane2.setVisible(true);
//				} else if (qr.type == SLEXMMEvent.class) {
//					MetaModelTableUtils.setEventsTableContent(sqlResultTable, qr.result, null);
//					scrollPane2.setVisible(true);
//				} else if (qr.type == SLEXMMActivity.class) {
//					MetaModelTableUtils.setActivitiesTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMCase.class) {
//					MetaModelTableUtils.setCasesTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMActivityInstance.class) {
//					MetaModelTableUtils.setActivityInstancesTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMClass.class) {
//					MetaModelTableUtils.setClassesTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMRelation.class) {
//					MetaModelTableUtils.setObjectRelationsTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMRelationship.class) {
//					MetaModelTableUtils.setRelationshipsTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMAttribute.class) {
//					MetaModelTableUtils.setAttributesTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMPeriod.class) {
//					MetaModelTableUtils.setPeriodsTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMDataModel.class) {
//					MetaModelTableUtils.setDatamodelsTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMLog.class) {
//					MetaModelTableUtils.setLogsTableContent(sqlResultTable, qr.result);
//				} else if (qr.type == SLEXMMProcess.class) {
//					MetaModelTableUtils.setProcessesTableContent(sqlResultTable, qr.result);
//				} else {
//					String msg = "ERROR: Unknown type of result "+qr.type;
//					System.err.println(msg);
//					setMessage(msg);
//				}
				
//				revalidate();
			
			} catch (Exception e) {
				e.printStackTrace();
				String msg = e.getMessage();
				if (msg == null) {
					msg = e.toString();
				}
				setMessage(msg);
			} finally {
				queryPane.setEnabled(true);
				//btnExecutePOQLQuery.setEnabled(true);
				progressBar.setIndeterminate(false);
				queryRunning = false;
				btnExecutePOQLQuery.setText(EXECUTE_BUTTON_TEXT);
			}
		}
	}
}
