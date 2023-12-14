DROP TABLE IF EXISTS `db_log`;
CREATE TABLE IF NOT EXISTS `db_log`(
	id VARCHAR(255) NOT NULL UNIQUE,
	operation VARCHAR(255),
	table_name VARCHAR(255),
	message TEXT,

	PRIMARY KEY(id)
);

CREATE INDEX `db_log_index` ON `db_log`(table_name);