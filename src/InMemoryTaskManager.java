package manager;

import tasks.*;
import status.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;

    private final HistoryManager historyManager;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();   // возвр historyManager.getHistory()

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
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            int newSubtaskId = generateId();
            subtask.setId(newSubtaskId);
            subtasks.put(newSubtaskId, subtask);
            epic.addSubtaskId(newSubtaskId);
            updateStatusEpic(epic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        historyManager.remove(id);
        if (task == null) {
            System.out.println("Простая задача не найдена");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id); // remove отработает как get, дополнительно будет произведено удаление
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task value = tasks.get(id);
        historyManager.add(value);  // add in history
        return value;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic value = epics.get(id);
        historyManager.add(value);  // add in history
        return value;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask value = subtasks.get(id);
        historyManager.add(value); // add in history
        return value;
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Список задач пуст");
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Список сложных задач пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Список подзадач пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(int id) {

        Epic epic = epics.get(id);
        if (epic == null) {
            return Collections.emptyList();
        }
        List<Subtask> subtasksNew = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask newSubtask = subtasks.get(subtaskId);
            subtasksNew.add(newSubtask);
        }
        return subtasksNew;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Простая задача не найдена");
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

    private void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            }
            int countDone = 0;
            int countNew = 0;

            for (Integer subtaskId : epic.getSubtaskIds()) {
                Status status = subtasks.get(subtaskId).getStatus();
                if (Status.DONE == status) {
                    countDone++;
                } else if (Status.NEW == status) {
                    countNew++;
                } else if (Status.IN_PROGRESS == status) {
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
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    private void remove(int id) {
        historyManager.remove(id);
    }

    private int generateId() {
        return id++;
    }

}
