package org.example.taskmanager;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.taskmanager.database.DatabaseManager;
import org.example.taskmanager.views.LoginForm;

public class TaskManagerApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Инициализация базы данных
        DatabaseManager.initializeTables();

        // Запуск окна логина
        LoginForm loginForm = new LoginForm();
        loginForm.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}