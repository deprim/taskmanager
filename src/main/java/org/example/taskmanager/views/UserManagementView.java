package org.example.taskmanager.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.taskmanager.dialogs.AddUserDialog;
import org.example.taskmanager.dialogs.EditUserDialog;
import org.example.taskmanager.models.User;
import org.example.taskmanager.repositories.UserRepository;

import java.util.List;

public class UserManagementView extends BorderPane {

    private TableView<User> userTable;
    private UserRepository userRepository;

    public UserManagementView() {
        userRepository = new UserRepository();

        // Верхняя часть с заголовком
        Label header = new Label("Управление пользователями");
        header.setStyle("-fx-font-size: 18px; -fx-padding: 10px;");

        // Основная таблица для отображения пользователей
        userTable = new TableView<>();
        configureUserTable();
        loadUsers();

        // Кнопки управления
        Button addUserButton = new Button("Добавить пользователя");
        Button editUserButton = new Button("Редактировать");
        Button deleteUserButton = new Button("Удалить");

        addUserButton.setOnAction(e -> addNewUser());
        editUserButton.setOnAction(e -> editSelectedUser());
        deleteUserButton.setOnAction(e -> deleteSelectedUser());

        // Нижняя панель с кнопками
        HBox buttonBar = new HBox(10, addUserButton, editUserButton, deleteUserButton);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setStyle("-fx-background-color: #e0e0e0;");

        // Компоновка элементов
        this.setTop(header);
        this.setCenter(userTable);
        this.setBottom(buttonBar);
    }

    // Настройка отображения данных в таблице
    private void configureUserTable() {
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<User, String> firstNameColumn = new TableColumn<>("Имя");
        firstNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));

        TableColumn<User, String> lastNameColumn = new TableColumn<>("Фамилия");
        lastNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

        TableColumn<User, String> roleColumn = new TableColumn<>("Роль");
        roleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));

        userTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, roleColumn);
    }

    // Загрузка пользователей в таблицу
    private void loadUsers() {
        List<User> users = userRepository.getAllUsers();
        userTable.getItems().clear();
        userTable.getItems().addAll(users);
    }

    // Добавление нового пользователя
    private void addNewUser() {
        AddUserDialog dialog = new AddUserDialog();
        dialog.showAndWait();
        loadUsers(); // Обновляем таблицу после добавления пользователя
    }

    // Редактирование выбранного пользователя
    private void editSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            EditUserDialog dialog = new EditUserDialog(selectedUser); // Окно редактирования
            dialog.showAndWait();
            loadUsers(); // Обновление данных в таблице после редактирования
        } else {
            showAlert("Выберите пользователя для редактирования.");
        }
    }

    // Удаление выбранного пользователя
    private void deleteSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userRepository.deleteUser(selectedUser.getId());
            loadUsers();
        } else {
            showAlert("Выберите пользователя для удаления.");
        }
    }

    // Вспомогательный метод для отображения уведомлений
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}