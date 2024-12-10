package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIds = new ArrayList<>();
    LocalDateTime endTime;

    public Epic(String title, String description, Duration duration, LocalDateTime startTime) {

        super(title, description, duration, startTime);
    }

    public Epic(int id, String title, String description, Status status, Duration duration, LocalDateTime startTime, ArrayList<Integer> subTaskIds) {
        super(id, title, description, status, duration, startTime);
        if (subTaskIds != null) {
            this.subTaskIds = subTaskIds;
        }
    }

    public TypeTask getTypeTask() {
        return TypeTask.EPIC;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void addSubTaskIds(int subTaskId) {
        this.subTaskIds.add(subTaskId);
    }

    public String toStringAsModel() {
        return "model.Epic{" +
                "id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", subTaskIds=" + this.getSubTaskIds() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIds, epic.subTaskIds);
    }

}
