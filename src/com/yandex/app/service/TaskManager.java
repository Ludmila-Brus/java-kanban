package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    int addTask(Task task);

    int addSubTask(SubTask subTask);

    int addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteSubTask(int id);

    void deleteEpic(int id);

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
