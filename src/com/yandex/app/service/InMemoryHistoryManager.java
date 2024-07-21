package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_MAX_SIZE = 10;
    private final ArrayList<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyList.size() == HISTORY_MAX_SIZE) {
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }


}
