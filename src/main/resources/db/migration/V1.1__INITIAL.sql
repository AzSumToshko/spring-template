CREATE TABLE logs (
  id UUID PRIMARY KEY,
  user_id VARCHAR(100),
  level VARCHAR(10) NOT NULL,
  message TEXT NOT NULL,
  details TEXT,
  source VARCHAR(255),
  timestamp TIMESTAMP NOT NULL,
  ip_address VARCHAR(255),
  user_agent TEXT
);
