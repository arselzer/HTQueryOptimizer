SELECT * FROM "gender", "artist", "artist_type", "area" WHERE "gender"."id" = "artist"."gender" AND "artist"."type" = "artist_type"."id" AND "artist"."area" = "area"."id";
