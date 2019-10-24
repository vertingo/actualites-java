CREATE TABLE actualites.thread(
	id BIGSERIAL PRIMARY KEY,
	owner VARCHAR(36) NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT NOW(),
	modified TIMESTAMP NOT NULL DEFAULT NOW(),
	CONSTRAINT type_owner_fk FOREIGN KEY(owner) REFERENCES actualites.users(id) ON UPDATE CASCADE ON DELETE CASCADE,

	title VARCHAR(255) NOT NULL,
	icon VARCHAR(255),
	mode SMALLINT NOT NULL
);

ALTER TABLE actualites.thread_shares ADD CONSTRAINT thread_fk FOREIGN KEY(resource_id) REFERENCES actualites.thread(id) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE actualites.info(
	id BIGSERIAL PRIMARY KEY,
	owner VARCHAR(36) NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT NOW(),
	modified TIMESTAMP NOT NULL DEFAULT NOW(),
	CONSTRAINT info_owner_fk FOREIGN KEY(owner) REFERENCES actualites.users(id) ON UPDATE CASCADE ON DELETE CASCADE,

	title VARCHAR(255) NOT NULL,
	content TEXT,
	status SMALLINT NOT NULL,
	publication_date BIGINT,
	expiration_date BIGINT,
	is_headline boolean DEFAULT false,
	thread_id BIGINT NOT NULL,
	CONSTRAINT thread_fk FOREIGN KEY(thread_id) REFERENCES actualites.thread(id) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT valid_dates CHECK (expiration_date >= publication_date),
	CONSTRAINT valid_status CHECK (status IN (0, 1, 2, 3))
);

ALTER TABLE actualites.info_shares ADD CONSTRAINT info_fk FOREIGN KEY(resource_id) REFERENCES actualites.info(id) ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE actualites.comment(
	id BIGSERIAL PRIMARY KEY,
	owner VARCHAR(36) NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT NOW(),
	modified TIMESTAMP NOT NULL DEFAULT NOW(),
	CONSTRAINT comment_owner_fk FOREIGN KEY(owner) REFERENCES actualites.users(id) ON UPDATE CASCADE ON DELETE CASCADE,

	comment TEXT,
	info_id BIGINT NOT NULL,
	CONSTRAINT info_fk FOREIGN KEY(info_id) REFERENCES actualites.info(id) ON UPDATE CASCADE ON DELETE CASCADE
);
