-- clean index
DROP INDEX IF EXISTS actualites.info_content_idx;
DROP INDEX IF EXISTS actualites.info_title_idx;

DROP TEXT SEARCH CONFIGURATION IF EXISTS fr cascade;
-- specific configuration to language natively create vectors without accents (one configuration per supported language)
CREATE TEXT SEARCH CONFIGURATION  fr ( COPY = french ) ;
ALTER TEXT SEARCH CONFIGURATION fr ALTER MAPPING
FOR hword, hword_part, word WITH unaccent, french_stem;

-- With evolution of create services, it is possible to manage multiple languages to search for the same application instance
ALTER TABLE actualites.info ADD language VARCHAR(2) NOT NULL DEFAULT('fr');

ALTER TABLE actualites.info ADD COLUMN text_searchable tsvector;
UPDATE actualites.info SET text_searchable =
     to_tsvector(language::regconfig, coalesce(title,'') || ' ' || coalesce(regexp_replace(content, '<[^>]*>',' ','g'),'')    );
CREATE INDEX textsearch_idx ON actualites.info USING gin(text_searchable);


CREATE FUNCTION actualites.text_searchable_trigger() RETURNS trigger AS $$
begin
  new.text_searchable := to_tsvector(new.language::regconfig, coalesce(new.title,'') || ' ' || coalesce(regexp_replace(new.content, '<[^>]*>',' ','g'),''));
  return new;
end
$$ LANGUAGE plpgsql;

CREATE TRIGGER tsvector_update_trigger BEFORE INSERT OR UPDATE
    ON actualites.info FOR EACH ROW EXECUTE PROCEDURE actualites.text_searchable_trigger();