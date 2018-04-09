select C.id as "id", CAT.name, CATV.value, CATV.type from
"case" as C
left join case_attribute_value as CATV on CATV.case_id = C.id
left join case_attribute_name as CAT on CAT.id = CATV.case_attribute_name_id
join activity_instance_to_case as AITC on AITC.case_id = C.id
join activity_instance as AI on AITC.activity_instance_id = AI.id
join event as E on E.activity_instance_id = AI.id
join event_to_object_version as ETOV on ETOV.event_id = E.id
join object_version as OV on OV.id = ETOV.object_version_id
join object as O on O.id = OV.object_id
