-- ShopSphere database initialisation
-- Runs once when the MySQL container starts for the first time.
-- The `authService` database is created automatically by MYSQL_DATABASE, but we still add it here for idempotency.

CREATE DATABASE IF NOT EXISTS authService CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS Catalog_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS OrderService CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
