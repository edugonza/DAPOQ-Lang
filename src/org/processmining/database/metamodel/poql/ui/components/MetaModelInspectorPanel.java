package org.processmining.database.metamodel.poql.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.openslex.metamodel.SLEXMMActivityInstanceResultSet;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMAttributeValue;
import org.processmining.openslex.metamodel.SLEXMMCaseResultSet;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMDataModelResultSet;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMEventResultSet;
import org.processmining.openslex.metamodel.SLEXMMObjectResultSet;
import org.processmining.openslex.metamodel.SLEXMMObjectVersionResultSet;
import org.processmining.openslex.metamodel.SLEXMMRelationResultSet;
import org.processmining.openslex.metamodel.SLEXMMStorageMetaModel;

public class MetaModelInspectorPanel extends JPanel {

	private SLEXMMStorageMetaModel slxmm = null;
	
	
	private JTable tableObjectsAll;
	private JTable tableObjectsPerClass;

	private JTable tableObjectVersions;
	private JTable tableObjectRelations;

	private JTable tableCasesAll;
	private JTable tableCasesPerActivity;

	private JTable tableActivityInstancesAll;
	private JTable tableActivityInstancesPerActivity;
	private JTable tableActivityInstancesPerCase;
	
	private JTable tableEventsAll;
	private JTable tableEventsPerActivity;
	private JTable tableEventsPerCase;
	private JTable tableEventsPerActivityInstance;

	private JTable tableEventAttributes;

	private JTable tableObjectVersionAttributes;

	private JProgressBar topProgressBar;
	private DiagramComponent datamodelPanel;
	private JPanel processModelPanel;
	private JTable processActivitiesTable;
	
	private JButton refreshButton;
	
	
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
	
	private void fillWithData() {
		try {
			SLEXMMCaseResultSet crset = getMetaModel().getCases();
			MetaModelTableUtils.setCasesTableContent(tableCasesAll,crset);
				
			MetaModelTableUtils.setActivitiesTableContent(processActivitiesTable,getMetaModel().getActivities());
				
			SLEXMMObjectResultSet orset = getMetaModel().getObjects();
			MetaModelTableUtils.setObjectsTableContent(tableObjectsAll,orset);
				
			SLEXMMEventResultSet erset = getMetaModel().getEvents();
			MetaModelTableUtils.setEventsTableContent(tableEventsAll,erset,topProgressBar);
				
			SLEXMMActivityInstanceResultSet airset = getMetaModel().getActivityInstances();
			MetaModelTableUtils.setActivityInstancesTableContent(tableActivityInstancesAll,airset);
					
			datamodelPanel.setDataModel(getDataModel());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MetaModelInspectorPanel(SLEXMMStorageMetaModel mm) {
		
		this.slxmm = mm;
		
		JPanel inspectorPanel = this;
		
		JSplitPane inspectorSplitPane = new JSplitPane();
		
		inspectorPanel.setLayout(new BorderLayout());
		inspectorPanel.add(inspectorSplitPane, BorderLayout.CENTER);
		
		inspectorSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JSplitPane splitPane_1 = new JSplitPane();
		inspectorSplitPane.setLeftComponent(splitPane_1);

		JPanel leftTopPanel = new JPanel(new BorderLayout(0, 0));
		splitPane_1.setLeftComponent(leftTopPanel);

		processModelPanel = new JPanel(new BorderLayout(0, 0));
		processModelPanel.setMinimumSize(new Dimension(300, 200));
		leftTopPanel.add(processModelPanel, BorderLayout.CENTER);

		JScrollPane scrollPaneProcessModel = new JScrollPane();
		processModelPanel.add(scrollPaneProcessModel, BorderLayout.CENTER);
		processActivitiesTable = new JTable();
		processActivitiesTable.setFillsViewportHeight(true);
		processActivitiesTable.setModel(new MetaModelTableUtils.ActivitiesTableModel());
		
		processActivitiesTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedActivity(processActivitiesTable);
								if (selected != null) {
									try {
										MetaModelTableUtils.setCasesTableContent(
												tableCasesPerActivity,
												getMetaModel().getCasesForActivity(selected));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									try {
										MetaModelTableUtils.setActivityInstancesTableContent(
												tableActivityInstancesPerActivity,
												getMetaModel().getActivityInstancesForActivity(selected));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									try {
										MetaModelTableUtils.setEventsTableContent(
												tableEventsPerActivity,
												getMetaModel().getEventsForActivity(selected),
												topProgressBar);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		scrollPaneProcessModel.setViewportView(processActivitiesTable);

		JPanel rightTopPanel = new JPanel();
		splitPane_1.setRightComponent(rightTopPanel);
		rightTopPanel.setLayout(new BorderLayout(0, 0));

		refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						refreshButton.setEnabled(false);
						fillWithData();
						refreshButton.setEnabled(true);
					}
				}).start();
			}
		});
		topProgressBar = new JProgressBar();
		JPanel progressAndRefreshPanel = new JPanel(new BorderLayout());
		progressAndRefreshPanel.add(topProgressBar, BorderLayout.CENTER);
		progressAndRefreshPanel.add(refreshButton, BorderLayout.EAST);
		rightTopPanel.add(progressAndRefreshPanel, BorderLayout.NORTH);

		JSplitPane splitPanelTopLeft = new JSplitPane();
		splitPanelTopLeft.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		rightTopPanel.add(splitPanelTopLeft, BorderLayout.CENTER);

		tableCasesAll = new JTable();
		tableCasesAll.setFillsViewportHeight(true);
		tableCasesAll.setModel(new MetaModelTableUtils.CasesTableModel());

		tableCasesAll.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableCasesAll.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedCase(tableCasesAll);
								if (selected != null) {
									SLEXMMEventResultSet erset = getMetaModel().getEventsForCase(selected);
									SLEXMMActivityInstanceResultSet airset = getMetaModel().getActivityInstancesForCase(selected);
									try {
										MetaModelTableUtils.setEventsTableContent(tableEventsPerCase,erset,topProgressBar);
										MetaModelTableUtils.setActivityInstancesTableContent(tableActivityInstancesPerCase, airset);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		tableCasesPerActivity = new JTable();
		tableCasesPerActivity.setFillsViewportHeight(true);
		tableCasesPerActivity.setModel(new MetaModelTableUtils.CasesTableModel());

		tableCasesPerActivity.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableCasesPerActivity.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedCase(tableCasesPerActivity);
								if (selected != null) {
									SLEXMMEventResultSet erset = getMetaModel().getEventsForCase(selected);
									SLEXMMActivityInstanceResultSet airset = getMetaModel().getActivityInstancesForCase(selected);
									try {
										MetaModelTableUtils.setEventsTableContent(tableEventsPerCase,erset,topProgressBar);
										MetaModelTableUtils.setActivityInstancesTableContent(tableActivityInstancesPerCase, airset);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		JScrollPane casesScrollPane_all = new JScrollPane(tableCasesAll);
		casesScrollPane_all.setMinimumSize(new Dimension(220, 0));
		
		JScrollPane casesScrollPane_perActivity = new JScrollPane(tableCasesPerActivity);
		casesScrollPane_perActivity.setMinimumSize(new Dimension(220, 0));

		tableEventsAll = new JTable();
		tableEventsAll.setFillsViewportHeight(true);
		tableEventsAll.setModel(new MetaModelTableUtils.EventsTableModel());

		tableEventsAll.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEventsAll.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedEvent(tableEventsAll);
								if (selected != null) {
									SLEXMMEvent ev = getMetaModel().getEventForId(selected);
									try {
										MetaModelTableUtils.setEventAttributesTableContent(tableEventAttributes,
												ev.getAttributeValues(),ev.getLifecycle(),ev.getResource(),
												String.valueOf(ev.getTimestamp()));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
								
					}
				});

		tableEventsPerActivity = new JTable();
		tableEventsPerActivity.setFillsViewportHeight(true);
		tableEventsPerActivity.setModel(new MetaModelTableUtils.EventsTableModel());

		tableEventsPerActivity
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEventsPerActivity.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedEvent(tableEventsPerActivity);
								if (selected != null) {
									SLEXMMEvent ev = getMetaModel().getEventForId(selected);
									try {
										MetaModelTableUtils.setEventAttributesTableContent(tableEventAttributes,
												ev.getAttributeValues(),ev.getLifecycle(),ev.getResource(),
												String.valueOf(ev.getTimestamp()));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		tableEventsPerCase = new JTable();
		tableEventsPerCase.setFillsViewportHeight(true);
		tableEventsPerCase.setModel(new MetaModelTableUtils.EventsTableModel());

		tableEventsPerCase
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEventsPerCase.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedEvent(tableEventsPerCase);
								if (selected != null) {
									SLEXMMEvent ev = getMetaModel().getEventForId(selected);
									try {
										MetaModelTableUtils.setEventAttributesTableContent(tableEventAttributes,
												ev.getAttributeValues(),ev.getLifecycle(),ev.getResource(),
												String.valueOf(ev.getTimestamp()));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		tableEventsPerActivityInstance = new JTable();
		tableEventsPerActivityInstance.setFillsViewportHeight(true);
		tableEventsPerActivityInstance.setModel(new MetaModelTableUtils.EventsTableModel());

		tableEventsPerActivityInstance
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEventsPerActivityInstance.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedEvent(tableEventsPerActivityInstance);
								if (selected != null) {
									SLEXMMEvent ev = getMetaModel().getEventForId(selected);
									try {
										MetaModelTableUtils.setEventAttributesTableContent(tableEventAttributes,
												ev.getAttributeValues(),ev.getLifecycle(),ev.getResource(),
												String.valueOf(ev.getTimestamp()));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
						
					}
				});

		tableActivityInstancesAll = new JTable();
		tableActivityInstancesAll.setFillsViewportHeight(true);
		tableActivityInstancesAll.setModel(new MetaModelTableUtils.ActivityInstanceTableModel());

		tableActivityInstancesAll.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableActivityInstancesAll.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedActivityInstance(tableActivityInstancesAll);
								if (selected != null) {
									try {
										MetaModelTableUtils.setEventsTableContent(
												tableEventsPerActivityInstance,
												getMetaModel().getEventsForActivityInstance(selected),
												topProgressBar);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});

		tableActivityInstancesPerActivity = new JTable();
		tableActivityInstancesPerActivity.setFillsViewportHeight(true);
		tableActivityInstancesPerActivity.setModel(new MetaModelTableUtils.ActivityInstanceTableModel());

		tableActivityInstancesPerActivity
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableActivityInstancesPerActivity.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedActivityInstance(tableActivityInstancesPerActivity);
								if (selected != null) {
									try {
										MetaModelTableUtils.setEventsTableContent(
												tableEventsPerActivityInstance,
												getMetaModel().getEventsForActivityInstance(selected),
												topProgressBar);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		tableActivityInstancesPerCase = new JTable();
		tableActivityInstancesPerCase.setFillsViewportHeight(true);
		tableActivityInstancesPerCase.setModel(new MetaModelTableUtils.ActivityInstanceTableModel());

		tableActivityInstancesPerCase
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableActivityInstancesPerCase.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedActivityInstance(tableActivityInstancesPerCase);
								if (selected != null) {
									try {
										MetaModelTableUtils.setEventsTableContent(
												tableEventsPerActivityInstance,
												getMetaModel().getEventsForActivityInstance(selected),
												topProgressBar);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});
		
		JTabbedPane activityInstancesTabbedPane = new JTabbedPane();
		activityInstancesTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		
		JScrollPane scrollPaneActivityInstancesAll = new JScrollPane(tableActivityInstancesAll);
		scrollPaneActivityInstancesAll.setMinimumSize(new Dimension(180, 0));
		
		JScrollPane scrollPaneActivityInstancesPerActivity = new JScrollPane(tableActivityInstancesPerActivity);
		scrollPaneActivityInstancesPerActivity.setMinimumSize(new Dimension(180, 0));
		
		JScrollPane scrollPaneActivityInstancesPerCase = new JScrollPane(tableActivityInstancesPerCase);
		scrollPaneActivityInstancesPerCase.setMinimumSize(new Dimension(180, 0));
		
		activityInstancesTabbedPane.addTab("All Activity Instances", scrollPaneActivityInstancesAll);
		activityInstancesTabbedPane.addTab("Per Activity", scrollPaneActivityInstancesPerActivity);
		activityInstancesTabbedPane.addTab("Per Case", scrollPaneActivityInstancesPerCase);
		
		JSplitPane splitPanelTopRight = new JSplitPane();
		splitPanelTopRight.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		JSplitPane splitPanelCasesActivityInstances = new JSplitPane();
		splitPanelCasesActivityInstances.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		
		JTabbedPane casesTabbedPane = new JTabbedPane();
		casesTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		
		casesTabbedPane.addTab("All Cases", casesScrollPane_all);
		casesTabbedPane.addTab("Per Activity", casesScrollPane_perActivity);
		
		splitPanelCasesActivityInstances.setLeftComponent(casesTabbedPane);
		splitPanelCasesActivityInstances.setRightComponent(activityInstancesTabbedPane);
		
		splitPanelTopLeft.setRightComponent(splitPanelTopRight);
		splitPanelTopLeft.setLeftComponent(splitPanelCasesActivityInstances);

		JTabbedPane eventsTabbedPane = new JTabbedPane();
		eventsTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

		JScrollPane scrollPaneEventsAll = new JScrollPane(tableEventsAll);
		scrollPaneEventsAll.setMinimumSize(new Dimension(180, 0));
		eventsTabbedPane.addTab("All Events", null, scrollPaneEventsAll, null);

		JScrollPane scrollPaneEventsPerActivity = new JScrollPane(
				tableEventsPerActivity);
		scrollPaneEventsPerActivity.setMinimumSize(new Dimension(180, 0));
		eventsTabbedPane
				.addTab("Per Activity", null, scrollPaneEventsPerActivity, null);
		
		JScrollPane scrollPaneEventsPerActivityInstance = new JScrollPane(
				tableEventsPerActivityInstance);
		scrollPaneEventsPerActivityInstance.setMinimumSize(new Dimension(180, 0));
		eventsTabbedPane
				.addTab("Per Activity Instance", null, scrollPaneEventsPerActivityInstance, null);

		
		JScrollPane scrollPaneEventsPerCase = new JScrollPane(
				tableEventsPerCase);
		scrollPaneEventsPerCase.setMinimumSize(new Dimension(180, 0));
		eventsTabbedPane
				.addTab("Per Case", null, scrollPaneEventsPerCase, null);

		splitPanelTopRight.setLeftComponent(eventsTabbedPane);

		tableEventAttributes = new JTable();
		tableEventAttributes.setFillsViewportHeight(true);
		tableEventAttributes.setModel(new MetaModelTableUtils.EventAttributesTableModel());

		JScrollPane scrollPane_right = new JScrollPane(tableEventAttributes);
		// tableEventAttributes.setMinimumSize(new Dimension(300, 0));
		splitPanelTopRight.setRightComponent(scrollPane_right);

		JSplitPane splitPane_2 = new JSplitPane();
		inspectorSplitPane.setRightComponent(splitPane_2);

		JPanel leftBottomPanel = new JPanel();
		leftBottomPanel.setMinimumSize(new Dimension(180, 0));
		splitPane_2.setLeftComponent(leftBottomPanel);
		leftBottomPanel.setLayout(new BorderLayout(0, 0));

		NodeSelectionHandler classSelectionHandler = new NodeSelectionHandler() {

			@Override
			public void run(final SLEXMMClass c) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						SLEXMMObjectResultSet orset = getMetaModel().getObjectsForClass(c.getId());
						try {
							MetaModelTableUtils.setObjectsTableContent(tableObjectsPerClass, orset);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}).start();
			}
		};
		
		datamodelPanel = new DiagramComponent(classSelectionHandler);
		datamodelPanel.setMinimumSize(new Dimension(300, 200));
		leftBottomPanel.add(datamodelPanel, BorderLayout.CENTER);

		tableObjectsAll = new JTable();
		tableObjectsAll.setFillsViewportHeight(true);
		tableObjectsAll.setModel(new MetaModelTableUtils.ObjectsTableModel());

		tableObjectsAll.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableObjectsAll.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer[] selected = MetaModelTableUtils.getSelectedObject(tableObjectsAll);
								if (selected != null) {
									try {
										SLEXMMObjectVersionResultSet ovrset = getMetaModel().
											getObjectVersionsForObject(selected[0]);
										MetaModelTableUtils.setObjectVersionsTableContent(tableObjectVersions,ovrset);
										SLEXMMRelationResultSet[] rrset = new SLEXMMRelationResultSet[2];
										rrset[0] = getMetaModel().getRelationsForSourceObject(selected[0]);
										rrset[1] = getMetaModel().getRelationsForTargetObject(selected[0]);
										MetaModelTableUtils.setObjectRelationsTableContent(tableObjectRelations,rrset);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
						
					}
				});

		tableObjectsPerClass = new JTable();
		tableObjectsPerClass.setFillsViewportHeight(true);
		tableObjectsPerClass.setModel(new MetaModelTableUtils.ObjectsTableModel());

		tableObjectsPerClass
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableObjectsPerClass.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer[] selected = MetaModelTableUtils.getSelectedObject(tableObjectsPerClass);
								if (selected != null) {
									try {
										SLEXMMObjectVersionResultSet ovrset = getMetaModel().
											getObjectVersionsForObject(selected[0]);
										MetaModelTableUtils.setObjectVersionsTableContent(tableObjectVersions,ovrset);
										SLEXMMRelationResultSet[] rrset = new SLEXMMRelationResultSet[2];
										rrset[0] = getMetaModel().getRelationsForSourceObject(selected[0]);
										rrset[1] = getMetaModel().getRelationsForTargetObject(selected[0]);
										MetaModelTableUtils.setObjectRelationsTableContent(tableObjectRelations,rrset);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
					}
				});

		JTabbedPane objectsTabbedPane = new JTabbedPane();
		objectsTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		JScrollPane scrollPaneTableObjectsAll = new JScrollPane(tableObjectsAll);
		scrollPaneTableObjectsAll.setMinimumSize(new Dimension(180, 0));
		
		objectsTabbedPane.addTab("All Objects", null,
				scrollPaneTableObjectsAll, null);
		JScrollPane scrollPaneTableObjectsPerClass = new JScrollPane(
				tableObjectsPerClass);
		scrollPaneTableObjectsPerClass.setMinimumSize(new Dimension(180, 0));
		objectsTabbedPane.addTab("Per Class", null,
				scrollPaneTableObjectsPerClass, null);

		JPanel rightBottomPanel = new JPanel();
		JSplitPane splitPane_22 = new JSplitPane();
		splitPane_2.setRightComponent(splitPane_22);
		splitPane_22.setLeftComponent(objectsTabbedPane);
		splitPane_22.setRightComponent(rightBottomPanel);
		rightBottomPanel.setLayout(new BoxLayout(rightBottomPanel,
				BoxLayout.X_AXIS));

		

		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setResizeWeight(0.5);
		splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		rightBottomPanel.add(splitPane_3);

		JPanel topObjectVersionsPanel = new JPanel();
		topObjectVersionsPanel.setLayout(new BorderLayout(0, 0));

		tableObjectVersionAttributes = new JTable();
		tableObjectVersionAttributes.setFillsViewportHeight(true);
		tableObjectVersionAttributes
				.setModel(new MetaModelTableUtils.ObjectVersionAttributesTableModel());

		JScrollPane objectVersionDetailsPanel = new JScrollPane(
				tableObjectVersionAttributes);

		JSplitPane splitPane_4 = new JSplitPane();
		splitPane_4.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane_3.setTopComponent(splitPane_4);
		splitPane_4.setLeftComponent(topObjectVersionsPanel);
		splitPane_4.setRightComponent(objectVersionDetailsPanel);

		tableObjectVersions = new JTable();
		tableObjectVersions.setFillsViewportHeight(true);
		tableObjectVersions.setModel(new MetaModelTableUtils.ObjectVersionsTableModel());
		tableObjectVersions.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								Integer selected = MetaModelTableUtils.getSelectedEvent(tableObjectVersions);
								if (selected != null) {
									HashMap<SLEXMMAttribute, SLEXMMAttributeValue> atts =
											getMetaModel().getAttributeValuesForObjectVersion(selected);
									try {
										MetaModelTableUtils.setObjectVersionAttributesTableContent(tableObjectVersionAttributes,atts);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						}).start();
						
					}
				});

		JScrollPane scrollPaneTableObjectVersions = new JScrollPane(
				tableObjectVersions);
		scrollPaneTableObjectVersions.setMinimumSize(new Dimension(360, 0));
		topObjectVersionsPanel.add(scrollPaneTableObjectVersions);

		JPanel bottomObjectVersionsPanel = new JPanel();
		splitPane_3.setBottomComponent(bottomObjectVersionsPanel);
		bottomObjectVersionsPanel.setLayout(new BorderLayout(0, 0));

		tableObjectRelations = new JTable();
		tableObjectRelations.setFillsViewportHeight(true);
		tableObjectRelations.setModel(new MetaModelTableUtils.ObjectRelationsTableModel());

		JScrollPane scrollPaneTableObjectRelations = new JScrollPane(
				tableObjectRelations);
		bottomObjectVersionsPanel.add(scrollPaneTableObjectRelations);
	}
}
