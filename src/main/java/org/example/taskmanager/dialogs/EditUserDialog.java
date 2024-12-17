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

public class EditUserDialog {

    private User user;
    private UserRepository userRepository;

    public EditUserDialog(User user) {
        this.user = user;
        this.userRepository = new UserRepository();
    }

    public void showAndWait() {
        // Создание окна
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Редактировать пользователя");

        // Макет для размещения элементов
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Поля с предзаполненными данными пользователя
        TextField firstNameField = new TextField(user.getFirstName());
        TextField lastNameField = new TextField(user.getLastName());
        TextField usernameField = new TextField(user.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Введите новый пароль (если хотите изменить)");

        ComboBox<String> roleField = new ComboBox<>();
        roleField.getItems().addAll("Admin", "Manager", "Employee"); // Пример ролей
        roleField.setValue(user.getRole());

        // Размещение элементов на сетке
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

        // Кнопки сохранения и отмены
        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");

        HBox buttonPanel = new HBox(10, saveButton, cancelButton);
        grid.add(buttonPanel, 1, 5);

        // Действие кнопки "Сохранить"
        saveButton.setOnAction(e -> {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                    usernameField.getText().isEmpty() || roleField.getValue() == null) {
                showAlert("Все поля должны быть заполнены.");
                return;
            }

            // Обновление данных пользователя
            user.setFirstName(firstNameField.getText());
            user.setLastName(lastNameField.getText());
            user.setUsername(usernameField.getText());
            if (!passwordField.getText().isEmpty()) {
                user.setPassword(passwordField.getText());
            }
            user.setRole(roleField.getValue());

            // Сохранение изменений в базе
            userRepository.updateUser(user);

            stage.close();
        });

        // Действие кнопки "Отмена"
        cancelButton.setOnAction(e -> stage.close());

        // Устанавливаем сцену и показываем окно
        stage.setScene(new Scene(grid, 400, 300));
        stage.showAndWait();
    }

    // Отображение предупреждения
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}