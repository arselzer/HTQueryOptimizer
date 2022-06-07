SELECT * FROM "label_ipi", "label", "release_label", "label_type", "area", "iso_3166_2", "iso_3166_1", "release", "area_alias", "medium", "release_packaging", "country_area", "medium_format", "release_group", "release_group_secondary_type_join" WHERE "label_ipi"."label" = "label"."id" AND "label"."id" = "release_label"."label" AND "label"."type" = "label_type"."id" AND "label"."area" = "area"."id" AND "area"."id" = "iso_3166_2"."area" AND "area"."id" = "iso_3166_1"."area" AND "release_label"."release" = "release"."id" AND "area"."id" = "area_alias"."area" AND "release"."id" = "medium"."release" AND "release"."packaging" = "release_packaging"."id" AND "area"."id" = "country_area"."area" AND "medium"."format" = "medium_format"."id" AND "release"."release_group" = "release_group"."id" AND "release_group"."id" = "release_group_secondary_type_join"."release_group";