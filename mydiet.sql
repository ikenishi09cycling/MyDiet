CREATE DATABASE mydietdb;

USE mydiet;

CREATE TABLE users (
	user_id		INT				PRIMARY KEY AUTO_INCREMENT,
	first_name	VARCHAR(50)		NOT NULL,
	last_name	VARCHAR(50)		NOT NULL,
	gender		TINYINT			NOT NULL,
	height		DECIMAL(4,1)	NOT NULL,
	create_dt	TIMESTAMP		NOT NULL DEFAULT CURRENT_TIMESTAMP,
	update_dt	TIMESTAMP		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	CHECK (gender IN (0, 1))
);

CREATE TABLE users_auth (
	user_id			INT				PRIMARY KEY,
	mailaddress		VARCHAR(255)	NOT NULL UNIQUE,
	password_hash	VARCHAR(255)	NOT NULL,
	create_dt		TIMESTAMP		NOT NULL DEFAULT CURRENT_TIMESTAMP,
	update_dt		TIMESTAMP		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE weight (
    user_id    INT          NOT NULL,
    weight_dt  DATE         NOT NULL,
    weight     DECIMAL(4,1) NULL,
    fat        DECIMAL(4,1) NULL,
    memo       VARCHAR(200) NULL,
    create_dt  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_dt  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, weight_dt)
);

CREATE TABLE weight_goal (
	user_id		INT				NOT NULL,
	start_dt	DATE			NOT NULL,
	end_dt		DATE			NOT NULL,
	weight_goal	DECIMAL(4,1)	NOT NULL,
	create_dt	DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP,
	update_dt	DATETIME		NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
	PRIMARY KEY (user_id, end_dt)
);

CREATE TABLE bmi_category (
	label_id	INT				PRIMARY KEY NOT NULL,
	label_name	VARCHAR(10)		NOT NULL,
	bmi_st		DECIMAL(3,1)	NOT NULL,
	bmi_ed		DECIMAL(3,1)	NOT NULL,
	color		VARCHAR(7)		NOT NULL
);

INSERT INTO bmi_category (
	label_id,
	label_name,
	bmi_st,
	bmi_ed,
	color
)
VALUES
	(1, '低体重', 0.0, 18.5, '#74b9ff'),
	(2, '普通体重', 18.5, 25.0, '#55efc4'),
	(3, '肥満（1度）', 25.0, 30.0, '#ffdf91'),
	(4, '肥満（2度）', 30.0, 35.0, '#fab1a0'),
	(5, '肥満（3度）', 35.0, 40.0, '#ff7675'),
	(6, '肥満（4度）', 40.0, 99.9, '#d63031');

CREATE VIEW v_weight_goal_config AS SELECT CAST('2999-12-31' AS DATE) AS indefinite_date;
