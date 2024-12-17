module org.example.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.taskmanager to javafx.fxml;
    exports org.example.taskmanager;
    exports org.example.taskmanager.database;
    opens org.example.taskmanager.database to javafx.fxml;
}