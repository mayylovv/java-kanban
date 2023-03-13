package manager;

import tasks.*;
import status.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 0;

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateId() {
        return id++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();

    }

    @Override
    public void addTask(Task task) {
        int newTaskId = generateId();
        task.setId(newTaskId);
        tasks.put(newTaskId, task);
    }

    @Override
    public void addEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        int newSubtaskId = generateId();
        subtask.setId(newSubtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(newSubtaskId, subtask);
            epic.addSubtaskId(newSubtaskId);
            updateStatusEpic(epic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Простая задача не найдена");
        }
    }

    @Override
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

    @Override
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

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));  // add in history
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));  // add in history
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id)); // add in history
        return subtasks.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Список задач пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Список сложных задач пуст");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Список подзадач пуст");
            return null;
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                Subtask constant = subtasks.get(epic.getSubtaskIds().get(i));
                subtasksNew.add(constant);
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Простая задача не найдена");
        }
    }

    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                ArrayList<Subtask> allSubtasks = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                    allSubtasks.add(subtasks.get(epic.getSubtaskIds().get(i)));
                }

                for (Subtask subtask : allSubtasks) {
                    if (Status.DONE.equals(subtask.getStatus())) {
                        countDone++;
                    }
                    if (Status.NEW.equals(subtask.getStatus())) {
                        countNew++;
                    }
                    if (Status.IN_PROGRESS.equals(subtask.getStatus())) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }

                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epics.containsKey(epic.getId())) {
                subtasks.put(subtask.getId(), subtask);
                updateStatusEpic(epic);
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusEpic(epic);
        } else {
            System.out.println("Сложная задача не найдена");
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
