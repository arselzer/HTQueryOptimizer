SELECT * FROM "medium", "medium_format", "medium_cdtoc", "cdtoc" WHERE "medium"."format" = "medium_format"."id" AND "medium"."id" = "medium_cdtoc"."medium" AND "medium_cdtoc"."cdtoc" = "cdtoc"."id";
