# Kristalball Asset Management - Deployment Guide

## Backend
Deploy the project root as a Docker web service on Render or an equivalent host.

Environment variables:

- `DB_URL`: MySQL JDBC URL
- `DB_USERNAME`: database user
- `DB_PASSWORD`: database password
- `JWT_SECRET`: long random secret
- `JWT_EXPIRATION_MS`: `3600000`
- `FRONTEND_ORIGIN`: deployed frontend URL
- `DDL_AUTO`: `update` for initial deployment; switch to `validate` after schema verification

The server listens on `${PORT:9090}`.

## Frontend
Deploy `frontend/` to Vercel, Netlify, or equivalent.

Environment variable:

- `VITE_API_URL=https://<backend-domain>/api`

Build command: `npm run build`
Output directory: `dist`

## Final verification
1. Restore `database/kristalball_asset_management_dump.sql` into MySQL, or start with an empty database and let Hibernate/DataInitializer create the schema and demo data.
2. Start the backend and confirm `GET /api/dashboard` works after login.
3. Start the frontend and verify login for all three demo users.
4. Test purchase, transfer, assignment and expenditure flows.
5. Verify audit logs after each transaction.
6. Replace the deployment placeholders in the submission PDF/form with the real URLs.
