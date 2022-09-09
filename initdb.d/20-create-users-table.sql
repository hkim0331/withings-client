use withings;

DROP TABLE if exists users;
CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,

  valid BOOLEAN DEFAULT true,

  name    VARCHAR(255) NOT NULL UNIQUE,
  belong  VARCHAR(255),
  email   VARCHAR(255),

  cid     VARCHAR(255),
  secret  VARCHAR(255),
  access  VARCHAR(255),
  refresh VARCHAR(255),
  userid  VARCHAR(255),

  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
