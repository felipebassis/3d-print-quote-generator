CREATE TABLE IF NOT EXISTS "instance"
(
    "id"                INTEGER     PRIMARY KEY AUTOINCREMENT,
    "instance_id"       TEXT        NOT NULL,
    "last_heartbeat"    TIMESTAMP   NOT NULL,
    "status"            TEXT        NOT NULL    CHECK ( "status" IN ('ALIVE', 'DEAD', 'SHUTTING_DOWN') )
);

CREATE INDEX IF NOT EXISTS "idx_instance_status" ON "instance"("status");
CREATE INDEX IF NOT EXISTS "idx_instance_last_heartbeat" ON "instance"("last_heartbeat");
