CREATE DATABASE IF NOT EXISTS `access`;
CREATE DATABASE IF NOT EXISTS `db`;
GRANT ALL PRIVILEGES ON access.* TO 'root' identified by 'password';
GRANT ALL PRIVILEGES ON db.* TO 'root' identified by 'password';

