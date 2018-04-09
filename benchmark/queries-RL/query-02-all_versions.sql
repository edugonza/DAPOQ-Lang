select * from object_version as ov
left join attribute_value as av on ov.id = av.object_version_id
left join attribute_name as an on av.attribute_name_id = an.id
order by ov.id
