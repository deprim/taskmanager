package org.example.taskmanager.dialogs;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.taskmanager.models.User;
import org.example.taskmanager.repositories.UserRepository;

public class AddUserDialog {

    private UserRepository userRepository;

    public AddUserDialog() {
        userRepository = new UserRepository();
    }

    public void showAndWait() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Добавить пользователя");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Поля для ввода данных
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleField = new ComboBox<>();
        roleField.getItems().addAll("Admin", "Manager", "Employee"); // Пример ролей

        // Лейблы и текстовые поля
        grid.add(new Label("Имя:"), 0, 0);
        grid.add(firstNameField, 1, 0);

        grid.add(new Label("Фамилия:"), 0, 1);
        grid.add(lastNameField, 1, 1);

        grid.add(new Label("Имя пользователя:"), 0, 2);
        grid.add(usernameField, 1, 2);

        grid.add(new Label("Пароль:"), 0, 3);
        grid.add(passwordField, 1, 3);

        grid.add(new Label("Роль:"), 0, 4);
        grid.add(roleField, 1, 4);

        // Кнопки
        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");
        HBox buttonPanel = new HBox(10, saveButton, cancelButton);
        grid.add(buttonPanel, 1, 5);

        // Обработчик кнопки "Сохранить"
        saveButton.setOnAction(e -> {
            // Проверка ввода обязательных данных
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                    usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || roleField.getValue() == null) {
                showAlert("Все поля должны быть заполнены.");
                return;
            }

            // Создаем пользователя
            User newUser = new User(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    roleField.getValue()
            );

            // Сохраняем пользователя в базу
            userRepository.createUser(newUser);

            stage.close();
        });

        // Обработчик кнопки "Отмена"
        cancelButton.setOnAction(e -> stage.close());

        // Устанавливаем сцену и показываем окно
        stage.setScene(new Scene(grid, 400, 300));
        stage.showAndWait();
    }

    // Метод для предупреждения
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}