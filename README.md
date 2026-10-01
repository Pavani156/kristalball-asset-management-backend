# Kristalball Military Asset Management System

A Spring Boot + React/Vite application for managing military assets, purchases, transfers, assignments and expenditures across multiple bases.

## Requirement coverage

### Dashboard
- Opening Balance
- Closing Balance
- Net Movement = Purchases + Transfer In - Transfer Out
- Assigned
- Expended
- Filters: date range, base and equipment type/category
- Net Movement detail popup for purchases, transfer-in and transfer-out

### Core operations
- Purchases with date, base and equipment-type/category filtering
- Transfers with source/destination bases, quantity, status and timestamp
- Assignment of assets to personnel
- Expenditure tracking
- Transfer and transaction history
- API audit logging for purchases, transfers, assignments and expenditures

### RBAC
- `ADMIN`: full application access
- `BASE_COMMANDER`: operations scoped to the assigned base; transfers must involve the assigned base
- `LOGISTICS_OFFICER`: purchase and transfer operations, plus dashboard/reference-data read access

### Technology
- Backend: Java 17, Spring Boot, Spring Data JPA, Spring Security
- Authentication: JWT + BCrypt
- Database: MySQL
- Frontend: React + Vite
- API style: REST

MySQL is used because the application has strongly related entities (bases, users, roles, categories, assets, purchases, transfers, assignments, expenditures and audit logs) and needs transactional integrity and foreign-key relationships.

## Local setup

### 1. Database

Option A — clean database:
1. Start MySQL.
2. Run `database/reset.sql`.
3. Start the Spring Boot application. Hibernate creates/updates the tables and `DataInitializer` creates demo data.

Option B — restore the included dump:
1. Start MySQL.
2. Run `database/kristalball_asset_management_dump.sql`.
3. Start the Spring Boot application.

The included dump contains the schema, roles, bases, equipment categories, opening balances, representative assets and demo users.

### 2. Backend

Set the following environment variables for your local machine/deployment:

```text
DB_URL=jdbc:mysql://localhost:3306/kristalball_asset_management?createDatabaseIfNotExist=true&serverTimezone=Asia/Kolkata
DB_USERNAME=root
DB_PASSWORD=<your-mysql-password>
JWT_SECRET=<base64-encoded-secret-at-least-256-bits>
JWT_EXPIRATION_MS=3600000
FRONTEND_ORIGIN=http://localhost:5173
```

Run:

```bash
./mvnw spring-boot:run
```

or run `KristalballAssetManagementApplication.java` from STS.

Backend:

`http://localhost:9090`

Health check: `GET http://localhost:9090/api/health`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:

`http://localhost:5173`

For deployment, set:

```text
VITE_API_URL=https://<your-backend-domain>/api
```

## Demo credentials

These credentials are intended for the take-home demonstration only:

- Admin: `admin` / `admin123`
- Base Commander: `commander` / `commander123`
- Logistics Officer: `logistics` / `logistics123`

Change them before any real deployment.

## Main API endpoints

| Feature | Endpoint |
|---|---|
| Login | `POST /api/auth/login` |
| Dashboard | `GET /api/dashboard` |
| Purchases | `/api/purchases` |
| Transfers | `/api/transfers` |
| Assignments | `/api/assignments` |
| Expenditures | `/api/expenditures` |
| Assets | `/api/assets` |
| Bases | `/api/bases` |
| Equipment types | `/api/categories` |
| Inventory balances | `/api/inventory-balances` |
| Audit logs | `/api/audit-logs` |
| Users | `/api/users` |
| Roles | `/api/roles` |

## Security notes

- Passwords are stored with BCrypt hashing.
- JWT authentication is stateless.
- JWT secret and database password are configurable through environment variables.
- Do not commit production secrets.
- CORS allows local development origins and the configured `FRONTEND_ORIGIN`.

## Database dump

`database/kristalball_asset_management_dump.sql` is included in the submission archive.

For a fresh dump from a live MySQL database, use:

```bash
database/export-db.sh
```

or on Windows:

```bat
database\export-db.bat
```

## Production deployment

### Backend (Render)
Set these environment variables in the Render service:

- `DB_URL` = JDBC URL of the managed MySQL database
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` = long random secret
- `FRONTEND_ORIGIN` = deployed frontend URL
- `DDL_AUTO` = `update` for first deployment, then `validate` after the schema is verified

The application uses Render's `PORT` environment variable automatically.

### Frontend (Vercel/Netlify)
Set:

- `VITE_API_URL` = `https://<backend-domain>/api`

Do not commit production secrets. The repository intentionally contains only local demo defaults.
