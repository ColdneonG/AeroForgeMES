# Project Structure

This structure follows the lightweight microservice design in `design/microservice_architecture`.

```text
fan-mes
├─ backend
│  ├─ mes-parent
│  ├─ mes-common
│  ├─ mes-gateway
│  ├─ mes-auth
│  ├─ mes-system
│  ├─ mes-production
│  ├─ mes-quality
│  ├─ mes-equipment
│  ├─ mes-report
│  └─ mes-integration
├─ frontend
│  ├─ apps
│  │  ├─ admin-web
│  │  ├─ workshop-pad
│  │  ├─ wechat-miniapp
│  │  └─ kanban-screen
│  └─ packages
│     ├─ api-client
│     ├─ components
│     ├─ constants
│     └─ utils
├─ database
│  ├─ docs
│  └─ mysql
│     ├─ common
│     ├─ mes_auth
│     ├─ mes_system
│     ├─ mes_production
│     ├─ mes_quality
│     ├─ mes_equipment
│     ├─ mes_report
│     └─ mes_integration
├─ deploy
│  ├─ compose
│  ├─ docker
│  ├─ nacos
│  └─ nginx
├─ docs
│  ├─ api
│  ├─ architecture
│  └─ deployment
├─ scripts
│  ├─ db
│  ├─ deploy
│  └─ dev
└─ tests
   ├─ api
   ├─ contract
   ├─ e2e
   ├─ integration
   └─ load
```

## Backend Package Convention

Each runnable service uses this package shape:

```text
src/main/java/com/fanmes/<service>
├─ controller
├─ service
├─ domain
└─ infrastructure
```

The current scaffold creates the first three layers. Add `infrastructure`, `repository`, `mapper`, `dto`, `vo`, or `client` folders only when the service starts receiving real code.

## Service Ownership

| Service | Owned business scope |
|---|---|
| `mes-auth` | Authentication and token identity |
| `mes-system` | User, role, menu, permission, dictionary, numbering |
| `mes-production` | Order, barcode, shopfloor, process, piecework wage |
| `mes-quality` | Inspection standard, inspection document, defect, traceability |
| `mes-equipment` | Equipment asset, maintenance, repair, andon |
| `mes-report` | Dashboard, kanban, report, read-only aggregation |
| `mes-integration` | ERP, device, OpenAPI, sync log |

