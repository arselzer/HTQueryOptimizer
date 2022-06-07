SELECT * FROM "isrc", "recording", "artist_credit", "release", "release_unknown_country", "release_packaging", "release_status", "release_label", "release_country", "release_group", "medium", "artist_credit_name", "country_area", "track", "medium_cdtoc", "area", "release_group_secondary_type_join", "area_type", "release_group_primary_type", "language", "script", "cdtoc", "iso_3166_2", "medium_format", "artist" WHERE "isrc"."recording" = "recording"."id" AND "recording"."artist_credit" = "artist_credit"."id" AND "artist_credit"."id" = "release"."artist_credit" AND "release"."id" = "release_unknown_country"."release" AND "release"."packaging" = "release_packaging"."id" AND "release"."status" = "release_status"."id" AND "release"."id" = "release_label"."release" AND "release"."id" = "release_country"."release" AND "release"."release_group" = "release_group"."id" AND "artist_credit"."id" = "release_group"."artist_credit" AND "release"."id" = "medium"."release" AND "artist_credit"."id" = "artist_credit_name"."artist_credit" AND "release_country"."country" = "country_area"."area" AND "medium"."id" = "track"."medium" AND "medium"."id" = "medium_cdtoc"."medium" AND "country_area"."area" = "area"."id" AND "release_group"."id" = "release_group_secondary_type_join"."release_group" AND "area"."type" = "area_type"."id" AND "release_group"."type" = "release_group_primary_type"."id" AND "release"."language" = "language"."id" AND "release"."script" = "script"."id" AND "medium_cdtoc"."cdtoc" = "cdtoc"."id" AND "area"."id" = "iso_3166_2"."area" AND "medium"."format" = "medium_format"."id" AND "artist_credit_name"."artist" = "artist"."id";