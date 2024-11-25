package com.yandex.app.model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, int epicId) {

        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public TypeTask getTypeTask() {
        return TypeTask.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    public String toStringAsModel() {
        return "model.SubTask{" +
                "id=" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                ", epicId=" + this.getEpicId() +
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
                this.getDescription(),
                Integer.valueOf(this.getEpicId()).toString()
        );
    }
}
