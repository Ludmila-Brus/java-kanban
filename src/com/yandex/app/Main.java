package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import com.yandex.app.model.Status;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void printTestDataByList(TaskManager taskManager) {
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

        System.out.println("Поехали вперед!");
        TaskManager taskManager = Managers.getDefault();

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
        printTestDataByList(taskManager);

        System.out.println("Меняем статус задачи");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println("Меняем статус подзадачи");
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        printTestDataByList(taskManager);

        System.out.println("Меняем описание эпика");
        epic1.setDescription("Еженедельная уборка квартиры");
        taskManager.updateEpic(epic1);
        printTestDataByList(taskManager);

        System.out.println("Получить подзадачи эпика с id " + epic1.getId() + ", " + epic1.getTitle());
        ArrayList<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epic1.getId());
        for (SubTask subTask : subTasksByEpic) {
            System.out.println(subTask);
        }
        System.out.println();

        System.out.println("Получить подзадачи эпика с несуществующим id = - 1");
        subTasksByEpic = taskManager.getSubTasksByEpic(-1);
        if (!subTasksByEpic.isEmpty()) {
            for (SubTask subTask : subTasksByEpic) {
                System.out.println(subTask);
            }
        } else {
            System.out.println("Список подзадач пуст");
        }
        System.out.println();

        System.out.println("Удаляем задачу с id " + task2.getId());
        taskManager.deleteTask(task2.getId());
        System.out.println("Удаляем подзадачу с id " + subTask2.getId());
        taskManager.deleteSubTask(subTask2.getId());
        System.out.println("Удаляем эпик с id " + epic2.getId());
        taskManager.deleteEpic(epic2.getId());
        printTestDataByList(taskManager);

        System.out.println("Меняем статус всех подзадач");
        ArrayList<SubTask> subTaskList = taskManager.getSubTasks();
        for (SubTask subTask : subTaskList) {
            subTask.setStatus(Status.DONE);
            taskManager.updateSubTask(subTask);
        }
        printTestDataByList(taskManager);

        System.out.println("Получить задачу по id " + task3.getId());
        System.out.println(taskManager.getTask(task3.getId()));
        System.out.println("Получить подзадачу по id " + subTask3.getId());
        System.out.println(taskManager.getSubTask(subTask3.getId()));
        System.out.println("Получить эпик по id " + epic1.getId());
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println();

        System.out.println("Удаляем все задачи");
        taskManager.deleteAllTasks();
        System.out.println("Удаляем все подзадачи");
        taskManager.deleteAllSubTasks();
        printTestDataByList(taskManager);

        Epic epic3 = new Epic("Эпик номер 3", "Навести порядок");
        int epic3Id = taskManager.addEpic(epic3);
        Epic epic4 = new Epic("Эпик номер 4", "Пересадить отросток");
        int epic4Id = taskManager.addEpic(epic4);
        printTestDataByList(taskManager);

        SubTask subTask7 = new SubTask("Помыть посуду", "Помыть тарелки", epic3Id);
        SubTask subTask8 = new SubTask("Полить цветы", "Полить всех", epic3Id);
        SubTask subTask9 = new SubTask("Подмести пол", "Подмести всех", epic3Id);
        SubTask subTask10 = new SubTask("Купить горшок", "Выбрать горшок", epic4Id);
        SubTask subTask11 = new SubTask("Заполнить землей", "Купить грунт", epic4Id);
        SubTask subTask12 = new SubTask("Посадить цветок", "Посадить отросток", epic4Id);
        taskManager.addSubTask(subTask7);
        taskManager.addSubTask(subTask8);
        taskManager.addSubTask(subTask9);
        taskManager.addSubTask(subTask10);
        taskManager.addSubTask(subTask11);
        taskManager.addSubTask(subTask12);
        printTestDataByList(taskManager);

        System.out.println("Удаляем все эпики");
        taskManager.deleteAllEpics();
        printTestDataByList(taskManager);

    }
}
