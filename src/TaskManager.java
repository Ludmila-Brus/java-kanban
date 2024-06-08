import java.util.HashMap;

public class TaskManager {

    int nextId = 1;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public void addTask(Task task) {
        task.id = nextId;
        nextId++;
        tasks.put(task.id, task);

    }

    public void addTask(SubTask subTask) {
        subTask.id = nextId;
        nextId++;
        subTasks.put(subTask.id, subTask);

    }
    
    public void addTask(Epic epic) {
        epic.id = nextId;
        nextId++;
        epics.put(epic.id, epic);
        syncEpic(epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }

    public void updateTask(SubTask subTask) {
        subTasks.put(subTask.id, subTask);
        syncEpic(epics.get(subTask.epicId));
    }

    public void updateTask(Epic epic) {
        epics.put(epic.id, epic);
        syncEpic(epic);
    }

    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) {
           int epicId = subTasks.get(id).epicId;
           Epic epic = epics.get(epicId);
           subTasks.remove(id);
           syncEpic(epic);
        } else if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (Integer subTaskId : epic.subTaskIds) {
                SubTask subTask = subTasks.get(subTaskId);
                subTask.epicId = 0;
            }
            epics.remove(id);
        } else {
            System.out.println("Задача с id = " + id + "не найдена");
        }
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpic() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasksByEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            if (!epic.subTaskIds.isEmpty()) {
                HashMap<Integer, SubTask> subTasksByEpic = new HashMap<>();
                for (Integer subTaskId : epic.subTaskIds) {
                    SubTask subTask = subTasks.get(subTaskId);
                    subTasksByEpic.put(subTask.id, subTask);
                }
                return subTasksByEpic;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public void deleteAllTasks(){
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст"); 
        } else {
            tasks.clear();
        }
    }
    
    public void deleteAllSubTasks(){
        if (subTasks.isEmpty()) {
            System.out.println("Список подзадач пуст");
        } else {
            for (SubTask subTask : subTasks.values()) {
                if (epics.containsKey(subTask.epicId)) {
                    Epic epic = epics.get(subTask.epicId);
                    epic.subTaskIds.clear();
                    syncEpic(epic);
                }
            }
            subTasks.clear();
        }
    }

    public void deleteAllEpics(){
        if (epics.isEmpty()) {
            System.out.println("Список епиков пуст");
        } else {
            for (Epic epic : epics.values()) {
                for (Integer subTaskId : epic.subTaskIds) {
                    if (subTasks.containsKey(subTaskId)) {
                        SubTask subTask = subTasks.get(subTaskId);
                        subTask.epicId = 0;
                    }
                }
            }
            epics.clear();
        }
    }

    // всем подзадачам эпика присвоим id эпика и
    // определим статус эпика
    private void syncEpic(Epic epic) {
        Status epicStatus = null;
        for (Integer subTaskId : epic.subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            subTask.epicId = epic.id;
            if (subTask.status == Status.NEW) {
                if (epicStatus != Status.NEW && epicStatus != null) {
                    epicStatus = Status.IN_PROGRESS;
                } else {
                    epicStatus = Status.NEW;
                }
            } else if (subTask.status == Status.DONE) {
                if (epicStatus != Status.DONE && epicStatus != null) {
                    epicStatus = Status.IN_PROGRESS;
                } else {
                    epicStatus = Status.DONE;
                }
            } else if (subTask.status == Status.IN_PROGRESS) {
                epicStatus = Status.IN_PROGRESS;
            }
        }
        epic.status = (epicStatus != null) ? epicStatus : Status.NEW;
    }

}
