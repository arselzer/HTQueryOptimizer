
--
-- Name: aka_name_idx_md5; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_name_idx_md5 ON public.aka_name USING btree (md5sum);


--
-- Name: aka_name_idx_name; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_name_idx_name ON public.aka_name USING btree (name);


--
-- Name: aka_name_idx_pcode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_name_idx_pcode ON public.aka_name USING btree (surname_pcode);


--
-- Name: aka_name_idx_pcodecf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_name_idx_pcodecf ON public.aka_name USING btree (name_pcode_cf);


--
-- Name: aka_name_idx_pcodenf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_name_idx_pcodenf ON public.aka_name USING btree (name_pcode_nf);


--
-- Name: aka_name_idx_person; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_name_idx_person ON public.aka_name USING btree (person_id);


--
-- Name: aka_title_idx_epof; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_epof ON public.aka_title USING btree (episode_of_id);


--
-- Name: aka_title_idx_kindid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_kindid ON public.aka_title USING btree (kind_id);


--
-- Name: aka_title_idx_md5; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_md5 ON public.aka_title USING btree (md5sum);


--
-- Name: aka_title_idx_movieid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_movieid ON public.aka_title USING btree (movie_id);


--
-- Name: aka_title_idx_pcode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_pcode ON public.aka_title USING btree (phonetic_code);


--
-- Name: aka_title_idx_title; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_title ON public.aka_title USING btree (title);


--
-- Name: aka_title_idx_year; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX aka_title_idx_year ON public.aka_title USING btree (production_year);


--
-- Name: cast_info_idx_cid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX cast_info_idx_cid ON public.cast_info USING btree (person_role_id);


--
-- Name: cast_info_idx_mid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX cast_info_idx_mid ON public.cast_info USING btree (movie_id);


--
-- Name: cast_info_idx_pid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX cast_info_idx_pid ON public.cast_info USING btree (person_id);


--
-- Name: cast_info_idx_rid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX cast_info_idx_rid ON public.cast_info USING btree (role_id);


--
-- Name: char_name_idx_imdb_id; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX char_name_idx_imdb_id ON public.char_name USING btree (imdb_id);


--
-- Name: char_name_idx_md5; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX char_name_idx_md5 ON public.char_name USING btree (md5sum);


--
-- Name: char_name_idx_name; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX char_name_idx_name ON public.char_name USING btree (name);


--
-- Name: char_name_idx_pcode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX char_name_idx_pcode ON public.char_name USING btree (surname_pcode);


--
-- Name: char_name_idx_pcodenf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX char_name_idx_pcodenf ON public.char_name USING btree (name_pcode_nf);


--
-- Name: comp_cast_type_kind; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX comp_cast_type_kind ON public.comp_cast_type USING btree (kind);


--
-- Name: company_name_idx_ccode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_name_idx_ccode ON public.company_name USING btree (country_code);


--
-- Name: company_name_idx_imdb_id; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_name_idx_imdb_id ON public.company_name USING btree (imdb_id);


--
-- Name: company_name_idx_md5; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_name_idx_md5 ON public.company_name USING btree (md5sum);


--
-- Name: company_name_idx_name; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_name_idx_name ON public.company_name USING btree (name);


--
-- Name: company_name_idx_pcodenf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_name_idx_pcodenf ON public.company_name USING btree (name_pcode_nf);


--
-- Name: company_name_idx_pcodesf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_name_idx_pcodesf ON public.company_name USING btree (name_pcode_sf);


--
-- Name: company_type_kind; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX company_type_kind ON public.company_type USING btree (kind);


--
-- Name: complete_cast_idx_mid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX complete_cast_idx_mid ON public.complete_cast USING btree (movie_id);


--
-- Name: complete_cast_idx_sid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX complete_cast_idx_sid ON public.complete_cast USING btree (subject_id);


--
-- Name: info_type_info; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX info_type_info ON public.info_type USING btree (info);


--
-- Name: keyword_idx_keyword; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX keyword_idx_keyword ON public.keyword USING btree (keyword);


--
-- Name: keyword_idx_pcode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX keyword_idx_pcode ON public.keyword USING btree (phonetic_code);


--
-- Name: kind_type_kind; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX kind_type_kind ON public.kind_type USING btree (kind);


--
-- Name: link_type_link; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX link_type_link ON public.link_type USING btree (link);


--
-- Name: movie_companies_idx_cid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_companies_idx_cid ON public.movie_companies USING btree (company_id);


--
-- Name: movie_companies_idx_ctypeid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_companies_idx_ctypeid ON public.movie_companies USING btree (company_type_id);


--
-- Name: movie_companies_idx_mid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_companies_idx_mid ON public.movie_companies USING btree (movie_id);


--
-- Name: movie_info_idx_infotypeid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_info_idx_infotypeid ON public.movie_info USING btree (info_type_id);


--
-- Name: movie_info_idx_mid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_info_idx_mid ON public.movie_info USING btree (movie_id);


--
-- Name: movie_keyword_idx_keywordid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_keyword_idx_keywordid ON public.movie_keyword USING btree (keyword_id);


--
-- Name: movie_keyword_idx_mid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_keyword_idx_mid ON public.movie_keyword USING btree (movie_id);


--
-- Name: movie_link_idx_lmid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_link_idx_lmid ON public.movie_link USING btree (linked_movie_id);


--
-- Name: movie_link_idx_ltypeid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_link_idx_ltypeid ON public.movie_link USING btree (link_type_id);


--
-- Name: movie_link_idx_mid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX movie_link_idx_mid ON public.movie_link USING btree (movie_id);


--
-- Name: name_idx_gender; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_gender ON public.name USING btree (gender);


--
-- Name: name_idx_imdb_id; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_imdb_id ON public.name USING btree (imdb_id);


--
-- Name: name_idx_md5; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_md5 ON public.name USING btree (md5sum);


--
-- Name: name_idx_name; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_name ON public.name USING btree (name);


--
-- Name: name_idx_pcode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_pcode ON public.name USING btree (surname_pcode);


--
-- Name: name_idx_pcodecf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_pcodecf ON public.name USING btree (name_pcode_cf);


--
-- Name: name_idx_pcodenf; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX name_idx_pcodenf ON public.name USING btree (name_pcode_nf);


--
-- Name: person_info_idx_itypeid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX person_info_idx_itypeid ON public.person_info USING btree (info_type_id);


--
-- Name: person_info_idx_pid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX person_info_idx_pid ON public.person_info USING btree (person_id);


--
-- Name: role_type_role; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX role_type_role ON public.role_type USING btree (role);


--
-- Name: title_idx_episode_nr; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_episode_nr ON public.title USING btree (episode_nr);


--
-- Name: title_idx_epof; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_epof ON public.title USING btree (episode_of_id);


--
-- Name: title_idx_imdb_id; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_imdb_id ON public.title USING btree (imdb_id);


--
-- Name: title_idx_kindid; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_kindid ON public.title USING btree (kind_id);


--
-- Name: title_idx_md5; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_md5 ON public.title USING btree (md5sum);


--
-- Name: title_idx_pcode; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_pcode ON public.title USING btree (phonetic_code);


--
-- Name: title_idx_season_nr; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_season_nr ON public.title USING btree (season_nr);


--
-- Name: title_idx_title; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_title ON public.title USING btree (title);


--
-- Name: title_idx_year; Type: INDEX; Schema: public; Owner: test
--

CREATE INDEX title_idx_year ON public.title USING btree (production_year);


--
-- PostgreSQL database dump complete
--

