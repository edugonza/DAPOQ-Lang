package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.processmining.openslex.metamodel.SLEXMMSQLResult;
import org.processmining.openslex.metamodel.SLEXMMSQLResultSet;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModelImpl;

public class SQLQueryPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5849304365480188323L;
	private int id = 0;
	private SLEXMMStorageMetaModel slxmm = null;
	
	private JTable sqlResultTable = null;
	private JTextArea sqlQueryField = null;
	private JButton btnExecuteSQLQuery = null;
	
	private JScrollPane scrollPane = null;
	private JProgressBar progressBar = null;
	
	private QueryThread queryThread = null;
	private boolean queryRunning = false;
	
	private static final String EXECUTE_BUTTON_TEXT = "Execute SQL Query";
	private static final String STOP_BUTTON_TEXT = "Stop SQL Query";
	private static final String STOPPING_BUTTON_TEXT = "Stopping SQL Query";
	
	public int getId() {
		return this.id;
	}
	
	public SQLQueryPanel(SLEXMMStorageMetaModel mm, int id) {
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
		this.add(sqlQueryPanel, BorderLayout.NORTH);

		sqlQueryField = new JTextArea(5,0);
		JScrollPane scrollQueryPane = new JScrollPane(sqlQueryField);

		btnExecuteSQLQuery = new JButton("Execute SQL Query");
		sqlQueryPanel.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		
		sqlQueryPanel.add(scrollQueryPane, BorderLayout.CENTER);
		sqlQueryPanel.add(btnExecuteSQLQuery, BorderLayout.EAST);
		sqlQueryPanel.add(progressBar, BorderLayout.SOUTH);

		scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);

		sqlResultTable = new JTable();
		sqlResultTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(sqlResultTable);
		
		btnExecuteSQLQuery.addActionListener(new ExecuteSQLQueryAction());
		
	}
	
	private void setMessage(String msg) {
		JLabel msgLabel = new JLabel();
		msgLabel.setText(msg);
		scrollPane.setViewportView(msgLabel);
	}
	
	private void setTable(JTable table) {
		scrollPane.setViewportView(table);
	}
	
	public boolean isQueryRunning() {
		return queryRunning;
	}
	
	public void killQuery() {
		if (queryThread != null) {
			btnExecuteSQLQuery.setEnabled(false);
			btnExecuteSQLQuery.setText(STOPPING_BUTTON_TEXT);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					queryThread.stopThread();
					sqlQueryField.setEnabled(true);
					progressBar.setIndeterminate(false);
					try {
						slxmm = new SLEXMMStorageMetaModelImpl(slxmm.getPath(), slxmm.getFilename());
					} catch (Exception e) {
						e.printStackTrace();
					}
					queryRunning = false;
					btnExecuteSQLQuery.setText(EXECUTE_BUTTON_TEXT);
					btnExecuteSQLQuery.setEnabled(true);
				}
			}).start();
		}
	}
	
	public class ExecuteSQLQueryAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if (queryRunning) {
				
				killQuery();
				
			} else {
			
				QueryThread querySQLThread = new QueryThread();
				queryThread = querySQLThread;
				querySQLThread.start();
				
			}
		}
	}
	
	private class QueryThread extends Thread {
		
		private void stopThread() {
			if (slxmm != null) {
				slxmm.abort();
				this.interrupt();
			}
		}
		
		@Override
		public void run() {
			
			queryRunning = true;
			
			String query = SQLQueryPanel.this.sqlQueryField.getText();
			
			try {
				sqlQueryField.setEnabled(false);
				//btnExecuteSQLQuery.setEnabled(false);
				btnExecuteSQLQuery.setText(STOP_BUTTON_TEXT);
				progressBar.setIndeterminate(true);
			
				SLEXMMSQLResultSet rset = SQLQueryPanel.this.slxmm
						.executeSQL(query);

				if (rset != null) {
					SLEXMMSQLResult r = null;
					
					final DefaultTableModel model = new DefaultTableModel(rset.getColumnNames(), rset.getRowCount());
					
					setTable(sqlResultTable);

					while ((r = rset.getNext()) != null) {
						model.addRow(r.getValues());
					}
					
					SwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							sqlResultTable.setModel(model);
						}
					});
					
				} else {
					System.out.println("No Values");
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error executing sql query: "
						+ query);
				String msg = e.getMessage();
				if (msg == null) {
					msg = e.toString();
				}
				setMessage(msg);
			} finally {
				sqlQueryField.setEnabled(true);
				//btnExecuteSQLQuery.setEnabled(true);
				progressBar.setIndeterminate(false);
				btnExecuteSQLQuery.setText(EXECUTE_BUTTON_TEXT);
				queryRunning = false;
			}
		}
	}
}