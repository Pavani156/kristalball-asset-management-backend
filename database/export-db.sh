#!/usr/bin/env bash
set -e
mkdir -p backup
mysqldump -u root -p kristalball_asset_management > backup/kristalball_asset_management.sql
echo "Database dump created at database/backup/kristalball_asset_management.sql"
