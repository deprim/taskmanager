package org.example.taskmanager.repositories;

import org.example.taskmanager.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskHistoryRepository {

    // Добавить запись в историю задач
    public void addTaskHistory(int taskId, int userId, String comment) {
        String sql = "INSERT INTO task_history (task_id, user_id, comment) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setInt(2, userId);
            statement.setString(3, comment);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to add task history: " + e.getMessage());
        }
    }

    // Получить все записи истории для задачи
    public List<String> getTaskHistory(int taskId) {
        String sql = "SELECT th.comment, th.timestamp, u.username " +
                "FROM task_history th " +
                "JOIN users u ON th.user_id = u.id " +
                "WHERE th.task_id = ? " +
                "ORDER BY th.timestamp ASC";

        List<String> history = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String comment = resultSet.getString("comment");
                    String timestamp = resultSet.getString("timestamp");

                    history.add(String.format("%s (%s): %s", username, timestamp, comment));
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch task history: " + e.getMessage());
        }

        return history;
    }
}