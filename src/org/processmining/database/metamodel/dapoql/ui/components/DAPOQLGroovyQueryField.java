package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

//import groovy.ui.Console;

public class DAPOQLGroovyQueryField extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3010858285848810915L;
	
	private RSyntaxTextArea dapoqlQueryField = null;
//	private Console console = null;
	
	private static final String COMMIT_ACTION = "commit";
	private static final String SHIFT_ACTION = "shift";
	
	public DAPOQLGroovyQueryField() {
		super();
		
		this.setLayout(new BorderLayout(0, 0));

		JPanel sqlQueryPanel = new JPanel();
		
		sqlQueryPanel.setLayout(new BorderLayout(0, 0));
		
//		console = new Console();
//		console.run();
//		
//		sqlQueryPanel.add(console.getFrame().getRootPane(),BorderLayout.CENTER);
		
		dapoqlQueryField = new RSyntaxTextArea(20, 60);
		dapoqlQueryField.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		dapoqlQueryField.setCodeFoldingEnabled(true);
	    //RTextScrollPane sp = new RTextScrollPane(dapoqlQueryField);
	    RTextScrollPane sp = new RTextScrollPane();
	    sp.setViewportView(dapoqlQueryField);
	    sqlQueryPanel.add(sp, BorderLayout.CENTER);
				
//		dapoqlQueryField = new JTextArea(5, 0);
//		JScrollPane scrollQueryPane = new JScrollPane(dapoqlQueryField);
//		
//		// Without this, cursor always leaves text field
//		dapoqlQueryField.setFocusTraversalKeysEnabled(false);
//		Autocomplete autoComplete = new Autocomplete(dapoqlQueryField, new ArrayList<String>());
//		dapoqlQueryField.getDocument().addDocumentListener(autoComplete);
//		// Maps the tab key to the commit action, which finishes the
//		// autocomplete
//		// when given a suggestion
//		dapoqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("TAB"),SHIFT_ACTION);
//		dapoqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),COMMIT_ACTION);
//		dapoqlQueryField.getActionMap().put(COMMIT_ACTION,autoComplete.new CommitAction());
//		dapoqlQueryField.getActionMap().put(SHIFT_ACTION,autoComplete.new ShiftAction());
//
//		sqlQueryPanel.setLayout(new BorderLayout(0, 0));
//
//		sqlQueryPanel.add(scrollQueryPane, BorderLayout.CENTER);
//		
		add(sqlQueryPanel,BorderLayout.CENTER);
//		
	}

	public String getQuery() {
		return this.dapoqlQueryField.getText();
//		return console.getInputArea().getText();
	}
	
	public void setQuery(String query) {
		this.dapoqlQueryField.setText(query);
//		console.getInputArea().setText(query);
	}
	
}
