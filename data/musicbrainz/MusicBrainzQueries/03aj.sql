SELECT * FROM "iswc", "work", "work_attribute" WHERE "iswc"."work" = "work"."id" AND "work"."id" = "work_attribute"."work";
