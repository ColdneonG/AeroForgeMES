# Fan MES

Fan MES is organized as a lightweight microservice project for fan manufacturing execution.

## Top-level Layout

- `backend/`: Spring Boot and Spring Cloud microservices.
- `frontend/`: Vue admin, workshop pad, mini program, and kanban frontends.
- `database/`: MySQL schema, migration, and seed scripts split by service ownership.
- `deploy/`: Docker, Nacos, Nginx, and local compose deployment assets.


## Backend Services

- `mes-gateway`: unified API gateway, routing, CORS, and basic token checks.
- `mes-auth`: login, logout, token issue, token refresh, and current user identity.
- `mes-system`: users, roles, menus, permissions, dictionaries, numbering, and audit logs.
- `mes-production`: production orders, barcode, shopfloor execution, process routes, and piecework wage basics.
- `mes-quality`: inspection standards, inspection documents, results, defects, and quality traceability.
- `mes-equipment`: equipment assets, inspection, maintenance, repair, status, and andon exceptions.
- `mes-report`: reports, dashboards, kanban, mini-program data aggregation, and read-only trace queries.
- `mes-integration`: ERP, standard OpenAPI, device access, idempotency, and sync logs.
- `mes-common`: shared response models, exceptions, base entities, security helpers, and utilities.

