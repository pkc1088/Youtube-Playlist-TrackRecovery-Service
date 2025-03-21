drop table if exists Music CASCADE;
CREATE TABLE music (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   video_id VARCHAR(30),
   video_title VARCHAR(255),
   video_uploader VARCHAR(50),
   video_description TEXT,
   video_tags TEXT,
   video_playlist_position INT,
   playlist_id VARCHAR(50) NOT NULL,
   FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE
);
