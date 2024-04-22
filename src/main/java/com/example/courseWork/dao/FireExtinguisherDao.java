package com.example.courseWork.dao;

import com.example.courseWork.model.FireExtinguisherData;
import com.example.courseWork.dao.exception.DbException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FireExtinguisherDao {

    private static final String GET_FIRE_EXTINGUISHER_ID = "SELECT * FROM fire_extinguishers WHERE id=(?);";
    private static final String GET_FIRE_EXTINGUISHERS = "SELECT * FROM fire_extinguishers ORDER BY id;";
    private static final String INSERT_FIRE_EXTINGUISHER = "INSERT INTO fire_extinguishers (location, expirationDate) VALUES (?, ?);";
    private static final String UPDATE_FIRE_EXTINGUISHER = "UPDATE fire_extinguishers SET expirationDate=(?) WHERE id=(?);";

    private final DbManager dbManager;

    public FireExtinguisherDao() {
        dbManager = DbManager.getInstance();
    }

    public FireExtinguisherData getById(long id) throws DbException {
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_FIRE_EXTINGUISHER_ID)) {
            int k = 0;
            statement.setLong(++k, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapFireExtinguisher(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DbException("Can not to get fire extinguishers by id", e);
        }
    }

    private FireExtinguisherData mapFireExtinguisher(ResultSet resultSet) throws SQLException {
        FireExtinguisherData fireExtinguisherData = new FireExtinguisherData();
        fireExtinguisherData.setId(resultSet.getLong("id"));
        fireExtinguisherData.setLocation(resultSet.getString("location"));
        fireExtinguisherData.setExpirationDate(resultSet.getDate("expirationDate").toLocalDate());
        return fireExtinguisherData;
    }

    public List<FireExtinguisherData> getAll() throws DbException {
        List<FireExtinguisherData> users = new ArrayList<>();
        try (Connection connection = dbManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_FIRE_EXTINGUISHERS)) {
            while (resultSet.next()) {
                users.add(mapFireExtinguisher(resultSet));
            }
        } catch (SQLException e) {
            throw new DbException("Can not to get all users", e);
        }
        return users;
    }
    public List<FireExtinguisherData> delete(FireExtinguisherData extinguisher) throws DbException {
        try (Connection conn = dbManager.getConnection()) {
            String sql = "DELETE FROM fire_extinguishers WHERE location = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, extinguisher.getLocation());
                stmt.executeUpdate();
            }
            // Після видалення отримуємо оновлений список усіх вогнегасників
            return getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("Помилка при видаленні вогнегасника з бази даних", e);
        }
    }

    public void insert(FireExtinguisherData fireExtinguisher) throws DbException {
        Connection connection = null;
        try {
            connection = dbManager.getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            processInsert(connection, fireExtinguisher);
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            throw new DbException("Can not to insert fire extinguisher", e);
        } finally {
            close(connection);
        }
    }

    private void processInsert(Connection connection, FireExtinguisherData fireExtinguisher) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_FIRE_EXTINGUISHER, Statement.RETURN_GENERATED_KEYS)) {
            int k = 0;
            statement.setString(++k, fireExtinguisher.getLocation());
            statement.setDate(++k, Date.valueOf(fireExtinguisher.getExpirationDate()));
            int count = statement.executeUpdate();
            if (count > 0) {
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        fireExtinguisher.setId(resultSet.getLong(1));
                    }
                }
            } else {
                throw new SQLException("Failed to insert fire extinguisher, no rows affected.");
            }
        }
    }


    public void update(FireExtinguisherData fireExtinguisher) throws DbException {
        try (Connection connection = dbManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_FIRE_EXTINGUISHER)) {
            int k = 0;
            statement.setDate(++k, Date.valueOf(fireExtinguisher.getExpirationDate()));
            statement.setLong(++k, fireExtinguisher.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Can not to update user", e);
        }
    }

    private void rollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void close(AutoCloseable stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
