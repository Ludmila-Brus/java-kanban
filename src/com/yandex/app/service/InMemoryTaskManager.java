package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public int getNextId() {
        return this.nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    @Override
    public int addTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    // определить статус эпика
    private void syncEpicStatus(Epic epic) {
        Status epicStatus = null;
        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStatus() == Status.NEW) {
                if (epicStatus != Status.NEW && epicStatus != null) {
                    epicStatus = Status.IN_PROGRESS;
                } else {
                    epicStatus = Status.NEW;
                }
            } else if (subTask.getStatus() == Status.DONE) {
                if (epicStatus != Status.DONE && epicStatus != null) {
                    epicStatus = Status.IN_PROGRESS;
                } else {
                    epicStatus = Status.DONE;
                }
            } else if (subTask.getStatus() == Status.IN_PROGRESS) {
                epicStatus = Status.IN_PROGRESS;
            }
        }
        epic.setStatus((epicStatus != null) ? epicStatus : Status.NEW);
    }

    // определить время начала, продолжительность и время окончания эпика
    // TODO
    private void syncEpicDuration(Epic epic) {
        Status epicStatus = null;
        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStatus() == Status.NEW) {
                if (epicStatus != Status.NEW && epicStatus != null) {
                    epicStatus = Status.IN_PROGRESS;
                } else {
                    epicStatus = Status.NEW;
                }
            } else if (subTask.getStatus() == Status.DONE) {
                if (epicStatus != Status.DONE && epicStatus != null) {
                    epicStatus = Status.IN_PROGRESS;
                } else {
                    epicStatus = Status.DONE;
                }
            } else if (subTask.getStatus() == Status.IN_PROGRESS) {
                epicStatus = Status.IN_PROGRESS;
            }
        }
        epic.setStatus((epicStatus != null) ? epicStatus : Status.NEW);
    }

    @Override
    public int addSubTask(SubTask subTask) {
        // получить эпик
        int epicId = subTask.getEpicId();
        Epic epic = getEpic(epicId);
        // установить id
        subTask.setId(nextId);
        nextId++;
        // добавить подзадачу в hash-список менеджера
        subTasks.put(subTask.getId(), subTask);
        // добавить подзадачу в список эпика
        epic.addSubTaskIds(subTask.getId());
        // обновить статус эпика
        syncEpicStatus(epic);
        return subTask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        syncEpicStatus(epics.get(subTask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        syncEpicStatus(epic);
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача с id = " + id + " не найдена");
        }
    }

    @Override
    public void deleteSubTask(int id) {
        if (subTasks.containsKey(id)) {
            int epicId = subTasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);
            // удаление подзадачи не по индексу, а по объекту
            // поэтому используем обертку (Integer)
            epic.getSubTaskIds().remove((Integer) id);
            subTasks.remove(id);
            syncEpicStatus(epic);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с id = " + id + " не найдена");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик с id = " + id + " не найден");
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // получить список задач
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // получить список подзадач
    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // получить список эпиков
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Integer subTaskId : epics.get(epicId).getSubTaskIds()) {
                SubTask subTask = subTasks.get(subTaskId);
                subTasksByEpic.add(subTask);
            }
        }
        // если нет эпика или в нем нет подзадач вернуть пустой список
        return subTasksByEpic;
    }

    @Override
    public void deleteAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст");
        } else {
            for (Integer taskId : tasks.keySet()) {
                historyManager.remove(taskId);
            }
            tasks.clear();
        }
    }

    @Override
    public void deleteAllSubTasks() {
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст");
        } else {
            for (Map.Entry entry : subTasks.entrySet()) {
                SubTask subTask = (SubTask) entry.getValue();
                Integer subTaskId = (Integer) entry.getKey();
                historyManager.remove(subTaskId);
                if (epics.containsKey(subTask.getEpicId())) {
                    Epic epic = epics.get(subTask.getEpicId());
                    epic.getSubTaskIds().clear();
                    syncEpicStatus(epic);
                }
            }
            subTasks.clear();
        }
    }

    @Override
    public void deleteAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Список епиков пуст");
        } else {
            for (Integer subTaskId : subTasks.keySet()) {
                historyManager.remove(subTaskId);
            }
            for (Integer epicId : epics.keySet()) {
                historyManager.remove(epicId);
            }
            subTasks.clear();
            epics.clear();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

}
