@echo off
mkdir backup 2>nul
mysqldump -u root -p kristalball_asset_management > backup\kristalball_asset_management.sql
if %errorlevel% neq 0 exit /b %errorlevel%
echo Database dump created at database\backup\kristalball_asset_management.sql
