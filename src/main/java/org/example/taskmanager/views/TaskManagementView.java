package org.example.taskmanager.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.taskmanager.dialogs.AddTaskDialog;
import org.example.taskmanager.dialogs.EditTaskDialog;
import org.example.taskmanager.models.Task;
import org.example.taskmanager.models.User;
import org.example.taskmanager.repositories.TaskHistoryRepository;
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
        Button historyButton = new Button("История");
        Button addCommentButton = new Button("Добавить комментарий");

        addTaskButton.setOnAction(e -> addNewTask());
        editTaskButton.setOnAction(e -> editSelectedTask());
        deleteTaskButton.setOnAction(e -> deleteSelectedTask());
        historyButton.setOnAction(e -> openTaskHistory());
        addCommentButton.setOnAction(e -> addCommentToSelectedTask());


        // Панель с кнопками управления
        HBox buttonBar = new HBox(10, addTaskButton, editTaskButton, deleteTaskButton, historyButton, addCommentButton);
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

    private void openTaskHistory() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Пожалуйста, выберите задачу для просмотра истории.");
            return;
        }

        // Получить историю задач
        TaskHistoryRepository historyRepository = new TaskHistoryRepository();
        List<String> history = historyRepository.getTaskHistory(selectedTask.getId());

        // Создать окно для отображения
        Alert historyDialog = new Alert(Alert.AlertType.INFORMATION);
        historyDialog.setTitle("История задачи");
        historyDialog.setHeaderText("История и комментарии");
        Image icon = new Image(getClass().getResourceAsStream("/icons/history.png")); // Укажите путь к вашей иконке
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(48); // Размер иконки (высота)
        iconView.setFitWidth(48);  // Размер иконки (ширина)
        historyDialog.getDialogPane().setGraphic(iconView); // Меняем стандартную иконку


        StringBuilder content = new StringBuilder();
        for (String entry : history) {
            content.append(entry).append("\n");
        }

        historyDialog.setContentText(content.toString());
        historyDialog.showAndWait();
    }

    private void addCommentToSelectedTask() {
        // Получаем выбранную задачу из таблицы
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Пожалуйста, выберите задачу для добавления комментария.");
            return;
        }

        // Открываем диалоговое окно для ввода комментария
        TextInputDialog commentDialog = new TextInputDialog();
        commentDialog.setHeaderText("Добавить комментарий");
        commentDialog.setContentText("Введите ваш комментарий:");

        // Добавляем пользовательскую иконку
        Image icon = new Image(getClass().getResourceAsStream("/icons/comment.png")); // Укажите ваш путь к иконке
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(48); // Устанавливаем высоту иконки
        iconView.setFitWidth(48);  // Устанавливаем ширину иконки
        commentDialog.getDialogPane().setGraphic(iconView); // Устанавливаем иконку в диалог

        commentDialog.showAndWait().ifPresent(comment -> {
            // Проверяем, что был введен текст
            if (comment.trim().isEmpty()) {
                showAlert("Комментарий не может быть пустым.");
                return;
            }

            // Добавляем комментарий в историю задачи
            TaskHistoryRepository historyRepository = new TaskHistoryRepository();
            historyRepository.addTaskHistory(selectedTask.getId(), currentUserId, comment);

            showAlert("Комментарий успешно добавлен!");
        });
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Меняем тип на INFORMATION
        alert.setHeaderText("Уведомление"); // Заголовок диалога
        alert.setContentText(message);
        Image icon = new Image(getClass().getResourceAsStream("/icons/success.png")); // Укажите путь к вашей иконке
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(48); // Размер иконки (высота)
        iconView.setFitWidth(48);  // Размер иконки (ширина)

        // Устанавливаем иконку в диалог
        alert.getDialogPane().setGraphic(iconView); // Меняем стандартную иконку
        alert.showAndWait();
    }
}