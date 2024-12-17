package org.example.taskmanager.models;

import java.time.LocalDate;

public class Task {

    private int id;
    private String title;
    private String description;
    private LocalDate deadline;
    private int assigneeId; // ID сотрудника, назначенного ответственным за задачу
    private String status; // Статус задачи (New, Resolved, Closed и т.д.)
    private String priority; // Приоритет задачи (Urgent, High, Low и т.д.)
    private String category; // Категория задачи (IT, BO, User Support и т.д.)

    // Конструктор для загрузки задачи из базы данных
    public Task(int id, String title, String description, LocalDate deadline, int assigneeId, String status, String priority, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.assigneeId = assigneeId;
        this.status = status;
        this.priority = priority;
        this.category = category;
    }

    // Конструктор для создания новой задачи
    public Task(String title, String description, LocalDate deadline, int assigneeId, String status, String priority, String category) {
        this(-1, title, description, deadline, assigneeId, status, priority, category);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public int getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(int assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", assigneeId=" + assigneeId +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}