package manager;

import tasks.*;
import status.Status;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public int taskId = 0;

    protected final HistoryManager historyManager;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    //    private final Comparator<Task> comparatorT = Comparator.comparing(Task::getStartTime);
    protected TreeSet<Task> prioritizedTasksSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void addToHistory(int id) {
        assert historyManager != null;
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();   // возвр historyManager.getHistory()
    }

    @Override
    public Task addTask(Task task) {
        if (task != null) {
            task.setId(generateId());
            try {
                validate(task);
            } catch (ManagerValidateException e) {
                throw new ManagerValidateException("Задачи пересекаются");
            }
            tasks.put(task.getId(), task);
            prioritizedTasksSet.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (epic == null) return null;
        int newEpicId = generateId();
        epic.setId(newEpicId);
        updateTimeEpic(epic);
        epics.put(newEpicId, epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (subtask == null) return null;
        int newId = generateId();
        subtask.setId(newId);
        try {
            validate(subtask);
        } catch (ManagerValidateException e) {
            throw new ManagerValidateException("Задачи пересекаются");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(newId, subtask);
            epic.addSubtaskId(newId);
            updateStatusEpic(epic);
            updateTimeEpic(epic);
            prioritizedTasksSet.add(subtask);
            return subtask;
        } else {
            System.out.println("Сложная задача для подзадачи не найдена");
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)){
            prioritizedTasksSet.removeIf(task -> task.getId() == id);
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Простая задача не найдена");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtaskId -> {
                prioritizedTasksSet.removeIf(task -> Objects.equals(task.getId(), subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id); // remove отработает как get, дополнительно будет произведено удаление
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            updateTimeEpic(epic);
            prioritizedTasksSet.remove(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void deleteAllTasks() {
        assert historyManager != null;
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
        prioritizedTasksSet.clear();
    }

    @Override
    public void deleteAllEpics() {
        assert historyManager != null;
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
        assert historyManager != null;
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask subtask = subtasks.get(subtaskId);
            prioritizedTasksSet.remove(subtask);
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasksSet.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubtaskIds().clear();
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task value = tasks.get(id);
        if (value != null) {
            historyManager.add(value);  // add in history
        }
        return value;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic value = epics.get(id);
        if (value != null) {
            historyManager.add(value);  // add in history
        }
        return value;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask value = subtasks.get(id);
        if (value != null) {
            historyManager.add(value);  // add in history
        }
        return value;
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
        if (task != null && tasks.containsKey(task.getId())) {
            try {
                validate(task);
            } catch (ManagerValidateException e) {
                return;
            }
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Простая задача не найдена");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epics.containsKey(epic.getId())) {
                try {
                    validate(subtask);
                } catch (ManagerValidateException e) {
                    return;
                }
                subtasks.put(subtask.getId(), subtask);
                updateStatusEpic(epic);
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusEpic(epic);
            updateTimeEpic(epic);
        } else {
            System.out.println("Сложная задача не найдена");
        }
    }

    @Override
    public void validate(Task newTask) throws ManagerValidateException {
        Instant startTimeInput = newTask.getStartTime();
        Instant endTimeInput = newTask.getEndTime();
        if (prioritizedTasksSet.isEmpty()) {
            return;
        }
        for (Task existingTask: prioritizedTasksSet) {
            if (existingTask.equals(newTask)) {
                break;
            }
            if ((startTimeInput.isBefore(existingTask.getStartTime()) && endTimeInput.isAfter(existingTask.getStartTime()))
                    || (startTimeInput.isBefore(existingTask.getEndTime()) && endTimeInput.isAfter(existingTask.getEndTime()))
                    || (startTimeInput.isAfter(existingTask.getStartTime()) && endTimeInput.isBefore(existingTask.getEndTime()))
                    || startTimeInput.equals(existingTask.getStartTime()) || endTimeInput.equals(existingTask.getEndTime())) {
                throw new ManagerValidateException("Задачи пересекаются");
            }
        }
    }

    @Override
    public List<Task> getPrioritizedTasksSet() {
        return new ArrayList<>(prioritizedTasksSet);
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

    public int generateId() {
        return taskId++;
    }

    private void updateTimeEpic(Epic epic) {
        Instant startEpic = Instant.now();
        Instant endEpic = Instant.MIN;
        long durationEpic = 0;
        ArrayList<Integer> subtasksId = (ArrayList<Integer>) epic.getSubtaskIds();
        if (subtasksId.isEmpty()) {
            epic.setDuration(0);
            epic.setStartTime(startEpic);
            epic.setEndTime(endEpic);
            return;
        }
        for (int i : subtasksId) {
            Subtask subtask = subtasks.get(i);
            Instant startTime = subtask.getStartTime();
            Instant endTime = subtask.getEndTime();
            if (startTime.isBefore(startEpic)) {
                startEpic = startTime;
            }
            if (endEpic.isAfter(endTime)) {
                endEpic = endTime;
            }
            durationEpic = durationEpic + subtask.getDuration();
        }
        epic.setStartTime(startEpic);
        epic.setEndTime(endEpic);
        epic.setDuration(durationEpic);
    }
}
