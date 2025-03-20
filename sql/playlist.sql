drop table if exists Playlist CASCADE;
CREATE TABLE playlist (
    playlist_id VARCHAR(50) PRIMARY KEY,
    playlist_title VARCHAR(255),
    service_type VARCHAR(30),
    user_id VARCHAR(30) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);