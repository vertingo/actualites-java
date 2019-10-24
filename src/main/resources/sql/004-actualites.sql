CREATE FUNCTION actualites.insert_user(key VARCHAR, username VARCHAR) RETURNS VOID AS
$$
declare
    cnt integer;
BEGIN
    SELECT count(*) INTO cnt FROM actualites.users WHERE id = key;
    IF cnt = 0 THEN
        INSERT INTO actualites.users VALUES (key, username);
    END IF;
    RETURN;
END;
$$
LANGUAGE plpgsql;

CREATE FUNCTION actualites.insert_group(key VARCHAR, name VARCHAR) RETURNS VOID AS
$$
declare
    cnt integer;
BEGIN
    SELECT count(*) INTO cnt FROM actualites.groups WHERE id = key;
    IF cnt = 0 THEN
	INSERT INTO actualites.groups VALUES (key, name);
    END IF;
    RETURN;
END;
$$
LANGUAGE plpgsql;