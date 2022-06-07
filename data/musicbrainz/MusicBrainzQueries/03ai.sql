SELECT * FROM "label_alias", "label", "release_label" WHERE "label_alias"."label" = "label"."id" AND "label"."id" = "release_label"."label";
