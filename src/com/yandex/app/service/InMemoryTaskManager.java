package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

// реализация согласована
public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    @Override
    public int addTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addTask(SubTask subTask) {
        subTask.setId(nextId);
        nextId++;
        subTasks.put(subTask.getId(), subTask);
        return subTask.getId();
    }
    
    @Override
    public int addTask(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        syncEpic(epic);
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        if (subTask.getEpicId() != 0) {
            syncEpic(epics.get(subTask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Epic epic) {
        epics.put(epic.getId(), epic);
        syncEpic(epic);
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) {
           int epicId = subTasks.get(id).getEpicId();
           Epic epic = epics.get(epicId);
           // удаление подзадачи не по индексу, а по объекту
           // поэтому используем обертку (Integer)
           epic.getSubTaskIds().remove((Integer) id);
           subTasks.remove(id);
           syncEpic(epic);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subTaskId : epic.getSubTaskIds()) {
                subTasks.remove(subTaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Задача с id = " + id + "не найдена");
        }
    }

    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    // получить список копий задач
//    public ArrayList<Task> getCopyOfTasks() {
//        ArrayList<Task> copyOfTasks = new ArrayList<>();
//        for (Task task : tasks.values()) {
//            Task copyOfTask = new Task(task.getId(), task.getTitle(), task.getDescription(), task.getStatus());
//            copyOfTasks.add(copyOfTask);
//        }
//        return copyOfTasks;
//    }

    // получить список задач
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    // получить список копий подзадач
//    public ArrayList<SubTask> getCopyOfSubTasks() {
//        ArrayList<SubTask> copyOfSubTasks = new ArrayList<>();
//        for (SubTask subTask : subTasks.values()) {
//            SubTask copyOfSubTask = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getStatus(), subTask.getEpicId());
//            copyOfSubTasks.add(copyOfSubTask);
//        }
//        return copyOfSubTasks;
//    }

    // получить список подзадач
    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // получить список копий эпиков
//    public ArrayList<Epic> getCopyOfEpics() {
//        ArrayList<Epic> copyOfEpics = new ArrayList<>();
//        for (Epic epic : epics.values()) {
//            ArrayList<Integer> subTaskIds = epic.getSubTaskIds();
//            ArrayList<Integer> copyOfSubTaskIds = new ArrayList<>();
//            copyOfSubTaskIds.addAll(subTaskIds);
//            Epic copyOfEpic = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus(), copyOfSubTaskIds);
//            copyOfEpics.add(copyOfEpic);
//        }
//        return copyOfEpics;
//    }

    // получить список эпиков
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        // без проверки epics.containsKey(epicId)
        // придется проверять на epic null, чтобы не было ошибки при epic.getSubTaskIds()
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            if (!epic.getSubTaskIds().isEmpty()) {
                ArrayList<SubTask> subTasksByEpic = new ArrayList<>();
                for (Integer subTaskId : epic.getSubTaskIds()) {
                    SubTask subTask = subTasks.get(subTaskId);
                    subTasksByEpic.add(subTask);
                }
                return subTasksByEpic;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void deleteAllTasks(){
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст"); 
        } else {
            tasks.clear();
        }
    }
    
    @Override
    public void deleteAllSubTasks(){
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст");
        } else {
            for (SubTask subTask : subTasks.values()) {
                if (epics.containsKey(subTask.getEpicId())) {
                    Epic epic = epics.get(subTask.getEpicId());
                    epic.getSubTaskIds().clear();
                    syncEpic(epic);
                }
            }
            subTasks.clear();
        }
    }

    @Override
    public void deleteAllEpics(){
        if (epics.isEmpty()) {
            System.out.println("Список епиков пуст");
        } else {
            subTasks.clear();
            epics.clear();
        }
    }

    // всем подзадачам эпика присвоим id эпика и
    // определим статус эпика
    private void syncEpic(Epic epic) {
        Status epicStatus = null;
        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            subTask.setEpicId(epic.getId());
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

}
