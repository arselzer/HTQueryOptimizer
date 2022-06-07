SELECT * FROM "release_packaging", "release", "artist_credit" WHERE "release_packaging"."id" = "release"."packaging" AND "release"."artist_credit" = "artist_credit"."id";
