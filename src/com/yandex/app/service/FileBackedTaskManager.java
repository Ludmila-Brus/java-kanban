package com.yandex.app.service;

import com.yandex.app.model.*;

//import java.nio.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

                writer.write("id,type,name,status,description,epic");
                writer.newLine();

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

    public static FileBackedTaskManager loadFromFile(File fileBacked) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(fileBacked);
        try {
            final String csvStr = Files.readString(fileBacked.toPath());
            final String[] lines = csvStr.split(System.lineSeparator());
            // пропустить i = 0 с заголовком колонок
            // предполагается, что сначала в файле будуь эпики, потом подзадачи
            for (int i = 1; i < lines.length; i++) {
                final Task task = fileBackedTaskManager.fromString(lines[i]);
                if (task.getTypeTask() == TypeTask.TASK) {
                    if (task.getId() > fileBackedTaskManager.getNextId()) {
                        fileBackedTaskManager.setNextId(task.getId());
                    }
                    fileBackedTaskManager.addTask(task);
                } else if (task.getTypeTask() == TypeTask.EPIC) {
                    if (task.getId() > fileBackedTaskManager.getNextId()) {
                        fileBackedTaskManager.setNextId(task.getId());
                    }
                    fileBackedTaskManager.addEpic((Epic) task);
                } else if (task.getTypeTask() == TypeTask.SUBTASK) {
                    if (task.getId() > fileBackedTaskManager.getNextId()) {
                        fileBackedTaskManager.setNextId(task.getId());
                    }
                    fileBackedTaskManager.addSubTask((SubTask) task);
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка чтения из файла " + fileBacked.getName() + " ", exc.getMessage());
        }
        
        return fileBackedTaskManager;
    }

    public static void printTestDataByList(
            TaskManager taskManager
    ) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Подзадачи:");
        for (SubTask subTask : taskManager.getSubTasks()) {
            System.out.println(subTask);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Start");
        FileBackedTaskManager taskManager = null;

        final String HOME = System.getProperty("user.home");
        System.out.println("home dir " + HOME);
        File fileBacked = new File(String.valueOf(Paths.get(HOME, "testFile.txt")));
        if (fileBacked.exists()) {
            boolean Deleted = fileBacked.delete();
        }
        boolean createFile = fileBacked.createNewFile();
        if (createFile) {
            System.out.println("Файл успешно создан.");
            taskManager = new FileBackedTaskManager(fileBacked);

            // добавить задачи, эпики, подзадачи
            Task task1 = new Task("Задача номер 1", "Вызвать мастера");
            Task task2 = new Task("Задача номер 2", "Заехать на мойку");
            Task task3 = new Task("Задача номер 3", "Зайти в магазин");
            Task task4 = new Task("Задача номер 4", "Выбрать рюкзак");

            final int task1Id = taskManager.addTask(task1);
            final int task2Id = taskManager.addTask(task2);
            final int task3Id = taskManager.addTask(task3);
            final int task4Id = taskManager.addTask(task4);

            Epic epic1 = new Epic("Эпик номер 1", "Навести порядок");
            final int epic1Id = taskManager.addEpic(epic1);
            Epic epic2 = new Epic("Эпик номер 2", "Пересадить отросток");
            final int epic2Id = taskManager.addEpic(epic2);

            SubTask subTask1 = new SubTask("Подзадача номер 1", "Помыть тарелки и чашки", epic1Id);
            SubTask subTask2 = new SubTask("Подзадача номер 2", "Полить цветы", epic1Id);
            SubTask subTask3 = new SubTask("Подзадача номер 3", "Подмести", epic1Id);
            SubTask subTask4 = new SubTask("Подзадача номер 4", "Выбрать горшок", epic2Id);
            SubTask subTask5 = new SubTask("Подзадача номер 5", "Купить грунт", epic2Id);
            SubTask subTask6 = new SubTask("Подзадача номер 6", "Посадить цвет", epic2Id);

            final int subTask1Id = taskManager.addSubTask(subTask1);
            final int subTask2Id = taskManager.addSubTask(subTask2);
            final int subTask3Id = taskManager.addSubTask(subTask3);
            final int subTask4Id = taskManager.addSubTask(subTask4);
            final int subTask5Id = taskManager.addSubTask(subTask5);
            final int subTask6Id = taskManager.addSubTask(subTask6);

            System.out.println("Меняем статус задачи");
            task1.setStatus(Status.IN_PROGRESS);
            taskManager.updateTask(task1);
            System.out.println("Меняем статус подзадачи");
            subTask1.setStatus(Status.IN_PROGRESS);
            taskManager.updateSubTask(subTask1);

            System.out.println("Меняем описание эпика");
            epic1.setDescription("Еженедельная уборка квартиры");
            taskManager.updateEpic(epic1);
            // посмотреть напечатать что сейчас в старом менеджере
            printTestDataByList(taskManager);

            FileBackedTaskManager taskManagerSecond = FileBackedTaskManager.loadFromFile(fileBacked);
            // посмотреть напечатать что получилось в новом менеджере
            printTestDataByList(taskManagerSecond);
        }
    }
}
