SELECT * FROM "script", "release", "medium", "release_packaging" WHERE "script"."id" = "release"."script" AND "release"."id" = "medium"."release" AND "release"."packaging" = "release_packaging"."id";
