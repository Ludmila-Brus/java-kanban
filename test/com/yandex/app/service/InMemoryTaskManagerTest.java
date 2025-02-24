package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager getTaskManager(){
        return new InMemoryTaskManager();
    }

    @Test
    void shouldBeTrueIsTwoTaskIntersect() {

        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);

        SubTask subTask_1 = new SubTask("Подзадача 1", "Подзадача 1 описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask_2 = new SubTask("Подзадача 2", "Подзадача 2 описание", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));

        Task task = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());

        boolean IsIntersect = taskManager.isTwoTaskIntersect(subTask_1, task);
        assertTrue(IsIntersect, "Задачи пересекаются - это должно быть истинно");

        IsIntersect = taskManager.isTwoTaskIntersect(subTask_1, subTask_2);
        assertFalse(IsIntersect, "Задачи пересекаются - это должно быть ложно");

    }

    @Test
    void shouldBeTrueIsTaskIntersect() {

        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);

        SubTask subTask_1 = new SubTask("Подзадача 1", "Подзадача 1 описание", epicId, Duration.ofMinutes(30), LocalDateTime.now().plusDays(1));
        final int subTaskId_1 = taskManager.addSubTask(subTask_1);
        SubTask subTask_2 = new SubTask("Подзадача 2", "Подзадача 2 описание", epicId, Duration.ofMinutes(30), LocalDateTime.now());
        final int subTaskId_2 = taskManager.addSubTask(subTask_2);
        Task task_1 = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now().plusDays(2));
        final int taskId_1 = taskManager.addTask(task_1);
        Task task_2 = new Task("Задача 1", "Выбрать рюкзак", Duration.ofMinutes(30), LocalDateTime.now());

        boolean IsIntersect = taskManager.isTaskIntersect(task_2);
        assertTrue(IsIntersect, "Задача пересекается с другими - это должно быть истинно");

        task_2.setStartTime(LocalDateTime.now().plusDays(4));
        IsIntersect = taskManager.isTwoTaskIntersect(subTask_1, subTask_2);
        assertFalse(IsIntersect, "Задача пересекается с другими - это должно быть ложно");

    }

}