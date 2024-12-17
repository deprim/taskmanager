package org.example.taskmanager.repositories;

import org.example.taskmanager.database.DatabaseManager;
import org.example.taskmanager.models.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    // Создание новой задачи
    public void createTask(Task task) {
        String sql = """
        INSERT INTO tasks (title, description, deadline, assignee_id, status, priority, category)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Включаем транзакцию
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Установка значений
                statement.setString(1, task.getTitle());
                statement.setString(2, task.getDescription());
                statement.setDate(3, (task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null));
                statement.setInt(4, task.getAssigneeId());
                statement.setString(5, task.getStatus());
                statement.setString(6, task.getPriority());
                statement.setString(7, task.getCategory());

                statement.executeUpdate(); // Выполнение запроса
                connection.commit(); // Фиксация изменений
                System.out.println("Task created successfully.");
            } catch (SQLException ex) {
                connection.rollback(); // Отмена изменений при ошибке
                System.err.println("Failed to create task: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Получение всех задач
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Task task = new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getDate("deadline") != null ? resultSet.getDate("deadline").toLocalDate() : null,
                        resultSet.getInt("assignee_id"),
                        resultSet.getString("status"),
                        resultSet.getString("priority"),
                        resultSet.getString("category")
                );
                tasks.add(task);
            }

        } catch (SQLException e) {
            System.err.println("Failed to fetch tasks: " + e.getMessage());
        }

        return tasks;
    }

    // Обновление задачи
    public void updateTask(Task task) throws SQLException {
        String sql = """
            UPDATE tasks SET title = ?, description = ?, deadline = ?, assignee_id = ?, status = ?, priority = ?, category = ?
            WHERE id = ?
        """;

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Включаем транзакции
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setDate(3, task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null);
            statement.setInt(4, task.getAssigneeId());
            statement.setString(5, task.getStatus());
            statement.setString(6, task.getPriority());
            statement.setString(7, task.getCategory());
            statement.setInt(8, task.getId());

                int updatedRows = statement.executeUpdate();
                if (updatedRows > 0) {
                    connection.commit(); // Фиксация транзакции
                    System.out.println("Task updated successfully.");
                } else {
                    connection.rollback(); // Откат, если ничего не обновлено
                    System.out.println("Task not found.");
                }
            } catch (SQLException ex) {
                connection.rollback(); // Откат изменений при ошибке
                System.err.println("Failed to update task: " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public List<Task> getTasksForUser(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE assignee_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Task task = new Task(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getDate("deadline") != null ? resultSet.getDate("deadline").toLocalDate() : null,
                            resultSet.getInt("assignee_id"),
                            resultSet.getString("status"),
                            resultSet.getString("priority"),
                            resultSet.getString("category")
                    );
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch user tasks: " + e.getMessage());
        }

        return tasks;
    }

    // Удаление задачи
    public void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Включаем транзакции
            try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId);

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

    // Получение задачи по ID
    public Task getTaskById(int taskId) {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Task(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getDate("deadline") != null ? resultSet.getDate("deadline").toLocalDate() : null,
                        resultSet.getInt("assignee_id"),
                        resultSet.getString("status"),
                        resultSet.getString("priority"),
                        resultSet.getString("category")
                );
            } else {
                System.out.println("Task not found.");
            }

        } catch (SQLException e) {
            System.err.println("Failed to fetch task by ID: " + e.getMessage());
        }

        return null;
    }
}