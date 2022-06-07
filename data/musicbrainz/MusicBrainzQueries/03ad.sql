SELECT * FROM "cdtoc", "medium_cdtoc", "medium" WHERE "cdtoc"."id" = "medium_cdtoc"."cdtoc" AND "medium_cdtoc"."medium" = "medium"."id";
