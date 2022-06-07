SELECT * FROM "medium_format", "medium", "track", "medium_cdtoc" WHERE "medium_format"."id" = "medium"."format" AND "medium"."id" = "track"."medium" AND "medium"."id" = "medium_cdtoc"."medium";
