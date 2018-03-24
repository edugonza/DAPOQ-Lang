select distinct LG.id as "id", LGAT.name, LGATV.value, LGATV.type from
log as LG
left join log_attribute_value as LGATV on LGATV.log_id = LG.id
left join log_attribute_name as LGAT on LGAT.id = LGATV.log_attribute_name_id
join case_to_log as CTL on CTL.log_id = LG.id
join "case" as C on C.id = CTL.case_id
join activity_instance_to_case as AITC on AITC.case_id = C.id
join activity_instance as AI on AITC.activity_instance_id = AI.id
join activity as ACT on AI.activity_id = ACT.id
