CREATE SCHEMA actualites;

CREATE TABLE actualites.users (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	username VARCHAR(255)
);

CREATE TABLE actualites.groups (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	name VARCHAR(255)
);

CREATE TABLE actualites.members (
	id VARCHAR(36) NOT NULL PRIMARY KEY,
	user_id VARCHAR(36),
	group_id VARCHAR(36),
	CONSTRAINT user_fk FOREIGN KEY(user_id) REFERENCES actualites.users(id) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT group_fk FOREIGN KEY(group_id) REFERENCES actualites.groups(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE actualites.thread_shares (
	member_id VARCHAR(36) NOT NULL,
	resource_id BIGINT NOT NULL,
	action VARCHAR(255) NOT NULL,
	CONSTRAINT thread_share PRIMARY KEY (member_id, resource_id, action),
	CONSTRAINT thread_share_member_fk FOREIGN KEY(member_id) REFERENCES actualites.members(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE actualites.info_shares (
	member_id VARCHAR(36) NOT NULL,
	resource_id BIGINT NOT NULL,
	action VARCHAR(255) NOT NULL,
	CONSTRAINT info_share PRIMARY KEY (member_id, resource_id, action),
	CONSTRAINT info_share_member_fk FOREIGN KEY(member_id) REFERENCES actualites.members(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE actualites.scripts (
	filename VARCHAR(255) NOT NULL PRIMARY KEY,
	passed TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE FUNCTION actualites.merge_users(key VARCHAR, data VARCHAR) RETURNS VOID AS
$$
BEGIN
    LOOP
        UPDATE actualites.users SET username = data WHERE id = key;
        IF found THEN
            RETURN;
        END IF;
        BEGIN
            INSERT INTO actualites.users(id,username) VALUES (key, data);
            RETURN;
        EXCEPTION WHEN unique_violation THEN
        END;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION actualites.insert_users_members() RETURNS TRIGGER AS 
$$
BEGIN
	IF (TG_OP = 'INSERT') THEN
        INSERT INTO actualites.members (id, user_id) VALUES (NEW.id, NEW.id);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ 
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION actualites.insert_groups_members() RETURNS TRIGGER AS 
$$
BEGIN
	IF (TG_OP = 'INSERT') THEN
        INSERT INTO actualites.members (id, group_id) VALUES (NEW.id, NEW.id);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ 
LANGUAGE plpgsql;

CREATE TRIGGER users_trigger
AFTER INSERT ON actualites.users
    FOR EACH ROW EXECUTE PROCEDURE actualites.insert_users_members();

CREATE TRIGGER groups_trigger
AFTER INSERT ON actualites.groups
    FOR EACH ROW EXECUTE PROCEDURE actualites.insert_groups_members();
    
CREATE TYPE actualites.share_tuple as (member_id VARCHAR(36), action VARCHAR(255));