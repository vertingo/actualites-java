DROP FUNCTION IF EXISTS actualites.insert_user(key VARCHAR, name VARCHAR);
CREATE FUNCTION actualites.insert_user(key VARCHAR, name VARCHAR) RETURNS VOID AS
$$
declare
    cnt integer;
    nm varchar;
BEGIN
    SELECT count(*) INTO cnt FROM actualites.users WHERE id = key;
    SELECT username INTO nm FROM actualites.users WHERE id = key;
    IF cnt <= 0 THEN
        INSERT INTO actualites.users VALUES (key, name);
    ELSIF nm IS NULL AND name IS NOT NULL THEN
    	UPDATE actualites.users SET username = name WHERE id = key;
    END IF;
    RETURN;
END;
$$
LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS actualites.insert_group(key VARCHAR, groupname VARCHAR);
CREATE FUNCTION actualites.insert_group(key VARCHAR, groupname VARCHAR) RETURNS VOID AS
$$
declare
    cnt integer;
    nm varchar;
BEGIN
    SELECT count(*) INTO cnt FROM actualites.groups WHERE id = key;
    SELECT name INTO nm FROM actualites.groups WHERE id = key;
    IF cnt = 0 THEN
    	INSERT INTO actualites.groups VALUES (key, groupname);
	ELSIF nm IS NULL AND groupname IS NOT NULL THEN
    	UPDATE actualites.groups SET name = groupname WHERE id = key;
    END IF;
    RETURN;
END;
$$
LANGUAGE plpgsql;