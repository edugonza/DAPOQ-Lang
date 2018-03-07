SELECT C.id as "id",
	E.timestamp as "E:time:timestamp",
	AC.name as "E:concept:name",
	E.resource as "E:org:resource",
	E.lifecycle as "E:lifecycle:transition",
	E.ordering
FROM
	event as E,
	"case" as C,
	activity_instance as AI,
	activity_instance_to_case as AITC,
	activity as AC
WHERE
	C.id = AITC.case_id AND
	AITC.activity_instance_id = AI.id AND
	E.activity_instance_id = AI.id AND
	AI.activity_id = AC.id AND
	C.id IN
	(
		SELECT C.id
		FROM
			"case" as C,
			class as CL,
			object as O,
			object_version as OV,
			object_version as OVP,
			event as E,
			activity_instance as AI,
			activity_instance_to_case as AITC,
			event_to_object_version as ETOV,
			attribute_name as AT,
			attribute_value as AV,
			attribute_value as AVP
		WHERE
			E.activity_instance_id = AI.id AND
			AITC.activity_instance_id = AI.id AND
			AITC.case_id = C.id AND
			ETOV.event_id = E.id AND
			ETOV.object_version_id = OV.id AND
			OV.object_id = O.id AND
			O.class_id = CL.id AND
			CL.name = "CUSTOMER" AND
			E.timestamp > "527292000000" AND
			E.timestamp < "1480531444303" AND
			AT.name = "BIRTH_DATE" AND
			AV.attribute_name_id = AT.id AND
			AV.object_version_id = OV.id AND
			AV.value LIKE "30-NOV-55" AND
			AVP.attribute_name_id = AT.id AND
			AVP.object_version_id = OVP.id AND
			AVP.value LIKE "03-DEC-49" AND
			OVP.id IN
			(
				SELECT OVP.id
				FROM object_version as OVP
				WHERE
					OVP.start_timestamp < OV.start_timestamp AND
					OVP.object_id = OV.object_id
				ORDER BY OVP.start_timestamp DESC LIMIT 1
			)
	)
ORDER BY C.id, E.ordering;
