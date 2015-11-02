package org.processmining.database.metamodel.poql.ui.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.processmining.database.metamodel.poql.POQLRunner;
import org.processmining.database.metamodel.poql.SuggestionsResult;

public class Autocomplete implements DocumentListener {

	private JTextArea textField;
	private List<String> keywords;

	private boolean shift = false;
	private int shift_count = 0;

	public Autocomplete(JTextArea textField, List<String> keywords) {
		this.textField = textField;
		setKeywords(keywords);
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
		if (this.keywords != null) {
			Collections.sort(keywords);
		} else {
			this.keywords = new ArrayList<>();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent ev) {
		shift = false;
		shift_count = 0;
	}

	@Override
	public void removeUpdate(DocumentEvent ev) {
		shift = false;
		shift_count = 0;
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		shift = false;
		shift_count = 0;
	}

	public class CommitAction extends AbstractAction {
		/**
     * 
     */
		private static final long serialVersionUID = 5794543109646743416L;

		@Override
		public void actionPerformed(ActionEvent ev) {
			int pos = textField.getSelectionEnd();
			StringBuffer sb = new StringBuffer(textField.getText());
			textField.setText(sb.toString());
			textField.setCaretPosition(pos);
			shift = false;
		}
	}

	public class ShiftAction extends AbstractAction {
		/**
	     * 
	     */
		private static final long serialVersionUID = 5794543109646743416L;

		@Override
		public void actionPerformed(ActionEvent ev) {
			
			int pos = textField.getCaretPosition();
			
			String content = null;
			try {
				content = textField.getText(0, pos);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			SuggestionsResult suggRes = null;
			try {
				POQLRunner runner = new POQLRunner();
				suggRes = runner.executeQueryForSuggestions(content);
			} catch (Exception e) {
				e.printStackTrace();
			}

			setKeywords(suggRes.suggestions);

			int w = suggRes.initOffendingToken;
			
			String prefix = content.substring(w).toUpperCase();

			int n = Collections.binarySearch(keywords, prefix);

			if (shift) {
				n = n - shift_count;
				shift_count++;
			} else {
				shift = true;
				shift_count = 1;
			}

			if (n < 0 && -n <= keywords.size()) {
				String match = keywords.get(-n - 1);
				if (match.startsWith(prefix)) {
					// A completion is found
					String completion = match.substring(pos - w).toLowerCase();
					// We cannot modify Document from within notification,
					// so we submit a task that does the change later
					SwingUtilities.invokeLater(new CompletionTask(completion,
							pos));
				} else {
					shift = false;
					SwingUtilities.invokeLater(new HighlightTask(
							suggRes.initOffendingToken,
							suggRes.endOffendingToken));
				}
			} else {
				shift = false;
				SwingUtilities.invokeLater(new HighlightTask(
						suggRes.initOffendingToken, suggRes.endOffendingToken));
			}

		}
	}

	private class CompletionTask implements Runnable {
		private String completion;
		private int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		public void run() {
			int selectionStart = textField.getSelectionStart();
			int selectionEnd = textField.getSelectionEnd();
			StringBuffer sb = new StringBuffer(textField.getText().substring(0,
					selectionStart)
					+ textField.getText().substring(selectionEnd));
			sb.insert(position, completion);
			textField.getDocument().removeDocumentListener(Autocomplete.this);
			textField.setText(sb.toString());
			textField.setCaretPosition(position + completion.length());
			textField.moveCaretPosition(position);
			textField.getDocument().addDocumentListener(Autocomplete.this);
		}
	}

	private class HighlightTask implements Runnable {
		private int start;
		private int end;
		private Color HILIT_COLOR = Color.pink;
		private Highlighter hilit;
		private Highlighter.HighlightPainter painter;

		HighlightTask(int start, int end) {
			this.start = start;
			this.end = end;
		}

		public void run() {
			hilit = textField.getHighlighter();
			if (hilit == null) {
				hilit = new DefaultHighlighter();
			}
			painter = new DefaultHighlighter.DefaultHighlightPainter(
					HILIT_COLOR);
			hilit.removeAllHighlights();
			textField.setHighlighter(hilit);
			try {
				hilit.addHighlight(start, end + 1, painter);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

}
