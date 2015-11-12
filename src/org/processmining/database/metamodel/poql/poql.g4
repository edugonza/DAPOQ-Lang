// Define a grammar

grammar poql;

@header {
  import java.util.List;
  import java.util.Set;
  import org.processmining.openslex.metamodel.SLEXMMCase;
  import org.processmining.openslex.metamodel.SLEXMMObject;
  import org.processmining.openslex.metamodel.SLEXMMObjectVersion;
  import org.processmining.openslex.metamodel.SLEXMMEvent;
  import org.processmining.openslex.metamodel.SLEXMMActivity;
  import org.processmining.openslex.metamodel.SLEXMMCase;
  import org.processmining.openslex.metamodel.SLEXMMClass;
  import org.processmining.openslex.metamodel.SLEXMMActivityInstance;
  import org.processmining.openslex.metamodel.SLEXMMRelation;
  import org.processmining.openslex.metamodel.SLEXMMRelationship;
  import org.processmining.openslex.metamodel.SLEXMMAttribute;
}

@lexer::members 
{
  @Override
  public void recover(RecognitionException ex) 
  {
    throw new RuntimeException(ex.getMessage()); 
  }
}

@parser::members {
  
  public POQLFunctions poql = new POQLFunctions();
  
  @Override
  public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException ex)
  {
  	IntervalSet expectedTokens = getExpectedTokens();
	Set<Integer> set = expectedTokens.toSet();
	Token offTok = poql.getOffendingToken();
	if (offTok == null) {
		offTok = offendingToken;
	}
	poql.computeSuggestions(offTok,set);
	
    throw new RuntimeException(msg); 
  }
  
}

prog [int level] returns [Set<Object> result, Class type]: 
	  VAR VAR_NAME
	  { if (poql.findVariable($level,$VAR_NAME.text) != null) {
	  		poql.setOffendingToken(getTokenStream().get(getCurrentToken().getTokenIndex()-1));
	  	}
	  }
	  { poql.findVariable($level,$VAR_NAME.text) == null }?
	  {
	  	POQLVariable var = poql.createVariable($level,$VAR_NAME.text,null,null);
	  }
	  ASSIGNMENT_SIGN vv=variable_value
	  {
	  	var.setType($vv.type);
	  	var.setValue($vv.result);
	  }
	  END_STATEMENT p=prog[level]
	  {
	  	$result = $p.result; $type = $p.type;
	  }
	| RETURN ro=returnable_object { $result = $ro.list; $type = $ro.type; }
;

returnable_object returns [Set<Object> list, Class type]:
	  t=things { $list = $t.list; $type = $t.type; }
	;

variable [int typeAllowed] returns [Set<Object> list, Class type]:
	  VAR_NAME
	  { if (poql.findVariable(poql.getLastLevel(),$VAR_NAME.text) == null) {
	  		poql.setOffendingToken(getTokenStream().get(getCurrentToken().getTokenIndex()-1));
	  	}
	  }
	  { poql.findVariable(poql.getLastLevel(),$VAR_NAME.text) != null && 
	  	($typeAllowed == poql.ID_TYPE_ANY ||
	  	 $typeAllowed == poql.typeToInt(poql.findVariable(poql.getLastLevel(),$VAR_NAME.text).getType())
	  	)
	  }?
	  { POQLVariable var = poql.findVariable(poql.getLastLevel(),$VAR_NAME.text); $list = var.getValue(); $type = var.getType(); } 
	  (f=filter[poql.typeToInt($type)])? { if ($f.ctx != null) { $list = poql.filter($list,$type,$f.conditions); } }
	;

variable_value returns [Set<Object> result, Class type]:
	  t=things { $result = $t.list; $type = $t.type; }
	| NULL { $result = null; $type = null; }
	;

set_operator returns [Integer type]:
	  UNION { $type = $UNION.type; }
	| INTERSECTION { $type = $INTERSECTION.type; }
	| EXCLUDING { $type = $EXCLUDING.type; }
	;

things returns [Set<Object> list, Class type]:
	  {poql.checkVariable(this,poql.ID_TYPE_CASE)}? t1=cases (o=set_operator tt1=cases)? { $type = $t1.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t1.list,$tt1.list,$type);} else { $list = $t1.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_OBJECT)}? t2=objects (o=set_operator tt2=objects)? { $type = $t2.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t2.list,$tt2.list,$type);} else { $list = $t2.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_EVENT)}? t3=events (o=set_operator tt3=events)? { $type = $t3.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t3.list,$tt3.list,$type);} else { $list = $t3.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_CLASS)}? t4=classes (o=set_operator tt4=classes)? { $type = $t4.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t4.list,$tt4.list,$type);} else { $list = $t4.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_VERSION)}? t5=versions (o=set_operator tt5=versions)? { $type = $t5.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t5.list,$tt5.list,$type);} else { $list = $t5.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_ACTIVITY)}? t6=activities (o=set_operator tt6=activities)? { $type = $t6.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t6.list,$tt6.list,$type);} else { $list = $t6.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_RELATION)}? t7=relations (o=set_operator tt7=relations)? { $type = $t7.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t7.list,$tt7.list,$type);} else { $list = $t7.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_RELATIONSHIP)}? t8=relationships (o=set_operator tt8=relationships)? { $type = $t8.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t8.list,$tt8.list,$type);} else { $list = $t8.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_ACTIVITY_INSTANCE)}? t9=activityinstances (o=set_operator tt9=activityinstances)? { $type = $t9.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t9.list,$tt9.list,$type);} else { $list = $t9.list; } }
	| {poql.checkVariable(this,poql.ID_TYPE_ATTRIBUTE)}? t10=attributes (o=set_operator tt10=attributes)? { $type = $t10.type; if ($o.ctx != null) {$list = poql.set_operation($o.type,$t10.list,$tt10.list,$type);} else { $list = $t10.list; } }
	;
 	
objects returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  OBJECTSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.objectsOf($t1.list,$t1.type); $type=SLEXMMObject.class; }
	| t2=allObjects{ $list = $t2.list; $type = $t2.type; }
	| t3=objects f=filter[poql.ID_TYPE_OBJECT] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_OBJECT] { $list = $t4.list; $type = $t4.type; }
	;
 	
cases returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  CASESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.casesOf($t1.list,$t1.type); $type=SLEXMMCase.class; }
	| t2=allCases{ $list = $t2.list; $type = $t2.type; }
	| t3=cases f=filter[poql.ID_TYPE_CASE] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_CASE] { $list = $t4.list; $type = $t4.type; }
	;
	
events returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  EVENTSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.eventsOf($t1.list,$t1.type); $type=SLEXMMEvent.class;}
	| t2=allEvents{ $list = $t2.list; $type = $t2.type; }
	| t3=events f=filter[poql.ID_TYPE_EVENT] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_EVENT] { $list = $t4.list; $type = $t4.type; }
	;
	
classes returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  CLASSESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.classesOf($t1.list,$t1.type); $type=SLEXMMClass.class;}
	| t2=allClasses{ $list = $t2.list; $type = $t2.type; }
	| t3=classes f=filter[poql.ID_TYPE_CLASS] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_CLASS] { $list = $t4.list; $type = $t4.type; }
	; 
	
versions returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  VERSIONSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.versionsOf($t1.list,$t1.type); $type=SLEXMMObjectVersion.class;}
	| t2=allVersions{ $list = $t2.list; $type = $t2.type; }
	| VERSIONSRELATEDTO OPEN_PARENTHESIS t5=versions CLOSE_PARENTHESIS { $list = poql.versionsRelatedTo($t5.list,$t5.type); $type=SLEXMMObjectVersion.class; }
	| t3=versions f=filter[poql.ID_TYPE_VERSION] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_VERSION] { $list = $t4.list; $type = $t4.type; }
	;
	
activities returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  ACTIVITIESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.activitiesOf($t1.list,$t1.type); $type=SLEXMMActivity.class;}
	| t2=allActivities { $list = $t2.list; $type = $t2.type; }
	| t3=activities f=filter[poql.ID_TYPE_ACTIVITY] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_ACTIVITY] { $list = $t4.list; $type = $t4.type; }
	;
	
relations returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  RELATIONSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.relationsOf($t1.list,$t1.type); $type=SLEXMMRelation.class;}
	| t2=allRelations { $list = $t2.list; $type = $t2.type; }
	| t3=relations f=filter[poql.ID_TYPE_RELATION] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_RELATION] { $list = $t4.list; $type = $t4.type; }
	;
	
relationships returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  RELATIONSHIPSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.relationshipsOf($t1.list,$t1.type); $type=SLEXMMRelationship.class;}
	| t2=allRelationships { $list = $t2.list; $type = $t2.type; }
	| t3=relationships f=filter[poql.ID_TYPE_RELATIONSHIP] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_RELATIONSHIP] { $list = $t4.list; $type = $t4.type; }
	;
	
activityinstances returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  ACTIVITYINSTANCESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.activityInstancesOf($t1.list,$t1.type); $type=SLEXMMActivityInstance.class;}
	| t2=allActivityInstances { $list = $t2.list; $type = $t2.type; }
	| t3=activityinstances f=filter[poql.ID_TYPE_ACTIVITY_INSTANCE] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_ACTIVITY_INSTANCE] { $list = $t4.list; $type = $t4.type; }
	;
	
attributes returns [Set<Object> list, Class type] locals [POQLVariable var]:
	  ATTRIBUTESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS { $list = poql.attributesOf($t1.list,$t1.type); $type=SLEXMMAttribute.class;}
	| t2=allAttributes { $list = $t2.list; $type = $t2.type; }
	| t3=attributes f=filter[poql.ID_TYPE_ATTRIBUTE] { $list = poql.filter($t3.list,$t3.type,$f.conditions); $type = $t3.type; }
	| t4=variable[poql.ID_TYPE_ATTRIBUTE] { $list = $t4.list; $type = $t4.type; }
	;
	
filter [int type_id] returns [FilterTree conditions]: WHERE f=filter_expression[$type_id] { $conditions = $f.tree; }
	;

filter_expression [int type_id] returns [FilterTree tree]:
	  NOT f0=filter_expression[$type_id] { $tree = poql.createNotNode($f0.tree); }
	| OPEN_PARENTHESIS f1=filter_expression[$type_id] node f2=filter_expression[$type_id] CLOSE_PARENTHESIS { $tree = poql.createNode($f1.tree,$f2.tree,$node.node_id); }
	| ids[$type_id] operator[$type_id,$ids.att] {
		if ($operator.operator_id == FilterTree.OPERATOR_CHANGED) {
			$tree = poql.createChangedTerminalFilter($ids.name,$operator.valueFrom,$operator.valueTo);
		} else {
			$tree = poql.createTerminalFilter($ids.id,$ids.name,$operator.value,$operator.operator_id,$ids.att);
		}
	}
	;

node returns [int node_id]:
	  AND {$node_id = FilterTree.NODE_AND; }
	| OR  {$node_id = FilterTree.NODE_OR; }
	;

operator [int type_id, boolean att] returns [int operator_id, String value, String valueFrom, String valueTo]:
	  EQUAL STRING {$operator_id = FilterTree.OPERATOR_EQUAL; $value = $STRING.text; }
	| DIFFERENT STRING {$operator_id = FilterTree.OPERATOR_DIFFERENT; $value = $STRING.text; }
	| EQUAL_OR_GREATER STRING {$operator_id = FilterTree.OPERATOR_EQUAL_OR_GREATER_THAN; $value = $STRING.text; }
	| EQUAL_OR_SMALLER STRING {$operator_id = FilterTree.OPERATOR_EQUAL_OR_SMALLER_THAN; $value = $STRING.text; }
	| GREATER STRING {$operator_id = FilterTree.OPERATOR_GREATER_THAN; $value = $STRING.text; }
	| SMALLER STRING {$operator_id = FilterTree.OPERATOR_SMALLER_THAN; $value = $STRING.text; }
	| CONTAINS STRING {$operator_id = FilterTree.OPERATOR_CONTAINS; $value = $STRING.text; }
	| {$type_id == poql.ID_TYPE_VERSION && $att}? CHANGED (FROM f13=STRING)? (TO f14=STRING)? {$operator_id = FilterTree.OPERATOR_CHANGED; $valueFrom = $f13.text; $valueTo = $f14.text;}
	;
	
ids [int type_id] returns [String name, boolean att, int id]:
	  {$type_id == poql.ID_TYPE_VERSION}? i1=id_version {$name = $i1.name; $att = $i1.att; $id = $i1.id;}
	| {$type_id == poql.ID_TYPE_OBJECT}? i2=id_object {$name = $i2.name; $att = $i2.att; $id = $i2.id;}
	| {$type_id == poql.ID_TYPE_CLASS}? i3=id_class {$name = $i3.name; $att = $i3.att; $id = $i3.id;}
	| {$type_id == poql.ID_TYPE_RELATIONSHIP}? i4=id_relationship {$name = $i4.name; $att = $i4.att; $id = $i4.id;}
	| {$type_id == poql.ID_TYPE_RELATION}? i5=id_relation {$name = $i5.name; $att = $i5.att; $id = $i5.id;}
	| {$type_id == poql.ID_TYPE_EVENT}? i6=id_event {$name = $i6.name; $att = $i6.att; $id = $i6.id;}
	| {$type_id == poql.ID_TYPE_CASE}? i7=id_case {$name = $i7.name; $att = $i7.att; $id = $i7.id;}
	| {$type_id == poql.ID_TYPE_ACTIVITY_INSTANCE}? i8=id_activity_instance {$name = $i8.name; $att = $i8.att; $id = $i8.id;}
	| {$type_id == poql.ID_TYPE_ACTIVITY}? i9=id_activity {$name = $i9.name; $att = $i9.att; $id = $i9.id;}
	| {$type_id == poql.ID_TYPE_ATTRIBUTE}? i10=id_attribute {$name = $i10.name; $att = $i10.att; $id = $i10.id;}
	;

id_version returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| OBJECT_ID {$name = $OBJECT_ID.text; $att = false; $id = $OBJECT_ID.type;}
	| START_TIMESTAMP {$name = $START_TIMESTAMP.text; $att = false; $id = $START_TIMESTAMP.type;}
	| END_TIMESTAMP {$name = $END_TIMESTAMP.text; $att = false; $id = $END_TIMESTAMP.type;}
	| IDATT {$name = $IDATT.text; $att = true;}
	;
	
id_object returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| CLASS_ID {$name = $CLASS_ID.text; $att = false; $id = $CLASS_ID.type;}
	;
	
id_class returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| DATAMODEL_ID {$name = $DATAMODEL_ID.text; $att = false; $id = $DATAMODEL_ID.type;}
	| NAME {$name = $NAME.text; $att = false; $id = $NAME.type;}
	;
	
id_relationship returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| SOURCE {$name = $SOURCE.text; $att = false; $id = $SOURCE.type;}
	| TARGET {$name = $TARGET.text; $att = false; $id = $TARGET.type;}
	| NAME {$name = $NAME.text; $att = false; $id = $NAME.type;}
	;
	
id_relation returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| SOURCE_OBJECT_VERSION_ID {$name = $SOURCE_OBJECT_VERSION_ID.text; $att = false; $id = $SOURCE_OBJECT_VERSION_ID.type;}
	| TARGET_OBJECT_VERSION_ID {$name = $TARGET_OBJECT_VERSION_ID.text; $att = false; $id = $TARGET_OBJECT_VERSION_ID.type;}
	| RELATIONSHIP_ID {$name = $RELATIONSHIP_ID.text; $att = false; $id = $RELATIONSHIP_ID.type;}
	| START_TIMESTAMP {$name = $START_TIMESTAMP.text; $att = false; $id = $START_TIMESTAMP.type;}
	| END_TIMESTAMP {$name = $END_TIMESTAMP.text; $att = false; $id = $END_TIMESTAMP.type;}
	;
	
id_event returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| ACTIVITY_INSTANCE_ID {$name = $ACTIVITY_INSTANCE_ID.text; $att = false; $id = $ACTIVITY_INSTANCE_ID.type;}
	| ORDERING {$name = $ORDERING.text; $att = false; $id = $ORDERING.type;}
	| TIMESTAMP {$name = $TIMESTAMP.text; $att = false; $id = $TIMESTAMP.type;}
	| LIFECYCLE {$name = $LIFECYCLE.text; $att = false; $id = $LIFECYCLE.type;}
	| RESOURCE {$name = $RESOURCE.text; $att = false; $id = $RESOURCE.type;}
	| IDATT {$name = $IDATT.text; $att = true;}
	;
	
id_case returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| NAME {$name = $NAME.text; $att = false; $id = $NAME.type;}
	;
	
id_activity_instance returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type;}
	| ACTIVITY_ID {$name = $ACTIVITY_ID.text; $att = false; $id = $ACTIVITY_ID.type;}
	;
	
id_activity returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type; }
	| PROCESS_ID {$name = $PROCESS_ID.text; $att = false; $id = $PROCESS_ID.type; }
	| NAME {$name = $NAME.text; $att = false; $id = $NAME.type; }
	;
	
id_attribute returns [String name, boolean att, int id]:
	  ID {$name = $ID.text; $att = false; $id = $ID.type; }
	| CLASS_ID {$name = $CLASS_ID.text; $att = false; $id = $CLASS_ID.type; }
	| NAME {$name = $NAME.text; $att = false; $id = $NAME.type; }
	;

allObjects returns [Set<Object> list, Class type]: ALLOBJECTS { $list = poql.getAllObjects(); $type=SLEXMMObject.class;};
allCases returns [Set<Object> list, Class type]: ALLCASES { $list = poql.getAllCases(); $type=SLEXMMCase.class;};
allEvents returns [Set<Object> list, Class type]: ALLEVENTS { $list = poql.getAllEvents(); $type=SLEXMMEvent.class;};
allClasses returns [Set<Object> list, Class type]: ALLCLASSES { $list = poql.getAllClasses(); $type=SLEXMMClass.class;};
allVersions returns [Set<Object> list, Class type]: ALLVERSIONS { $list = poql.getAllVersions(); $type=SLEXMMObjectVersion.class;};
allActivities returns [Set<Object> list, Class type]: ALLACTIVITIES { $list = poql.getAllActivities(); $type=SLEXMMActivity.class;};
allRelations returns [Set<Object> list, Class type]: ALLRELATIONS { $list = poql.getAllRelations(); $type=SLEXMMRelation.class;};
allRelationships returns [Set<Object> list, Class type]: ALLRELATIONSHIPS { $list = poql.getAllRelationships(); $type=SLEXMMRelationship.class;};
allActivityInstances returns [Set<Object> list, Class type]: ALLACTIVITYINSTANCES { $list = poql.getAllActivityInstances(); $type=SLEXMMActivityInstance.class;};
allAttributes returns [Set<Object> list, Class type]: ALLATTRIBUTES { $list = poql.getAllAttributes(); $type=SLEXMMAttribute.class;};

UNION: U N I O N ;
INTERSECTION: I N T E R S E C T I O N ;
EXCLUDING: E X C L U D I N G ;

CASESOF: C A S E S O F ;
OBJECTSOF: O B J E C T S O F ;
EVENTSOF: E V E N T S O F ;
CLASSESOF: C L A S S E S O F ;
VERSIONSOF: V E R S I O N S O F ;
ACTIVITIESOF: A C T I V I T I E S O F ;
VERSIONSRELATEDTO: V E R S I O N S R E L A T E D T O ;
RELATIONSOF: R E L A T I O N S O F ;
RELATIONSHIPSOF: R E L A T I O N S H I P S O F ;
ACTIVITYINSTANCESOF: A C T I V I T Y I N S T A N C E S O F ;
ATTRIBUTESOF: A T T R I B U T E S O F ;

ALLOBJECTS: A L L O B J E C T S ;
ALLCASES: A L L C A S E S ;
ALLEVENTS: A L L E V E N T S ;
ALLCLASSES: A L L C L A S S E S ;
ALLVERSIONS: A L L V E R S I O N S ;
ALLACTIVITIES: A L L A C T I V I T I E S ;
ALLRELATIONS: A L L R E L A T I O N S ;
ALLRELATIONSHIPS: A L L R E L A T I O N S H I P S ;
ALLACTIVITYINSTANCES: A L L A C T I V I T Y I N S T A N C E S ;
ALLATTRIBUTES: A L L A T T R I B U T E S ;

// tokens for filters
ID: I D ;
DATAMODEL_ID: D A T A M O D E L '_' I D ;
NAME: N A M E ;
CLASS_ID: C L A S S '_' I D ;
SOURCE: S O U R C E ;
TARGET: T A R G E T ;
OBJECT_ID: O B J E C T '_' I D ;
START_TIMESTAMP: S T A R T '_' T I M E S T A M P ;
END_TIMESTAMP: E N D '_' T I M E S T A M P ;
SOURCE_OBJECT_VERSION_ID: S O U R C E '_' O B J E C T '_' V E R S I O N '_' I D ;
TARGET_OBJECT_VERSION_ID: T A R G E T '_' O B J E C T '_' V E R S I O N '_' I D ;
RELATIONSHIP_ID: R E L A T I O N S H I P '_' I D ;
ACTIVITY_INSTANCE_ID: A C T I V I T Y '_' I N S T A N C E '_' I D ;
ORDERING: O R D E R I N G ;
TIMESTAMP: T I M E S T A M P ;
LIFECYCLE: L I F E C Y C L E ;
RESOURCE: R E S O U R C E ;
ACTIVITY_ID: A C T I V I T Y '_' I D ;
PROCESS_ID: P R O C E S S '_' I D ;

OPEN_PARENTHESIS: PARENTHESIS_LEFT ;
CLOSE_PARENTHESIS: PARENTHESIS_RIGHT ;

WHERE: W H E R E ;

EQUAL: EQUAL_SIGN EQUAL_SIGN;
DIFFERENT: SMALLER_SIGN GREATER_SIGN;
EQUAL_OR_GREATER: EQUAL_SIGN GREATER_SIGN;
EQUAL_OR_SMALLER: EQUAL_SIGN SMALLER_SIGN;
GREATER: GREATER_SIGN;
SMALLER: SMALLER_SIGN;
CONTAINS: C O N T A I N S ;
AND: A N D;
OR: O R;
NOT: N O T ;
CHANGED: C H A N G E D ;
FROM: F R O M ;
TO: T O ;

VAR: V A R ;
RETURN: R E T U R N ;
NULL: N U L L ;

END_STATEMENT: SEMICOLON;
ASSIGNMENT_SIGN: EQUAL_SIGN;

STRING: '"' ~('\r' | '\n' | '"' )* '"' { setText(getText().substring(1, getText().length() - 1)); };

IDATT : 'at.' IDNOATT { setText(getText().substring(3, getText().length())); };

VAR_NAME: '_'[a-z,A-Z]+[0-9,a-z,A-Z]*;

IDNOATT : ~('\r' | '\n' | '\t' | ' ' | '(' | ')' | '<' | '>' | '=' | ';' )+ ;

COMMENT: SLASH SLASH ~('\r'|'\n')* -> skip ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

fragment SLASH:('/');
fragment SEMICOLON:(';');
fragment PARENTHESIS_LEFT:('(');
fragment PARENTHESIS_RIGHT:(')');
fragment SMALLER_SIGN:('<');
fragment GREATER_SIGN:('>');
fragment EQUAL_SIGN:('=');
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');