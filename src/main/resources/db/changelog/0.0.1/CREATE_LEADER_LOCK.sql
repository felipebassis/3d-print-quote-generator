CREATE TABLE IF NOT EXISTS "leader_lock"(
    id              TEXT        PRIMARY KEY,
    current_leader  TEXT        NOT NULL,
    expires_at      TIMESTAMP   NOT NULL
);

INSERT OR IGNORE INTO "leader_lock" (id, current_leader, expires_at)
VALUES ('singleton', '', unixepoch());