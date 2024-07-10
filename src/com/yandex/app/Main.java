package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import com.yandex.app.model.Status;
import com.yandex.app.service.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void printTestData(
        HashMap<Integer, Task> tasks,
        HashMap<Integer, SubTask> subTasks,
        HashMap<Integer, Epic> epics
    ){

        System.out.println("Задачи:");
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
        System.out.println("Подзадачи:");
        for (SubTask subTask : subTasks.values()) {
            System.out.println(subTask);
        }
        System.out.println("Эпики:");
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }
        System.out.println();
    }

    public static void printTestDataByCopy(
        ArrayList<Task> copyOfTasks,
        ArrayList<SubTask> copyOfSubTasks,
        ArrayList<Epic> copyOfEpics
    ){

        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, SubTask> subTasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        for (Task task : copyOfTasks) {
            tasks.put(task.getId(), task);
        }
        for (SubTask subTask : copyOfSubTasks) {
            subTasks.put(subTask.getId(), subTask);
        }
        for (Epic epic : copyOfEpics) {
            epics.put(epic.getId(), epic);
        }
        printTestData(tasks, subTasks, epics);
    }

    public static void printTestDataByList(
        ArrayList<Task> tasks,
        ArrayList<SubTask> subTasks,
        ArrayList<Epic> epics,
        ArrayList<Task> hitasks
    ){
        System.out.println("Задачи:");
        for (Task task : tasks) {
            System.out.println(task);
        }
        System.out.println("Подзадачи:");
        for (SubTask subTask : subTasks) {
            System.out.println(subTask);
        }
        System.out.println("Эпики:");
        for (Epic epic : epics) {
            System.out.println(epic);
        }
        System.out.println("История:");
        for (Task task : hitasks) {
            System.out.println(task);
        }
        System.out.println();
    }

    public static void main(String[] args) {

        System.out.println("Поехали!");
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Отремонтировать плиту", "Вызвать мастера по ремонту плиты");
        Task task2 = new Task("Помыть машину", "Заехать вечером на мойку и помыть машину");
        Task task3 = new Task("Купить продукты", "Зайти в магазин или заказать онлайн продукты на дачу");
        Task task4 = new Task("Купить новый рюкзак", "Выбрать рюезак для похода и купить его");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);

        SubTask subTask1 = new SubTask("Помыть посуду","Помыть тарелки и чашки");
        SubTask subTask2 = new SubTask("Полить цветы", "Полить цветы во всех комнатах");
        SubTask subTask3 = new SubTask("Подмести пол", "Подмести или пропылесосить пол во всех комнатах");
        SubTask subTask4 = new SubTask("Купить цветочный горшок","Выбрать цветочный горшок и купить его");
        SubTask subTask5 = new SubTask("Заполнить горшок землей", "Купить хорошего грунта и насыпать в горшок");
        SubTask subTask6 = new SubTask("Посадить цветок", "Посадить новый отросток цветка в горшок с землей");
        
        taskManager.addTask(subTask1);
        taskManager.addTask(subTask2);
        taskManager.addTask(subTask3);
        taskManager.addTask(subTask4);
        taskManager.addTask(subTask5);
        taskManager.addTask(subTask6);

        Epic epic1 = new Epic("Убраться в квартире", "Навести порядок во всех комнатах и на кухне");
        epic1.getSubTaskIds().add(subTask1.getId());
        epic1.getSubTaskIds().add(subTask2.getId());
        epic1.getSubTaskIds().add(subTask3.getId());
        taskManager.addTask(epic1);

        Epic epic2 = new Epic("Пересадить отросток цветка", "Пересадить проросший отросток цветка в горшок с землей");
        epic2.getSubTaskIds().add(subTask4.getId());
        epic2.getSubTaskIds().add(subTask5.getId());
        epic2.getSubTaskIds().add(subTask6.getId());
        taskManager.addTask(epic2);

        ArrayList<Task> lnkTasks = taskManager.getTasks();
        ArrayList<SubTask> lnkSubTasks = taskManager.getSubTasks();
        ArrayList<Epic> lnkEpics = taskManager.getEpics();
        ArrayList<Task> hiTasks = taskManager.getHistory();
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

        System.out.println("Меняем статус задачи");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println("Меняем статус подзадачи");
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(subTask1);
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

        System.out.println("Меняем описание эпика");
        epic1.setDescription("Еженедельная уборка квартиры");
        taskManager.updateTask(epic1);
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

        System.out.println("Получить подзадачи эпика с id " + epic1.getId() + ", " + epic1.getTitle());
        ArrayList<SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epic1.getId());
        for (SubTask subTask : subTasksByEpic) {
            System.out.println(subTask);
        }
        System.out.println();

        System.out.println("Получить подзадачи эпика с несуществующим id = - 1");
        subTasksByEpic = taskManager.getSubTasksByEpic(-1);
        if (subTasksByEpic != null) {
            for (SubTask subTask : subTasksByEpic) {
                System.out.println(subTask);
            }
        }
        System.out.println();

        System.out.println("Удаляем задачу с id " + task2.getId());
        taskManager.deleteTask(task2.getId());
        System.out.println("Удаляем подзадачу с id " + subTask2.getId());
        taskManager.deleteTask(subTask2.getId());
        System.out.println("Удаляем эпик с id " + epic2.getId());
        taskManager.deleteTask(epic2.getId());

        lnkTasks = taskManager.getTasks();
        lnkSubTasks = taskManager.getSubTasks();
        lnkEpics = taskManager.getEpics();
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

        System.out.println("Меняем статус всех подзадач");
        ArrayList<SubTask> subTaskList = taskManager.getSubTasks();
        for (SubTask subTask : subTaskList) {
            subTask.setStatus(Status.DONE);
            taskManager.updateTask(subTask);
        }
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

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

        lnkTasks = taskManager.getTasks();
        lnkSubTasks = taskManager.getSubTasks();
        lnkEpics = taskManager.getEpics();
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

        SubTask subTask7 = new SubTask("Помыть посуду","Помыть тарелки и чашки");
        SubTask subTask8 = new SubTask("Полить цветы", "Полить цветы во всех комнатах");
        SubTask subTask9 = new SubTask("Подмести пол", "Подмести или пропылесосить пол во всех комнатах");
        SubTask subTask10 = new SubTask("Купить цветочный горшок","Выбрать цветочный горшок и купить его");
        SubTask subTask11 = new SubTask("Заполнить горшок землей", "Купить хорошего грунта и насыпать в горшок");
        SubTask subTask12 = new SubTask("Посадить цветок", "Посадить новый отросток цветка в горшок с землей");

        taskManager.addTask(subTask7);
        taskManager.addTask(subTask8);
        taskManager.addTask(subTask9);
        taskManager.addTask(subTask10);
        taskManager.addTask(subTask11);
        taskManager.addTask(subTask12);

        Epic epic3 = new Epic("Убраться в квартире", "Навести порядок во всех комнатах и на кухне");
        epic3.getSubTaskIds().add(subTask7.getId());
        epic3.getSubTaskIds().add(subTask8.getId());
        epic3.getSubTaskIds().add(subTask9.getId());
        taskManager.addTask(epic3);

        Epic epic4 = new Epic("Пересадить отросток цветка", "Пересадить проросший отросток цветка в горшок с землей");
        epic4.getSubTaskIds().add(subTask10.getId());
        epic4.getSubTaskIds().add(subTask11.getId());
        epic4.getSubTaskIds().add(subTask12.getId());
        taskManager.addTask(epic4);

        lnkTasks = taskManager.getTasks();
        lnkSubTasks = taskManager.getSubTasks();
        lnkEpics = taskManager.getEpics();
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

        ArrayList<Task> historyTask = taskManager.getHistory();
        System.out.println(historyTask);

        System.out.println("Удаляем все эпики");
        taskManager.deleteAllEpics();

        lnkTasks = taskManager.getTasks();
        lnkSubTasks = taskManager.getSubTasks();
        lnkEpics = taskManager.getEpics();
        printTestDataByList(lnkTasks, lnkSubTasks, lnkEpics, hiTasks);

    }
}
