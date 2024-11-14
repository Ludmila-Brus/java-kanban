package com.yandex.app.model;

import java.util.Objects;

public class Task {

    private int id;
    private String title;
    private String description;
    private Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public TypeTask getTypeTask() {return TypeTask.TASK;}

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    // используется в TaskManager.addTask
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
            title.equals(task.title) &&
            description.equals(task.description) &&
            status == task.status;
    }

    public String toStringAsModel() {
        return "model.Task{" +
                "id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }

    @Override
    public String toString() {
        return String.join(
                ",",
                Integer.valueOf(this.getId()).toString(),
                this.getTypeTask().toString(),
                this.getTitle(),
                this.getStatus().toString(),
                this.getDescription()
        );
    }

}
