CREATE TABLE IF NOT EXISTS "task"
(

    "id"                     TEXT PRIMARY KEY,
    "stl_directory"          TEXT      NOT NULL,
    "created_at"             TIMESTAMP NOT NULL,
    "updated_at"             TIMESTAMP NOT NULL,
    "attempts"               INTEGER   NOT NULL,
    "status"                 TEXT      NOT NULL,
    "last_error"             TEXT      NULL,
    "processing_instance_id" TEXT      NULL,
    "customer_id"            TEXT      NOT NULL,
    CONSTRAINT "task_customer_fk" FOREIGN KEY ("customer_id") REFERENCES "customer" ("id")
);

CREATE INDEX IF NOT EXISTS "idx_task_status" ON "task" ("status");