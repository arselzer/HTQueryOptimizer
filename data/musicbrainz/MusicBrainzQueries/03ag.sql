SELECT * FROM "label_isni", "label", "label_type" WHERE "label_isni"."label" = "label"."id" AND "label"."type" = "label_type"."id";
