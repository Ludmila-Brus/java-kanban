package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    int addTask(Task task);

    int addTask(SubTask subTask);

    int addTask(Epic epic);

    void updateTask(Task task);

    void updateTask(SubTask subTask);

    void updateTask(Epic epic);

    void deleteTask(int id);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    // получить список задач
    ArrayList<Task> getTasks();

    // получить список подзадач
    ArrayList<SubTask> getSubTasks();

    // получить список эпиков
    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasksByEpic(int epicId);

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    ArrayList<Task> getHistory();
}
