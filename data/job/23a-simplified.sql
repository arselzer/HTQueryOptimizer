SELECT kind_type.kind,
       title.title
FROM complete_cast,
     comp_cast_type,
     company_name,
     company_type,
     info_type,
     keyword,
     kind_type,
     movie_companies,
     movie_info,
     movie_keyword,
     title
WHERE kind_type.id = title.kind_id
  AND title.id = movie_info.movie_id
  AND title.id = movie_keyword.movie_id
  AND title.id = movie_companies.movie_id
  AND title.id = complete_cast.movie_id
  AND movie_keyword.movie_id = movie_info.movie_id
  AND movie_keyword.movie_id = movie_companies.movie_id
  AND movie_keyword.movie_id = complete_cast.movie_id
  AND movie_info.movie_id = movie_companies.movie_id
  AND movie_info.movie_id = complete_cast.movie_id
  AND movie_companies.movie_id = complete_cast.movie_id
  AND keyword.id = movie_keyword.keyword_id
  AND info_type.id = movie_info.info_type_id
  AND company_name.id = movie_companies.company_id
  AND company_type.id = movie_companies.company_type_id
  AND comp_cast_type.id = complete_cast.status_id;

