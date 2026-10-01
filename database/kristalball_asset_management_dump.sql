-- Kristalball Asset Management database dump
-- Generated from the project JPA model for submission/deployment.
-- MySQL 8.x

DROP DATABASE IF EXISTS kristalball_asset_management;
CREATE DATABASE kristalball_asset_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE kristalball_asset_management;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB;

CREATE TABLE bases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    location VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_bases_name (name)
) ENGINE=InnoDB;

CREATE TABLE asset_categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_asset_categories_name (name)
) ENGINE=InnoDB;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255),
    role_id BIGINT,
    base_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_base FOREIGN KEY (base_id) REFERENCES bases(id)
) ENGINE=InnoDB;

CREATE TABLE assets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    serial_number VARCHAR(255),
    category_id BIGINT,
    base_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_assets_serial_number (serial_number),
    CONSTRAINT fk_assets_category FOREIGN KEY (category_id) REFERENCES asset_categories(id),
    CONSTRAINT fk_assets_base FOREIGN KEY (base_id) REFERENCES bases(id)
) ENGINE=InnoDB;

CREATE TABLE inventory_balances (
    id BIGINT NOT NULL AUTO_INCREMENT,
    opening_balance INT,
    base_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory_base_category (base_id, category_id),
    CONSTRAINT fk_inventory_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_inventory_category FOREIGN KEY (category_id) REFERENCES asset_categories(id)
) ENGINE=InnoDB;

CREATE TABLE purchases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    purchase_date DATE,
    quantity INT,
    supplier VARCHAR(255),
    category_id BIGINT NOT NULL,
    base_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_purchases_category FOREIGN KEY (category_id) REFERENCES asset_categories(id),
    CONSTRAINT fk_purchases_base FOREIGN KEY (base_id) REFERENCES bases(id)
) ENGINE=InnoDB;

CREATE TABLE transfers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transfer_date DATETIME,
    quantity INT,
    status VARCHAR(255),
    asset_id BIGINT NOT NULL,
    from_base_id BIGINT NOT NULL,
    to_base_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transfers_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_transfers_from_base FOREIGN KEY (from_base_id) REFERENCES bases(id),
    CONSTRAINT fk_transfers_to_base FOREIGN KEY (to_base_id) REFERENCES bases(id)
) ENGINE=InnoDB;

CREATE TABLE assignments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    personnel_name VARCHAR(255),
    quantity INT,
    assigned_date DATETIME,
    status VARCHAR(255),
    asset_id BIGINT NOT NULL,
    base_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_assignments_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_assignments_base FOREIGN KEY (base_id) REFERENCES bases(id)
) ENGINE=InnoDB;

CREATE TABLE expenditures (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantity INT NOT NULL,
    reason VARCHAR(255),
    expenditure_date DATE NOT NULL,
    expended_date DATETIME,
    description VARCHAR(255),
    asset_id BIGINT NOT NULL,
    base_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_by BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_expenditures_asset FOREIGN KEY (asset_id) REFERENCES assets(id),
    CONSTRAINT fk_expenditures_base FOREIGN KEY (base_id) REFERENCES bases(id),
    CONSTRAINT fk_expenditures_category FOREIGN KEY (category_id) REFERENCES asset_categories(id),
    CONSTRAINT fk_expenditures_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    action VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id BIGINT,
    username VARCHAR(255),
    timestamp DATETIME,
    details VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Roles
INSERT INTO roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'BASE_COMMANDER'),
(3, 'LOGISTICS_OFFICER');

-- Bases
INSERT INTO bases (id, name, location) VALUES
(1, 'Alpha Base', 'Bengaluru'),
(2, 'Bravo Base', 'Chennai');

-- Equipment types
INSERT INTO asset_categories (id, name, description) VALUES
(1, 'Vehicles', 'Vehicles and transport equipment'),
(2, 'Weapons', 'Weapons'),
(3, 'Ammunition', 'Ammunition');

-- Demo users. Passwords are BCrypt hashes for the credentials documented in README.md.
INSERT INTO users (id, full_name, username, password, role_id, base_id) VALUES
(1, 'System Administrator', 'admin', '$2a$10$e0Cy.3p0z2x1Nv9bb31VMeQ8hubf5BwOWE8NEabp8mfYrFQJZCVFO', 1, NULL),
(2, 'Alpha Base Commander', 'commander', '$2a$10$MrKRafplN5Vb8XZurfr/GuIUX09vbXfpA.yi/2At.1zvWPWvdopPy', 2, 1),
(3, 'Logistics Officer', 'logistics', '$2a$10$W6y9lPSqwK0oJ7jolJnGFuLcCGmVu7YEwZlTPV8pQboxX/j8.Z7ba', 3, NULL);

-- Opening balances
INSERT INTO inventory_balances (id, opening_balance, base_id, category_id) VALUES
(1, 50, 1, 1),
(2, 100, 1, 2),
(3, 500, 1, 3),
(4, 25, 2, 1),
(5, 75, 2, 2),
(6, 250, 2, 3);

-- Representative assets
INSERT INTO assets (id, name, description, status, serial_number, category_id, base_id) VALUES
(1, '4x4 Military Vehicle', 'Standard military transport vehicle', 'AVAILABLE', 'VEH-001', 1, 1),
(2, 'Service Rifle', 'Standard service weapon', 'AVAILABLE', 'WPN-001', 2, 1),
(3, '5.56mm Ammunition', '5.56mm ammunition stock', 'AVAILABLE', 'AMMO-001', 3, 1),
(4, '4x4 Military Vehicle', 'Standard military transport vehicle', 'AVAILABLE', 'VEH-002', 1, 2),
(5, 'Service Rifle', 'Standard service weapon', 'AVAILABLE', 'WPN-002', 2, 2),
(6, '5.56mm Ammunition', '5.56mm ammunition stock', 'AVAILABLE', 'AMMO-002', 3, 2);

SET FOREIGN_KEY_CHECKS = 1;
