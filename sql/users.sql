drop table if exists Users CASCADE;
CREATE TABLE users
(
    user_id varchar(30) PRIMARY KEY,
    user_name varchar(50),
    user_channel_id varchar(50),
    user_email varchar(50),
    refresh_token VARCHAR(250)
);
