SELECT * FROM "iso_3166_3", "area", "artist", "place" WHERE "iso_3166_3"."area" = "area"."id" AND "area"."id" = "artist"."area" AND "area"."id" = "place"."area";
