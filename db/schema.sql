-- region
CREATE TABLE region (
  id VARCHAR(8) NOT NULL,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

-- channel
CREATE TABLE channel (
  id BIGINT NOT NULL AUTO_INCREMENT,
  youtube_id VARCHAR(255) NOT NULL,
  title VARCHAR(255) NOT NULL,
  thumbnail_url VARCHAR(255),
  last_updated_at DATETIME,
  PRIMARY KEY (id),
  CONSTRAINT uk_channel_youtube_id UNIQUE (youtube_id)
);

-- video
CREATE TABLE video (
  id BIGINT NOT NULL AUTO_INCREMENT,
  youtube_id VARCHAR(255) NOT NULL,
  channel_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  thumbnail_url VARCHAR(255),
  last_updated_at DATETIME,
  PRIMARY KEY (id),
  CONSTRAINT uk_video_youtube_id UNIQUE (youtube_id),
  CONSTRAINT fk_video_channel_id FOREIGN KEY (channel_id) REFERENCES channel(id)
);

-- trending_snapshot
CREATE TABLE trending_snapshot (
  id BIGINT NOT NULL AUTO_INCREMENT,
  region_id VARCHAR(8) NOT NULL,
  captured_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_region_id_caputred_at FOREIGN KEY (region_id) REFERENCES region(id)
);
CREATE INDEX idx_region_id_caputred_at ON trending_snapshot (region_id, captured_at);

-- trending_snapshot_video
CREATE TABLE trending_snapshot_video (
  id BIGINT NOT NULL AUTO_INCREMENT,
  trending_snapshot_id BIGINT NOT NULL,
  trending_video_id BIGINT NOT NULL,
  list_idx INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tsv_snapshot_id FOREIGN KEY (trending_snapshot_id) REFERENCES trending_snapshot (id)
    ON DELETE CASCADE,
  CONSTRAINT fk_tsv_video_id FOREIGN KEY (trending_video_id) REFERENCES video (id)
);

-- member
CREATE TABLE member (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT uk_username UNIQUE (username)
);

-- for mysql
-- ALTER TABLE member MODIFY username VARCHAR(255) COLLATE utf8mb4_bin;
-- ALTER TABLE member MODIFY password_hash VARCHAR(255) COLLATE utf8mb4_bin;
-- ALTER TABLE member MODIFY email VARCHAR(255) COLLATE utf8mb4_bin;
