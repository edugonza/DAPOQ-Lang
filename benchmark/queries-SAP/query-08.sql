select distinct E.id as "id", EAT.name, EATV.value, EATV.type from
event as E
join activity_instance as AI on AI.id = E.activity_instance_id
join activity_instance_to_case as AITC on AITC.activity_instance_id = AI.id
join "case" as C on C.id = AITC.case_id
join case_to_log as CTL on CTL.case_id = C.id
join log as LG on CTL.log_id = LG.id
left join event_attribute_value as EATV on EATV.event_id = E.id
left join event_attribute_name as EAT on EAT.id = EATV.event_attribute_name_id
