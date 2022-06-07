SELECT * FROM "artist", "artist_credit_name", "area", "iso_3166_1" WHERE "artist"."id" = "artist_credit_name"."artist" AND "artist"."area" = "area"."id" AND "area"."id" = "iso_3166_1"."area";
