CREATE DATABASE meeting;

CREATE TABLE "tb_user"
(
    user_id         BIGINT PRIMARY KEY,
    email           VARCHAR(50) UNIQUE NOT NULL,
    nick_name       VARCHAR(256)       NULL,
    sex             INT2               NOT NULL,
    password        VARCHAR(512)       NOT NULL,
    status          INT2               NOT NULL,
    last_login_time BIGINT,
    last_off_time   BIGINT,
    create_time     BIGINT,
    update_time     BIGINT
);

CREATE TABLE "tb_meeting"
(
    meeting_id   BIGINT PRIMARY KEY,
    meeting_no   VARCHAR(256) NOT NULL,
    meeting_name VARCHAR(512) NULL,
    start_time   BIGINT       NOT NULL,
    end_time     BIGINT       NOT NULL,
    join_type    INT2         NOT NULL,
    join_pass    VARCHAR(5)   NULL,
    creator      BIGINT       NOT NULL,
    create_time  BIGINT,
    update_time  BIGINT
);

CREATE TABLE "tb_meeting_member"
(
    meeting_id  BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    nick_name   VARCHAR(256) NULL,
    role        INT2         NOT NULL,
    status      INT2         NOT NULL,
    create_time BIGINT,
    update_time BIGINT,
    PRIMARY KEY (meeting_id, user_id)
);