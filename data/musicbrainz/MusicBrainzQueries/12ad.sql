SELECT * FROM "isrc", "recording", "artist_credit", "artist_credit_name", "release", "script", "medium", "release_status", "medium_cdtoc", "language", "medium_format", "release_packaging" WHERE "isrc"."recording" = "recording"."id" AND "recording"."artist_credit" = "artist_credit"."id" AND "artist_credit"."id" = "artist_credit_name"."artist_credit" AND "artist_credit"."id" = "release"."artist_credit" AND "release"."script" = "script"."id" AND "release"."id" = "medium"."release" AND "release"."status" = "release_status"."id" AND "medium"."id" = "medium_cdtoc"."medium" AND "release"."language" = "language"."id" AND "medium"."format" = "medium_format"."id" AND "release"."packaging" = "release_packaging"."id";