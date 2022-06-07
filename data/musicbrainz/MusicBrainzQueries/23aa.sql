SELECT * FROM "country_area", "area", "iso_3166_2", "iso_3166_3", "iso_3166_1", "label", "area_type", "label_isni", "label_type", "label_ipi", "release_label", "label_alias", "artist", "artist_type", "area_alias", "artist_alias", "artist_isni", "artist_alias_type", "label_alias_type", "release", "gender", "medium", "medium_cdtoc" WHERE "country_area"."area" = "area"."id" AND "area"."id" = "iso_3166_2"."area" AND "area"."id" = "iso_3166_3"."area" AND "area"."id" = "iso_3166_1"."area" AND "area"."id" = "label"."area" AND "area"."type" = "area_type"."id" AND "label"."id" = "label_isni"."label" AND "label"."type" = "label_type"."id" AND "label"."id" = "label_ipi"."label" AND "label"."id" = "release_label"."label" AND "label"."id" = "label_alias"."label" AND "area"."id" = "artist"."area" AND "artist"."type" = "artist_type"."id" AND "area"."id" = "area_alias"."area" AND "artist"."id" = "artist_alias"."artist" AND "artist"."id" = "artist_isni"."artist" AND "artist_alias"."type" = "artist_alias_type"."id" AND "label_alias"."type" = "label_alias_type"."id" AND "release_label"."release" = "release"."id" AND "artist"."gender" = "gender"."id" AND "release"."id" = "medium"."release" AND "medium"."id" = "medium_cdtoc"."medium";