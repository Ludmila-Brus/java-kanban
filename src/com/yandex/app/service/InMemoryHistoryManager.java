package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_MAX_SIZE = 10;
    private ArrayList<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (historyList.size() == HISTORY_MAX_SIZE) {
            historyList.remove(0);
        }
        Task newTask = new Task(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus()
        );
        SubTask newSubTask = new SubTask(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            -1
        );
        Epic newEpic = new Epic(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            null
        );

        if (task.getClass() == newTask.getClass()) {
            historyList.add(newTask);
        } else if (task.getClass() == newSubTask.getClass()) {
            SubTask subTask = (SubTask) task;
            newSubTask.setEpicId(subTask.getEpicId());
            historyList.add(newSubTask);
        } else if (task.getClass() == newEpic.getClass()) {
            Epic epic = (Epic) task;
            newEpic.setSubTaskIds(epic.getSubTaskIds());
            historyList.add(newEpic);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }


}
