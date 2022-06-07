SELECT * FROM "area_alias_type", "area_alias", "area" WHERE "area_alias_type"."id" = "area_alias"."type" AND "area_alias"."area" = "area"."id";
