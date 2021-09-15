CREATE TABLE IF NOT EXISTS model
(
    id            TEXT PRIMARY KEY,
    text          TEXT                     NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
)
