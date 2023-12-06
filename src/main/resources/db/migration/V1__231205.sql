DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user`(
	id INT,
	name VARCHAR(255),
	username VARCHAR(255),
	no_telephone_email VARCHAR(255),
	password VARCHAR(255),
	profile_picture VARCHAR(255),
	description VARCHAR(255),

	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by INT,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_by INT,
	deleted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted_by INT,

	PRIMARY KEY(id)
)
ENGINE = INNODB;

CREATE INDEX `user_index` ON `user`(name, username, no_telephone_email);

DROP TABLE IF EXISTS `thread`;
CREATE TABLE IF NOT EXISTS `thread`(
	id INT,
	description VARCHAR(255),
	scheduled_date DATE,
	image VARCHAR(255),
	fk_user_id INT,

	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by INT,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_by INT,
	deleted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted_by INT,

	PRIMARY KEY(id)
)
ENGINE = INNODB;

CREATE INDEX `thread_index` ON `thread`(fk_user_id);

DROP TABLE IF EXISTS `like`;
CREATE TABLE IF NOT EXISTS `like`(
	id INT,
	fk_user_id INT,
	fk_thread_id INT,

	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by INT,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_by INT,
	deleted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted_by INT,

	PRIMARY KEY(id)
) ENGINE = INNODB;

CREATE INDEX `like_index` ON `like`(fk_user_id, fk_thread_id);

DROP TABLE IF EXISTS `following`;
CREATE TABLE IF NOT EXISTS `following`(
	id INT,
	fk_user_id_followed INT,
	fk_user_id_following INT,

	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by INT,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_by INT,
	deleted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted_by INT,

	PRIMARY KEY(id)
) ENGINE = INNODB;

CREATE INDEX `following_index` ON `following`(fk_user_id_followed, fk_user_id_following);

DROP TABLE IF EXISTS `retweet`;
CREATE TABLE IF NOT EXISTS `retweet`(
	id INT,
	fk_user_id INT,
	fk_thread_id INT,

	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by INT,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_by INT,
	deleted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted_by INT,

	PRIMARY KEY(id)
) ENGINE = INNODB;

CREATE INDEX `retweet_index` ON `retweet`(fk_user_id, fk_thread_id);

DROP TABLE IF EXISTS `comment`;
CREATE TABLE IF NOT EXISTS `comment`(
	id INT,
	fk_user_id INT,
	description TEXT,

	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by INT,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_by INT,
	deleted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	deleted_by INT,

	PRIMARY KEY(id)
) ENGINE = INNODB;

CREATE INDEX `comment_index` ON `comment`(fk_user_id);
