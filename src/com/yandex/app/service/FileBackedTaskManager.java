package com.yandex.app.service;

import com.yandex.app.model.*;

//import java.nio.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
//import ManagerSaveException;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File fileBacked;

    public FileBackedTaskManager(File fileBacked) {
        super();
        this.fileBacked = fileBacked;
    }

    public Task fromString(String value) {

        String[] paramTask = value.split(",");

        int id = Integer.parseInt(paramTask[0]);
        TypeTask typeTask = TypeTask.valueOf(paramTask[1]);
        String title = paramTask[2];
        Status status = Status.valueOf(paramTask[3]);
        String description = paramTask[4];

        if (typeTask == TypeTask.TASK) {
            return new Task(id, title, description, status);
        } else if
        (typeTask == TypeTask.SUBTASK) {
            int epicId = Integer.parseInt(paramTask[5]);
            return new SubTask(id, title, description, status, epicId);
        } else if
        (typeTask == TypeTask.EPIC) {
            return new Epic(id, title, description, status, null);
        }
        return null;
    }

    // сохранить текущее состояние менеджера в указанный файл
    private void save() {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileBacked))) {

                ArrayList<Task> taskArrayList = super.getTasks();
                for (Task task : taskArrayList) {
                    String taskString = task.toString();
                    writer.write(taskString);
                    writer.newLine();
                }
                // сначала сохраняем эпики, потом подзадачи
                ArrayList<Epic> epicArrayList = super.getEpics();
                for (Epic epic : epicArrayList) {
                    String epicString = epic.toString();
                    writer.write(epicString);
                    writer.newLine();
                }

                ArrayList<SubTask> subTaskArrayList = super.getSubTasks();
                for (SubTask subTask : subTaskArrayList) {
                    String subTaskString = subTask.toString();
                    writer.write(subTaskString);
                    writer.newLine();
                }

            } catch (IOException exc) {
                throw new ManagerSaveException("Ошибка записи эпиков в файл " + this.fileBacked.getName() + " ", exc.getMessage());
            }
        } catch (ManagerSaveException exception) {
            System.out.println(exception.getDetailMessage());
        }
    }

    @Override
    public int addTask(Task task) {
        int k;
        k = super.addTask(task);
        save();
        return k;
    }

    @Override
    public int addSubTask(SubTask subTask) {
        int k = super.addSubTask(subTask);
        save();
        return k;
    }

    @Override
    public int addEpic(Epic epic) {
        int k = super.addEpic(epic);
        save();
        return k;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public static void loadFromFile(File fileBacked) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(fileBacked);
        try {
            final String csvStr = Files.readString(fileBacked.toPath());
            final String[] lines = csvStr.split(System.lineSeparator());
            // пропустить i = 0 с заголовком колонок
            // предполагается, что сначала в файле будуь эпики, потом подзадачи
            for (int i = 1; i < lines.length; i++) {
                final Task task = fileBackedTaskManager.fromString(lines[i]);
                if (task.getTypeTask() == TypeTask.TASK) {
                    fileBackedTaskManager.addTask(task);
                } else if (task.getTypeTask() == TypeTask.SUBTASK) {
                    fileBackedTaskManager.addSubTask((SubTask) task);
                } else if (task.getTypeTask() == TypeTask.EPIC) {
                    fileBackedTaskManager.addEpic((Epic) task);
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка чтения из файла " + fileBacked.getName() + " ", exc.getMessage());
        }
        
        // test дорабатываем ?
    }
}
