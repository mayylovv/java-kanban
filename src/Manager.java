import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private static int id = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateId() {
        return id++;
    }

    public void addTask(Task task) {
        int newTaskId = generateId();
        task.setId(newTaskId);
        tasks.put(newTaskId, task);
    }

    public void addEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
    }

    public void addSubtask(Subtask subtask) {
        int newSubtaskId = generateId();
        subtask.setId(newSubtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(newSubtaskId, subtask);
            epic.setSubtaskIds(newSubtaskId);
            updateStatusEpic(epic);
        }
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Простая задача не найдена");
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            subtasks.remove(id);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Список задач пуст");
            return null;
        }
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Список сложных задач пуст");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Список подзадач пуст");
            return null;
        }
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
            }
            return subtasksNew;
        } else {
            return null;
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Простая задача не найдена");
        }
    }

    public void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().size() == 0) {
                epic.setStatus("NEW");
            } else {
                ArrayList<Subtask> subtasksNew = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                    subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
                }

                for (Subtask subtask : subtasksNew) {
                    if (subtask.getStatus().equals("DONE")) {
                        countDone++;
                    }
                    if (subtask.getStatus().equals("NEW")) {
                        countNew++;
                    }
                    if (subtask.getStatus().equals("IN_PROGRESS")) {
                        epic.setStatus("IN_PROGRESS");
                        return;
                    }
                }

                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus("DONE");
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus("NEW");
                } else {
                    epic.setStatus("IN_PROGRESS");
                }
            }
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    public void printTasks() {
        if (tasks.size() == 0) {
            System.out.println("Список простых задач пуст");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println("Task{" + "id=" + task.getId() + ", title='" + task.getTitle() +
                    "', description='" + task.getDescription() + "', status=" + task.getStatus() + "};");
        }
    }

    public void printEpics() {
        if (epics.size() == 0) {
            System.out.println("Список сложных задач пуст");
            return;
        }
        for (Epic epic : epics.values()) {
            System.out.println("Epic{" +"id=" + epic.getId() + ", title='" + epic.getTitle() +
                    "', description='" + epic.getDescription() + "', " +
                    "status=" + epic.getStatus() + ", subtasksIds=" + epic.getSubtaskIds() + "};");
        }
    }

    public void printSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Список подзадач пуст");
            return;
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask{" + "epicId=" + subtask.getEpicId() + ", id=" + subtask.getId() +
                    ", title='" + subtask.getTitle() + "', description='" + subtask.getDescription() +
                    "', status=" + subtask.getStatus() + "}");
        }
    }
}