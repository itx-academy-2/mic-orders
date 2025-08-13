package com.academy.orders.karate.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static java.sql.DriverManager.getConnection;

public class DbUtils {
    private final String url;
    private final String username;
    private final String password;

    public DbUtils(Map<String, Object> config) {
        this.url = (String) config.get("url");
        this.username = (String) config.get("username");
        this.password =(String) config.get("password");

    }

    public void increaseProductQuantity(String productId){
        try(var connection = getConnection(url, username, password)) {
            increaseQuantity(connection, productId);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot increase product quantity by id: "+ productId);
        }
    }

    private void increaseQuantity(Connection connection, String productId) throws SQLException {
        var sql = "UPDATE products SET quantity = quantity + 1 WHERE id = ?";
        performUpdateRequest(connection, sql, UUID.fromString(productId));
    }

    public Integer getProductQuantity(String productId) {
        try(var connection = getConnection(url, username, password)) {
            return getQuantity(connection,productId);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot get product quantity by id: "+ productId);
        }
    }

    private int getQuantity(Connection connection, String productId) throws SQLException {
        var sql = "SELECT quantity FROM products p WHERE p.id = ?";
        var resultSet = performGetRequestRequest(connection, sql, UUID.fromString(productId));
        return getQuantity(resultSet);
    }

    private int getQuantity(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return resultSet.getInt("quantity");
    }


    private ResultSet performGetRequestRequest(Connection connection, String sql, Object... params) throws
        SQLException {
        var statement = connection.prepareStatement(sql);
        prepareParams(statement, params);
        return statement.executeQuery();
    }

    private void performUpdateRequest(Connection connection, String sql, Object... params) throws SQLException {
        var statement = connection.prepareStatement(sql);
        prepareParams(statement, params);
        executeUpdate(statement);
    }

    private void prepareParams(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i+1 , params[i]);
        }
    }

    private void executeUpdate(PreparedStatement statement) throws SQLException {
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) {
            throw new RuntimeException("No rows affected");
        }
    }

    public void executeUpdateWithParams(String sql, Object... params) {
        try (var connection = getConnection(url, username, password)) {
            var statement = connection.prepareStatement(sql);
            prepareParams(statement, params);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute update", e);
        }
    }

    public String getPostAddressIdByOrderId(UUID orderId) {
        try (var connection = getConnection(url, username, password)) {
            var sql = "SELECT post_address_v2_id FROM orders_v2 WHERE id = ?";
            var rs = performGetRequestRequest(connection, sql, orderId);
            if (rs.next()) {
                return rs.getString("post_address_v2_id");
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch post_address_v2_id", e);
        }
    }

    public UUID getPostAddressIdByTitle(String title) {
        String sql = "SELECT id FROM post_addresses_v2 WHERE title = ?";
        try (var connection = getConnection(url, username, password);
             var statement = connection.prepareStatement(sql)) {

            statement.setString(1, title);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getObject("id", java.util.UUID.class);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get post address id by title", e);
        }
    }

    public String getPostAddressTitleById(UUID id) {
        String sql = "SELECT title FROM post_addresses_v2 WHERE id = ?";
        try (var connection = getConnection(url, username, password);
             var statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("title");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get post address title by id", e);
        }
    }
}
