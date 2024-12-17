package org.example.taskmanager.dialogs;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.taskmanager.models.Task;
import org.example.taskmanager.models.User;
import org.example.taskmanager.repositories.TaskRepository;
import org.example.taskmanager.repositories.UserRepository;

public class AddTaskDialog {

    private TaskRepository taskRepository;
    private UserRepository userRepository;

    public AddTaskDialog() {
        taskRepository = new TaskRepository();
        userRepository = new UserRepository();
    }

    public void showAndWait() {
        // Создаем основное окно
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Добавить задачу");

        // Форма для ввода данных
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Поля ввода
        TextField titleField = new TextField();
        TextArea descriptionField = new TextArea();
        DatePicker deadlinePicker = new DatePicker();

        // Создаем ComboBox для выбора ответственного
        ComboBox<User> assigneeField = new ComboBox<>();
        assigneeField.getItems().addAll(userRepository.getAllUsers()); // Загрузка пользователей
        assigneeField.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getFirstName() + " " + user.getLastName() + " (" + user.getRole() + ")");
                }
            }
        });
        assigneeField.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText("Выберите ответственного");
                } else {
                    setText(user.getFirstName() + " " + user.getLastName() + " (" + user.getRole() + ")");
                }
            }
        });

        ComboBox<String> statusField = new ComboBox<>();
        statusField.getItems().addAll("New", "In Progress", "Completed", "Closed");
        ComboBox<String> priorityField = new ComboBox<>();
        priorityField.getItems().addAll("Low", "Medium", "High", "Urgent");

        // Размещение меток и полей на сетке
        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);

        grid.add(new Label("Описание:"), 0, 1);
        grid.add(descriptionField, 1, 1);

        grid.add(new Label("Дедлайн:"), 0, 2);
        grid.add(deadlinePicker, 1, 2);

        grid.add(new Label("Ответственный:"), 0, 3);
        grid.add(assigneeField, 1, 3);

        grid.add(new Label("Статус:"), 0, 4);
        grid.add(statusField, 1, 4);

        grid.add(new Label("Приоритет:"), 0, 5);
        grid.add(priorityField, 1, 5);

        // Кнопки сохранения и отмены
        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");

        // Панель для кнопок
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        grid.add(buttonBox, 1, 6);

        // Действие кнопки "Сохранить"
        saveButton.setOnAction(e -> {
            if (titleField.getText().isEmpty() ||
                    statusField.getValue() == null ||
                    priorityField.getValue() == null ||
                    assigneeField.getValue() == null ||
                    deadlinePicker.getValue() == null) {
                showAlert("Все поля должны быть заполнены.");
                return;
            }

            // Получаем выбранного пользователя
            User selectedUser = assigneeField.getValue();

            // Создание нового объекта задачи
            Task newTask = new Task(
                    titleField.getText(),
                    descriptionField.getText(),
                    deadlinePicker.getValue(),
                    selectedUser.getId(), // ID выбранного пользователя
                    statusField.getValue(),
                    priorityField.getValue(),
                    null // Категория, если нужно, добавьте ComboBox
            );

            // Сохранение в базу
            taskRepository.createTask(newTask, selectedUser.getId());
            stage.close();
        });

        // Действие кнопки "Отмена"
        cancelButton.setOnAction(e -> stage.close());

        // Устанавливаем сцену и показываем окно
        stage.setScene(new Scene(grid, 600, 400));
        stage.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}