package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }
    @Test
    void shouldBeFalseWhenEpicToEpic() {
        // создать первый эпик
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // добавить в эпик подзадачу
        SubTask subTask1 = new SubTask("Помыть посуду","Помыть тарелки и чашки", epicId);
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
        SubTask subTask = new SubTask("NewSubtask", "NewSubtask description", epicId);
        int subTaskId = taskManager.addSubTask(subTask);
        // попытка создать вторую подзадачу, присвоив ей в качестве эпика первую подзадачу
        SubTask subTaskOther = new SubTask("NewSubtaskOther", "NewSubtaskOther description", subTaskId);
        Exception thrown = assertThrows(Exception.class, () -> {
            taskManager.addSubTask(subTaskOther);
        }, "Exception was expected");
        assertNotNull(thrown.getMessage());
    }

    @Test
    void shouldAddTask() {
        Task task = new Task("Задача 1", "Выбрать рюкзак");
        final int taskId = taskManager.addTask(task);
        assertEquals(taskId, task.getId(), "Id не совпадают");
        assertTrue(task instanceof Task, "Объект не принадлежит классу Задача");
    }

    @Test
    void shouldAddSubTask() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        //
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId);
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
        Task task1 = new Task("Задача 1", "Выбрать рюкзак");
        final int task1Id = taskManager.addTask(task1);
        Task task2 = new Task(task1Id, "Задача 2", "Задача 2 описание", Status.NEW);
        final int task2Id = taskManager.addTask(task2);
        assertNotEquals(task1Id, task2Id, "Id разных задач совпадают");
    }

    @Test
    void shouldBeEqualsAfterAdd() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId);
        final int subTaskId = taskManager.addSubTask(subTask);
        assertEquals(epic, taskManager.getEpic(epicId), "Эпики не совпадают по все полям");
        assertEquals(subTask, taskManager.getSubTask(subTaskId), "Подзадачи не совпадают по все полям");
    }

    @Test
    void shouldBeGoodHistoryTask() {
        // создать задачу
        Task task = new Task("Задача 1", "Выбрать рюкзак");
        final int taskId = taskManager.addTask(task);
        // сохранить в историю
        Task savedTask = taskManager.getTask(taskId);
        // изменить описание задачи
        task.setDescription("Другое описание задачи");
        taskManager.updateTask(task);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        Task historyTask = tasks.get(0);
        assertEquals("Другое описание задачи", historyTask.getDescription());
        assertEquals(1, tasks.size(), "Количество в списке истории не равно 1");
    }

    @Test
    void shouldBeGoodHistorySubTask() {
        // создать эпик для подзадачи
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId);
        final int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        // изменить статус подзадачи
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        Task historyTask = tasks.get(1);
        assertEquals(Status.DONE, historyTask.getStatus());
        assertEquals(2, tasks.size(), "Количество в списке истории не равно 1");
    }

    @Test
    void shouldBeGoodHistoryEpic() {
        // создать эпик
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        // создать подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача описание", epicId);
        final int subTaskId = taskManager.addSubTask(subTask);
        // сохранить в историю
        Epic savedEpic = taskManager.getEpic(epicId);
        // изменить название эпика
        epic.setTitle("Другое название эпика");
        taskManager.updateEpic(epic);
        // получить историю
        ArrayList<Task> tasks = taskManager.getHistory();
        Task historyTask = tasks.get(1);
        assertEquals("Другое название эпика", historyTask.getTitle());
        assertEquals(2, tasks.size(), "Количество в списке истории не равно 1");
    }
}