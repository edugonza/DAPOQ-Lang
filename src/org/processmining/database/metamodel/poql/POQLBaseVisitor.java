package org.processmining.database.metamodel.poql;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.Token;
import org.processmining.database.metamodel.poql.poqlParser.*;
import org.processmining.openslex.metamodel.SLEXMMActivity;
import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
import org.processmining.openslex.metamodel.SLEXMMAttribute;
import org.processmining.openslex.metamodel.SLEXMMCase;
import org.processmining.openslex.metamodel.SLEXMMClass;
import org.processmining.openslex.metamodel.SLEXMMEvent;
import org.processmining.openslex.metamodel.SLEXMMObject;
import org.processmining.openslex.metamodel.SLEXMMObjectVersion;
import org.processmining.openslex.metamodel.SLEXMMRelation;
import org.processmining.openslex.metamodel.SLEXMMRelationship;

public class POQLBaseVisitor extends poqlBaseVisitor<POQLValue> {

	private POQLFunctions poql;

	public POQLBaseVisitor(POQLFunctions poql) {
		this.poql = poql;
	}

	@Override
	public POQLValue visitAllActivities(AllActivitiesContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllActivities();
		value.type = SLEXMMActivity.class;

		return value;
	}

	@Override
	public POQLValue visitAllActivityInstances(AllActivityInstancesContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllActivityInstances();
		value.type = SLEXMMActivityInstance.class;

		return value;
	}

	@Override
	public POQLValue visitAllAttributes(AllAttributesContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllAttributes();
		value.type = SLEXMMAttribute.class;

		return value;
	}

	@Override
	public POQLValue visitAllCases(AllCasesContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllCases();
		value.type = SLEXMMCase.class;

		return value;
	}

	@Override
	public POQLValue visitAllClasses(AllClassesContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllClasses();
		value.type = SLEXMMClass.class;

		return value;
	}

	@Override
	public POQLValue visitAllEvents(AllEventsContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllEvents();
		value.type = SLEXMMEvent.class;

		return value;
	}

	@Override
	public POQLValue visitAllObjects(AllObjectsContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllObjects();
		value.type = SLEXMMObject.class;

		return value;
	}

	@Override
	public POQLValue visitAllRelations(AllRelationsContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllRelations();
		value.type = SLEXMMRelation.class;

		return value;
	}

	@Override
	public POQLValue visitAllRelationships(AllRelationshipsContext ctx) {
		POQLValue value = new POQLValue();

		value.result = poql.getAllRelationships();
		value.type = SLEXMMRelationship.class;

		return value;
	}

	@Override
	public POQLValue visitAllVersions(AllVersionsContext ctx) {
		POQLValue value = new POQLValue();
		value.result = poql.getAllVersions();
		value.type = SLEXMMObjectVersion.class;

		return value;
	}
	
	@Override
	public POQLValue visitVariable_definition(Variable_definitionContext ctx) {
		String var_name = ctx.VAR_NAME().getText();
		
		POQLVariable var = poql.findVariable(var_name);
		
		POQLValue v = null;
		
		if (var == null) {
		
			v = this.visit(ctx.variable_value());
		
			var = poql.createVariable(var_name, v.type, v.result);
		
		} else {
			throw new RuntimeException("Var "+var_name+" already exists!");
		}
		
		return v;
	}

	@Override
	public POQLValue visitVariable_value(Variable_valueContext ctx) {
		POQLValue v = new POQLValue();
		if (ctx.NULL() != null) {
			v.result = new HashSet<Object>();
			v.type = null;
			return v;
		} else {
			return super.visitVariable_value(ctx);
		}
	}
	
	@Override
	public POQLValue visitVariable_assignment(Variable_assignmentContext ctx) {
		String var_name = ctx.VAR_NAME().getText();
		
		POQLVariable var = poql.findVariable(var_name);
		
		POQLValue v = null;
		
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
	public POQLValue visitReturn_statement(Return_statementContext ctx) {
		return this.visit(ctx.returnable_object());
	}
	
	@Override
	public POQLValue visitReturnable_object(Returnable_objectContext ctx) {
		return this.visit(ctx.things());
	}
	
	@Override
	public POQLValue visitVariable(VariableContext ctx) {
		String var_name = ctx.VAR_NAME().getText();
		
		POQLVariable var = poql.findVariable(var_name);
		
		POQLValue v = new POQLValue();
		
		if (var != null) {
			v.result = var.getValue();
			v.type = var.getType();		
		} else {
			throw new RuntimeException("Var "+var_name+" does not exist!");
		}
		
		return v;
	}
	
	@Override
	public POQLValue visitFilterNegation(FilterNegationContext ctx) {
		POQLValue value = new POQLValue();
		
		POQLValue vsubtree = this.visit(ctx.filter_expression());
		FilterTree subtree = vsubtree.filterTree;
		FilterTree tree = poql.createNotNode(subtree);
		
		value.filterTree = tree;
		
		return value;
	}
	
	@Override
	public POQLValue visitFilterCombined(FilterCombinedContext ctx) {
		POQLValue value = new POQLValue();
		
		POQLValue voperator = this.visit(ctx.node());
		int operator = voperator.nodeType;		
		
		POQLValue vsubtreeL = this.visit(ctx.filter_expression(0));
		POQLValue vsubtreeR = this.visit(ctx.filter_expression(1));
		FilterTree subtreeL = vsubtreeL.filterTree;
		FilterTree subtreeR = vsubtreeR.filterTree;
		FilterTree tree = poql.createNode(subtreeL, subtreeR, operator);
		
		value.filterTree = tree;
		
		return value;
	}
	
	@Override
	public POQLValue visitNode(NodeContext ctx) {
		POQLValue v = new POQLValue();
		
		switch (ctx.n.getType()) {
		case poqlParser.AND:
			v.nodeType = FilterTree.NODE_AND;
			break;
		case poqlParser.OR:
			v.nodeType = FilterTree.NODE_OR;
			break;
		default:
			break;
		}
		
		return v;
	}
	
	@Override
	public POQLValue visitFilterField(FilterFieldContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue voperator = this.visit(ctx.operator());
		POQLValue vid = this.visit(ctx.ids());
		
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
	public POQLValue visitIds(IdsContext ctx) {
		POQLValue v = new POQLValue();
		
		Token tk = ctx.getStart();
				
		int tokenType = tk.getType();
		String tokenStr = tk.getText();
		
		if (tokenType == poqlParser.IDATT) {
			v.isAttribute = true;
		} else {
			v.isAttribute = false;
		}
		
		v.keyId = tokenType;
		v.keyStr = tokenStr;
		
		return v;
	}
	
	@Override
	public POQLValue visitOperator(OperatorContext ctx) {
		POQLValue v = new POQLValue();
		
		v.operatorValue = ctx.v.getText();
		
		switch (ctx.op.getType()) {
		case poqlParser.EQUAL:
			v.operator = FilterTree.OPERATOR_EQUAL;
			break;
		case poqlParser.EQUAL_OR_GREATER:
			v.operator = FilterTree.OPERATOR_EQUAL_OR_GREATER_THAN;
			break;
		case poqlParser.EQUAL_OR_SMALLER:
			v.operator = FilterTree.OPERATOR_EQUAL_OR_SMALLER_THAN;
			break;
		case poqlParser.DIFFERENT:
			v.operator = FilterTree.OPERATOR_DIFFERENT;
			break;
		case poqlParser.GREATER:
			v.operator = FilterTree.OPERATOR_GREATER_THAN;
			break;
		case poqlParser.SMALLER:
			v.operator = FilterTree.OPERATOR_SMALLER_THAN;
			break;
		case poqlParser.CONTAINS:
			v.operator = FilterTree.OPERATOR_CONTAINS;
			break;
		case poqlParser.CHANGED:
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
	public POQLValue visitSet_operator(Set_operatorContext ctx) {
		POQLValue v = new POQLValue();
		
		v.setOperator = ctx.o.getType();
		
		return v;
	}
	
	@Override
	public POQLValue visitThingsSetOperator(ThingsSetOperatorContext ctx) {
		POQLValue v = new POQLValue();
		
		if (ctx.o != null) {
			POQLValue vL = this.visit(ctx.children.get(0));
			POQLValue vR = this.visit(ctx.children.get(2));
			
			POQLValue vO = this.visit(ctx.o);
			
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
			
			v.result = poql.set_operation(vO.setOperator, vL.result, vR.result, type);
			v.type = type;
		} else {
			v = this.visit(ctx.children.get(0));
		}
		
		return v;
	}
	
	@Override
	public POQLValue visitFilterObjects(FilterObjectsContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.objects());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterCases(FilterCasesContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.cases());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterActivities(FilterActivitiesContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.activities());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterActivityInstances(
			FilterActivityInstancesContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.activityinstances());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterAttributes(FilterAttributesContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.attributes());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterClasses(FilterClassesContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.classes());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterEvents(FilterEventsContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.events());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterRelations(FilterRelationsContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.relations());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterRelationships(FilterRelationshipsContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.relationships());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterVersions(FilterVersionsContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.versions());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitFilterPeriods(FilterPeriodsContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vob = this.visit(ctx.periods());
		POQLValue vf = this.visit(ctx.f);
		v.result = poql.filter(vob.result,vob.type, vf.filterTree);
		v.type = vob.type;
		return v;
	}
	
	@Override
	public POQLValue visitObjectsOf(ObjectsOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.objectsOf(vthings.result, vthings.type);
		v.type = SLEXMMObject.class;
		return v;
	}
	
	@Override
	public POQLValue visitCasesOf(CasesOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.casesOf(vthings.result, vthings.type);
		v.type = SLEXMMCase.class;
		return v;
	}
	
	@Override
	public POQLValue visitPeriodsOf(PeriodsOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.periodsOf(vthings.result, vthings.type);
		v.type = POQLPeriod.class;
		return v;
	}
	
	@Override
	public POQLValue visitClassesOf(ClassesOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.classesOf(vthings.result, vthings.type);
		v.type = SLEXMMClass.class;
		return v;
	}
	
	@Override
	public POQLValue visitActivitiesOf(ActivitiesOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.activitiesOf(vthings.result, vthings.type);
		v.type = SLEXMMActivity.class;
		return v;
	}
	
	@Override
	public POQLValue visitActivityInstancesOf(ActivityInstancesOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.activityInstancesOf(vthings.result, vthings.type);
		v.type = SLEXMMActivityInstance.class;
		return v;
	}
	
	@Override
	public POQLValue visitAttributesOf(AttributesOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.attributesOf(vthings.result, vthings.type);
		v.type = SLEXMMAttribute.class;
		return v;
	}
	
	@Override
	public POQLValue visitEventsOf(EventsOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.eventsOf(vthings.result, vthings.type);
		v.type = SLEXMMEvent.class;
		return v;
	}
	
	@Override
	public POQLValue visitRelationshipsOf(RelationshipsOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.relationshipsOf(vthings.result, vthings.type);
		v.type = SLEXMMRelationship.class;
		return v;
	}
	
	@Override
	public POQLValue visitRelationsOf(RelationsOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.relationsOf(vthings.result, vthings.type);
		v.type = SLEXMMRelation.class;
		return v;
	}
	
	@Override
	public POQLValue visitVersionsOf(VersionsOfContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.things());
		v.result = poql.versionsOf(vthings.result, vthings.type);
		v.type = SLEXMMObjectVersion.class;
		return v;
	}
	
	@Override
	public POQLValue visitVersionsRelatedTo(VersionsRelatedToContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vthings = this.visit(ctx.versions());
		v.result = poql.versionsRelatedTo(vthings.result, vthings.type);
		v.type = SLEXMMObjectVersion.class;
		return v;
	}
	
	@Override
	public POQLValue visitLoop(LoopContext ctx) {
		POQLValue v = new POQLValue();
		
		String var_name = ctx.VAR_NAME().getText();
		
		if (poql.findVariable(var_name) == null) {
		
			POQLValue vthings = this.visit(ctx.things());
			
			POQLVariable var = poql.createVariable(var_name, vthings.type, null);
					
			for (Object o: vthings.result) {
		
				Set<Object> s = new HashSet<Object>();
				s.add(o);
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
	public POQLValue visitConditionEmpty(ConditionEmptyContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vT = this.visit(ctx.t);
		
		if (vT.result.isEmpty()) {
			v.conditionBoolean = true;
		} else {
			v.conditionBoolean = false;
		}
		
		return v;
	}
	
	@Override
	public POQLValue visitConditionNotEmpty(ConditionNotEmptyContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vT = this.visit(ctx.t);
		
		if (vT.result.isEmpty()) {
			v.conditionBoolean = false;
		} else {
			v.conditionBoolean = true;
		}
		
		return v;
	}
	
	@Override
	public POQLValue visitIf_block(If_blockContext ctx) {
		POQLValue v = new POQLValue();
		
		POQLValue vC = new POQLValue();
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
	
}
