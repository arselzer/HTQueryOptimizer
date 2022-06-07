SELECT * FROM "artist_credit_name", "artist", "artist_isni" WHERE "artist_credit_name"."artist" = "artist"."id" AND "artist"."id" = "artist_isni"."artist";
