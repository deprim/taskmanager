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

import java.sql.SQLException;
import java.time.LocalDate;

public class EditTaskDialog {

    private Task task;
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    public EditTaskDialog(Task task) {
        this.task = task;
        this.taskRepository = new TaskRepository();
        this.userRepository = new UserRepository();
    }

    public void showAndWait() {
        // Создание окна
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Редактировать задачу");

        // Форма
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Поля ввода с предзаполненными значениями
        TextField titleField = new TextField(task.getTitle());
        TextArea descriptionField = new TextArea(task.getDescription());
        DatePicker deadlinePicker = new DatePicker(task.getDeadline());

        // Заменяем TextField на ComboBox для выбора ответственного
        ComboBox<User> assigneeField = new ComboBox<>();
        assigneeField.getItems().addAll(userRepository.getAllUsers()); // Загрузка пользователей из базы
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

        // Устанавливаем текущего ответственного
        User currentAssignee = userRepository.getUserById(task.getAssigneeId());
        if (currentAssignee != null) {
            assigneeField.getSelectionModel().select(currentAssignee);
        }

        // Поля для статуса и приоритета
        ComboBox<String> statusField = new ComboBox<>();
        statusField.getItems().addAll("New", "In Progress", "Completed", "Closed");
        statusField.setValue(task.getStatus());

        ComboBox<String> priorityField = new ComboBox<>();
        priorityField.getItems().addAll("Low", "Medium", "High", "Urgent");
        priorityField.setValue(task.getPriority());

        // Располагаем элементы на сетке
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
        Button saveButton = new Button("Сохранить изменения");
        Button cancelButton = new Button("Отмена");

        // Обработка события при нажатии на кнопку "Сохранить"
        saveButton.setOnAction(e -> {
            if (titleField.getText().isEmpty() ||
                    statusField.getValue() == null ||
                    priorityField.getValue() == null ||
                    assigneeField.getValue() == null ||
                    deadlinePicker.getValue() == null) {
                showAlert("Все поля должны быть заполнены.");
                return;
            }

            // Обновляем данные задачи
            task.setTitle(titleField.getText());
            task.setDescription(descriptionField.getText());
            task.setDeadline(deadlinePicker.getValue());

            // Получаем ID выбранного пользователя
            User selectedUser = assigneeField.getValue();
            task.setAssigneeId(selectedUser.getId());

            task.setStatus(statusField.getValue());
            task.setPriority(priorityField.getValue());

            // Обновляем задачу в базе
            try {
                taskRepository.updateTask(task);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        });

        // Кнопка "Отмена"
        cancelButton.setOnAction(e -> stage.close());

        // Панель с кнопками
        HBox buttonPanel = new HBox(10, saveButton, cancelButton);
        grid.add(buttonPanel, 1, 6);

        // Устанавливаем сцену
        stage.setScene(new Scene(grid, 400, 400));
        stage.showAndWait();
    }

    // Метод для отображения предупреждений
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}