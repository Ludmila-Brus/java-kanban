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

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

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
        epic1.subTaskIds.add(subTask1.id);
        epic1.subTaskIds.add(subTask2.id);
        epic1.subTaskIds.add(subTask3.id);
        taskManager.addTask(epic1);

        Epic epic2 = new Epic("Пересадить отросток цветка", "Пересадить проросший отросток цветка в горшок с землей");
        epic2.subTaskIds.add(subTask4.id);
        epic2.subTaskIds.add(subTask5.id);
        epic2.subTaskIds.add(subTask6.id);
        taskManager.addTask(epic2);

        HashMap<Integer, Task> tasks = taskManager.getTasks();
        HashMap<Integer, SubTask> subTasks = taskManager.getSubTasks();
        HashMap<Integer, Epic> epics = taskManager.getEpics();
        printTestData(tasks, subTasks, epics);

        System.out.println("Меняем статус задачи");
        task1.status = Status.IN_PROGRESS;
        taskManager.updateTask(task1);

        System.out.println("Меняем статус подзадачи");
        subTask1.status = Status.IN_PROGRESS;
        taskManager.updateTask(subTask1);

        printTestData(tasks, subTasks, epics);

        System.out.println("Меняем описание эпика");
        epic1.description = "Еженедельная уборка квартиры";
        taskManager.updateTask(epic1);

        printTestData(tasks, subTasks, epics);

        System.out.println("Получить подзадачи эпика с id " + epic1.id + ", " + epic1.title);
        HashMap<Integer, SubTask> subTasksByEpic = taskManager.getSubTasksByEpic(epic1.id);
        for (SubTask subTask : subTasksByEpic.values()) {
            System.out.println(subTask);
        }
        System.out.println();

        System.out.println("Удаляем задачу с id " + task2.id);
        taskManager.deleteTask(task2.id);
        System.out.println("Удаляем подзадачу с id " + subTask2.id);
        taskManager.deleteTask(subTask2.id);
        System.out.println("Удаляем эпик с id " + epic2.id);
        taskManager.deleteTask(epic2.id);

        printTestData(tasks, subTasks, epics);

        System.out.println("Меняем статус всех подзадач");
        for (SubTask subTask : taskManager.subTasks.values()) {
            subTask.status = Status.DONE;
            taskManager.updateTask(subTask);
        }
        printTestData(tasks, subTasks, epics);

        System.out.println("Получить задачу по id " + task3.id);
        System.out.println(taskManager.getTask(task3.id));
        System.out.println("Получить подзадачу по id " + subTask3.id);
        System.out.println(taskManager.getSubTask(subTask3.id));
        System.out.println("Получить эпик по id " + epic1.id);
        System.out.println(taskManager.getEpic(epic1.id));
        System.out.println();

        System.out.println("Удаляем все задачи");
        taskManager.deleteAllTasks();
        System.out.println("Удаляем все подзадачи");
        taskManager.deleteAllSubTasks();
        System.out.println("Удаляем все эпики");
        taskManager.deleteAllEpics();

        printTestData(tasks, subTasks, epics);
    }
}
