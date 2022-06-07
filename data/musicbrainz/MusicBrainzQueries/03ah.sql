SELECT * FROM "place_alias", "place_alias_type", "place" WHERE "place_alias"."type" = "place_alias_type"."id" AND "place_alias"."place" = "place"."id";
