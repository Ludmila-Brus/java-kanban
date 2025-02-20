package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = getTaskManager();
    }

    protected abstract T getTaskManager();

    @Test
    void shouldBeFalseWhenEpicToEpic() {
        // создать первый эпик
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // добавить в эпик подзадачу
        SubTask subTask1 = new SubTask("Помыть посуду","Помыть тарелки и чашки", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addSubTask(subTask1);
        // попытка в эпик добавить самого себя
        epic.addSubTaskIds(epicId);
        Exception thrown = assertThrows(Exception.class, () -> {
            taskManager.updateEpic(epic);
        }, "Exception was expected");
        assertNotNull(thrown.getMessage());
    }

    @Test
    void shouldBeFalseWhenSubtaskToSubtaskAsEpic() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("NewSubtask", "NewSubtask description", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        int subTaskId = taskManager.addSubTask(subTask);
        // попытка создать вторую подзадачу, присвоив ей в качестве эпика первую подзадачу
        SubTask subTaskOther = new SubTask("NewSubtaskOther", "NewSubtaskOther description", subTaskId, Duration.ofMinutes(130), LocalDateTime.now().plusDays(1));
        Exception thrown = assertThrows(Exception.class, () -> {
            taskManager.addSubTask(subTaskOther);
        }, "Exception was expected");
        assertNotNull(thrown.getMessage());
    }

    @Test
    void shouldAddTask() {
        Task task = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId = taskManager.addTask(task);
        assertEquals(taskId, task.getId(), "Id не совпадают");
        assertTrue(task instanceof Task, "Объект не принадлежит классу Задача");
    }

    @Test
    void shouldNotAddTaskWithIntersect() {
        Task task_1 = new Task("Задача 1", "Выбрать рюкзак 1", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId_1 = taskManager.addTask(task_1);
        Task task_2 = new Task("Задача 2", "Выбрать рюкзак 2", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(10));
        final int taskId_2 = taskManager.addTask(task_2);
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        SubTask subTask_3 = new SubTask("ПодЗадача 2", "Выбрать рюкзак 3", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15));
        final int subTaskId_3 = taskManager.addSubTask(subTask_3);
        assertFalse(taskManager.getTasks().contains(task_2), "Задача не должна быть добавлена");
        assertFalse(taskManager.getSubTasks().contains(subTask_3), "ПодЗадача не должна быть добавлена");
    }

    @Test
    void shouldAddTaskWithoutIntersect() {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        Task task_1 = new Task("Задача 1", "Выбрать рюкзак 1", Duration.ofMinutes(20), localDateTimeNow);
        final int taskId_1 = taskManager.addTask(task_1);
        Task task_2 = new Task("Задача 2", "Выбрать рюкзак 2", Duration.ofMinutes(20), localDateTimeNow.plusMinutes(20));
        final int taskId_2 = taskManager.addTask(task_2);
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        SubTask subTask_3 = new SubTask("ПодЗадача 2", "Выбрать рюкзак 3", epicId, Duration.ofMinutes(20), localDateTimeNow.minusMinutes(20));
        final int subTaskId_3 = taskManager.addSubTask(subTask_3);
        assertTrue(taskManager.getTasks().contains(task_2), "Задача должна быть добавлена");
        assertTrue(taskManager.getSubTasks().contains(subTask_3), "ПодЗадача должна быть добавлена");
    }

    @Test
    void shouldAddSubTask() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        //
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        final int subTaskId = taskManager.addSubTask(subTask);
        assertEquals(subTaskId, subTask.getId(), "Id не совпадают");
        assertTrue(subTask instanceof SubTask, "Объект не принадлежит классу Подзадача");
    }

    @Test
    void shouldAddEpic() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        assertEquals(epicId, epic.getId(), "Id не совпадают");
        assertTrue(epic instanceof Epic, "Объект не принадлежит классу Эпик");
    }

    @Test
    void shouldBeGoodTaskId() {
        Task task1 = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int task1Id = taskManager.addTask(task1);
        Task task2 = new Task(task1Id, "Задача 2", "Задача 2 описание", Status.NEW, Duration.ofMinutes(130), LocalDateTime.now().plusDays(2));
        final int task2Id = taskManager.addTask(task2);
        assertNotEquals(task1Id, task2Id, "Id разных задач совпадают");
    }

    @Test
    void shouldBeEqualsAfterAdd() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId, Duration.ofMinutes(230), LocalDateTime.now().plusDays(3));
        final int subTaskId = taskManager.addSubTask(subTask);
        assertEquals(epic, taskManager.getEpic(epicId), "Эпики не совпадают по все полям");
        assertEquals(subTask, taskManager.getSubTask(subTaskId), "Подзадачи не совпадают по все полям");
    }

    @Test
    void shouldBeEmptyHistoryTask() {
        // создать задачу
        Task task_1 = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId_1 = taskManager.addTask(task_1);
        task_1.setDescription("Другое описание задачи 1");
        taskManager.updateTask(task_1);
        Task task_2 = new Task("Задача 2", "Выбрать рюкзак 2", Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));
        final int taskId_2 = taskManager.addTask(task_2);
        task_2.setDescription("Другое описание задачи 2");
        taskManager.updateTask(task_2);
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("NewSubtask", "NewSubtask description", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        int subTaskId = taskManager.addSubTask(subTask);
        // получить историю
        ArrayList<Task> historyTaskArray = taskManager.getHistory();
        assertEquals(0, historyTaskArray.size(), "Количество в списке истории должно быть равно 0");
    }

    @Test
    void shouldBeNoDupleHistoryTask() {
        // создать задачу
        Task task = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId = taskManager.addTask(task);
        // сохранить в историю
        Task savedTask = taskManager.getTask(taskId);
        // изменить описание задачи
        task.setDescription("Другое описание задачи 1");
        taskManager.updateTask(task);
        // сохранить в историю еще раз
        Task savedTask_1 = taskManager.getTask(taskId);
        // получить историю
        ArrayList<Task> historyTaskArray = taskManager.getHistory();
        assertEquals(1, historyTaskArray.size(), "Количество в списке истории должно быть равно 1");
    }

    @Test
    void shouldBeGoodHistoryTask() {
        // создать задачу
        Task task = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId = taskManager.addTask(task);
        // сохранить в историю
        Task savedTask = taskManager.getTask(taskId);
        // изменить описание задачи
        task.setDescription("Другое описание задачи");
        taskManager.updateTask(task);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        int historyTaskIndex = tasks.indexOf(task);
        Task historyTask = tasks.get(historyTaskIndex);
        assertEquals("Другое описание задачи", historyTask.getDescription());
        assertEquals(1, tasks.size(), "Количество в списке истории не равно 1");
    }

    @Test
    void shouldBeGoodHistorySubTask() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId, Duration.ofMinutes(330), LocalDateTime.now().plusDays(4));
        final int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        // изменить статус подзадачи
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        int historyTaskIndex = tasks.indexOf(subTask);
        Task historyTask = tasks.get(historyTaskIndex);
        assertEquals(Status.DONE, historyTask.getStatus());
        assertEquals(2, tasks.size(), "Количество в списке истории не равно 1");
    }

    @Test
    void shouldBeGoodHistoryEpic() {
        // создать эпик
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        final int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        Epic savedEpic = taskManager.getEpic(epicId);
        // изменить название эпика
        epic.setTitle("Другое название эпика");
        taskManager.updateEpic(epic);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        int historyTaskId = tasks.indexOf(savedEpic);
        Task historyTask = tasks.get(historyTaskId);
        assertEquals("Другое название эпика", historyTask.getTitle());
        assertEquals(1, tasks.size(), "Количество в списке истории не равно 1");
    }

    @Test
    void shouldBeDeletedFromHistoryTask() {
        // создать задачу
        Task task = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId = taskManager.addTask(task);
        // сохранить в историю
        Task savedTask = taskManager.getTask(taskId);
        // удалить задачу
        taskManager.deleteTask(taskId);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        int historyTaskIndex = tasks.indexOf(task);
        assertEquals(-1, historyTaskIndex, "Задача должна быть удалена из истории");
    }

    @Test
    void shouldBeDeletedFromHistorySubTask() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        final int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        // удалить подзадачу
        taskManager.deleteSubTask(subTaskId);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        int historySubTaskIndex = tasks.indexOf(subTask);
        assertEquals(-1, historySubTaskIndex, "Подзадача должна быть удалена из истории");
    }

    @Test
    void shouldBeDeletedFromMiddleHistoryTask() {
        // создать задачу
        Task task_1 = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());
        final int taskId_1 = taskManager.addTask(task_1);
        // сохранить в историю
        Task savedTask_1 = taskManager.getTask(taskId_1);
        task_1.setDescription("Другое описание задачи 1");
        taskManager.updateTask(task_1);
        // создать задачу
        Task task_2 = new Task("Задача 2", "Выбрать рюкзак 2", Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));
        final int taskId_2 = taskManager.addTask(task_2);
        // сохранить в историю
        Task savedTask_2 = taskManager.getTask(taskId_2);
        task_2.setDescription("Другое описание задачи 2");
        taskManager.updateTask(task_2);
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("NewSubtask", "NewSubtask description", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        // сохранить в историю
        Epic savedEpic = taskManager.getEpic(epicId);
        // удалить задачу
        taskManager.deleteTask(taskId_2);
        // получить историю
        ArrayList<Task> savedTasks = taskManager.getHistory();
        int historyTaskIndex = savedTasks.indexOf(task_2);
        assertEquals(-1, historyTaskIndex, "Задача должна быть удалена из истории");
    }

    @Test
    void shouldBeDeletedFromHistoryEpic() {
        // создать эпик
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        final int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        Epic savedEpic = taskManager.getEpic(epicId);
        // удалить эпик
        taskManager.deleteEpic(epicId);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        int historyEpicId = tasks.indexOf(savedEpic);
        int historySubTaskId = tasks.indexOf(subTask);
        assertEquals(-1, historyEpicId, "Эпик должен быть удален из истории");
        assertEquals(-1, historySubTaskId, "Подзадача должна быть удалена из истории");
    }

    @Test
    void shouldBeNewDoneInProgressStatusEpic() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        //
        SubTask subTask_1 = new SubTask("Подзадача 1", "Подзадача 1 описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        final int subTaskId_1 = taskManager.addSubTask(subTask_1);
        SubTask subTask_2 = new SubTask("Подзадача 2", "Подзадача 2 описание", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));
        final int subTaskId_2 = taskManager.addSubTask(subTask_2);
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть равен NEW");

        subTask_1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask_1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть равен IN_PROGRESS");

        subTask_1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask_1);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть равен IN_PROGRESS");

        subTask_2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask_2);
        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть равен DONE");

    }

}
