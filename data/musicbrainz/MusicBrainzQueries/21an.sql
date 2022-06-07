SELECT * FROM "recording", "isrc", "track", "medium", "medium_cdtoc", "medium_format", "artist_credit", "cdtoc", "release", "release_country", "release_label", "language", "country_area", "artist_credit_name", "release_packaging", "area", "label", "script", "release_status", "label_isni", "area_type" WHERE "recording"."id" = "isrc"."recording" AND "recording"."id" = "track"."recording" AND "track"."medium" = "medium"."id" AND "medium"."id" = "medium_cdtoc"."medium" AND "medium"."format" = "medium_format"."id" AND "recording"."artist_credit" = "artist_credit"."id" AND "track"."artist_credit" = "artist_credit"."id" AND "medium_cdtoc"."cdtoc" = "cdtoc"."id" AND "medium"."release" = "release"."id" AND "release"."id" = "release_country"."release" AND "release"."id" = "release_label"."release" AND "release"."language" = "language"."id" AND "release_country"."country" = "country_area"."area" AND "artist_credit"."id" = "artist_credit_name"."artist_credit" AND "release"."packaging" = "release_packaging"."id" AND "country_area"."area" = "area"."id" AND "release_label"."label" = "label"."id" AND "release"."script" = "script"."id" AND "release"."status" = "release_status"."id" AND "label"."id" = "label_isni"."label" AND "area"."type" = "area_type"."id";