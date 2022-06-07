SELECT * FROM "artist_alias", "artist", "artist_alias_type" WHERE "artist_alias"."artist" = "artist"."id" AND "artist_alias"."type" = "artist_alias_type"."id";
