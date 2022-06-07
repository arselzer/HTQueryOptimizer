SELECT * FROM "release_group_secondary_type", "release_group_secondary_type_join", "release_group", "release_group_primary_type", "release", "medium", "medium_cdtoc", "medium_format", "release_unknown_country", "release_label", "track", "script", "label", "area", "label_alias", "label_ipi", "release_country", "place", "area_alias", "recording", "place_type", "language", "cdtoc", "place_alias", "artist", "artist_credit_name" WHERE "release_group_secondary_type"."id" = "release_group_secondary_type_join"."secondary_type" AND "release_group_secondary_type_join"."release_group" = "release_group"."id" AND "release_group"."type" = "release_group_primary_type"."id" AND "release_group"."id" = "release"."release_group" AND "release"."id" = "medium"."release" AND "medium"."id" = "medium_cdtoc"."medium" AND "medium"."format" = "medium_format"."id" AND "release"."id" = "release_unknown_country"."release" AND "release"."id" = "release_label"."release" AND "medium"."id" = "track"."medium" AND "release"."script" = "script"."id" AND "release_label"."label" = "label"."id" AND "label"."area" = "area"."id" AND "label"."id" = "label_alias"."label" AND "label"."id" = "label_ipi"."label" AND "release"."id" = "release_country"."release" AND "area"."id" = "place"."area" AND "area"."id" = "area_alias"."area" AND "track"."recording" = "recording"."id" AND "place"."type" = "place_type"."id" AND "release"."language" = "language"."id" AND "medium_cdtoc"."cdtoc" = "cdtoc"."id" AND "place"."id" = "place_alias"."place" AND "area"."id" = "artist"."area" AND "artist"."id" = "artist_credit_name"."artist";