package org.example.taskmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.taskmanager.views.TaskManagementView;
import org.example.taskmanager.views.UserManagementView;

public class MainView extends Application {

    private int userId; // ID текущего пользователя
    private BorderPane root; // Главный контейнер
    private VBox sideMenu; // Боковое меню
    private String userRole; // Добавлено: роль пользователя

    // Конструктор для задания роли
    public MainView(int userId, String userRole) {
        this.userId = userId; // Сохраняем ID текущего пользователя
        this.userRole = userRole;
    }

    @Override
    public void start(Stage primaryStage) {
        // Создаем корневой контейнер
        root = new BorderPane();

        // Боковая панель с кнопками навигации
        sideMenu = createSideMenu();

        // Центральная часть приложения (пустое состояние)
        Label contentPlaceholder = new Label("Выберите раздел из бокового меню.");
        contentPlaceholder.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");
        root.setCenter(contentPlaceholder);

        // Верхняя панель с заголовком приложения
        ToolBar topBar = new ToolBar(new Label("Task Manager"));
        root.setTop(topBar);

        // Добавляем боковое меню в макет
        root.setLeft(sideMenu);

        // Создаем сцену и запускаем приложение
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Task Manager: " + userRole);
        primaryStage.show();
    }

    // Создание бокового меню с переключателями
    private VBox createSideMenu() {
        Button tasksButton = new Button("Управление задачами");

        VBox menu;
        if ("Admin".equals(userRole)) {
            Button usersButton = new Button("Управление пользователями");

            // Добавляем обработчики событий для кнопок
            usersButton.setOnAction(e -> switchToUserManagement());
            tasksButton.setOnAction(e -> switchToTaskManagement(userId)); // Передаём ID текущего пользователя

            menu = new VBox(10, usersButton, tasksButton);
        } else {
            // Для сотрудников доступен только задачи
            tasksButton.setOnAction(e -> switchToTaskManagement(userId)); // Передаём ID текущего пользователя
            menu = new VBox(10, tasksButton);
        }

        menu.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");
        return menu;
    }

    // Переключение на раздел "Управление пользователями"
    private void switchToUserManagement() {
        UserManagementView userManagementView = new UserManagementView();
        root.setCenter(userManagementView);
    }

    // Переключение на раздел "Управление задачами"
    // Новый метод для передачи userId
    private void switchToTaskManagement(int userId) {
        TaskManagementView taskManagementView = new TaskManagementView(userId); // Передаём ID текущего пользователя
        root.setCenter(taskManagementView);
    }
}