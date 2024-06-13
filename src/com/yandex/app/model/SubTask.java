package com.yandex.app.model;

import com.yandex.app.service.Status;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description) {
        super(title, description);
    }

    public SubTask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.SubTask{" +
                "epicId=" + this.getEpicId() +
                ", id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
