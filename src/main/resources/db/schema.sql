CREATE TABLE IF NOT EXISTS franchise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS branch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    franchise_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    KEY idx_branch_franchise (franchise_id),
    CONSTRAINT fk_branch_franchise FOREIGN KEY (franchise_id) REFERENCES franchise (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    stock INT NOT NULL,
    KEY idx_product_branch (branch_id),
    CONSTRAINT fk_product_branch FOREIGN KEY (branch_id) REFERENCES branch (id) ON DELETE CASCADE,
    CONSTRAINT chk_product_stock CHECK (stock >= 0)
);
