# Database setup

The application uses MySQL with Spring Data JPA/Hibernate.

## Option 1: fresh local database

1. Start MySQL.
2. Run `reset.sql`.
3. Set `DB_USERNAME` and `DB_PASSWORD`.
4. Start Spring Boot.
5. Hibernate creates/updates the tables and `DataInitializer` creates demo data.

## Option 2: restore the submission dump

Run `kristalball_asset_management_dump.sql`. It contains the schema and demo seed data, including the three demo users.

## Demo users

- Admin: `admin` / `admin123`
- Base Commander: `commander` / `commander123`
- Logistics Officer: `logistics` / `logistics123`

## Creating a fresh dump

Use `export-db.sh` on Linux/macOS or `export-db.bat` on Windows after the application has populated the database.
