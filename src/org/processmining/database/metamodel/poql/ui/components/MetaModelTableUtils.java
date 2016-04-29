package org.processmining.database.metamodel.poql.ui.components;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.processmining.openslex.metamodel.*;

public class MetaModelTableUtils {

	public static Integer[] getSelectedObject(JTable table) {
		Integer[] selected = new Integer[2];

		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			selected[0] = (int) table.getModel().getValueAt(selectedRow, 0);
			selected[1] = (int) table.getModel().getValueAt(selectedRow, 1);

			return selected;
		} else {
			return null;
		}
	}

	public static Integer getSelectedLog(JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			return (int) table.getModel().getValueAt(selectedRow, 0);
		} else {
			return null;
		}
	}
	
	public static Integer getSelectedActivity(JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			return (int) table.getModel().getValueAt(selectedRow, 0);
		} else {
			return null;
		}
	}
	
	public static Integer getSelectedCase(JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			return (int) table.getModel().getValueAt(selectedRow, 0);
		} else {
			return null;
		}
	}

	public static Integer getSelectedEvent(JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			return (int) table.getModel().getValueAt(selectedRow, 0);
		} else {
			return null;
		}
	}
	
	public static Integer getSelectedActivityInstance(JTable table) {
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			return (int) table.getModel().getValueAt(selectedRow, 0);
		} else {
			return null;
		}
	}
	
	
	public static class ActivitiesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, String.class};
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public ActivitiesTableModel() {
			super(new String[] { "Activity Id", "Name"}, 0);
		}

	}

	public static void setActivitiesTableContent(final JTable table, SLEXMMActivityResultSet actrset) throws Exception {
		final ActivitiesTableModel model = new ActivitiesTableModel();

		SLEXMMActivity act = null;
		
		while ((act = actrset.getNext()) != null) {
			model.addRow(new Object[] { act.getId(), act.getName()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});

	}
	
	public static void setActivitiesTableContent(final JTable table, Collection<Object> actrset) throws Exception {
		final ActivitiesTableModel model = new ActivitiesTableModel();
		
		for (Object o: actrset) {
			SLEXMMActivity act = (SLEXMMActivity) o;
			model.addRow(new Object[] { act.getId(), act.getName()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});

	}
	
	public static class LogsTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, String.class,
				Integer.class };
		boolean[] columnEditables = new boolean[] { false, false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public LogsTableModel() {
			super(new String[] { "Log Id", "Name", "Process Id" }, 0);
		}

	}
	
	public static void setLogsTableContent(final JTable table, Collection<Object> logSet) throws Exception {
		
		final LogsTableModel model = new LogsTableModel();

		for (Object o: logSet) {
			SLEXMMLog log = (SLEXMMLog) o;
			model.addRow(new Object[] { log.getId(), log.getName(), log.getProcessId()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
				table.getColumnModel().getColumn(2).setMinWidth(75);
			}
		});
	}
	
	public static void setLogsTableContent(final JTable table, SLEXMMLogResultSet logrset) throws Exception {
		
		final LogsTableModel model = new LogsTableModel();

		SLEXMMLog log = null;
		
		while ((log = logrset.getNext()) != null) {
			model.addRow(new Object[] { log.getId(), log.getName(), log.getProcessId()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
				table.getColumnModel().getColumn(2).setMinWidth(75);
			}
		});
	}
	
	public static class ClassesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, String.class,
				Integer.class };
		boolean[] columnEditables = new boolean[] { false, false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public ClassesTableModel() {
			super(new String[] { "Class Id", "Name", "DataModel Id" }, 0);
		}

	}

	public static void setClassesTableContent(final JTable table, Collection<Object> classes) throws Exception {
		final ClassesTableModel model = new ClassesTableModel();
		
		

		for (Object o : classes) {
			SLEXMMClass c = (SLEXMMClass) o;
			model.addRow(new Object[] { c.getId(), c.getName(),
					c.getDataModelId() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
				table.getColumnModel().getColumn(2).setMinWidth(75);
			}
		});
	}
	
	public static class AttributesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, Integer.class,
				String.class };
		boolean[] columnEditables = new boolean[] { false, false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public AttributesTableModel() {
			super(new String[] { "Attribute Id", "Class Id", "Name" }, 0);
		}

	}

	public static void setAttributesTableContent(final JTable table, Collection<Object> classes) throws Exception {
		final AttributesTableModel model = new AttributesTableModel();
		

		for (Object o : classes) {
			SLEXMMAttribute c = (SLEXMMAttribute) o;
			model.addRow(new Object[] { c.getId(), c.getClassId(),
					c.getName() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
				table.getColumnModel().getColumn(2).setMinWidth(75);
			}
		});
	}

	public static class ObjectsTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, Integer.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public ObjectsTableModel() {
			super(new String[] { "Object Id", "Class Id" }, 0);
		}

	}

	public static void setObjectsTableContent(final JTable table, SLEXMMObjectResultSet orset) throws Exception {
		final ObjectsTableModel model = new ObjectsTableModel();
		
		SLEXMMObject obj = null;

		while ((obj = orset.getNext()) != null) {
			model.addRow(new Object[] { obj.getId(), obj.getClassId() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});

	}

	public static void setObjectsTableContent(final JTable table, Collection<Object> list) throws Exception {
		final ObjectsTableModel model = new ObjectsTableModel();

		SLEXMMObject obj = null;
		for (Object o : list) {
			obj = (SLEXMMObject) o;
			model.addRow(new Object[] { obj.getId(), obj.getClassId() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}

	public static class ActivityInstanceTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, Integer.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public ActivityInstanceTableModel() {
			super(new String[] { "Activity Instance Id", "Activity Id" }, 0);
		}

	}

	public static void setActivityInstancesTableContent(final JTable table, SLEXMMActivityInstanceResultSet airset) throws Exception {
		final ActivityInstanceTableModel model = new ActivityInstanceTableModel();
		
		SLEXMMActivityInstance ai = null;

		while ((ai = airset.getNext()) != null) {
			model.addRow(new Object[] { ai.getId(), ai.getActivityId() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});

	}

	public static void setActivityInstancesTableContent(final JTable table, Collection<Object> list) throws Exception {
		final ActivityInstanceTableModel model = new ActivityInstanceTableModel();
		

		SLEXMMActivityInstance ai = null;
		for (Object o : list) {
			ai = (SLEXMMActivityInstance) o;
			model.addRow(new Object[] { ai.getId(), ai.getActivityId() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}
	
	public static class ObjectVersionsTableModel extends DefaultTableModel {

		ArrayList<Class> columnTypes = new ArrayList<>();

		public Class getColumnClass(int columnIndex) {
			return columnTypes.get(columnIndex);
		}

		public void addColumnClass(Class c) {
			columnTypes.add(c);
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public ObjectVersionsTableModel() {
			super(new String[] { "Version Id", "Object Id", "Start Timestamp",
					"End Timestamp" }, 0);
			columnTypes.add(Integer.class);
			columnTypes.add(Integer.class);
			columnTypes.add(Long.class);
			columnTypes.add(Long.class);
		}

	}

	public static void setObjectVersionsTableContent(final JTable table, SLEXMMObjectVersionResultSet orset) throws Exception {
		final ObjectVersionsTableModel model = new ObjectVersionsTableModel();

		

		SLEXMMObjectVersion objv = null;

		while ((objv = orset.getNext()) != null) {

			Object[] row = new Object[model.getColumnCount()];

			row[0] = Integer.valueOf(objv.getId());
			row[1] = Integer.valueOf(objv.getObjectId());
			row[2] = Long.valueOf(objv.getStartTimestamp());
			row[3] = Long.valueOf(objv.getEndTimestamp());

			model.addRow(row);
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
			}
		});
	}

	public static void setObjectVersionsTableContent(final JTable table, Collection<Object> list) throws Exception {
		final ObjectVersionsTableModel model = new ObjectVersionsTableModel();

		for (Object o : list) {
			SLEXMMObjectVersion objv = (SLEXMMObjectVersion) o;

			Object[] row = new Object[model.getColumnCount()];

			row[0] = Integer.valueOf(objv.getId());
			row[1] = Integer.valueOf(objv.getObjectId());
			row[2] = Long.valueOf(objv.getStartTimestamp());
			row[3] = Long.valueOf(objv.getEndTimestamp());

			model.addRow(row);
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
			}
		});
		

	}
	
	public static class RelationshipTableModel extends DefaultTableModel {

		ArrayList<Class> columnTypes = new ArrayList<>();

		public Class getColumnClass(int columnIndex) {
			return columnTypes.get(columnIndex);
		}

		public void addColumnClass(Class c) {
			columnTypes.add(c);
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public RelationshipTableModel() {
			super(new String[] { "Relationship Id", "Source Class Id", "Target Class Id",
					"Name" }, 0);
			columnTypes.add(Integer.class);
			columnTypes.add(Integer.class);
			columnTypes.add(Integer.class);
			columnTypes.add(String.class);
		}

	}

	public static void setRelationshipsTableContent(final JTable table, Collection<Object> list) throws Exception {
		final RelationshipTableModel model = new RelationshipTableModel();

		for (Object o : list) {
			SLEXMMRelationship rs = (SLEXMMRelationship) o;

			Object[] row = new Object[model.getColumnCount()];

			row[0] = Integer.valueOf(rs.getId());
			row[1] = Integer.valueOf(rs.getSourceClassId());
			row[2] = Integer.valueOf(rs.getTargetClassId());
			row[3] = String.valueOf(rs.getName());

			model.addRow(row);
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
			}
		});

	}

	public static class ObjectRelationsTableModel extends DefaultTableModel {

		ArrayList<Class> columnTypes = new ArrayList<>();

		public Class getColumnClass(int columnIndex) {
			return columnTypes.get(columnIndex);
		}

		public void addColumnClass(Class c) {
			columnTypes.add(c);
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public ObjectRelationsTableModel() {
			super(new String[] { "Relation Id", "Relationship Id",
					"Source Object Version Id", "Target Object Version Id",
					"Start Timestamp", "End Timestamp" }, 0);
			columnTypes.add(Integer.class);
			columnTypes.add(Integer.class);
			columnTypes.add(Integer.class);
			columnTypes.add(Integer.class);
			columnTypes.add(Long.class);
			columnTypes.add(Long.class);
		}

	}

	public static void setObjectRelationsTableContent(final JTable table, SLEXMMRelationResultSet[] orrset) throws Exception {
		final ObjectRelationsTableModel model = new ObjectRelationsTableModel();

		for (int i = 0; i < orrset.length; i++) {
		
			SLEXMMRelation rel = null;
		
			while ((rel = orrset[i].getNext()) != null) {
				
				Object[] row = new Object[model.getColumnCount()];

				row[0] = Integer.valueOf(rel.getId());
				row[1] = Integer.valueOf(rel.getRelationshipId());
				row[2] = Integer.valueOf(rel.getSourceObjectVersionId());
				row[3] = Integer.valueOf(rel.getTargetObjectVersionId());
				row[4] = Long.valueOf(rel.getStartTimestamp());
				row[5] = Long.valueOf(rel.getEndTimestamp());

				model.addRow(row);
			}
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
			}
		});
	}
	
	public static void setObjectRelationsTableContent(final JTable table, Collection<Object> list) throws Exception {
		final ObjectRelationsTableModel model = new ObjectRelationsTableModel();

		for  (Object o: list) {
				
			SLEXMMRelation rel = (SLEXMMRelation) o;
				
			Object[] row = new Object[model.getColumnCount()];

			row[0] = Integer.valueOf(rel.getId());
			row[1] = Integer.valueOf(rel.getRelationshipId());
			row[2] = Integer.valueOf(rel.getSourceObjectVersionId());
			row[3] = Integer.valueOf(rel.getTargetObjectVersionId());
			row[4] = Long.valueOf(rel.getStartTimestamp());
			row[5] = Long.valueOf(rel.getEndTimestamp());

			model.addRow(row);
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
			}
		});

	}

	public static class CasesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, String.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public CasesTableModel() {
			super(new String[] { "Case Id", "Name" }, 0);
		}

	}

	public static void setCasesTableContent(final JTable table, SLEXMMCaseResultSet orset) throws Exception {
		final CasesTableModel model = new CasesTableModel();

		SLEXMMCase c = null;

		while ((c = orset.getNext()) != null) {
			model.addRow(new Object[] { c.getId(), c.getName() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});

	}
	
	public static void setCasesTableContent(final JTable table, Collection<Object> list) throws Exception {
		try {
			final CasesTableModel model = new CasesTableModel();
			

			for (Object o : list) {
				SLEXMMCase c = (SLEXMMCase) o;

				model.addRow(new Object[] { c.getId(), c.getName() });
			}
			
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					table.setModel(model);
					table.getColumnModel().getColumn(0).setMinWidth(75);
					table.getColumnModel().getColumn(1).setMinWidth(75);
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	public static class EventsTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, Integer.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public EventsTableModel() {
			super(new String[] { "Event Id", "Ordering" }, 0);
		}

	}

	public static void setEventsTableContent(final JTable table, final SLEXMMEventResultSet orset, final JProgressBar progress) throws Exception {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					if (progress != null) {
						progress.setIndeterminate(true);
					}

					final EventsTableModel model = new EventsTableModel();

					

					SLEXMMEvent ev = null;

					while ((ev = orset.getNext()) != null) {
						model.addRow(new Object[] { ev.getId(), ev.getOrder() });
					}
					
					SwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							table.setModel(model);

							table.getColumnModel().getColumn(0)
								.setMinWidth(75);
							table.getColumnModel().getColumn(1)
								.setMinWidth(75);
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (progress != null) {
					progress.setIndeterminate(false);
				}
			}
		});

		thread.start();

	}
	
	public static void setEventsTableContent(final JTable table, final Collection<Object> list, final JProgressBar progress) throws Exception {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					if (progress != null) {
						progress.setIndeterminate(true);
					}

					final EventsTableModel model = new EventsTableModel();

					

					SLEXMMEvent ev = null;

					for (Object o : list) {
						ev = (SLEXMMEvent) o;
						model.addRow(new Object[] { ev.getId(), ev.getOrder() });
					}
					SwingUtilities.invokeAndWait(new Runnable() {
						
						@Override
						public void run() {
							table.setModel(model);

							table.getColumnModel().getColumn(0)
									.setMinWidth(75);
							table.getColumnModel().getColumn(1)
									.setMinWidth(75);
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (progress != null) {
					progress.setIndeterminate(false);
				}
			}
		});

		thread.start();

	}

	public static class EventAttributesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { String.class, String.class,
				String.class };
		boolean[] columnEditables = new boolean[] { false, false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public EventAttributesTableModel() {
			super(new String[] { "Attribute Name", "Value", "Type" }, 0);
		}

	}

	public static void setEventAttributesTableContent(final JTable table,
			HashMap<SLEXMMEventAttribute, SLEXMMEventAttributeValue> attrs,
			String lifecycle, String resource, String timestamp) throws Exception {

		final EventAttributesTableModel model = new EventAttributesTableModel();

		

		for (SLEXMMEventAttribute at : attrs.keySet()) {
			SLEXMMEventAttributeValue attV = attrs.get(at);
			model.addRow(new Object[] { at.getName(), attV.getValue(),
					attV.getType() });
		}

		model.addRow(new Object[] { "Event Lifecycle", lifecycle,
				"STRING" });
		model.addRow(new Object[] { "Event Resource", resource, "STRING" });
		model.addRow(new Object[] { "Event Timestamp", timestamp, "LONG" });

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);

				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}

	public static class ObjectVersionAttributesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { String.class, String.class,
				String.class };
		boolean[] columnEditables = new boolean[] { false, false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public ObjectVersionAttributesTableModel() {
			super(new String[] { "Attribute Name", "Value", "Type" }, 0);
		}

	}

	public static void setObjectVersionAttributesTableContent(final JTable table,
			HashMap<SLEXMMAttribute, SLEXMMAttributeValue> attrs) throws Exception {

		final ObjectVersionAttributesTableModel model = new ObjectVersionAttributesTableModel();

		

		for (SLEXMMAttribute at : attrs.keySet()) {
			SLEXMMAttributeValue attV = attrs.get(at);
			model.addRow(new Object[] { at.getName(), attV.getValue(),
					attV.getType() });
		}

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);

				table.getColumnModel().getColumn(0)
					.setMinWidth(75);
				table.getColumnModel().getColumn(1)
					.setMinWidth(75);
				table.getColumnModel().getColumn(1)
					.setMinWidth(75);
			}
		});
	}

	public static class PeriodsTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Date.class, Date.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public PeriodsTableModel() {
			super(new String[] { "Start", "End" }, 0);
		}

	}
	
	public static void setPeriodsTableContent(final JTable table,
			Collection<Object> list) throws Exception {
		try {
			final PeriodsTableModel model = new PeriodsTableModel();

			for (Object o : list) {
				SLEXMMPeriod p = (SLEXMMPeriod) o;

				Date startDate = new Date(p.getStart());
				Date endDate = new Date(p.getEnd());
				
				model.addRow(new Object[] { startDate, endDate });
			}
			
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					table.setModel(model);
					table.getColumnModel().getColumn(0).setMinWidth(75);
					table.getColumnModel().getColumn(1).setMinWidth(75);
					table.getColumnModel().getColumn(0).setCellRenderer(new DateRenderer());
					table.getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void setProcessesTableContent(final JTable table, Collection<Object> procSet) throws Exception {
		
		final ProcessesTableModel model = new ProcessesTableModel();

		for (Object o: procSet) {
			SLEXMMProcess proc = (SLEXMMProcess) o;
			model.addRow(new Object[] { proc.getId(), proc.getName()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}
	
	public static void setProcessesTableContent(final JTable table, SLEXMMProcessResultSet procrset) throws Exception {
		
		final ProcessesTableModel model = new ProcessesTableModel();

		SLEXMMProcess proc = null;
		
		while ((proc = procrset.getNext()) != null) {
			model.addRow(new Object[] { proc.getId(), proc.getName()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}
	
	public static class ProcessesTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, String.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public ProcessesTableModel() {
			super(new String[] { "Process Id", "Name"}, 0);
		}

	}	
	
	public static void setProcessesDropboxContent(JComboBox<SLEXMMProcess> processComboBox,
			SLEXMMProcessResultSet prset) throws Exception {

		SLEXMMProcess p = null;

		while ((p = prset.getNext()) != null) {
			processComboBox.addItem(p);
		}
		
	}

	public static void setDatamodelsTableContent(final JTable table, Collection<Object> dmSet) throws Exception {
		
		final ProcessesTableModel model = new ProcessesTableModel();

		for (Object o: dmSet) {
			SLEXMMDataModel proc = (SLEXMMDataModel) o;
			model.addRow(new Object[] { proc.getId(), proc.getName()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}
	
	public static void setDatamodelsTableContent(final JTable table, SLEXMMDataModelResultSet dmrset) throws Exception {
		
		final DatamodelsTableModel model = new DatamodelsTableModel();

		SLEXMMDataModel dm = null;
		
		while ((dm = dmrset.getNext()) != null) {
			model.addRow(new Object[] { dm.getId(), dm.getName()});
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				table.setModel(model);
		
				table.getColumnModel().getColumn(0).setMinWidth(75);
				table.getColumnModel().getColumn(1).setMinWidth(75);
			}
		});
	}
	
	public static class DatamodelsTableModel extends DefaultTableModel {

		Class[] columnTypes = new Class[] { Integer.class, String.class };
		boolean[] columnEditables = new boolean[] { false, false };

		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}

		public DatamodelsTableModel() {
			super(new String[] { "DataModel Id", "Name"}, 0);
		}

	}	
	
}
