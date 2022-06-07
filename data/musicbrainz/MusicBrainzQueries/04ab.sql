SELECT * FROM "label", "label_isni", "label_alias", "label_type" WHERE "label"."id" = "label_isni"."label" AND "label"."id" = "label_alias"."label" AND "label"."type" = "label_type"."id";
