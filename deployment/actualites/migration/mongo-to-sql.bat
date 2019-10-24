mongo localhost:27017/one_gridfs mongo-to-sql.js > %USERPROFILE%\AppData\Local\Temp\mongo-to-sql.sql.tmp
more +2 %USERPROFILE%\AppData\Local\Temp\mongo-to-sql.sql.tmp > %USERPROFILE%\AppData\Local\Temp\mongo-to-sql.sql
psql -h localhost -p 5432 -d ong -U "web-education" -f %USERPROFILE%\AppData\Local\Temp\mongo-to-sql.sql
DEL %USERPROFILE%\AppData\Local\Temp\mongo-to-sql.sql.tmp
DEL %USERPROFILE%\AppData\Local\Temp\mongo-to-sql.sql