package org.example.taskmanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private int test;
    private static final String DATABASE_URL = "jdbc:sqlite:task_manager.db";

    // Singleton instance of the connection
    private static Connection connection;

    private DatabaseManager() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_URL); // Каждое соединение новое
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return null; // Возвращаем null в случае ошибки
        }
    }

    // Initialize tables in the database
    public static void initializeTables() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            // Table for users
            String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            );
        """;

            // Table for tasks
            String createTasksTable = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                deadline DATETIME,
                assignee_id INTEGER,
                status TEXT DEFAULT 'New',
                priority TEXT DEFAULT 'Normal',
                category TEXT,
                FOREIGN KEY (assignee_id) REFERENCES users (id)
            );
        """;

            statement.execute(createUsersTable); // Create users table
            statement.execute(createTasksTable); // Create tasks table

            System.out.println("Tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to initialize tables: " + e.getMessage());
        }
    }
}
