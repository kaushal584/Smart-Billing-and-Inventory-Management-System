package com.billing.system.repository;

import com.billing.system.model.Product;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteProductRepository extends SQLiteRepository<Product> {
    public SQLiteProductRepository() {
        super();
    }

    @Override
    protected String tableName() {
        return "products";
    }

    @Override
    protected String createTableSql() {
        return "CREATE TABLE IF NOT EXISTS products ("
                + "id TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "description TEXT,"
                + "price TEXT NOT NULL,"
                + "category TEXT,"
                + "quantity INTEGER NOT NULL,"
                + "sold_quantity INTEGER NOT NULL"
                + ")";
    }

    @Override
    protected String upsertSql() {
        return "INSERT INTO products (id, name, description, price, category, quantity, sold_quantity) VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT(id) DO UPDATE SET name=excluded.name, description=excluded.description, price=excluded.price, category=excluded.category, quantity=excluded.quantity, sold_quantity=excluded.sold_quantity";
    }

    @Override
    protected String selectAllSql() {
        return "SELECT id, name, description, price, category, quantity, sold_quantity FROM products ORDER BY name";
    }

    @Override
    protected String selectByIdSql() {
        return "SELECT id, name, description, price, category, quantity, sold_quantity FROM products WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM products WHERE id = ?";
    }

    @Override
    protected void bindUpsert(PreparedStatement statement, Product entity) throws SQLException {
        statement.setString(1, entity.getId());
        statement.setString(2, entity.getName());
        statement.setString(3, entity.getDescription());
        statement.setString(4, entity.getPrice().toPlainString());
        statement.setString(5, entity.getCategory());
        statement.setInt(6, entity.getQuantity());
        statement.setInt(7, entity.getSoldQuantity());
    }

    @Override
    protected Product mapRow(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                new BigDecimal(resultSet.getString("price")),
                resultSet.getString("category"),
                resultSet.getInt("quantity"),
                resultSet.getInt("sold_quantity"));
    }

    @Override
    protected String getId(Product entity) {
        return entity.getId();
    }

    @Override
    protected void importLegacyData() {
        if (!isTableEmpty() || !fileExists("products.csv")) {
            return;
        }

        for (String line : readAllLines("products.csv")) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);
            if (parts.length < 5) {
                continue;
            }

            int quantity = parts.length >= 6 && !parts[5].isBlank() ? Integer.parseInt(parts[5]) : 0;
            int soldQuantity = parts.length >= 7 && !parts[6].isBlank() ? Integer.parseInt(parts[6]) : 0;

            save(new Product(
                    parts[0],
                    parts[1],
                    parts[2],
                    new BigDecimal(parts[3]),
                    parts[4],
                    quantity,
                    soldQuantity));
        }
    }
}