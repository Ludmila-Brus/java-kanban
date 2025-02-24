package com.yandex.app.model;

import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;
    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldBeEqualsEpic() {
        Epic epic = new Epic("NewEpic", "NewEpic description");
        final int epicId = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

}