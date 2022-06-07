SELECT * FROM "iso_3166_1", "area", "area_alias" WHERE "iso_3166_1"."area" = "area"."id" AND "area"."id" = "area_alias"."area";
