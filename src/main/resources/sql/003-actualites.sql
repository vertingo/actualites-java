ALTER TABLE actualites.info DROP CONSTRAINT valid_dates; 
ALTER TABLE actualites.info ALTER COLUMN publication_date TYPE TIMESTAMP USING to_timestamp(publication_date) AT TIME ZONE 'UTC';
ALTER TABLE actualites.info ALTER COLUMN expiration_date TYPE TIMESTAMP USING to_timestamp(expiration_date) AT TIME ZONE 'UTC';
ALTER TABLE actualites.info ADD CONSTRAINT valid_dates CHECK (expiration_date >= publication_date);