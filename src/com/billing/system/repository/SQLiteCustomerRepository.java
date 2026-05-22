package com.billing.system.repository;

import com.billing.system.model.Customer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteCustomerRepository extends SQLiteRepository<Customer> {
    public SQLiteCustomerRepository() {
        super();
    }

    @Override
    protected String tableName() {
        return "customers";
    }

    @Override
    protected String createTableSql() {
        return "CREATE TABLE IF NOT EXISTS customers ("
                + "id TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "email TEXT,"
                + "phone TEXT,"
                + "type TEXT NOT NULL"
                + ")";
    }

    @Override
    protected String upsertSql() {
        return "INSERT INTO customers (id, name, email, phone, type) VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT(id) DO UPDATE SET name=excluded.name, email=excluded.email, phone=excluded.phone, type=excluded.type";
    }

    @Override
    protected String selectAllSql() {
        return "SELECT id, name, email, phone, type FROM customers ORDER BY name";
    }

    @Override
    protected String selectByIdSql() {
        return "SELECT id, name, email, phone, type FROM customers WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM customers WHERE id = ?";
    }

    @Override
    protected void bindUpsert(PreparedStatement statement, Customer entity) throws SQLException {
        statement.setString(1, entity.getId());
        statement.setString(2, entity.getName());
        statement.setString(3, entity.getEmail());
        statement.setString(4, entity.getPhone());
        statement.setString(5, entity.getType().name());
    }

    @Override
    protected Customer mapRow(ResultSet resultSet) throws SQLException {
        return new Customer(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                Customer.CustomerType.valueOf(resultSet.getString("type")));
    }

    @Override
    protected String getId(Customer entity) {
        return entity.getId();
    }

    @Override
    protected void importLegacyData() {
        if (!isTableEmpty() || !fileExists("customers.csv")) {
            return;
        }

        for (String line : readAllLines("customers.csv")) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);
            if (parts.length < 5) {
                continue;
            }

            save(new Customer(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    Customer.CustomerType.valueOf(parts[4])));
        }
    }
}