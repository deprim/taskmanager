package org.example.taskmanager.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.taskmanager.MainView;
import org.example.taskmanager.models.User;
import org.example.taskmanager.repositories.UserRepository;

public class LoginForm {

    private UserRepository userRepository;

    public LoginForm() {
        this.userRepository = new UserRepository();
    }

    public void show(Stage stage) {
        // Разметка
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Поля ввода
        Label usernameLabel = new Label("Имя пользователя:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Пароль:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Войти");

        // Добавляем элементы в сетку
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);

        // Обрабатываем кнопку "Войти"
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User user = userRepository.getUserByUsernameAndPassword(username, password);
            if (user != null) {
                MainView mainView = new MainView(user.getId(), user.getRole()); // Передаём userId и роль
                mainView.start(stage); // Открываем панель соответствующей роли
            } else {
                showAlert("Неверное имя пользователя или пароль");
            }
        });

        // Устанавливаем сцену
        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Авторизация");
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
