package org.processmining.database.metamodel.dapoql.lite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.processmining.database.metamodel.dapoql.DAPOQLTimestamp;
import org.processmining.database.metamodel.dapoql.DAPOQLValue;
import org.processmining.database.metamodel.dapoql.DAPOQLVariable;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ActivitiesOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ActivityInstancesOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllActivitiesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllActivityInstancesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllAttributesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllCasesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllClassesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllDatamodelsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllEventsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllLogsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllObjectsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllProcessesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllRelationsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllRelationshipsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AllVersionsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.AttributesOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.CasesOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ClassesOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithActivitiesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithActivityInstancesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithAttributesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithCasesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithClassesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithDatamodelsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithEventsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithLogsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithObjectsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithProcessesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithRelationsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithRelationshipsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConcurrentWithVersionsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConditionEmptyContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ConditionNotEmptyContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.CreatePeriodContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.DatamodelsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.EndPeriodContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.EventsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterActivitiesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterActivityInstancesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterAttributesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterCasesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterClassesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterCombinedContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterDatamodelsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterEventsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterFieldContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterLogsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterNegationContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterObjectsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterPeriodsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterProcessesContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterRelationsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterRelationshipsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.FilterVersionsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.IdsContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.If_blockContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.LogsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.LoopContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.NodeContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ObjectsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.OperatorContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.PeriodsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ProcessesOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.RelationsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.RelationshipsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.Return_statementContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.Returnable_objectContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ScopeContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.Set_operatorContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.StartPeriodContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.ThingsSetOperatorContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.TimeoperatorContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.TimestampFromStringContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.TimestampOffsetFromStringContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.TimestampOperationContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.VariableContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.Variable_assignmentContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.Variable_definitionContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.Variable_valueContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.VersionsOfContext;
import org.processmining.database.metamodel.dapoql.lite.dapoqlParser.VersionsRelatedToContext;
import org.processmining.openslex.metamodel.SLEXMMAbstractDatabaseObject;
import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMDataModel;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMLog;
import org.processmining.openslex.metamodel.SLEXMMObject;
import org.processmining.openslex.metamodel.SLEXMMObjectVersion;
import org.processmining.openslex.metamodel.SLEXMMPeriod;
import org.processmining.openslex.metamodel.SLEXMMProcess;
import org.processmining.openslex.metamodel.SLEXMMRelation;
import org.processmining.openslex.metamodel.SLEXMMRelationship;

public class DAPOQLVisitorImpl extends dapoqlBaseVisitor<DAPOQLValue> {

	private DAPOQLFunctions poql;

	public DAPOQLVisitorImpl(DAPOQLFunctions poql) {
		this.poql = poql;
	}

	@Override
	public DAPOQLValue visitAllActivities(AllActivitiesContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllActivities();
		value.type = SLEXMMActivity.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllActivityInstances(AllActivityInstancesContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllActivityInstances();
		value.type = SLEXMMActivityInstance.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllAttributes(AllAttributesContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllAttributes();
		value.type = SLEXMMAttribute.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllCases(AllCasesContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllCases();
		value.type = SLEXMMCase.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllClasses(AllClassesContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllClasses();
		value.type = SLEXMMClass.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllEvents(AllEventsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllEvents();
		value.type = SLEXMMEvent.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllObjects(AllObjectsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllObjects();
		value.type = SLEXMMObject.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllRelations(AllRelationsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllRelations();
		value.type = SLEXMMRelation.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllRelationships(AllRelationshipsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();

		value.result = poql.getAllRelationships();
		value.type = SLEXMMRelationship.class;

		return value;
	}

	@Override
	public DAPOQLValue visitAllVersions(AllVersionsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();
		value.result = poql.getAllVersions();
		value.type = SLEXMMObjectVersion.class;

		return value;
	}
	
	@Override
	public DAPOQLValue visitAllProcesses(AllProcessesContext ctx) {
		DAPOQLValue value = new DAPOQLValue();
		value.result = poql.getAllProcesses();
		value.type = SLEXMMProcess.class;

		return value;
	}
	
	@Override
	public DAPOQLValue visitAllLogs(AllLogsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();
		value.result = poql.getAllLogs();
		value.type = SLEXMMLog.class;

		return value;
	}
	
	@Override
	public DAPOQLValue visitAllDatamodels(AllDatamodelsContext ctx) {
		DAPOQLValue value = new DAPOQLValue();
		value.result = poql.getAllDatamodels();
		value.type = SLEXMMDataModel.class;

		return value;
	}
	
	@Override
	public DAPOQLValue visitVariable_definition(Variable_definitionContext ctx) {
		String var_name = ctx.VAR_NAME().getText();
		
		DAPOQLVariable var = poql.findVariable(var_name);
		
		DAPOQLValue v = null;
		
		if (var == null) {
		
			v = this.visit(ctx.variable_value());
		
			var = poql.createVariable(var_name, v.type, v.result);
		
		} else {
			throw new RuntimeException("Var "+var_name+" already exists!");
		}
		
		return v;
	}

	@Override
	public DAPOQLValue visitVariable_value(Variable_valueContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		if (ctx.NULL() != null) {
			v.result = new HashMap<>();
			v.type = null;
			return v;
		} else {
			return super.visitVariable_value(ctx);
		}
	}
	
	@Override
	public DAPOQLValue visitVariable_assignment(Variable_assignmentContext ctx) {
		String var_name = ctx.VAR_NAME().getText();
		
		DAPOQLVariable var = poql.findVariable(var_name);
		
		DAPOQLValue v = null;
		
		if (var != null) {
		
			v = this.visit(ctx.variable_value());
		
			var.setValue(v.result);
			var.setType(v.type);
		
		} else {
			throw new RuntimeException("Var "+var_name+" does not exist!");
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitReturn_statement(Return_statementContext ctx) {
		return this.visit(ctx.returnable_object());
	}
	
	@Override
	public DAPOQLValue visitReturnable_object(Returnable_objectContext ctx) {
		return this.visit(ctx.things());
	}
	
	@Override
	public DAPOQLValue visitVariable(VariableContext ctx) {
		String var_name = ctx.VAR_NAME().getText();
		
		DAPOQLVariable var = poql.findVariable(var_name);
		
		DAPOQLValue v = new DAPOQLValue();
		
		if (var != null) {
			v.result = var.getValue();
			v.type = var.getType();		
		} else {
			throw new RuntimeException("Var "+var_name+" does not exist!");
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterNegation(FilterNegationContext ctx) {
		DAPOQLValue value = new DAPOQLValue();
		
		DAPOQLValue vsubtree = this.visit(ctx.filter_expression());
		FilterTree subtree = vsubtree.filterTree;
		FilterTree tree = poql.createNotNode(subtree);
		
		value.filterTree = tree;
		
		return value;
	}
	
	@Override
	public DAPOQLValue visitFilterCombined(FilterCombinedContext ctx) {
		DAPOQLValue value = new DAPOQLValue();
		
		DAPOQLValue voperator = this.visit(ctx.node());
		int operator = voperator.nodeType;		
		
		DAPOQLValue vsubtreeL = this.visit(ctx.filter_expression(0));
		DAPOQLValue vsubtreeR = this.visit(ctx.filter_expression(1));
		FilterTree subtreeL = vsubtreeL.filterTree;
		FilterTree subtreeR = vsubtreeR.filterTree;
		FilterTree tree = poql.createNode(subtreeL, subtreeR, operator);
		
		value.filterTree = tree;
		
		return value;
	}
	
	@Override
	public DAPOQLValue visitNode(NodeContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		switch (ctx.n.getType()) {
		case dapoqlParser.AND:
			v.nodeType = FilterTree.NODE_AND;
			break;
		case dapoqlParser.OR:
			v.nodeType = FilterTree.NODE_OR;
			break;
		default:
			break;
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterField(FilterFieldContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue voperator = this.visit(ctx.operator());
		DAPOQLValue vid = this.visit(ctx.ids());
		
		FilterTree tree = null;
		
		if (voperator.operator == FilterTree.OPERATOR_CHANGED) {
			tree = poql.createChangedTerminalFilter(vid.keyStr,
					voperator.changedFrom,voperator.changedTo);
		} else {
			tree = poql.createTerminalFilter(
					vid.keyId, vid.keyStr, voperator.operatorValue, voperator.operator,
					vid.isAttribute);
		}
		
		v.filterTree = tree;
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitIds(IdsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		Token tk = ctx.getStart();
				
		int tokenType = tk.getType();
		String tokenStr = tk.getText();
		
		if (tokenType == dapoqlParser.IDATT) {
			v.isAttribute = true;
		} else {
			v.isAttribute = false;
		}
		
		v.keyId = tokenType;
		v.keyStr = tokenStr;
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitOperator(OperatorContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		v.operatorValue = ctx.v.getText();
		
		switch (ctx.op.getType()) {
		case dapoqlParser.EQUAL:
			v.operator = FilterTree.OPERATOR_EQUAL;
			break;
		case dapoqlParser.EQUAL_OR_GREATER:
			v.operator = FilterTree.OPERATOR_EQUAL_OR_GREATER_THAN;
			break;
		case dapoqlParser.EQUAL_OR_SMALLER:
			v.operator = FilterTree.OPERATOR_EQUAL_OR_SMALLER_THAN;
			break;
		case dapoqlParser.DIFFERENT:
			v.operator = FilterTree.OPERATOR_DIFFERENT;
			break;
		case dapoqlParser.GREATER:
			v.operator = FilterTree.OPERATOR_GREATER_THAN;
			break;
		case dapoqlParser.SMALLER:
			v.operator = FilterTree.OPERATOR_SMALLER_THAN;
			break;
		case dapoqlParser.CONTAINS:
			v.operator = FilterTree.OPERATOR_CONTAINS;
			break;
		case dapoqlParser.CHANGED:
			v.operator = FilterTree.OPERATOR_CHANGED;
			v.changedFrom = ctx.f.getText();
			v.changedTo = ctx.t.getText();
			break;
		default:
			break;
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitSet_operator(Set_operatorContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		v.setOperator = ctx.o.getType();
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitThingsSetOperator(ThingsSetOperatorContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		if (ctx.o != null) {
			DAPOQLValue vL = this.visit(ctx.children.get(0));
			DAPOQLValue vR = this.visit(ctx.children.get(2));
			
			DAPOQLValue vO = this.visit(ctx.o);
			
			Class<?> type = null;
			if (vL.type == vR.type) {
				type = vL.type;
			} else {
				if (vL.type == null) {
					type = vR.type;
				} else if (vR.type == null) {
					type = vL.type;
				} else {
					type = null;
				}
			}
			
			Set<Object> resultSetOperation = poql.set_operation(vO.setOperator, vL.result.keySet(), vR.result.keySet(), type);
			v.result = new HashMap<>();
			
			for (Object o: resultSetOperation) {
				v.result.put(o,null);
			}
			
			v.type = type;
		} else {
			v = this.visit(ctx.children.get(0));
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterObjects(FilterObjectsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.objects());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterCases(FilterCasesContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.cases());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterActivities(FilterActivitiesContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.activities());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterActivityInstances(
			FilterActivityInstancesContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.activityinstances());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterAttributes(FilterAttributesContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.attributes());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterClasses(FilterClassesContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.classes());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterEvents(FilterEventsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.events());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterRelations(FilterRelationsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.relations());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterRelationships(FilterRelationshipsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.relationships());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterVersions(FilterVersionsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.versions());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterPeriods(FilterPeriodsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.periods());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterProcesses(FilterProcessesContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.processes());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterLogs(FilterLogsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.logs());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitFilterDatamodels(FilterDatamodelsContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = this.visit(ctx.datamodels());
		DAPOQLValue vf = this.visit(ctx.f);
		Set<Object> resultFilter = poql.filter(vob.result.keySet(),vob.type, vf.filterTree);
		v.result = new HashMap<>();
		for (Object o: resultFilter) {
			v.result.put(o, null);
		}
		v.type = vob.type;
		return v;
	}
	
	@Override
	public DAPOQLValue visitObjectsOf(ObjectsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.objectsOf(vthings.result, vthings.type);
		v.type = SLEXMMObject.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitCasesOf(CasesOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.casesOf(vthings.result, vthings.type);
		v.type = SLEXMMCase.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitPeriodsOf(PeriodsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.periodsOf(vthings.result, vthings.type);
		v.type = SLEXMMPeriod.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitClassesOf(ClassesOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.classesOf(vthings.result, vthings.type);
		v.type = SLEXMMClass.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitActivitiesOf(ActivitiesOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.activitiesOf(vthings.result, vthings.type);
		v.type = SLEXMMActivity.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitActivityInstancesOf(ActivityInstancesOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.activityInstancesOf(vthings.result, vthings.type);
		v.type = SLEXMMActivityInstance.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitAttributesOf(AttributesOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.attributesOf(vthings.result, vthings.type);
		v.type = SLEXMMAttribute.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitEventsOf(EventsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.eventsOf(vthings.result, vthings.type);
		v.type = SLEXMMEvent.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitRelationshipsOf(RelationshipsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.relationshipsOf(vthings.result, vthings.type);
		v.type = SLEXMMRelationship.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitRelationsOf(RelationsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.relationsOf(vthings.result, vthings.type);
		v.type = SLEXMMRelation.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitVersionsOf(VersionsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.versionsOf(vthings.result, vthings.type);
		v.type = SLEXMMObjectVersion.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitProcessesOf(ProcessesOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.processesOf(vthings.result, vthings.type);
		v.type = SLEXMMProcess.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitLogsOf(LogsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.logsOf(vthings.result, vthings.type);
		v.type = SLEXMMLog.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitDatamodelsOf(DatamodelsOfContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.things());
		v.result = poql.datamodelsOf(vthings.result, vthings.type);
		v.type = SLEXMMDataModel.class;
		return v;
	}
	
	@Override
	public DAPOQLValue visitVersionsRelatedTo(VersionsRelatedToContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		DAPOQLValue vaux = new DAPOQLValue();
		
		DAPOQLValue vthings = this.visit(ctx.versions());
		vaux.result = poql.versionsRelatedTo(vthings.result.keySet(), vthings.type);
		vaux.type = SLEXMMObjectVersion.class;
		v.type = vaux.type;
		
		if (ctx.scope() != null) {
		
			DAPOQLValue scope = this.visit(ctx.scope());
				
			v = filterByScope(scope, vthings, vaux);
			
		} else {
			v.result = vaux.result;
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitLoop(LoopContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		String var_name = ctx.VAR_NAME().getText();
		
		if (poql.findVariable(var_name) == null) {
		
			DAPOQLValue vthings = this.visit(ctx.things());
			
			DAPOQLVariable var = poql.createVariable(var_name, vthings.type, null);
					
			for (Object o: vthings.result.keySet()) {
		
				HashMap<Object,HashSet<Integer>> s = new HashMap<>();
				s.put(o, null);
				var.setValue(s);
				
				this.visit(ctx.code_block());
				
			}
			
			poql.removeVariable(var_name);
			
		} else {
			throw new RuntimeException("Var "+var_name+" already exists");
		}
		return v;
	}
	
	@Override
	public DAPOQLValue visitConditionEmpty(ConditionEmptyContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vT = this.visit(ctx.t);
		
		if (vT.result.isEmpty()) {
			v.conditionBoolean = true;
		} else {
			v.conditionBoolean = false;
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitConditionNotEmpty(ConditionNotEmptyContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vT = this.visit(ctx.t);
		
		if (vT.result.isEmpty()) {
			v.conditionBoolean = false;
		} else {
			v.conditionBoolean = true;
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitIf_block(If_blockContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vC = new DAPOQLValue();
		vC.conditionBoolean = false;
		
		int elseifBlocks = ctx.conditional_expression().size();
		int i = 0;
		
		while (!vC.conditionBoolean && i < elseifBlocks) {
			vC = this.visit(ctx.conditional_expression(i));
			if (!vC.conditionBoolean) {
				i++;
			}
		}
		
		if (vC.conditionBoolean) {
			this.visit(ctx.code_block(i));
		} else {
			if (ctx.e != null) {
				this.visit(ctx.e);
			}
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitScope(ScopeContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		if (ctx.ACTIVITY() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_ACTIVITY;
		} else if (ctx.ACTIVITYINSTANCE() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_ACTIVITY_INSTANCE;
		} else if (ctx.OBJECT() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_OBJECT;
		} else if (ctx.VERSION() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_VERSION;
		} else if (ctx.CLASS() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_CLASS;
		} else if (ctx.ATTRIBUTE() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_ATTRIBUTE;
		} else if (ctx.RELATION() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_RELATION;
		} else if (ctx.RELATIONSHIP() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_RELATIONSHIP;
		} else if (ctx.CASE() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_CASE;
		} else if (ctx.EVENT() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_EVENT;
		} else if (ctx.DATAMODEL() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_DATAMODEL;
		} else if (ctx.PROCESS() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_PROCESS;
		} else if (ctx.LOG() != null) {
			v.scope = DAPOQLFunctions.ID_TYPE_LOG;
		} else {
			v.scope = DAPOQLFunctions.ID_TYPE_ANY;
		}
		
		return v;
	}
	
	public DAPOQLValue filterByScope(DAPOQLValue scope, DAPOQLValue valA, DAPOQLValue valB) {
		
		DAPOQLValue v = new DAPOQLValue();
		v.result = new HashMap<>();
		v.type = valA.type;
		
		HashMap<Integer,Object> elements = new HashMap<>();
		
		for (Object o: valA.result.keySet()) {
			SLEXMMAbstractDatabaseObject ob = (SLEXMMAbstractDatabaseObject) o;
			elements.put(ob.getId(), ob);
		}
		
		for (Object o: valB.result.keySet()) {
			SLEXMMAbstractDatabaseObject ob = (SLEXMMAbstractDatabaseObject) o;
			elements.put(ob.getId(), ob);
		}
		
		HashMap<Object,HashSet<Integer>> scopeA = poql.getScopeOf(scope.scope,valA.result, valA.type);
		HashMap<Object,HashSet<Integer>> scopeB = poql.getScopeOf(scope.scope,valB.result, valB.type);
	
		HashSet<Object> intersectionSet = new HashSet<>();
		intersectionSet.addAll(scopeA.keySet());
		intersectionSet.retainAll(scopeB.keySet());
	
		for (Object o: intersectionSet) {
			Set<Integer> originA = scopeA.get(o);
			Set<Integer> originB = scopeB.get(o);
			for (Integer concId: originB) {
				Object concElem = elements.get(concId);
				HashSet<Integer> originsOfBinA = valB.result.get(concElem);
				for (Integer originBinA: originsOfBinA) {
					if (originA.contains(originBinA)) {
						v.result.put(concElem,originsOfBinA);
						break;
					}
				}
			}
		}
		
		return v;
	}
	
	public DAPOQLValue concurrentWith(DAPOQLValue originalValues, boolean hasScope, DAPOQLValue scope) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue vob = originalValues;
		HashMap<Object,HashSet<Integer>> vconc = poql.concurrentWith(vob.result,vob.type);
		
		v.result = new HashMap<Object,HashSet<Integer>>();
		v.type = vob.type;
		
		if (hasScope) {
			
			DAPOQLValue vconval = new DAPOQLValue();
			vconval.result = vconc;
			vconval.type = vob.type;
			
			v = filterByScope(scope, vob, vconval);
			
		} else {
			v.result = vconc;
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithActivities(
			ConcurrentWithActivitiesContext ctx) {
		
		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);		
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithActivityInstances(
			ConcurrentWithActivityInstancesContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithAttributes(
			ConcurrentWithAttributesContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithCases(ConcurrentWithCasesContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithClasses(ConcurrentWithClassesContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithEvents(ConcurrentWithEventsContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithObjects(ConcurrentWithObjectsContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithRelations(
			ConcurrentWithRelationsContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithRelationships(
			ConcurrentWithRelationshipsContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithVersions(
			ConcurrentWithVersionsContext ctx) {

		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithProcesses(ConcurrentWithProcessesContext ctx) {
		
		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithLogs(ConcurrentWithLogsContext ctx) {
		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitConcurrentWithDatamodels(ConcurrentWithDatamodelsContext ctx) {
		DAPOQLValue vob = this.visit(ctx.t5);
		DAPOQLValue scope = null;
		boolean hasScope = false;
		
		if (ctx.scope() != null) {
			hasScope = true;
		}
		
		return concurrentWith(vob, hasScope, scope);
	}
	
	@Override
	public DAPOQLValue visitStartPeriod(StartPeriodContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		v.timestamp = new DAPOQLTimestamp();
		
		DAPOQLValue ps = this.visit(ctx.periods());
		
		long min = Long.MAX_VALUE;
		
		for (Object o: ps.result.keySet()) {
			SLEXMMPeriod p = (SLEXMMPeriod) o;
			if (p.getStart() < min) {
				min = p.getStart();
			}
		}
		
		v.timestamp.timestamp = min;
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitEndPeriod(EndPeriodContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		v.timestamp = new DAPOQLTimestamp();
		
		DAPOQLValue ps = this.visit(ctx.periods());
		
		long max = Long.MIN_VALUE;
		
		for (Object o: ps.result.keySet()) {
			SLEXMMPeriod p = (SLEXMMPeriod) o;
			if (p.getEnd() > max) {
				max = p.getEnd();
			}
		}
		
		v.timestamp.timestamp = max;
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitTimeoperator(TimeoperatorContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		switch (ctx.op.getType()) {
		case dapoqlParser.PLUS:
			v.operator = DAPOQLTimestamp.OPERATOR_PLUS;
			break;
		case dapoqlParser.MINUS:
			v.operator = DAPOQLTimestamp.OPERATOR_MINUS;
			break;
		default:
			throw new RuntimeException("Operator not valid");
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitTimestampFromString(TimestampFromStringContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		v.timestamp = new DAPOQLTimestamp();
		
		String dateStr = "";
		
		if (ctx.STRING() != null) {
			dateStr = ctx.STRING().getText();
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Date format not valid");
		}
		
		v.timestamp.timestamp = date.getTime();
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitTimestampOperation(TimestampOperationContext ctx) {
		
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue v1 = this.visit(ctx.t1);
		
		DAPOQLValue op = this.visit(ctx.op);
		
		DAPOQLValue v2 = this.visit(ctx.t2);
		
		switch(op.operator) {
		case DAPOQLTimestamp.OPERATOR_PLUS:
			v.timestamp = new DAPOQLTimestamp();
			v.timestamp.timestamp = v1.timestamp.timestamp + v2.timestamp.timestamp;
			break;
		case DAPOQLTimestamp.OPERATOR_MINUS:
			v.timestamp = new DAPOQLTimestamp();
			v.timestamp.timestamp = v1.timestamp.timestamp - v2.timestamp.timestamp;
			break;
		default:
			throw new RuntimeException("Operator not valid");
		}
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitTimestampOffsetFromString(TimestampOffsetFromStringContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		v.timestamp = new DAPOQLTimestamp();
		
		String dateStr = "";
		
		if (ctx.STRING() != null) {
			dateStr = ctx.STRING().getText();
		}
		
		DateFormat format = new SimpleDateFormat("dd HH:mm:ss", Locale.ENGLISH);
		
		Date date = null;
		Date dateOrigin = new Date(90000000L);
		try {
			date = format.parse(dateStr);
			date = new Date(dateOrigin.getTime() + date.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Date format not valid");
		}
		
		v.timestamp.timestamp = date.getTime();
		
		return v;
	}
	
	@Override
	public DAPOQLValue visitCreatePeriod(CreatePeriodContext ctx) {
		DAPOQLValue v = new DAPOQLValue();
		
		DAPOQLValue startTimestamp = this.visit(ctx.t5);
		DAPOQLValue endTimestamp = this.visit(ctx.t6);
		
		SLEXMMPeriod p = new SLEXMMPeriod(startTimestamp.timestamp.timestamp,
				endTimestamp.timestamp.timestamp);
		
		v.result = new HashMap<>();
		v.result.put(p, new HashSet<Integer>());
		
		v.type = SLEXMMPeriod.class;
		
		return v;
	};
	
}
