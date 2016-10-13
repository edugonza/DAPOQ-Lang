package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.TemplateCompletion;
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
	// private Console console = null;

//	private static final String COMMIT_ACTION = "commit";
//	private static final String SHIFT_ACTION = "shift";

	public DAPOQLGroovyQueryField() {
		super();

		this.setLayout(new BorderLayout(0, 0));

		JPanel sqlQueryPanel = new JPanel();

		sqlQueryPanel.setLayout(new BorderLayout(0, 0));

		// console = new Console();
		// console.run();
		//
		// sqlQueryPanel.add(console.getFrame().getRootPane(),BorderLayout.CENTER);

		dapoqlQueryField = new RSyntaxTextArea(20, 60);
		dapoqlQueryField.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		dapoqlQueryField.setCodeFoldingEnabled(true);

		CompletionProvider provider = createCompletionProvider();

		// An AutoCompletion acts as a "middle-man" between a text component
		// and a CompletionProvider. It manages any options associated with
		// the auto-completion (the popup trigger key, whether to display a
		// documentation window along with completion choices, etc.). Unlike
		// CompletionProviders, instances of AutoCompletion cannot be shared
		// among multiple text components.
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setParameterAssistanceEnabled(true);
		ac.install(dapoqlQueryField);

		// RTextScrollPane sp = new RTextScrollPane(dapoqlQueryField);
		RTextScrollPane sp = new RTextScrollPane();
		sp.setViewportView(dapoqlQueryField);
		sp.setLineNumbersEnabled(true);
		sqlQueryPanel.add(sp, BorderLayout.CENTER);

		// dapoqlQueryField = new JTextArea(5, 0);
		// JScrollPane scrollQueryPane = new JScrollPane(dapoqlQueryField);
		//
		// // Without this, cursor always leaves text field
		// dapoqlQueryField.setFocusTraversalKeysEnabled(false);
		// Autocomplete autoComplete = new Autocomplete(dapoqlQueryField, new
		// ArrayList<String>());
		// dapoqlQueryField.getDocument().addDocumentListener(autoComplete);
		// // Maps the tab key to the commit action, which finishes the
		// // autocomplete
		// // when given a suggestion
		// dapoqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("TAB"),SHIFT_ACTION);
		// dapoqlQueryField.getInputMap().put(KeyStroke.getKeyStroke("SPACE"),COMMIT_ACTION);
		// dapoqlQueryField.getActionMap().put(COMMIT_ACTION,autoComplete.new
		// CommitAction());
		// dapoqlQueryField.getActionMap().put(SHIFT_ACTION,autoComplete.new
		// ShiftAction());
		//
		// sqlQueryPanel.setLayout(new BorderLayout(0, 0));
		//
		// sqlQueryPanel.add(scrollQueryPane, BorderLayout.CENTER);
		//
		add(sqlQueryPanel, BorderLayout.CENTER);
		//
	}

	/**
	 * Create a simple provider that adds some Java-related completions.
	 */
	private CompletionProvider createCompletionProvider() {

		DefaultCompletionProvider provider = new DefaultCompletionProvider();

		provider.addCompletion(new BasicCompletion(provider, "allClasses()"));
		provider.addCompletion(new BasicCompletion(provider, "allRelationships()"));
		provider.addCompletion(new BasicCompletion(provider, "allAttributes()"));
		provider.addCompletion(new BasicCompletion(provider, "allObjects()"));
		provider.addCompletion(new BasicCompletion(provider, "allVersions()"));
		provider.addCompletion(new BasicCompletion(provider, "allRelations()"));
		provider.addCompletion(new BasicCompletion(provider, "allEvents()"));
		provider.addCompletion(new BasicCompletion(provider, "allActivityInstances()"));
		provider.addCompletion(new BasicCompletion(provider, "allCases()"));
		provider.addCompletion(new BasicCompletion(provider, "allLogs()"));
		provider.addCompletion(new BasicCompletion(provider, "allActivities()"));
		provider.addCompletion(new BasicCompletion(provider, "allProcesses()"));

		provider.addCompletion(new TemplateCompletion(provider, "classesOf", "classesOf", "classesOf(${cursor})"));

		provider.addCompletion(new TemplateCompletion(provider, ".where{", "where", ".where{ ${cursor} }"));
		
		provider.addCompletion(new TemplateCompletion(provider, "loop", "A loop",
				"for (int ${i} = 0; ${i} &lt; ${array}.length; ${i}++) {\n ${cursor}\n }"));

		// Shorthand completions don't require the input text to be the same
		// thing as the replacement text.
		provider.addCompletion(
				new ShorthandCompletion(provider, "sysout", "System.out.println(", "System.out.println("));
		provider.addCompletion(
				new ShorthandCompletion(provider, "syserr", "System.err.println(", "System.err.println("));

		provider.addCompletion(new TemplateCompletion(provider, "changed", "changed",
				"changed([at: \"${attribute}\", from: \"${from}\",to: \"${to}\"])${cursor}"));

		return provider;

	}

	public String getQuery() {
		return this.dapoqlQueryField.getText();
		// return console.getInputArea().getText();
	}

	public void setQuery(String query) {
		this.dapoqlQueryField.setText(query);
		// console.getInputArea().setText(query);
	}

}
