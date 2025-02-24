package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private final TreeSet<Task> tasksTree = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public int getNextId() {
        return this.nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    @Override
    public int addTask(Task task) {
        if (task.getStartTime() == null) {
            System.out.println("Нельзя добавить задачу с пустым временем старта !");
            return 0;
        }
        if (isTaskIntersect(task)) {
            System.out.println("Нельзя добавить задачу с пересечением !");
            return 0;
        }
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        tasksTree.add(task);
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
    private void syncEpicDuration(Epic epic) {
        LocalDateTime startTime = null;
        //LocalDateTime.now().plusDays(400);
        LocalDateTime endTime = null;
        Duration duration = null;
        //LocalDateTime.now().minusDays(400);
        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStartTime().isBefore( Objects.isNull(startTime) ? LocalDateTime.now().plusDays(400) : startTime)) {
                startTime = subTask.getStartTime();
            }
            if (subTask.getEndTime().isAfter(Objects.isNull(endTime) ? LocalDateTime.now().minusDays(400) : endTime)) {
                endTime = subTask.getEndTime();
            }
        }
        epic.setStartTime(startTime);
        if (!Objects.isNull(endTime) & !Objects.isNull(startTime)) {
            duration = Duration.between(startTime, endTime);
        }
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    @Override
    public int addSubTask(SubTask subTask) {
        if (subTask.getStartTime() == null) {
            System.out.println("Нельзя добавить задачу с пустым временем старта !");
            return 0;
        }
        if (isTaskIntersect(subTask)) {
            System.out.println("Нельзя добавить задачу с пересечением !");
            return 0;
        }
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
        syncEpicDuration(epic);
        tasksTree.add(subTask);
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
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такой задачи нет в списке");
        }
    }

    @Override
    public void updateTaskStartTime(Task task, LocalDateTime startTime) {

        Task updateTask = new Task(task.getTitle(), task.getDescription(), task.getDuration(), startTime);
        System.out.println(updateTask);
        if (isTaskIntersect(updateTask)) {
            System.out.println("Пересечение по интервалу выполнения задачи. Такая правка задачи не возможна ");
            return;
        }
        tasksTree.remove(task);
        task.setStartTime(startTime);
        System.out.println(task);
        tasksTree.add(task);

        if (subTasks.containsKey(task.getId())) {
            syncEpicDuration(epics.get(((SubTask) task).getEpicId()));
        }
    }

    @Override
    public void updateTaskDuration(Task task, Duration duration) {

        Task updateTask = new Task(task.getTitle(), task.getDescription(), duration, task.getStartTime());
        System.out.println(updateTask);
        if (isTaskIntersect(updateTask)) {
            System.out.println("Пересечение по интервалу выполнения задачи. Такая правка задачи не возможна ");
            return;
        }
        task.setDuration(duration);
        System.out.println(task);
    }


    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            syncEpicStatus(epics.get(subTask.getEpicId()));
            syncEpicDuration(epics.get(subTask.getEpicId()));
        } else {
            System.out.println("Такой подзадачи нет в списке");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            // проверить, что подзадачи имеют класс SubTask
            for (Integer subTaskIds: epic.getSubTaskIds()) {
                if (!subTasks.containsKey(subTaskIds)) {
                    throw new ManagerSaveException("Ошибка изменения эпика: потытка добавить в подзадачи объект другого класса с id ", subTaskIds.toString());
                }
            }
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Такого эпика нет в списке");
        }
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasksTree.remove(getTask(id));
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
            tasksTree.remove(getSubTask(id));
            subTasks.remove(id);
            syncEpicStatus(epic);
            syncEpicDuration(epic);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с id = " + id + " не найдена");
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            List<Integer> result = epics.get(id).getSubTaskIds().stream()
                .peek(subTasks::remove)
                .toList();

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
            List<SubTask> subTasksByEpicTmp = epics.get(epicId).getSubTaskIds().stream()
                    .map(subTasks::get)
                    .toList();
            subTasksByEpic.addAll(subTasksByEpicTmp);
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
                tasksTree.remove(getTask(taskId));
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
                tasksTree.remove(getSubTask(subTaskId));
                if (epics.containsKey(subTask.getEpicId())) {
                    Epic epic = epics.get(subTask.getEpicId());
                    epic.getSubTaskIds().clear();
                    syncEpicStatus(epic);
                    syncEpicDuration(epic);
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
                tasksTree.remove(getSubTask(subTaskId));
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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> tasksTreeNew = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        tasksTreeNew.addAll(tasksTree);
        return tasksTreeNew;
    }

    //  проверка что задачи и подзадачи не пересекаются по времени выполнения
    //  во время создания или изменения задач и подзадач
    public boolean isTwoTaskIntersect(Task task1, Task task2) {
        return  (task1.getStartTime().isBefore(task2.getStartTime()) | task1.getStartTime().isEqual(task2.getStartTime())) &
                (task1.getEndTime().isAfter(task2.getStartTime())) |
                (task2.getStartTime().isBefore(task1.getStartTime()) | task2.getStartTime().isEqual(task1.getStartTime())) &
                (task2.getEndTime().isAfter(task1.getStartTime()));
    }

    public boolean isTaskIntersect(Task newTask) {
        for (Task task: getPrioritizedTasks()) {
            // не сверять задачу саму с собой
            if (task.getId() != newTask.getId()) {
                if (isTwoTaskIntersect(task, newTask)) {
                    return true;
                }
            }
        }
        return false;
    }
}
