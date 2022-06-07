SELECT * FROM "label", "label_alias", "label_isni" WHERE "label"."id" = "label_alias"."label" AND "label"."id" = "label_isni"."label";
