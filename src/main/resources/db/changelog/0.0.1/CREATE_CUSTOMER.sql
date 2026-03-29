CREATE TABLE IF NOT EXISTS "customer"
(
    "id"                TEXT PRIMARY KEY,
    "customer_name"     TEXT NOT NULL,
    "customer_phone"    TEXT NOT NULL,
    "customer_email"    TEXT NULL,
    "customer_zip_code" TEXT NULL
);
