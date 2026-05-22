package com.billing.system.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SQLiteRepository<T> implements Repository<T> {
    private static final Path DATABASE_PATH = Paths.get("data", "billing.db");
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_PATH.toString().replace('\\', '/');

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SQLite JDBC driver not found", e);
        }
    }

    protected SQLiteRepository() {
        ensureDatabaseDirectory();
        initializeSchema();
        importLegacyData();
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    protected abstract String tableName();

    protected abstract String createTableSql();

    protected abstract String upsertSql();

    protected abstract String selectAllSql();

    protected abstract String selectByIdSql();

    protected abstract String deleteSql();

    protected abstract void bindUpsert(PreparedStatement statement, T entity) throws SQLException;

    protected abstract T mapRow(ResultSet resultSet) throws SQLException;

    protected abstract String getId(T entity);

    protected void importLegacyData() {
    }

    @Override
    public void save(T entity) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(upsertSql())) {
            bindUpsert(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save entity in " + tableName(), e);
        }
    }

    @Override
    public Optional<T> findById(String id) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(selectByIdSql())) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load entity from " + tableName(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<T> findAll() {
        List<T> results = new ArrayList<>();
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(selectAllSql());
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                results.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load entities from " + tableName(), e);
        }

        return results;
    }

    @Override
    public void delete(String id) {
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteSql())) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete entity from " + tableName(), e);
        }
    }

    protected boolean isTableEmpty() {
        String sql = "SELECT COUNT(*) FROM " + tableName();
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to inspect table " + tableName(), e);
        }
    }

    protected boolean fileExists(String relativePath) {
        return Files.exists(Paths.get(relativePath));
    }

    protected List<String> readAllLines(String relativePath) {
        try {
            return Files.readAllLines(Paths.get(relativePath));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read legacy data from " + relativePath, e);
        }
    }

    private void ensureDatabaseDirectory() {
        try {
            Path parent = DATABASE_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create SQLite data directory", e);
        }
    }

    private void initializeSchema() {
        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSql());
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize SQLite schema for " + tableName(), e);
        }
    }
}