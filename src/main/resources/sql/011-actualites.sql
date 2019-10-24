CREATE TABLE actualites.info_revision
(
  id bigserial NOT NULL,
  info_id bigint,
  created timestamp without time zone DEFAULT now(),
  title character varying(255),
  content text,
  owner character varying(36),
  event character varying,
  CONSTRAINT info_revision_pk PRIMARY KEY (id),
  CONSTRAINT info_pk FOREIGN KEY (info_id)
      REFERENCES actualites.info (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT user_fk FOREIGN KEY (owner)
      REFERENCES actualites.users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create index on actualites.info_revision (owner, info_id);