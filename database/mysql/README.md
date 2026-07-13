# MySQL database scripts

The scripts in this directory are the source of truth for local development,
automated tests, and reproducible integration environments. They target MySQL
8.0 with an `utf8mb4` client connection.

## Layout

- `bootstrap/` contains the complete baseline for a new environment.
- `migrations/` contains append-only, versioned upgrades for databases created
  from an earlier baseline. Apply each migration once and in filename order.
- `seed/` contains repeatable, non-production fixture data for testing and
  integration demonstrations.

## Execution order

### New environment

1. Run `bootstrap/00_all_in_one.sql` with a MySQL client configured for
   `utf8mb4`.
2. Load one fixture from `seed/` as needed. The comprehensive fixture includes
   cleanup for its own `TEST-20260713` records before inserting them again.

The current bootstrap already includes the SOP and barcode schema. Therefore,
do not apply `V20260711__sop_barcode_upgrade.sql` after this baseline.

### Existing environment

Do not rerun the bootstrap script. Apply only the migrations that have not
already been applied, in filename order, then load test fixtures only when
required.

## Data safety

Seed scripts are for development and test environments only. Do not store
production exports, credentials, or binary database backups in this directory;
place local archives under `database/archive/`, which Git ignores.
