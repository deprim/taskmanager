package org.example.taskmanager.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.taskmanager.dialogs.AddTaskDialog;
import org.example.taskmanager.dialogs.EditTaskDialog;
import org.example.taskmanager.models.Task;
import org.example.taskmanager.models.User;
import org.example.taskmanager.repositories.TaskRepository;
import org.example.taskmanager.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class TaskManagementView extends BorderPane {

    private TableView<Task> taskTable;
    private TaskRepository taskRepository;
    private int currentUserId; // ID текущего пользователя
    private String viewMode = "ALL"; // "ALL" или "USER"

    public TaskManagementView(int userId) {
        this.currentUserId = userId; // Сохраняем ID текущего пользователя
        taskRepository = new TaskRepository();

        // Заголовок для верхней части окна
        Label header = new Label("Управление задачами");
        header.setStyle("-fx-font-size: 18px; -fx-padding: 10px;");

        // Таблица задач
        taskTable = new TableView<>();
        configureTaskTable();
        loadTasks(); // Загружаем все задачи

        // *** Кнопки для переключения между "Все задачи" и "Мои задачи" ***
        Button allTasksButton = new Button("Все задачи");
        Button myTasksButton = new Button("Мои задачи");

        allTasksButton.setOnAction(e -> switchToAllTasks());
        myTasksButton.setOnAction(e -> switchToUserTasks());

        // Панель с кнопками переключения
        HBox viewFilterBar = new HBox(10, allTasksButton, myTasksButton);
        viewFilterBar.setPadding(new Insets(10));
        viewFilterBar.setStyle("-fx-background-color: #e0e0e0;");

        // *** Кнопки управления задачами ***
        Button addTaskButton = new Button("Добавить задачу");
        Button editTaskButton = new Button("Редактировать задачу");
        Button deleteTaskButton = new Button("Удалить задачу");

        addTaskButton.setOnAction(e -> addNewTask());
        editTaskButton.setOnAction(e -> editSelectedTask());
        deleteTaskButton.setOnAction(e -> deleteSelectedTask());

        // Панель с кнопками управления
        HBox buttonBar = new HBox(10, addTaskButton, editTaskButton, deleteTaskButton);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setStyle("-fx-background-color: #e0e0e0;");

        // Компоновка: заголовок + фильтр, таблица, кнопки управления
        this.setTop(new VBox(header, viewFilterBar)); // Верх: заголовок и кнопки фильтра
        this.setCenter(taskTable);
        this.setBottom(buttonBar);
    }

    // Переключение на "Все задачи"
    private void switchToAllTasks() {
        viewMode = "ALL";
        loadTasks(); // Загружаем все задачи
    }

    // Переключение на "Мои задачи"
    private void switchToUserTasks() {
        viewMode = "USER";
        loadUserTasks(); // Загружаем задачи текущего пользователя
    }

    // Настройка таблицы для отображения задач
    private void configureTaskTable() {
        TableColumn<Task, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getId()));

        TableColumn<Task, String> titleColumn = new TableColumn<>("Название");
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));

        TableColumn<Task, LocalDate> deadlineColumn = new TableColumn<>("Дедлайн");
        deadlineColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDeadline()));

        TableColumn<Task, String> assigneeColumn = new TableColumn<>("Ответственный");
        assigneeColumn.setCellValueFactory(data -> {
            int assigneeId = data.getValue().getAssigneeId();
            User user = new UserRepository().getUserById(assigneeId); // Получение пользователя по ID
            String assigneeName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(assigneeName);
        });

        TableColumn<Task, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<Task, String> priorityColumn = new TableColumn<>("Приоритет");
        priorityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPriority()));

        taskTable.getColumns().addAll(idColumn, titleColumn, descriptionColumn, deadlineColumn, assigneeColumn, statusColumn, priorityColumn);
    }

    // Загрузка всех задач
    private void loadTasks() {
        List<Task> tasks = taskRepository.getAllTasks();
        taskTable.getItems().clear();
        taskTable.getItems().addAll(tasks);
    }

    // Загрузка задач текущего пользователя
    private void loadUserTasks() {
        List<Task> tasks = taskRepository.getTasksForUser(currentUserId);
        taskTable.getItems().clear();
        taskTable.getItems().addAll(tasks);
    }

    // Добавление новой задачи
    private void addNewTask() {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.showAndWait();
        refreshTasks(); // Обновляем данные в соответствии с текущим режимом
    }

    // Редактирование выбранной задачи
    private void editSelectedTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            EditTaskDialog dialog = new EditTaskDialog(selectedTask);
            dialog.showAndWait();
            refreshTasks();
        } else {
            showAlert("Выберите задачу для редактирования.");
        }
    }

    // Удаление выбранной задачи
    private void deleteSelectedTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskRepository.deleteTask(selectedTask.getId());
            refreshTasks();
        } else {
            showAlert("Выберите задачу для удаления.");
        }
    }

    // Обновление данных в таблице в зависимости от текущего режима
    private void refreshTasks() {
        if ("ALL".equals(viewMode)) {
            loadTasks();
        } else {
            loadUserTasks();
        }
    }

    // Вспомогательный метод для показа сообщений
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}