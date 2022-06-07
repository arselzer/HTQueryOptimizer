SELECT * FROM "label_type", "label", "label_ipi", "label_isni" WHERE "label_type"."id" = "label"."type" AND "label"."id" = "label_ipi"."label" AND "label"."id" = "label_isni"."label";
