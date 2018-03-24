SELECT distinct C.id as "id", CAT.name, CATV.value, CATV.type
FROM
	"case" as C
JOIN activity_instance_to_case as AITC ON AITC.case_id = C.id
JOIN activity_instance as AI ON AI.id = AITC.activity_instance_id 
JOIN event as E ON E.activity_instance_id = AI.id
JOIN event_to_object_version as ETOV ON ETOV.event_id = E.id
JOIN object_version as OV ON ETOV.object_version_id = OV.id
JOIN object as O ON OV.object_id = O.id
JOIN class as CL ON O.class_id = CL.id AND CL.name = "EKPO"
JOIN attribute_name as AT ON AT.name = "MENGE"
JOIN attribute_value as AV ON AV.attribute_name_id = AT.id AND AV.object_version_id = OV.id
LEFT JOIN case_attribute_value as CATV ON CATV.case_id = C.id
LEFT JOIN case_attribute_name as CAT ON CAT.id = CATV.case_attribute_name_id
WHERE
	E.timestamp > "527292000000" AND
	E.timestamp < "1480531444303" AND
	EXISTS
	(
		SELECT OVP.id
		FROM
			object_version as OVP,
			attribute_value as AVP
		WHERE
			AVP.attribute_name_id = AT.id AND
			AVP.object_version_id = OVP.id AND
			OVP.object_id = OV.object_id AND
			AVP.value != AV.value AND
			OVP.id IN
			(
				SELECT OVPP.id
				FROM object_version as OVPP
				WHERE
					OVPP.end_timestamp <= OV.start_timestamp AND
					OVPP.end_timestamp >= 0 AND
					OVPP.object_id = OV.object_id AND
					OVPP.id != OV.id
				ORDER BY OVPP.end_timestamp DESC LIMIT 1
			)
	)
