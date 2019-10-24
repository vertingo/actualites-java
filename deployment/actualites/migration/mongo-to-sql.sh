#!/bin/bash
mongo localhost:27017/one_gridfs mongo-to-sql.js > /tmp/mongo-to-sql.sql.tmp
more +3 /tmp/mongo-to-sql.sql.tmp > /tmp/mongo-to-sql.sql
psql -h localhost -p 5432 -d ong -U "web-education" -f /tmp/mongo-to-sql.sql
rm /tmp/mongo-to-sql.sql.tmp
rm /tmp/mongo-to-sql.sql