public class Main {

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

        for (Task task : taskManager.tasks.values()) {
            System.out.println(task);
        }

        for (SubTask subTask : taskManager.subTasks.values()) {
            System.out.println(subTask);
        }

        for (Epic epic : taskManager.epics.values()) {
            System.out.println(epic);
        }
        System.out.println(taskManager.epics);

    }
}
