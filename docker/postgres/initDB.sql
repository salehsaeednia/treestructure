CREATE DATABASE nodedb;
CREATE USER nodeuser with ENCRYPTED PASSWORD 'nodepass'
GRANT ALL ON DATABASE nodedb to nodeuser;