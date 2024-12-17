package org.example.taskmanager.repositories;

import org.example.taskmanager.database.DatabaseManager;
import org.example.taskmanager.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // Создание нового пользователя
    public void createUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Включаем транзакции
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Установка значений
                statement.setString(1, user.getFirstName());
                statement.setString(2, user.getLastName());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getPassword());
                statement.setString(5, user.getRole());

                int rows = statement.executeUpdate(); // Выполнение запроса
                if (rows > 0) {
                    connection.commit(); // Фиксация данных
                    System.out.println("User created successfully.");
                } else {
                    connection.rollback(); // Откат изменений
                    System.out.println("Failed to insert user.");
                }
            } catch (SQLException ex) {
                connection.rollback(); // Откат изменений при ошибке
                System.err.println("Failed to create user: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Failed to fetch users: " + e.getMessage());
        }

        return users;
    }

    // Обновление данных пользователя
    public void updateUser(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ?, role = ? WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Включаем транзакции
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, user.getFirstName());
                statement.setString(2, user.getLastName());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getPassword());
                statement.setString(5, user.getRole());
                statement.setInt(6, user.getId());

                int updatedRows = statement.executeUpdate();
                if (updatedRows > 0) {
                    connection.commit(); // Фиксация транзакции
                    System.out.println("User updated successfully.");
                } else {
                    connection.rollback(); // Откат, если ничего не обновлено
                    System.out.println("User not found.");
                }
            } catch (SQLException ex) {
                connection.rollback(); // Откат изменений при ошибке
                System.err.println("User to update task: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Удаление пользователя
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Включаем транзакции
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

                int deletedRows = statement.executeUpdate();
                if (deletedRows > 0) {
                    connection.commit(); // Фиксация транзакции
                    System.out.println("Task deleted successfully.");
                } else {
                    connection.rollback(); // Откат, если ничего не удалено
                    System.out.println("Task not found.");
                }
            } catch (SQLException ex) {
                connection.rollback(); // Откат изменений при ошибке
                System.err.println("Failed to delete task: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Получение пользователя по ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
            } else {
                System.out.println("User not found.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Failed to fetch user by ID: " + e.getMessage());
            return null;
        }
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при аутентификации пользователя: " + e.getMessage());
        }
        return null; // Пользователь не найден
    }
}