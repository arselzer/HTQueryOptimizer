SELECT * FROM "iso_3166_3", "area", "artist", "gender" WHERE "iso_3166_3"."area" = "area"."id" AND "area"."id" = "artist"."area" AND "artist"."gender" = "gender"."id";
