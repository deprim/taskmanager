package org.example.taskmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.example.taskmanager.views.LoginForm;
import org.example.taskmanager.views.TaskManagementView;
import org.example.taskmanager.views.UserManagementView;

public class MainView extends Application {

    private int userId; // ID текущего пользователя
    private String firstName; // Имя текущего пользователя
    private String lastName; // ��амилия текущего пользователя
    private BorderPane root; // Главный контейнер
    private VBox sideMenu; // Боковое меню
    private String userRole; // Добавлено: роль пользователя
    private Label statusLabel; // Элемент для строки состояния

    // Конструктор для задания роли
    public MainView(int userId, String userRole, String firstName, String lastName) {
        this.userId = userId; // Сохраняем ID текущего пользователя
        this.userRole = userRole;
        this.firstName = firstName;
        this.lastName = lastName;
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

        // Создаем строку состояния для отображения пользователя
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar); // Добавляем строку состояния в нижнюю часть окна

        // Создаем сцену и запускаем приложение
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Task Manager: " + userRole);
        primaryStage.show();
    }

    // Создание бокового меню с переключателями
    private VBox createSideMenu() {
        Button tasksButton = new Button("Управление задачами");
        Button logoutButton = new Button("Сменить пользователя"); // Кнопка для смены пользователя
        Button exitButton = new Button("Выход"); // Кнопка для выхода из программы

        VBox menu;
        if ("Admin".equals(userRole)) {
            Button usersButton = new Button("Управление пользователями");

            // Добавляем обработчики событий для кнопок
            usersButton.setOnAction(e -> switchToUserManagement());
            tasksButton.setOnAction(e -> switchToTaskManagement(userId)); // Передаём ID текущего пользователя
            logoutButton.setOnAction(e -> switchToLoginScreen()); // Обработчик смены пользователя
            exitButton.setOnAction(e -> exitApplication()); // Обработчик выхода из программы

            menu = new VBox(10, usersButton, tasksButton, logoutButton, exitButton);
        } else {
            // Для сотрудников доступен только задачи
            tasksButton.setOnAction(e -> switchToTaskManagement(userId)); // Передаём ID текущего пользователя
            logoutButton.setOnAction(e -> switchToLoginScreen()); // Обработчик смены пользователя
            exitButton.setOnAction(e -> exitApplication()); // Обработчик выхода из программы

            menu = new VBox(10, tasksButton, logoutButton, exitButton);
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
    private void switchToTaskManagement(int userId) {
        TaskManagementView taskManagementView = new TaskManagementView(userId); // Передаём ID текущего пользователя
        root.setCenter(taskManagementView);
    }

    // Метод для возвращения на экран авторизации
    private void switchToLoginScreen() {
        Stage currentStage = (Stage) root.getScene().getWindow(); // Получаем текущее окно
        LoginForm loginForm = new LoginForm(); // Создаем новый экран авторизации
        loginForm.show(currentStage); // Показываем форму авторизации
    }

    // Метод для завершения приложения
    private void exitApplication() {
        Platform.exit(); // Завершаем работу приложения
    }

    // Создание строки состояния
    private HBox createStatusBar() {
        statusLabel = new Label("Залогинен как: " + firstName + " " + lastName + " (ID: " + userId + ")");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-padding: 5px;");

        HBox statusBar = new HBox(statusLabel);
        statusBar.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5px;");
        return statusBar;
    }
}