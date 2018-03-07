select * from event as e
left join event_attribute_value as eav on e.id = eav.event_id
left join event_attribute_name as ean on eav.event_attribute_name_id = ean.id
