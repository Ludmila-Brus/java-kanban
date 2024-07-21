package com.yandex.app.model;

import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private TaskManager taskManager;
    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldBeEqualsSubTask() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Убраться в квартире", "Навести порядок во всех комнатах и на кухне");
        int epicId = taskManager.addEpic(epic);

        SubTask subTask = new SubTask("NewSubTask", "NewSubTask description", epicId);
        final int subTaskId = taskManager.addSubTask(subTask);
        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);
        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");
    }

}