SELECT * FROM "artist_type", "artist", "artist_alias", "gender" WHERE "artist_type"."id" = "artist"."type" AND "artist"."id" = "artist_alias"."artist" AND "artist"."gender" = "gender"."id";
