package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class DAPOQLQueryField extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3010858285848810915L;
	
	private JTextArea dapoqlQueryField = null;
	
	private static final String COMMIT_ACTION = "commit";
	private static final String SHIFT_ACTION = "shift";
	
	public DAPOQLQueryField() {
		super();
		
		this.setLayout(new BorderLayout(0, 0));

		JPanel sqlQueryPanel = new JPanel();
		
		dapoqlQueryField = new JTextArea(5, 0);
		JScrollPane scrollQueryPane = new JScrollPane(dapoqlQueryField);
		
		// Without this, cursor always leaves text field
		dapoqlQueryField.setFocusTraversalKeysEnabled(false);
		Autocomplete autoComplete = new Autocomplete(dapoqlQueryField, new ArrayList<String>());
		dapoqlQueryField.getDocument().addDocumentListener(autoComplete);
		// Maps the tab key to the commit action, which finishes the
		// autocomplete
		// when given a suggestion
		dapoqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("TAB"),SHIFT_ACTION);
		dapoqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),COMMIT_ACTION);
		dapoqlQueryField.getActionMap().put(COMMIT_ACTION,autoComplete.new CommitAction());
		dapoqlQueryField.getActionMap().put(SHIFT_ACTION,autoComplete.new ShiftAction());

		sqlQueryPanel.setLayout(new BorderLayout(0, 0));

		sqlQueryPanel.add(scrollQueryPane, BorderLayout.CENTER);
		
		add(sqlQueryPanel,BorderLayout.CENTER);
		
	}

	public String getQuery() {
		return dapoqlQueryField.getText();
	}
	
	public void setQuery(String query) {
		this.dapoqlQueryField.setText(query);
	}
	
}
