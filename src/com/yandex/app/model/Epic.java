package com.yandex.app.model;

import com.yandex.app.service.Status;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, String description, Status status, ArrayList<Integer> subTaskIds) {
        super(id, title, description, status);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        return "com.yandex.app.model.Epic{" +
                "subTaskIds=" + this.getSubTaskIds() +
                ", id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
