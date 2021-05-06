SELECT movie_companies.note,
       title.title,
       title.production_year
FROM company_type,
     info_type,
     movie_companies,
     movie_info_idx,
     title
WHERE company_type.id = movie_companies.company_type_id
  AND title.id = movie_companies.movie_id
  AND title.id = movie_info_idx.movie_id
  AND movie_companies.movie_id = movie_info_idx.movie_id
  AND info_type.id = movie_info_idx.info_type_id;

