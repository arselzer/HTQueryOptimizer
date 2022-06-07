SELECT * FROM "work_alias_type", "work_alias", "work", "iswc" WHERE "work_alias_type"."id" = "work_alias"."type" AND "work_alias"."work" = "work"."id" AND "work"."id" = "iswc"."work";
