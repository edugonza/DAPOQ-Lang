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

prog:
	  code_block
	  r=return_statement
	;

code_block:
	  (  variable_definition
	   | variable_assignment
	   | loop
	   | if_block
	  )*
	; 

variable_definition: 
	  VAR VAR_NAME ASSIGNMENT_SIGN variable_value END_STATEMENT
	;
	
variable_assignment:
	  VAR_NAME ASSIGNMENT_SIGN variable_value END_STATEMENT
	;

if_block:
	  IF conditional_expression THEN code_block
	  (ELSE IF conditional_expression THEN code_block)*
	  (ELSE e=code_block)?
	  FI
	;
	
conditional_expression:
	  t=things ISEMPTY #conditionEmpty
	| t=things ISNOTEMPTY #conditionNotEmpty
	;
	
loop:
	  FOREACH VAR_NAME IN things DO code_block DONE
	;

return_statement:
	  RETURN returnable_object
	;

returnable_object:
	  things
	;

variable:
	  VAR_NAME
	;

variable_value:
	  things
	| NULL
	;

set_operator:
	  o=UNION
	| o=INTERSECTION
	| o=EXCLUDING
	;

things:
	  t1=cases (o=set_operator tt1=cases)? #thingsSetOperator
	| t2=objects (o=set_operator tt2=objects)? #thingsSetOperator
	| t3=events (o=set_operator tt3=events)? #thingsSetOperator
	| t4=classes (o=set_operator tt4=classes)? #thingsSetOperator
	| t5=versions (o=set_operator tt5=versions)? #thingsSetOperator
	| t6=activities (o=set_operator tt6=activities)? #thingsSetOperator
	| t7=relations (o=set_operator tt7=relations)? #thingsSetOperator
	| t8=relationships (o=set_operator tt8=relationships)? #thingsSetOperator
	| t9=activityinstances (o=set_operator tt9=activityinstances)? #thingsSetOperator
	| t10=attributes (o=set_operator tt10=attributes)? #thingsSetOperator
	| t11=periods (o=set_operator tt11=periods)? #thingsSetOperator
	;

periods:
	  PERIODSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #periodsOf
	| t3=periods f=filter #filterPeriods
	| t4=variable #periodsVariable
	;
 	
objects:
	  OBJECTSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #objectsOf
	| t2=allObjects #getAllObjects
	| t3=objects f=filter #filterObjects
	| t4=variable #objectsVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=objects CLOSE_PARENTHESIS (scope)? #concurrentWithObjects
	;
 	
cases:
	  CASESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #casesOf
	| t2=allCases #getAllCases
	| t3=cases f=filter #filterCases
	| t4=variable #casesVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=cases CLOSE_PARENTHESIS (scope)? #concurrentWithCases
	;
	
events:
	  EVENTSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #eventsOf
	| t2=allEvents #getAllEvents
	| t3=events f=filter #filterEvents
	| t4=variable #eventsVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=events CLOSE_PARENTHESIS (scope)? #concurrentWithEvents
	;
	
classes:
	  CLASSESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #classesOf
	| t2=allClasses #getAllClasses
	| t3=classes f=filter #filterClasses
	| t4=variable #classesVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=classes CLOSE_PARENTHESIS (scope)? #concurrentWithClasses
	; 
	
versions:
	  VERSIONSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #versionsOf
	| t2=allVersions #getAllVersions
	| t3=versions f=filter #filterVersions
	| t4=variable #versionsVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=versions CLOSE_PARENTHESIS (scope)? #concurrentWithVersions
	| VERSIONSRELATEDTO OPEN_PARENTHESIS t6=versions CLOSE_PARENTHESIS (scope)? #versionsRelatedTo
	;
	
activities:
	  ACTIVITIESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #activitiesOf
	| t2=allActivities #getAllActivities
	| t3=activities f=filter #filterActivities
	| t4=variable #activitiesVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=activities CLOSE_PARENTHESIS (scope)? #concurrentWithActivities
	;
	
relations:
	  RELATIONSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #relationsOf
	| t2=allRelations #getAllRelations
	| t3=relations f=filter #filterRelations
	| t4=variable #relationsVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=relations CLOSE_PARENTHESIS (scope)? #concurrentWithRelations
	;
	
relationships:
	  RELATIONSHIPSOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #relationshipsOf
	| t2=allRelationships #getAllRelationships
	| t3=relationships f=filter #filterRelationships
	| t4=variable #relationshipsVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=relationships CLOSE_PARENTHESIS (scope)? #concurrentWithRelationships
	;
	
activityinstances:
	  ACTIVITYINSTANCESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #activityInstancesOf
	| t2=allActivityInstances #getAllActivityInstances
	| t3=activityinstances f=filter #filterActivityInstances
	| t4=variable #activityInstancesVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=activityinstances CLOSE_PARENTHESIS (scope)? #concurrentWithActivityInstances
	;
	
attributes:
	  ATTRIBUTESOF OPEN_PARENTHESIS t1=things CLOSE_PARENTHESIS #attributesOf
	| t2=allAttributes #getAllAttributes
	| t3=attributes f=filter #filterAttributes
	| t4=variable #attributesVariable
	| CONCURRENTWITH OPEN_PARENTHESIS t5=attributes CLOSE_PARENTHESIS (scope)? #concurrentWithAttributes
	;
	
scope:
	  SCOPE (CASE|OBJECT|VERSION|CLASS|ATTRIBUTE|RELATIONSHIP|RELATION|ACTIVITYINSTANCE|ACTIVITY|EVENT) 
	;
	
filter:
	  WHERE f=filter_expression
	;

filter_expression:
	  NOT filter_expression #filterNegation
	| OPEN_PARENTHESIS 
	  filter_expression
	  node
	  filter_expression
	  CLOSE_PARENTHESIS		#filterCombined
	| ids operator			#filterField
	;

node:
	  n=AND
	| n=OR
	;

operator:
	  op=EQUAL v=STRING
	| op=DIFFERENT v=STRING
	| op=EQUAL_OR_GREATER v=STRING
	| op=EQUAL_OR_SMALLER v=STRING
	| op=GREATER v=STRING
	| op=SMALLER v=STRING
	| op=CONTAINS v=STRING
	| op=CHANGED (FROM f=STRING)? (TO t=STRING)?
	;
	
ids:
	  id_version
	| id_object
	| id_class
	| id_relationship
	| id_relation
	| id_event
	| id_case
	| id_activity_instance
	| id_activity
	| id_attribute
	| id_period
	;

id_period:
	  START
	| END
	;

id_version:
	  ID
	| OBJECT_ID
	| START_TIMESTAMP
	| END_TIMESTAMP
	| IDATT
	;
	
id_object:
	  ID
	| CLASS_ID
	;
	
id_class:
	  ID
	| DATAMODEL_ID
	| NAME
	;
	
id_relationship:
	  ID
	| SOURCE
	| TARGET
	| NAME
	;
	
id_relation:
	  ID
	| SOURCE_OBJECT_VERSION_ID
	| TARGET_OBJECT_VERSION_ID
	| RELATIONSHIP_ID
	| START_TIMESTAMP
	| END_TIMESTAMP
	;
	
id_event:
	  ID
	| ACTIVITY_INSTANCE_ID
	| ORDERING
	| TIMESTAMP
	| LIFECYCLE
	| RESOURCE
	| IDATT
	;
	
id_case:
	  ID
	| NAME
	;
	
id_activity_instance:
	  ID
	| ACTIVITY_ID
	;
	
id_activity:
	  ID
	| PROCESS_ID
	| NAME
	;
	
id_attribute:
	  ID
	| CLASS_ID
	| NAME
	;

allObjects: ALLOBJECTS;
allCases: ALLCASES;
allEvents: ALLEVENTS;
allClasses: ALLCLASSES;
allVersions: ALLVERSIONS;
allActivities: ALLACTIVITIES;
allRelations: ALLRELATIONS;
allRelationships: ALLRELATIONSHIPS;
allActivityInstances: ALLACTIVITYINSTANCES;
allAttributes: ALLATTRIBUTES;

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
PERIODSOF: P E R I O D S O F ;
CONCURRENTWITH: C O N C U R R E N T W I T H ;

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

// Scopes
SCOPE: S C O P E ;
CASE: C A S E ;
RELATIONSHIP: R E L A T I O N S H I P ;
OBJECT: O B J E C T ;
VERSION: V E R S I O N ;
EVENT: E V E N T ;
ACTIVITY: A C T I V I T Y ;
CLASS: C L A S S ;
ATTRIBUTE: A T T R I B U T E ;
RELATION: R E L A T I O N ;
ACTIVITYINSTANCE: A C T I V I T Y I N S T A N C E ;

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
START: S T A R T ;
END: E N D ;

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

FOREACH: F O R E A C H ;
IN: I N ;
DO: D O ;
DONE: D O N E ;

IF: I F ;
THEN: T H E N ;
ELSE: E L S E ;
FI: F I ; 

ISEMPTY: I S E M P T Y ;
ISNOTEMPTY: I S N O T E M P T Y ;

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