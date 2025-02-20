package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Status;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final LocalDateTime dateTimeFirst = LocalDateTime.parse("22.01.2025 09:00", formatter);

    @Override
    protected FileBackedTaskManager getTaskManager() {
        try {
            File tmpFile = File.createTempFile("test", null);
            // удалить при завершении программы
            tmpFile.deleteOnExit();
            return new FileBackedTaskManager(tmpFile);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания tmp-файла для теста");
        }
    }

    @Test
    void shouldBeSaveToFile() {
        try {
            File tmpFile = taskManager.getFileBacked();
            //
            Task task1 = new Task("Задача номер 1", "Вызвать мастера", Duration.ofMinutes(30), dateTimeFirst);
            Task task2 = new Task("Задача номер 2", "Заехать на мойку", Duration.ofMinutes(60), dateTimeFirst.plusDays(1));
            Task task3 = new Task("Задача номер 3", "Зайти в магазин", Duration.ofMinutes(90), dateTimeFirst.plusDays(2));
            Task task4 = new Task("Задача номер 4", "Выбрать рюкзак", Duration.ofMinutes(130), dateTimeFirst.plusDays(3));

            final int task1Id = taskManager.addTask(task1);
            final int task2Id = taskManager.addTask(task2);
            final int task3Id = taskManager.addTask(task3);
            final int task4Id = taskManager.addTask(task4);

            Epic epic1 = new Epic("Эпик номер 1", "Навести порядок");
            final int epic1Id = taskManager.addEpic(epic1);
            Epic epic2 = new Epic("Эпик номер 2", "Пересадить отросток");
            final int epic2Id = taskManager.addEpic(epic2);

            SubTask subTask1 = new SubTask("Подзадача номер 1", "Помыть тарелки и чашки", epic1Id, Duration.ofMinutes(30), dateTimeFirst.plusDays(4));
            SubTask subTask2 = new SubTask("Подзадача номер 2", "Полить цветы", epic1Id, Duration.ofMinutes(60), dateTimeFirst.plusDays(5));
            SubTask subTask3 = new SubTask("Подзадача номер 3", "Подмести", epic1Id, Duration.ofMinutes(90), dateTimeFirst.plusDays(6));
            SubTask subTask4 = new SubTask("Подзадача номер 4", "Выбрать горшок", epic2Id, Duration.ofMinutes(130), dateTimeFirst.plusDays(7));
            SubTask subTask5 = new SubTask("Подзадача номер 5", "Купить грунт", epic2Id, Duration.ofMinutes(160), dateTimeFirst.plusDays(8));
            SubTask subTask6 = new SubTask("Подзадача номер 6", "Посадить цвет", epic2Id, Duration.ofMinutes(190), dateTimeFirst.plusDays(9));

            final int subTask1Id = taskManager.addSubTask(subTask1);
            final int subTask2Id = taskManager.addSubTask(subTask2);
            final int subTask3Id = taskManager.addSubTask(subTask3);
            final int subTask4Id = taskManager.addSubTask(subTask4);
            final int subTask5Id = taskManager.addSubTask(subTask5);
            final int subTask6Id = taskManager.addSubTask(subTask6);

            // Меняем статус задачи
            task1.setStatus(Status.IN_PROGRESS);
            taskManager.updateTask(task1);
            // Меняем статус подзадачи
            subTask1.setStatus(Status.IN_PROGRESS);
            taskManager.updateSubTask(subTask1);
            //"Меняем описание эпика
            epic1.setDescription("Еженедельная уборка квартиры");
            taskManager.updateEpic(epic1);
            // Удаляем задачу с id task2.getId());
            taskManager.deleteTask(task2.getId());

            // проверить сохранение данных в файл
            // то, что сохранилось в файле
            final String csvStr = Files.readString(tmpFile.toPath());
            final String[] lines = csvStr.split(System.lineSeparator());
            // то, что ожидаем
            final ArrayList<String> expLines = getStrings();
            // проверить
            assertLinesMatch(expLines, Arrays.asList(lines), "Содержимое файла должно совпадать со списком задач");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldBeEmptyFileAfterSaveEmptyListToFile() {
        try {
            File tmpFile = taskManager.getFileBacked();

            // хотя в списки пусты, для пущей надежности выполним контрольное удаление всего
            taskManager.deleteAllTasks();
            taskManager.deleteAllSubTasks();
            taskManager.deleteAllEpics();

            // проверить сохранение данных в файл
            // то, что сохранилось в файле
            final String csvStr = Files.readString(tmpFile.toPath());
            final String[] lines = csvStr.split(System.lineSeparator());
            // то, что ожидаем - только заголовок + пустой список
            final ArrayList<String> expLines = new ArrayList<>();
            expLines.add(0, "id,type,name,status,description,epic,duration,startTime,endTime");
            // проверить
            assertLinesMatch(expLines, Arrays.asList(lines), "Содержимое файла - пусто, должно совпадать со списком задач - пусто");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldBeLoadFromFile() {
        File tmpFile = taskManager.getFileBacked();
        final ArrayList<String> expLines = getStrings();
        // записать в файл задачи
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {

            for (String expLine : expLines) {
                writer.write(expLine);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // выполнить загрузку из файла
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(tmpFile);

        // проверить что получилось, сравнив ранее записанный в файл список
        // со списком задач, получившихся в результате загрузки
        final ArrayList<String> taskStr = new ArrayList<>();
        taskStr.add("id,type,name,status,description,epic,duration,startTime,endTime");

        ArrayList<Task> taskArrayList = taskManagerFromFile.getTasks();
        for (Task task : taskArrayList) {
            taskStr.add(task.toString());
        }
        ArrayList<Epic> epicArrayList = taskManagerFromFile.getEpics();
        for (Epic epic : epicArrayList) {
            taskStr.add(epic.toString());
        }
        ArrayList<SubTask> subTaskArrayList = taskManagerFromFile.getSubTasks();
        for (SubTask subTask : subTaskArrayList) {
            taskStr.add(subTask.toString());
        }

        assertEquals(expLines, taskStr, "Список задач, эпиков, подзадач из файла должен совпадать со списком загруженных");
    }

    @Test
    void shouldBeEmptyListAfterLoadFromEmptyFile() {
        File tmpFile = taskManager.getFileBacked();
        final ArrayList<String> expLines = new ArrayList<>();
        expLines.add(0, "id,type,name,status,description,epic,duration,startTime,endTime");

        // подготовить файл: записать в файл задачи - пустой список - полуичим пустой файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {

            for (String expLine : expLines) {
                writer.write(expLine);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // выполнить загрузку из файла
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(tmpFile);

        // проверить что получилось, сравнив ранее записанный в файл список
        // со списком задач, получившихся в результате загрузки
        final ArrayList<String> taskStr = new ArrayList<>();
        taskStr.add("id,type,name,status,description,epic,duration,startTime,endTime");

        ArrayList<Task> taskArrayList = taskManagerFromFile.getTasks();
        for (Task task : taskArrayList) {
            taskStr.add(task.toString());
        }
        ArrayList<Epic> epicArrayList = taskManagerFromFile.getEpics();
        for (Epic epic : epicArrayList) {
            taskStr.add(epic.toString());
        }
        ArrayList<SubTask> subTaskArrayList = taskManagerFromFile.getSubTasks();
        for (SubTask subTask : subTaskArrayList) {
            taskStr.add(subTask.toString());
        }

        assertEquals(expLines, taskStr, "Пустой список задач, эпиков, подзадач из файла должен совпадать с пустым списком загруженных");
    }

    private static ArrayList<String> getStrings() {
        final ArrayList<String> expLines = new ArrayList<>();
        expLines.add(0, "id,type,name,status,description,epic,duration,startTime,endTime");
        expLines.add(1, "1,TASK,Задача номер 1,IN_PROGRESS,Вызвать мастера,null,PT30M,22.01.2025 09:00,22.01.2025 09:30");
        expLines.add(2, "3,TASK,Задача номер 3,NEW,Зайти в магазин,null,PT1H30M,24.01.2025 09:00,24.01.2025 10:30");
        expLines.add(3, "4,TASK,Задача номер 4,NEW,Выбрать рюкзак,null,PT2H10M,25.01.2025 09:00,25.01.2025 11:10");
        expLines.add(4, "5,EPIC,Эпик номер 1,IN_PROGRESS,Еженедельная уборка квартиры,null,PT49H30M,26.01.2025 09:00,28.01.2025 10:30");
        expLines.add(5, "6,EPIC,Эпик номер 2,NEW,Пересадить отросток,null,PT51H10M,29.01.2025 09:00,31.01.2025 12:10");
        expLines.add(6, "7,SUBTASK,Подзадача номер 1,IN_PROGRESS,Помыть тарелки и чашки,5,PT30M,26.01.2025 09:00,26.01.2025 09:30");
        expLines.add(7, "8,SUBTASK,Подзадача номер 2,NEW,Полить цветы,5,PT1H,27.01.2025 09:00,27.01.2025 10:00");
        expLines.add(8, "9,SUBTASK,Подзадача номер 3,NEW,Подмести,5,PT1H30M,28.01.2025 09:00,28.01.2025 10:30");
        expLines.add(9, "10,SUBTASK,Подзадача номер 4,NEW,Выбрать горшок,6,PT2H10M,29.01.2025 09:00,29.01.2025 11:10");
        expLines.add(10, "11,SUBTASK,Подзадача номер 5,NEW,Купить грунт,6,PT2H40M,30.01.2025 09:00,30.01.2025 11:40");
        expLines.add(11, "12,SUBTASK,Подзадача номер 6,NEW,Посадить цвет,6,PT3H10M,31.01.2025 09:00,31.01.2025 12:10");
        return expLines;
    }


}