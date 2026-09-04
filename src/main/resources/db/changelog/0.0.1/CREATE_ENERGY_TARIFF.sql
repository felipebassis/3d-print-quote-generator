CREATE TABLE IF NOT EXISTS "energy_tariff"
(
    "id"              TEXT PRIMARY KEY,
    "operator"        TEXT NOT NULL,
    "country"         TEXT NOT NULL,
    "tariff"          TEXT NOT NULL,
    "reference_month" TEXT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS "idx_energy_tariff_operator_reference_month" ON "energy_tariff" ("operator","reference_month");
