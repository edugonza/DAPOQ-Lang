package org.processmining.database.metamodel.dapoql.ui.components;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
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
		dapoqlQueryField.setAutoIndentEnabled(true);
		dapoqlQueryField.setAnimateBracketMatching(true);
		dapoqlQueryField.setAntiAliasingEnabled(true);
		dapoqlQueryField.setBracketMatchingEnabled(true);

		CompletionProvider provider = createCompletionProvider();

		// An AutoCompletion acts as a "middle-man" between a text component
		// and a CompletionProvider. It manages any options associated with
		// the auto-completion (the popup trigger key, whether to display a
		// documentation window along with completion choices, etc.). Unlike
		// CompletionProviders, instances of AutoCompletion cannot be shared
		// among multiple text components.
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setParameterAssistanceEnabled(true);
		ac.setShowDescWindow(true);
		ac.setAutoCompleteEnabled(true);
		ac.setAutoActivationEnabled(true);
		ac.setAutoCompleteSingleChoices(false);
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

		DAPOQLGroovyCompletionMetaData dgcmd = DAPOQLGroovyCompletionMetaData.getInstance();
		
		String[] basicCompArray = {
				"allDatamodels()",
				"allClasses()",
				"allRelationships()",
				"allAttributes()",
				"allObjects()",
				"allVersions()",
				"allRelations()",
				"allEvents()",
				"allActivityInstances()",
				"allCases()",
				"allLogs()",
				"allActivities()",
				"allProcesses()",
				};
		
		for (String k: basicCompArray) {
			String d = dgcmd.getDescriptionMap().get(k);
			String s = dgcmd.getSummaryMap().get(k);
			provider.addCompletion(new BasicCompletion(provider,k,d,s));
		}
		
		String[] templateCompArray = {
				"datamodelsOf",
				"classesOf",
				"relationshipsOf",
				"attributesOf",
				"objectsOf",
				"versionsOf",
				"versionsRelatedTo",
				"relationsOf",
				"eventsOf",
				"activityInstancesOf",
				"casesOf",
				"logsOf",
				"activitiesOf",
				"processesOf",
				"periodsOf",
				"globalPeriodOf",
				"union",
				"excluding",
				"intersection"
				};
		
		for (String k: templateCompArray) {
			String d = dgcmd.getDescriptionMap().get(k);
			String s = dgcmd.getSummaryMap().get(k);
			provider.addCompletion(new TemplateCompletion(provider, k, k, k+"(${cursor})", d, s));
		}
		
		provider.addCompletion(new TemplateCompletion(provider, "createPeriod", "createPeriod",
				"createPeriod(${cursor})",
				dgcmd.getDescriptionMap().get("createPeriod"),
				dgcmd.getSummaryMap().get("createPeriod")));
		
		provider.addCompletion(new TemplateCompletion(provider, "createPeriod", "createPeriod with Format",
				"createPeriod(${timestamp},${format})",
				dgcmd.getDescriptionMap().get("createPeriod with Format"),
				dgcmd.getSummaryMap().get("createPeriod with Format")));
		
		provider.addCompletion(new TemplateCompletion(provider, ".where{", "where", ".where{ ${cursor} }",
				dgcmd.getDescriptionMap().get("where"),
				dgcmd.getSummaryMap().get("where")));
		
		provider.addCompletion(new TemplateCompletion(provider, "changed", "changed",
				"changed([at: \"${attribute}\", from: \"${from}\",to: \"${to}\"])${cursor}",
				dgcmd.getDescriptionMap().get("changed"),
				dgcmd.getSummaryMap().get("changed")));
		
		provider.addCompletion(new TemplateCompletion(provider, "loop", "loop",
				"for (int ${i} = 0; ${i} < ${array}.length; ${i}++) {\n\t${cursor}\n}",
				dgcmd.getDescriptionMap().get("loop"),
				dgcmd.getSummaryMap().get("loop")));
		
		provider.addCompletion(new TemplateCompletion(provider, "if", "if",
				"if (${condition}) {\n\t${//then}\n} else {\n\t${//else}\n}\n${cursor}",
				dgcmd.getDescriptionMap().get("if"),
				dgcmd.getSummaryMap().get("if")));

		String[] periodsLogicCompArray = {
				"before",
				"after",
				"meets",
				"meetsInv",
				"overlaps",
				"overlapsInv",
				"starts",
				"startsInv",
				"during",
				"duringInv",
				"finishes",
				"finishesInv",
				"matches"
				};
		
		for (String k: periodsLogicCompArray) {
			String d = dgcmd.getDescriptionMap().get(k);
			String s = dgcmd.getSummaryMap().get(k);
			provider.addCompletion(new TemplateCompletion(provider, k, k, k+"(${periodA},${periodB})${cursor}", d, s));
		}
		
		// Shorthand completions don't require the input text to be the same
		// thing as the replacement text.
		provider.addCompletion(
				new ShorthandCompletion(provider, "sysout", "System.out.println(", "System.out.println("));
		provider.addCompletion(
				new ShorthandCompletion(provider, "syserr", "System.err.println(", "System.err.println("));

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
