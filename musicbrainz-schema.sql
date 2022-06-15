CREATE TABLE public.artist_release_group (
    is_track_artist boolean NOT NULL,
    artist integer NOT NULL,
    unofficial boolean NOT NULL,
    primary_type smallint,
    secondary_types smallint[],
    first_release_date integer,
    sort_character character(1) NOT NULL COLLATE musicbrainz.musicbrainz,
    release_group integer NOT NULL
)
PARTITION BY LIST (is_track_artist);
CREATE TABLE public.artist_release (
    is_track_artist boolean NOT NULL,
    artist integer NOT NULL,
    first_release_date integer,
    catalog_numbers text[],
    country_code character(2),
    barcode bigint,
    sort_character character(1) NOT NULL COLLATE musicbrainz.musicbrainz,
    release integer NOT NULL
)
PARTITION BY LIST (is_track_artist);
CREATE TABLE public.recording_first_release_date (
    recording integer NOT NULL,
    year smallint,
    month smallint,
    day smallint
);
CREATE TABLE public.release_first_release_date (
    release integer NOT NULL,
    year smallint,
    month smallint,
    day smallint
);
CREATE TABLE public.medium (
    id integer NOT NULL,
    release integer NOT NULL,
    "position" integer NOT NULL,
    format integer,
    name character varying DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    track_count integer DEFAULT 0 NOT NULL,
    CONSTRAINT medium_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT medium_name_check CHECK (musicbrainz.controlled_for_whitespace((name)::text))
);
CREATE TABLE cover_art_archive.art_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE cover_art_archive.cover_art (
    id bigint NOT NULL,
    release integer NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    edit integer NOT NULL,
    ordering integer NOT NULL,
    date_uploaded timestamp with time zone DEFAULT now() NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    mime_type text NOT NULL,
    filesize integer,
    thumb_250_filesize integer,
    thumb_500_filesize integer,
    thumb_1200_filesize integer,
    CONSTRAINT cover_art_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT cover_art_ordering_check CHECK ((ordering > 0))
);
CREATE TABLE cover_art_archive.cover_art_type (
    id bigint NOT NULL,
    type_id integer NOT NULL
);
CREATE TABLE cover_art_archive.image_type (
    mime_type text NOT NULL,
    suffix text NOT NULL
);
CREATE TABLE public.edit (
    id integer NOT NULL,
    editor integer NOT NULL,
    type smallint NOT NULL,
    status smallint NOT NULL,
    autoedit smallint DEFAULT 0 NOT NULL,
    open_time timestamp with time zone DEFAULT now(),
    close_time timestamp with time zone,
    expire_time timestamp with time zone NOT NULL,
    language integer,
    quality smallint DEFAULT 1 NOT NULL
);
CREATE TABLE cover_art_archive.release_group_cover_art (
    release_group integer NOT NULL,
    release integer NOT NULL
);
CREATE TABLE documentation.l_area_area_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_artist_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_event_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_instrument_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_label_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_place_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_area_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_artist_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_event_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_instrument_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_label_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_place_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_artist_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_event_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_instrument_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_label_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_place_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_event_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_instrument_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_label_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_place_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_instrument_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_label_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_place_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_label_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_place_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_place_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_recording_recording_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_recording_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_recording_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_recording_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_recording_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_recording_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_group_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_group_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_group_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_group_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_release_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_release_group_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_release_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_series_series_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_series_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_series_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_url_url_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_url_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.l_work_work_example (
    id integer NOT NULL,
    published boolean NOT NULL,
    name text NOT NULL
);
CREATE TABLE documentation.link_type_documentation (
    id integer NOT NULL,
    documentation text NOT NULL,
    examples_deleted smallint DEFAULT 0 NOT NULL
);
CREATE TABLE event_art_archive.art_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE event_art_archive.event_art (
    id bigint NOT NULL,
    event integer NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    edit integer NOT NULL,
    ordering integer NOT NULL,
    date_uploaded timestamp with time zone DEFAULT now() NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    mime_type text NOT NULL,
    filesize integer,
    thumb_250_filesize integer,
    thumb_500_filesize integer,
    thumb_1200_filesize integer,
    CONSTRAINT event_art_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT event_art_ordering_check CHECK ((ordering > 0))
);
CREATE TABLE event_art_archive.event_art_type (
    id bigint NOT NULL,
    type_id integer NOT NULL
);
CREATE TABLE json_dump.area_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.artist_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.control (
    last_processed_replication_sequence integer,
    full_json_dump_replication_sequence integer
);
CREATE TABLE json_dump.deleted_entities (
    entity_type character varying(50) NOT NULL,
    id integer NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE json_dump.event_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.instrument_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.label_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.place_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.recording_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.release_group_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.release_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.series_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE json_dump.tmp_checked_entities (
    id integer NOT NULL,
    entity_type character varying(50) NOT NULL
);
CREATE TABLE json_dump.work_json (
    id integer NOT NULL,
    replication_sequence integer NOT NULL,
    json jsonb NOT NULL,
    last_modified timestamp with time zone
);
CREATE TABLE public.l_artist_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.link (
    id integer NOT NULL,
    link_type integer NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    attribute_count integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT link_ended_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL))))
);
CREATE TABLE public.link_attribute_text_value (
    link integer NOT NULL,
    attribute_type integer NOT NULL,
    text_value text NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace(text_value)),
    CONSTRAINT only_non_empty CHECK ((text_value <> ''::text))
);
CREATE TABLE public.link_type (
    id integer NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    gid uuid NOT NULL,
    entity_type0 character varying(50) NOT NULL,
    entity_type1 character varying(50) NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    link_phrase character varying(255) NOT NULL,
    reverse_link_phrase character varying(255) NOT NULL,
    long_link_phrase character varying(255) NOT NULL,
    priority integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    is_deprecated boolean DEFAULT false NOT NULL,
    has_dates boolean DEFAULT true NOT NULL,
    entity0_cardinality smallint DEFAULT 0 NOT NULL,
    entity1_cardinality smallint DEFAULT 0 NOT NULL
);
CREATE TABLE public.series (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    type integer NOT NULL,
    ordering_attribute integer NOT NULL,
    ordering_type integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT series_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT series_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.l_event_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.area (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    type integer,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    ended boolean DEFAULT false NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    CONSTRAINT area_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT area_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT area_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.area_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.artist_credit_name (
    artist_credit integer NOT NULL,
    "position" smallint NOT NULL,
    artist integer NOT NULL,
    name character varying NOT NULL,
    join_phrase text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.artist_credit (
    id integer NOT NULL,
    name character varying NOT NULL,
    artist_count smallint NOT NULL,
    ref_count integer DEFAULT 0,
    created timestamp with time zone DEFAULT now(),
    edits_pending integer DEFAULT 0 NOT NULL,
    CONSTRAINT artist_credit_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.country_area (
    area integer NOT NULL
);
CREATE TABLE public.iso_3166_1 (
    area integer NOT NULL,
    code character(2) NOT NULL
);
CREATE TABLE public.iso_3166_3 (
    area integer NOT NULL,
    code character(4) NOT NULL
);
CREATE TABLE public.isrc (
    id integer NOT NULL,
    recording integer NOT NULL,
    isrc character(12) NOT NULL,
    source smallint,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    CONSTRAINT isrc_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT isrc_isrc_check CHECK ((isrc ~ '^[A-Z]{2}[A-Z0-9]{3}[0-9]{7}$'::text))
);
CREATE TABLE public.iswc (
    id integer NOT NULL,
    work integer NOT NULL,
    iswc character(15),
    source smallint,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT iswc_iswc_check CHECK ((iswc ~ '^T-?\d{3}.?\d{3}.?\d{3}[-.]?\d$'::text))
);
CREATE TABLE public.label_isni (
    label integer NOT NULL,
    isni character(16) NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    CONSTRAINT label_isni_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT label_isni_isni_check CHECK ((isni ~ '^\d{15}[\dX]$'::text))
);
CREATE TABLE public.label (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    label_code integer,
    type integer,
    area integer,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT label_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT label_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT label_ended_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT label_label_code_check CHECK (((label_code > 0) AND (label_code < 100000))),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.label_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.language (
    id integer NOT NULL,
    iso_code_2t character(3),
    iso_code_2b character(3),
    iso_code_1 character(2),
    name character varying(100) NOT NULL,
    frequency smallint DEFAULT 0 NOT NULL,
    iso_code_3 character(3),
    CONSTRAINT iso_code_check CHECK (((iso_code_2t IS NOT NULL) OR (iso_code_3 IS NOT NULL)))
);
CREATE TABLE public.medium_cdtoc (
    id integer NOT NULL,
    medium integer NOT NULL,
    cdtoc integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT medium_cdtoc_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.place_alias (
    id integer NOT NULL,
    place integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT place_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT place_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 2) OR ((type = 2) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL))))
);
CREATE TABLE public.place_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.place (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    type integer,
    address character varying DEFAULT ''::character varying NOT NULL,
    area integer,
    coordinates point,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT place_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT place_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT place_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.recording (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    artist_credit integer NOT NULL,
    length integer,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    video boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT recording_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT recording_length_check CHECK (((length IS NULL) OR (length > 0)))
);
CREATE TABLE public.release_country (
    release integer NOT NULL,
    country integer NOT NULL,
    date_year smallint,
    date_month smallint,
    date_day smallint
);
CREATE TABLE public.release_group_primary_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_group_secondary_type_join (
    release_group integer NOT NULL,
    secondary_type integer NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL
);
CREATE TABLE public.release_group (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    artist_credit integer NOT NULL,
    type integer,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT release_group_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT release_group_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.release_label (
    id integer NOT NULL,
    release integer NOT NULL,
    label integer,
    catalog_number character varying(255),
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT release_label_catalog_number_check CHECK (musicbrainz.controlled_for_whitespace((catalog_number)::text)),
    CONSTRAINT release_label_check CHECK (((catalog_number IS NOT NULL) OR (label IS NOT NULL)))
);
CREATE TABLE public.release_packaging (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    artist_credit integer NOT NULL,
    release_group integer NOT NULL,
    status integer,
    packaging integer,
    language integer,
    script integer,
    barcode character varying(255),
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    quality smallint DEFAULT '-1'::integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT release_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT release_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.release_status (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_unknown_country (
    release integer NOT NULL,
    date_year smallint,
    date_month smallint,
    date_day smallint,
    CONSTRAINT non_empty_date CHECK (((date_year IS NOT NULL) OR (date_month IS NOT NULL) OR (date_day IS NOT NULL)))
);
CREATE TABLE public.script (
    id integer NOT NULL,
    iso_code character(4) NOT NULL,
    iso_number character(3) NOT NULL,
    name character varying(100) NOT NULL,
    frequency smallint DEFAULT 0 NOT NULL
);
CREATE TABLE public.track (
    id integer NOT NULL,
    gid uuid NOT NULL,
    recording integer NOT NULL,
    medium integer NOT NULL,
    "position" integer NOT NULL,
    number text NOT NULL,
    name character varying NOT NULL,
    artist_credit integer NOT NULL,
    length integer,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    is_data_track boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT track_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT track_length_check CHECK (((length IS NULL) OR (length > 0))),
    CONSTRAINT track_number_check CHECK (musicbrainz.controlled_for_whitespace(number))
);
CREATE TABLE public.work_alias (
    id integer NOT NULL,
    work integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 2) OR ((type = 2) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL)))),
    CONSTRAINT work_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT work_alias_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.work_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.work_attribute (
    id integer NOT NULL,
    work integer NOT NULL,
    work_attribute_type integer NOT NULL,
    work_attribute_type_allowed_value integer,
    work_attribute_text text,
    CONSTRAINT work_attribute_check CHECK ((((work_attribute_type_allowed_value IS NULL) AND (work_attribute_text IS NOT NULL)) OR ((work_attribute_type_allowed_value IS NOT NULL) AND (work_attribute_text IS NULL))))
);
CREATE TABLE public.work_attribute_type_allowed_value (
    id integer NOT NULL,
    work_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.work_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.work_language (
    work integer NOT NULL,
    language integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    CONSTRAINT work_language_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.work (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    type integer,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT work_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT work_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.work_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.l_recording_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_recording_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_recording_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_group_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_group_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_group_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_series_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_series_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_series_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.alternative_medium (
    id integer NOT NULL,
    medium integer NOT NULL,
    alternative_release integer NOT NULL,
    name character varying,
    CONSTRAINT alternative_medium_name_check CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.alternative_medium_track (
    alternative_medium integer NOT NULL,
    track integer NOT NULL,
    alternative_track integer NOT NULL
);
CREATE TABLE public.alternative_release (
    id integer NOT NULL,
    gid uuid NOT NULL,
    release integer NOT NULL,
    name character varying,
    artist_credit integer,
    type integer NOT NULL,
    language integer NOT NULL,
    script integer NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    CONSTRAINT alternative_release_name_check CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.alternative_release_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.alternative_track (
    id integer NOT NULL,
    name character varying,
    artist_credit integer,
    ref_count integer DEFAULT 0 NOT NULL,
    CONSTRAINT alternative_track_check CHECK ((((name)::text <> ''::text) AND ((name IS NOT NULL) OR (artist_credit IS NOT NULL))))
);
CREATE TABLE public.annotation (
    id integer NOT NULL,
    editor integer NOT NULL,
    text text,
    changelog character varying(255),
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.application (
    id integer NOT NULL,
    owner integer NOT NULL,
    name text NOT NULL,
    oauth_id text NOT NULL,
    oauth_secret text NOT NULL,
    oauth_redirect_uri text
);
CREATE TABLE public.area_alias (
    id integer NOT NULL,
    area integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT area_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT area_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL)))
);
CREATE TABLE public.area_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.area_annotation (
    area integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.area_attribute (
    id integer NOT NULL,
    area integer NOT NULL,
    area_attribute_type integer NOT NULL,
    area_attribute_type_allowed_value integer,
    area_attribute_text text,
    CONSTRAINT area_attribute_check CHECK ((((area_attribute_type_allowed_value IS NULL) AND (area_attribute_text IS NOT NULL)) OR ((area_attribute_type_allowed_value IS NOT NULL) AND (area_attribute_text IS NULL))))
);
CREATE TABLE public.area_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.area_attribute_type_allowed_value (
    id integer NOT NULL,
    area_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.area_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.area_tag (
    area integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.area_tag_raw (
    area integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.artist (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    type integer,
    area integer,
    gender integer,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    ended boolean DEFAULT false NOT NULL,
    begin_area integer,
    end_area integer,
    CONSTRAINT artist_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT artist_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT artist_ended_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT artist_va_check CHECK (((id <> 1) OR ((type = 3) AND (gender IS NULL) AND (area IS NULL) AND (begin_area IS NULL) AND (end_area IS NULL) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT group_type_implies_null_gender CHECK ((((gender IS NULL) AND (type = ANY (ARRAY[2, 5, 6]))) OR (type <> ALL (ARRAY[2, 5, 6])) OR (type IS NULL))),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text))
);
CREATE TABLE public.artist_alias (
    id integer NOT NULL,
    artist integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT artist_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT artist_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 3) OR ((type = 3) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL))))
);
CREATE TABLE public.artist_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.artist_annotation (
    artist integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.artist_attribute (
    id integer NOT NULL,
    artist integer NOT NULL,
    artist_attribute_type integer NOT NULL,
    artist_attribute_type_allowed_value integer,
    artist_attribute_text text,
    CONSTRAINT artist_attribute_check CHECK ((((artist_attribute_type_allowed_value IS NULL) AND (artist_attribute_text IS NOT NULL)) OR ((artist_attribute_type_allowed_value IS NOT NULL) AND (artist_attribute_text IS NULL))))
);
CREATE TABLE public.artist_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.artist_attribute_type_allowed_value (
    id integer NOT NULL,
    artist_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.artist_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.artist_ipi (
    artist integer NOT NULL,
    ipi character(11) NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    CONSTRAINT artist_ipi_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT artist_ipi_ipi_check CHECK ((ipi ~ '^\d{11}$'::text))
);
CREATE TABLE public.artist_isni (
    artist integer NOT NULL,
    isni character(16) NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    CONSTRAINT artist_isni_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT artist_isni_isni_check CHECK ((isni ~ '^\d{15}[\dX]$'::text))
);
CREATE TABLE public.artist_meta (
    id integer NOT NULL,
    rating smallint,
    rating_count integer,
    CONSTRAINT artist_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.artist_rating_raw (
    artist integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT artist_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.artist_release_group_nonva (
    is_track_artist boolean NOT NULL,
    artist integer NOT NULL,
    unofficial boolean NOT NULL,
    primary_type smallint,
    secondary_types smallint[],
    first_release_date integer,
    sort_character character(1) NOT NULL COLLATE musicbrainz.musicbrainz,
    release_group integer NOT NULL
);
ALTER TABLE ONLY public.artist_release_group ATTACH PARTITION public.artist_release_group_nonva FOR VALUES IN (false);
CREATE TABLE public.artist_release_group_pending_update (
    release_group integer NOT NULL
);
CREATE TABLE public.artist_release_group_va (
    is_track_artist boolean NOT NULL,
    artist integer NOT NULL,
    unofficial boolean NOT NULL,
    primary_type smallint,
    secondary_types smallint[],
    first_release_date integer,
    sort_character character(1) NOT NULL COLLATE musicbrainz.musicbrainz,
    release_group integer NOT NULL
);
ALTER TABLE ONLY public.artist_release_group ATTACH PARTITION public.artist_release_group_va FOR VALUES IN (true);
CREATE TABLE public.artist_release_nonva (
    is_track_artist boolean NOT NULL,
    artist integer NOT NULL,
    first_release_date integer,
    catalog_numbers text[],
    country_code character(2),
    barcode bigint,
    sort_character character(1) NOT NULL COLLATE musicbrainz.musicbrainz,
    release integer NOT NULL
);
ALTER TABLE ONLY public.artist_release ATTACH PARTITION public.artist_release_nonva FOR VALUES IN (false);
CREATE TABLE public.artist_release_pending_update (
    release integer NOT NULL
);
CREATE TABLE public.artist_release_va (
    is_track_artist boolean NOT NULL,
    artist integer NOT NULL,
    first_release_date integer,
    catalog_numbers text[],
    country_code character(2),
    barcode bigint,
    sort_character character(1) NOT NULL COLLATE musicbrainz.musicbrainz,
    release integer NOT NULL
);
ALTER TABLE ONLY public.artist_release ATTACH PARTITION public.artist_release_va FOR VALUES IN (true);
CREATE TABLE public.artist_tag (
    artist integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.artist_tag_raw (
    artist integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.artist_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.autoeditor_election (
    id integer NOT NULL,
    candidate integer NOT NULL,
    proposer integer NOT NULL,
    seconder_1 integer,
    seconder_2 integer,
    status integer DEFAULT 1 NOT NULL,
    yes_votes integer DEFAULT 0 NOT NULL,
    no_votes integer DEFAULT 0 NOT NULL,
    propose_time timestamp with time zone DEFAULT now() NOT NULL,
    open_time timestamp with time zone,
    close_time timestamp with time zone,
    CONSTRAINT autoeditor_election_status_check CHECK ((status = ANY (ARRAY[1, 2, 3, 4, 5, 6])))
);
CREATE TABLE public.autoeditor_election_vote (
    id integer NOT NULL,
    autoeditor_election integer NOT NULL,
    voter integer NOT NULL,
    vote integer NOT NULL,
    vote_time timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT autoeditor_election_vote_vote_check CHECK ((vote = ANY (ARRAY['-1'::integer, 0, 1])))
);
CREATE TABLE public.cdtoc (
    id integer NOT NULL,
    discid character(28) NOT NULL,
    freedb_id character(8) NOT NULL,
    track_count integer NOT NULL,
    leadout_offset integer NOT NULL,
    track_offset integer[] NOT NULL,
    degraded boolean DEFAULT false NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.cdtoc_raw (
    id integer NOT NULL,
    release integer NOT NULL,
    discid character(28) NOT NULL,
    track_count integer NOT NULL,
    leadout_offset integer NOT NULL,
    track_offset integer[] NOT NULL
);
CREATE TABLE public.deleted_entity (
    gid uuid NOT NULL,
    data jsonb NOT NULL,
    deleted_at timestamp with time zone DEFAULT now() NOT NULL
);
CREATE TABLE public.edit_area (
    edit integer NOT NULL,
    area integer NOT NULL
);
CREATE TABLE public.edit_artist (
    edit integer NOT NULL,
    artist integer NOT NULL,
    status smallint NOT NULL
);
CREATE TABLE public.edit_data (
    edit integer NOT NULL,
    data jsonb NOT NULL
);
CREATE TABLE public.edit_event (
    edit integer NOT NULL,
    event integer NOT NULL
);
CREATE TABLE public.edit_instrument (
    edit integer NOT NULL,
    instrument integer NOT NULL
);
CREATE TABLE public.edit_label (
    edit integer NOT NULL,
    label integer NOT NULL,
    status smallint NOT NULL
);
CREATE TABLE public.edit_note (
    id integer NOT NULL,
    editor integer NOT NULL,
    edit integer NOT NULL,
    text text NOT NULL,
    post_time timestamp with time zone DEFAULT now()
);
CREATE TABLE public.edit_note_recipient (
    recipient integer NOT NULL,
    edit_note integer NOT NULL
);
CREATE TABLE public.edit_place (
    edit integer NOT NULL,
    place integer NOT NULL
);
CREATE TABLE public.edit_recording (
    edit integer NOT NULL,
    recording integer NOT NULL
);
CREATE TABLE public.edit_release (
    edit integer NOT NULL,
    release integer NOT NULL
);
CREATE TABLE public.edit_release_group (
    edit integer NOT NULL,
    release_group integer NOT NULL
);
CREATE TABLE public.edit_series (
    edit integer NOT NULL,
    series integer NOT NULL
);
CREATE TABLE public.edit_url (
    edit integer NOT NULL,
    url integer NOT NULL
);
CREATE TABLE public.edit_work (
    edit integer NOT NULL,
    work integer NOT NULL
);
CREATE TABLE public.editor (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    privs integer DEFAULT 0,
    email character varying(64) DEFAULT NULL::character varying,
    website character varying(255) DEFAULT NULL::character varying,
    bio text,
    member_since timestamp with time zone DEFAULT now(),
    email_confirm_date timestamp with time zone,
    last_login_date timestamp with time zone DEFAULT now(),
    last_updated timestamp with time zone DEFAULT now(),
    birth_date date,
    gender integer,
    area integer,
    password character varying(128) NOT NULL,
    ha1 character(32) NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);
CREATE TABLE public.editor_collection (
    id integer NOT NULL,
    gid uuid NOT NULL,
    editor integer NOT NULL,
    name character varying NOT NULL,
    public boolean DEFAULT false NOT NULL,
    description text DEFAULT ''::text NOT NULL,
    type integer NOT NULL
);
CREATE TABLE public.editor_collection_area (
    collection integer NOT NULL,
    area integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_area_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_artist (
    collection integer NOT NULL,
    artist integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_artist_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_collaborator (
    collection integer NOT NULL,
    editor integer NOT NULL
);
CREATE TABLE public.editor_collection_deleted_entity (
    collection integer NOT NULL,
    gid uuid NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_deleted_entity_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_event (
    collection integer NOT NULL,
    event integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_event_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.editor_collection_instrument (
    collection integer NOT NULL,
    instrument integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_instrument_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_label (
    collection integer NOT NULL,
    label integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_label_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_place (
    collection integer NOT NULL,
    place integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_place_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_recording (
    collection integer NOT NULL,
    recording integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_recording_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_release (
    collection integer NOT NULL,
    release integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_release_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_release_group (
    collection integer NOT NULL,
    release_group integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_release_group_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_series (
    collection integer NOT NULL,
    series integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_series_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_collection_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    entity_type character varying(50) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL,
    CONSTRAINT allowed_collection_entity_type CHECK (((entity_type)::text = ANY ((ARRAY['area'::character varying, 'artist'::character varying, 'event'::character varying, 'instrument'::character varying, 'label'::character varying, 'place'::character varying, 'recording'::character varying, 'release'::character varying, 'release_group'::character varying, 'series'::character varying, 'work'::character varying])::text[])))
);
CREATE TABLE public.editor_collection_work (
    collection integer NOT NULL,
    work integer NOT NULL,
    added timestamp with time zone DEFAULT now(),
    "position" integer DEFAULT 0 NOT NULL,
    comment text DEFAULT ''::text NOT NULL,
    CONSTRAINT editor_collection_work_position_check CHECK (("position" >= 0))
);
CREATE TABLE public.editor_language (
    editor integer NOT NULL,
    language integer NOT NULL,
    fluency musicbrainz.fluency NOT NULL
);
CREATE TABLE public.editor_oauth_token (
    id integer NOT NULL,
    editor integer NOT NULL,
    application integer NOT NULL,
    authorization_code text,
    refresh_token text,
    access_token text,
    expire_time timestamp with time zone NOT NULL,
    scope integer DEFAULT 0 NOT NULL,
    granted timestamp with time zone DEFAULT now() NOT NULL,
    code_challenge text,
    code_challenge_method musicbrainz.oauth_code_challenge_method,
    CONSTRAINT valid_code_challenge CHECK ((((code_challenge IS NULL) = (code_challenge_method IS NULL)) AND ((code_challenge IS NULL) OR (code_challenge ~ '^[A-Za-z0-9.~_-]{43,128}$'::text))))
);
CREATE TABLE public.editor_preference (
    id integer NOT NULL,
    editor integer NOT NULL,
    name character varying(50) NOT NULL,
    value character varying(100) NOT NULL
);
CREATE TABLE public.editor_subscribe_artist (
    id integer NOT NULL,
    editor integer NOT NULL,
    artist integer NOT NULL,
    last_edit_sent integer NOT NULL
);
CREATE TABLE public.editor_subscribe_artist_deleted (
    editor integer NOT NULL,
    gid uuid NOT NULL,
    deleted_by integer NOT NULL
);
CREATE TABLE public.editor_subscribe_collection (
    id integer NOT NULL,
    editor integer NOT NULL,
    collection integer NOT NULL,
    last_edit_sent integer NOT NULL,
    available boolean DEFAULT true NOT NULL,
    last_seen_name character varying(255)
);
CREATE TABLE public.editor_subscribe_editor (
    id integer NOT NULL,
    editor integer NOT NULL,
    subscribed_editor integer NOT NULL,
    last_edit_sent integer NOT NULL
);
CREATE TABLE public.editor_subscribe_label (
    id integer NOT NULL,
    editor integer NOT NULL,
    label integer NOT NULL,
    last_edit_sent integer NOT NULL
);
CREATE TABLE public.editor_subscribe_label_deleted (
    editor integer NOT NULL,
    gid uuid NOT NULL,
    deleted_by integer NOT NULL
);
CREATE TABLE public.editor_subscribe_series (
    id integer NOT NULL,
    editor integer NOT NULL,
    series integer NOT NULL,
    last_edit_sent integer NOT NULL
);
CREATE TABLE public.editor_subscribe_series_deleted (
    editor integer NOT NULL,
    gid uuid NOT NULL,
    deleted_by integer NOT NULL
);
CREATE TABLE public.editor_watch_artist (
    artist integer NOT NULL,
    editor integer NOT NULL
);
CREATE TABLE public.editor_watch_preferences (
    editor integer NOT NULL,
    notify_via_email boolean DEFAULT true NOT NULL,
    notification_timeframe interval DEFAULT '7 days'::interval NOT NULL,
    last_checked timestamp with time zone DEFAULT now() NOT NULL
);
CREATE TABLE public.editor_watch_release_group_type (
    editor integer NOT NULL,
    release_group_type integer NOT NULL
);
CREATE TABLE public.editor_watch_release_status (
    editor integer NOT NULL,
    release_status integer NOT NULL
);
CREATE TABLE public.event (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    "time" time without time zone,
    type integer,
    cancelled boolean DEFAULT false NOT NULL,
    setlist text,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT event_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT event_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT event_ended_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL))))
);
CREATE TABLE public.event_alias (
    id integer NOT NULL,
    event integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT event_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT event_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 2) OR ((type = 2) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL))))
);
CREATE TABLE public.event_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.event_annotation (
    event integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.event_attribute (
    id integer NOT NULL,
    event integer NOT NULL,
    event_attribute_type integer NOT NULL,
    event_attribute_type_allowed_value integer,
    event_attribute_text text,
    CONSTRAINT event_attribute_check CHECK ((((event_attribute_type_allowed_value IS NULL) AND (event_attribute_text IS NOT NULL)) OR ((event_attribute_type_allowed_value IS NOT NULL) AND (event_attribute_text IS NULL))))
);
CREATE TABLE public.event_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.event_attribute_type_allowed_value (
    id integer NOT NULL,
    event_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.event_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.event_meta (
    id integer NOT NULL,
    rating smallint,
    rating_count integer,
    event_art_presence musicbrainz.event_art_presence DEFAULT 'absent'::musicbrainz.event_art_presence NOT NULL,
    CONSTRAINT event_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.event_rating_raw (
    event integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT event_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.event_tag (
    event integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.event_tag_raw (
    event integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.event_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.gender (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.genre (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT genre_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.genre_alias (
    id integer NOT NULL,
    genre integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    primary_for_locale boolean DEFAULT false NOT NULL,
    CONSTRAINT genre_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL)))
);
CREATE TABLE public.instrument (
    id integer NOT NULL,
    gid uuid NOT NULL,
    name character varying NOT NULL,
    type integer,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    description text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT instrument_comment_check CHECK (musicbrainz.controlled_for_whitespace((comment)::text)),
    CONSTRAINT instrument_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.instrument_alias (
    id integer NOT NULL,
    instrument integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT instrument_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT instrument_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 2) OR ((type = 2) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL))))
);
CREATE TABLE public.instrument_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.instrument_annotation (
    instrument integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.instrument_attribute (
    id integer NOT NULL,
    instrument integer NOT NULL,
    instrument_attribute_type integer NOT NULL,
    instrument_attribute_type_allowed_value integer,
    instrument_attribute_text text,
    CONSTRAINT instrument_attribute_check CHECK ((((instrument_attribute_type_allowed_value IS NULL) AND (instrument_attribute_text IS NOT NULL)) OR ((instrument_attribute_type_allowed_value IS NOT NULL) AND (instrument_attribute_text IS NULL))))
);
CREATE TABLE public.instrument_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.instrument_attribute_type_allowed_value (
    id integer NOT NULL,
    instrument_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.instrument_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.instrument_tag (
    instrument integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.instrument_tag_raw (
    instrument integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.instrument_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.iso_3166_2 (
    area integer NOT NULL,
    code character varying(10) NOT NULL
);
CREATE TABLE public.l_area_area (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_area_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_area_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_area_artist (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_artist_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_artist_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_event (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_event_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_event_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_instrument (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_instrument_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_instrument_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_label (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_label_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_label_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_place (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_place_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_place_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_recording_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_area_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_area_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_area_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_artist (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_artist_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_artist_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_artist_event (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_event_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_event_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_instrument (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_instrument_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_instrument_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_label (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_label_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_label_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_place (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_place_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_place_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_recording_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_artist_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_artist_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_artist_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_event (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_event_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_event_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_event_instrument (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_instrument_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_instrument_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_label (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_label_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_label_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_place (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_place_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_place_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_recording_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_event_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_event_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_event_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_instrument (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_instrument_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_instrument_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_instrument_label (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_label_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_label_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_place (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_place_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_place_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_recording_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_instrument_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_instrument_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_instrument_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_label (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_label_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_label_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_label_place (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_place_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_place_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_recording_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_label_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_label_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_label_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_place_place (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_place_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_place_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_place_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_recording_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_place_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_place_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_place_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_series_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_place_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_place_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_place_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_place_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_recording_recording (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_recording_recording_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_recording_recording_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_recording_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_recording_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_recording_release_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_recording_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_recording_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_recording_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_recording_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_recording_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_recording_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_recording_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_recording_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_recording_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_group_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_group_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_group_release_group_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_release_group_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_group_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_group_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_group_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_group_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_group_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_release (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_release_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_release_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_release_release_group (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_release_group_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_release_group_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_release_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_release_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_release_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_series_series (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_series_series_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_series_series_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_series_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_series_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_series_url_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_url_url (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_url_url_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_url_url_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.l_url_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_url_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_url_work_link_order_check CHECK ((link_order >= 0))
);
CREATE TABLE public.l_work_work (
    id integer NOT NULL,
    link integer NOT NULL,
    entity0 integer NOT NULL,
    entity1 integer NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    link_order integer DEFAULT 0 NOT NULL,
    entity0_credit text DEFAULT ''::text NOT NULL,
    entity1_credit text DEFAULT ''::text NOT NULL,
    CONSTRAINT control_for_whitespace_entity0_credit CHECK (musicbrainz.controlled_for_whitespace(entity0_credit)),
    CONSTRAINT control_for_whitespace_entity1_credit CHECK (musicbrainz.controlled_for_whitespace(entity1_credit)),
    CONSTRAINT l_work_work_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT l_work_work_link_order_check CHECK ((link_order >= 0)),
    CONSTRAINT non_loop_relationship CHECK ((entity0 <> entity1))
);
CREATE TABLE public.label_alias (
    id integer NOT NULL,
    label integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT label_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT label_alias_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 2) OR ((type = 2) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL))))
);
CREATE TABLE public.label_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.label_annotation (
    label integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.label_attribute (
    id integer NOT NULL,
    label integer NOT NULL,
    label_attribute_type integer NOT NULL,
    label_attribute_type_allowed_value integer,
    label_attribute_text text,
    CONSTRAINT label_attribute_check CHECK ((((label_attribute_type_allowed_value IS NULL) AND (label_attribute_text IS NOT NULL)) OR ((label_attribute_type_allowed_value IS NOT NULL) AND (label_attribute_text IS NULL))))
);
CREATE TABLE public.label_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.label_attribute_type_allowed_value (
    id integer NOT NULL,
    label_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.label_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.label_ipi (
    label integer NOT NULL,
    ipi character(11) NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    created timestamp with time zone DEFAULT now(),
    CONSTRAINT label_ipi_edits_pending_check CHECK ((edits_pending >= 0)),
    CONSTRAINT label_ipi_ipi_check CHECK ((ipi ~ '^\d{11}$'::text))
);
CREATE TABLE public.label_meta (
    id integer NOT NULL,
    rating smallint,
    rating_count integer,
    CONSTRAINT label_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.label_rating_raw (
    label integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT label_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.label_tag (
    label integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.label_tag_raw (
    label integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.link_attribute (
    link integer NOT NULL,
    attribute_type integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.link_attribute_credit (
    link integer NOT NULL,
    attribute_type integer NOT NULL,
    credited_as text NOT NULL
);
CREATE TABLE public.link_attribute_type (
    id integer NOT NULL,
    parent integer,
    root integer NOT NULL,
    child_order integer DEFAULT 0 NOT NULL,
    gid uuid NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.link_creditable_attribute_type (
    attribute_type integer NOT NULL
);
CREATE TABLE public.link_text_attribute_type (
    attribute_type integer NOT NULL
);
CREATE TABLE public.link_type_attribute_type (
    link_type integer NOT NULL,
    attribute_type integer NOT NULL,
    min smallint,
    max smallint,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.medium_attribute (
    id integer NOT NULL,
    medium integer NOT NULL,
    medium_attribute_type integer NOT NULL,
    medium_attribute_type_allowed_value integer,
    medium_attribute_text text,
    CONSTRAINT medium_attribute_check CHECK ((((medium_attribute_type_allowed_value IS NULL) AND (medium_attribute_text IS NOT NULL)) OR ((medium_attribute_type_allowed_value IS NOT NULL) AND (medium_attribute_text IS NULL))))
);
CREATE TABLE public.medium_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.medium_attribute_type_allowed_format (
    medium_format integer NOT NULL,
    medium_attribute_type integer NOT NULL
);
CREATE TABLE public.medium_attribute_type_allowed_value (
    id integer NOT NULL,
    medium_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.medium_attribute_type_allowed_value_allowed_format (
    medium_format integer NOT NULL,
    medium_attribute_type_allowed_value integer NOT NULL
);
CREATE TABLE public.medium_format (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    year smallint,
    has_discids boolean DEFAULT false NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.medium_index (
    medium integer NOT NULL,
    toc public.cube
);
CREATE TABLE public.old_editor_name (
    name character varying(64) NOT NULL
);
CREATE TABLE public.orderable_link_type (
    link_type integer NOT NULL,
    direction smallint DEFAULT 1 NOT NULL,
    CONSTRAINT orderable_link_type_direction_check CHECK (((direction = 1) OR (direction = 2)))
);
CREATE TABLE public.place_annotation (
    place integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.place_attribute (
    id integer NOT NULL,
    place integer NOT NULL,
    place_attribute_type integer NOT NULL,
    place_attribute_type_allowed_value integer,
    place_attribute_text text,
    CONSTRAINT place_attribute_check CHECK ((((place_attribute_type_allowed_value IS NULL) AND (place_attribute_text IS NOT NULL)) OR ((place_attribute_type_allowed_value IS NOT NULL) AND (place_attribute_text IS NULL))))
);
CREATE TABLE public.place_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.place_attribute_type_allowed_value (
    id integer NOT NULL,
    place_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.place_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.place_meta (
    id integer NOT NULL,
    rating smallint,
    rating_count integer,
    CONSTRAINT place_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.place_rating_raw (
    place integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT place_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.place_tag (
    place integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.place_tag_raw (
    place integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.place_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.recording_alias (
    id integer NOT NULL,
    recording integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT recording_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT recording_alias_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.recording_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.recording_annotation (
    recording integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.recording_attribute (
    id integer NOT NULL,
    recording integer NOT NULL,
    recording_attribute_type integer NOT NULL,
    recording_attribute_type_allowed_value integer,
    recording_attribute_text text,
    CONSTRAINT recording_attribute_check CHECK ((((recording_attribute_type_allowed_value IS NULL) AND (recording_attribute_text IS NOT NULL)) OR ((recording_attribute_type_allowed_value IS NOT NULL) AND (recording_attribute_text IS NULL))))
);
CREATE TABLE public.recording_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.recording_attribute_type_allowed_value (
    id integer NOT NULL,
    recording_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.recording_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.recording_meta (
    id integer NOT NULL,
    rating smallint,
    rating_count integer,
    CONSTRAINT recording_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.recording_rating_raw (
    recording integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT recording_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.recording_tag (
    recording integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.recording_tag_raw (
    recording integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.release_alias (
    id integer NOT NULL,
    release integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT release_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT release_alias_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.release_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_annotation (
    release integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.release_attribute (
    id integer NOT NULL,
    release integer NOT NULL,
    release_attribute_type integer NOT NULL,
    release_attribute_type_allowed_value integer,
    release_attribute_text text,
    CONSTRAINT release_attribute_check CHECK ((((release_attribute_type_allowed_value IS NULL) AND (release_attribute_text IS NOT NULL)) OR ((release_attribute_type_allowed_value IS NOT NULL) AND (release_attribute_text IS NULL))))
);
CREATE TABLE public.release_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_attribute_type_allowed_value (
    id integer NOT NULL,
    release_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_coverart (
    id integer NOT NULL,
    last_updated timestamp with time zone,
    cover_art_url character varying(255)
);
CREATE TABLE public.release_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.release_group_alias (
    id integer NOT NULL,
    release_group integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT control_for_whitespace_sort_name CHECK (musicbrainz.controlled_for_whitespace((sort_name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text)),
    CONSTRAINT only_non_empty_sort_name CHECK (((sort_name)::text <> ''::text)),
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT release_group_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT release_group_alias_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.release_group_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_group_annotation (
    release_group integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.release_group_attribute (
    id integer NOT NULL,
    release_group integer NOT NULL,
    release_group_attribute_type integer NOT NULL,
    release_group_attribute_type_allowed_value integer,
    release_group_attribute_text text,
    CONSTRAINT release_group_attribute_check CHECK ((((release_group_attribute_type_allowed_value IS NULL) AND (release_group_attribute_text IS NOT NULL)) OR ((release_group_attribute_type_allowed_value IS NOT NULL) AND (release_group_attribute_text IS NULL))))
);
CREATE TABLE public.release_group_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_group_attribute_type_allowed_value (
    id integer NOT NULL,
    release_group_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_group_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.release_group_meta (
    id integer NOT NULL,
    release_count integer DEFAULT 0 NOT NULL,
    first_release_date_year smallint,
    first_release_date_month smallint,
    first_release_date_day smallint,
    rating smallint,
    rating_count integer,
    CONSTRAINT release_group_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.release_group_rating_raw (
    release_group integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT release_group_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.release_group_secondary_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.release_group_tag (
    release_group integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.release_group_tag_raw (
    release_group integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.release_meta (
    id integer NOT NULL,
    date_added timestamp with time zone DEFAULT now(),
    info_url character varying(255),
    amazon_asin character varying(10),
    amazon_store character varying(20),
    cover_art_presence musicbrainz.cover_art_presence DEFAULT 'absent'::musicbrainz.cover_art_presence NOT NULL
);
CREATE TABLE public.release_raw (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    artist character varying(255),
    added timestamp with time zone DEFAULT now(),
    last_modified timestamp with time zone DEFAULT now(),
    lookup_count integer DEFAULT 0,
    modify_count integer DEFAULT 0,
    source integer DEFAULT 0,
    barcode character varying(255),
    comment character varying(255) DEFAULT ''::character varying NOT NULL
);
CREATE TABLE public.release_tag (
    release integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.release_tag_raw (
    release integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.replication_control (
    id integer NOT NULL,
    current_schema_sequence integer NOT NULL,
    current_replication_sequence integer,
    last_replication_date timestamp with time zone
);
CREATE TABLE public.series_alias (
    id integer NOT NULL,
    series integer NOT NULL,
    name character varying NOT NULL,
    locale text,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    type integer,
    sort_name character varying NOT NULL,
    begin_date_year smallint,
    begin_date_month smallint,
    begin_date_day smallint,
    end_date_year smallint,
    end_date_month smallint,
    end_date_day smallint,
    primary_for_locale boolean DEFAULT false NOT NULL,
    ended boolean DEFAULT false NOT NULL,
    CONSTRAINT primary_check CHECK ((((locale IS NULL) AND (primary_for_locale IS FALSE)) OR (locale IS NOT NULL))),
    CONSTRAINT search_hints_are_empty CHECK (((type <> 2) OR ((type = 2) AND ((sort_name)::text = (name)::text) AND (begin_date_year IS NULL) AND (begin_date_month IS NULL) AND (begin_date_day IS NULL) AND (end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL) AND (primary_for_locale IS FALSE) AND (locale IS NULL)))),
    CONSTRAINT series_alias_check CHECK (((((end_date_year IS NOT NULL) OR (end_date_month IS NOT NULL) OR (end_date_day IS NOT NULL)) AND (ended = true)) OR ((end_date_year IS NULL) AND (end_date_month IS NULL) AND (end_date_day IS NULL)))),
    CONSTRAINT series_alias_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.series_alias_type (
    id integer NOT NULL,
    name text NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.series_annotation (
    series integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.series_attribute (
    id integer NOT NULL,
    series integer NOT NULL,
    series_attribute_type integer NOT NULL,
    series_attribute_type_allowed_value integer,
    series_attribute_text text,
    CONSTRAINT series_attribute_check CHECK ((((series_attribute_type_allowed_value IS NULL) AND (series_attribute_text IS NOT NULL)) OR ((series_attribute_type_allowed_value IS NOT NULL) AND (series_attribute_text IS NULL))))
);
CREATE TABLE public.series_attribute_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    comment character varying(255) DEFAULT ''::character varying NOT NULL,
    free_text boolean NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.series_attribute_type_allowed_value (
    id integer NOT NULL,
    series_attribute_type integer NOT NULL,
    value text,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.series_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.series_ordering_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL
);
CREATE TABLE public.series_tag (
    series integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.series_tag_raw (
    series integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE public.series_type (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    entity_type character varying(50) NOT NULL,
    parent integer,
    child_order integer DEFAULT 0 NOT NULL,
    description text,
    gid uuid NOT NULL,
    CONSTRAINT allowed_series_entity_type CHECK (((entity_type)::text = ANY ((ARRAY['artist'::character varying, 'event'::character varying, 'recording'::character varying, 'release'::character varying, 'release_group'::character varying, 'work'::character varying])::text[])))
);
CREATE TABLE public.tag (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    ref_count integer DEFAULT 0 NOT NULL,
    CONSTRAINT control_for_whitespace CHECK (musicbrainz.controlled_for_whitespace((name)::text)),
    CONSTRAINT only_non_empty CHECK (((name)::text <> ''::text))
);
CREATE TABLE public.tag_relation (
    tag1 integer NOT NULL,
    tag2 integer NOT NULL,
    weight integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT tag_relation_check CHECK ((tag1 < tag2))
);
CREATE TABLE public.track_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.track_raw (
    id integer NOT NULL,
    release integer NOT NULL,
    title character varying(255) NOT NULL,
    artist character varying(255),
    sequence integer NOT NULL
);
CREATE TABLE public.url (
    id integer NOT NULL,
    gid uuid NOT NULL,
    url text NOT NULL,
    edits_pending integer DEFAULT 0 NOT NULL,
    last_updated timestamp with time zone DEFAULT now(),
    CONSTRAINT url_edits_pending_check CHECK ((edits_pending >= 0))
);
CREATE TABLE public.url_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.vote (
    id integer NOT NULL,
    editor integer NOT NULL,
    edit integer NOT NULL,
    vote smallint NOT NULL,
    vote_time timestamp with time zone DEFAULT now(),
    superseded boolean DEFAULT false NOT NULL
);
CREATE TABLE public.work_annotation (
    work integer NOT NULL,
    annotation integer NOT NULL
);
CREATE TABLE public.work_gid_redirect (
    gid uuid NOT NULL,
    new_id integer NOT NULL,
    created timestamp with time zone DEFAULT now()
);
CREATE TABLE public.work_meta (
    id integer NOT NULL,
    rating smallint,
    rating_count integer,
    CONSTRAINT work_meta_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.work_rating_raw (
    work integer NOT NULL,
    editor integer NOT NULL,
    rating smallint NOT NULL,
    CONSTRAINT work_rating_raw_rating_check CHECK (((rating >= 0) AND (rating <= 100)))
);
CREATE TABLE public.work_tag (
    work integer NOT NULL,
    tag integer NOT NULL,
    count integer NOT NULL,
    last_updated timestamp with time zone DEFAULT now()
);
CREATE TABLE public.work_tag_raw (
    work integer NOT NULL,
    editor integer NOT NULL,
    tag integer NOT NULL,
    is_upvote boolean DEFAULT true NOT NULL
);
CREATE TABLE report.index (
    report_name text NOT NULL,
    generated_at timestamp with time zone DEFAULT now() NOT NULL
);
CREATE TABLE sitemaps.artist_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE sitemaps.control (
    last_processed_replication_sequence integer,
    overall_sitemaps_replication_sequence integer,
    building_overall_sitemaps boolean NOT NULL
);
CREATE TABLE sitemaps.label_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE sitemaps.place_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE sitemaps.recording_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE sitemaps.release_group_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE sitemaps.release_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE sitemaps.tmp_checked_entities (
    id integer NOT NULL,
    entity_type character varying(50) NOT NULL
);
CREATE TABLE sitemaps.work_lastmod (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    paginated boolean NOT NULL,
    sitemap_suffix_key character varying(50) NOT NULL,
    jsonld_sha1 bytea NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    replication_sequence integer NOT NULL
);
CREATE TABLE statistics.statistic (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    value integer NOT NULL,
    date_collected date DEFAULT now() NOT NULL
);
CREATE TABLE statistics.statistic_event (
    date date NOT NULL,
    title text NOT NULL,
    link text NOT NULL,
    description text NOT NULL,
    CONSTRAINT statistic_event_date_check CHECK ((date >= '2000-01-01'::date))
);
CREATE TABLE wikidocs.wikidocs_index (
    page_name text NOT NULL,
    revision integer NOT NULL
);
