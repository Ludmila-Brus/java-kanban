package com.yandex.app.model;

import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private static TaskManager taskManager;
    @BeforeAll
    public static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldBeEqualsTask() {
        Task task = new Task("NewTask", "NewTask description");
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

}