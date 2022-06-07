SELECT * FROM "artist_credit", "recording", "release" WHERE "artist_credit"."id" = "recording"."artist_credit" AND "artist_credit"."id" = "release"."artist_credit";
