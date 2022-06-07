SELECT * FROM "medium", "medium_cdtoc", "release", "script", "release_label", "release_group", "label", "area", "iso_3166_3", "release_country" WHERE "medium"."id" = "medium_cdtoc"."medium" AND "medium"."release" = "release"."id" AND "release"."script" = "script"."id" AND "release"."id" = "release_label"."release" AND "release"."release_group" = "release_group"."id" AND "release_label"."label" = "label"."id" AND "label"."area" = "area"."id" AND "area"."id" = "iso_3166_3"."area" AND "release"."id" = "release_country"."release";