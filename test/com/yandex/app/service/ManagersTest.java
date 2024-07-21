package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Экземпляр менеджера не найден.");
        Epic epic = new Epic("NewEpic", "NewEpic description");
        int epicId = taskManager.addEpic(epic);
        assertNotNull(epicId, "Экземпляр менеджера не работает.");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Экземпляр менеджера истории не найден.");
    }
}