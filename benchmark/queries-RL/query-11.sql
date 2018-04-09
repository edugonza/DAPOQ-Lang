select C.id as "id", CAT.name, CATV.value, CATV.type from
"case" as C
LEFT JOIN case_attribute_value as CATV ON CATV.case_id = C.id
LEFT JOIN case_attribute_name as CAT ON CAT.id = CATV.case_attribute_name_id
join (
select C.id as "id", max(E.timestamp) as "A_t", min(B.timestamp) as "B_t" from
"case" as C
join activity_instance_to_case as AITC on AITC.case_id = C.id
join activity_instance as AI on AI.id = AITC.activity_instance_id
join activity as ACTA on ACTA.id = AI.activity_id AND ACTA.name LIKE "%INSERT%"
join event as E on E.activity_instance_id = AI.id
join case_to_log as CTL on CTL.case_id = C.id
join log as LG on LG.id = CTL.log_id and LG.name = "log01"
join (
select C.id as "id", E.timestamp as "timestamp" from
"case" as C
join activity_instance_to_case as AITC on AITC.case_id = C.id
join activity_instance as AI on AI.id = AITC.activity_instance_id
join activity as ACTA on ACTA.id = AI.activity_id AND ACTA.name LIKE "%UPDATE%"
join event as E on E.activity_instance_id = AI.id
join case_to_log as CTL on CTL.case_id = C.id
join log as LG on LG.id = CTL.log_id and LG.name = "log01"
) as B on B.id = C.id
group by C.id having A_t < B_t
) as CC on CC.id = C.id
