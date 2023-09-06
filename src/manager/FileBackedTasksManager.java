package manager;

import TypeTask.TypeTask;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                }
                Task task = fromString(line);
                if (task instanceof Epic) {
                    addEpicIn((Epic) task);
                } else if (task instanceof Subtask) {
                    addSubtaskIn((Subtask) task);
                } else {
                    addTaskIn(task);
                }
            }

            String lineWithHistory = bufferedReader.readLine();
            for (int id : historyFromString(lineWithHistory)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }
    }

    public void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }

        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,startTime,duration,epicId\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }
    }

    public Task addTaskIn(Task task) {
        return super.addTask(task);
    }

    public Epic addEpicIn(Epic epic) {
        return super.addEpic(epic);
    }

    public Subtask addSubtaskIn(Subtask subtask) {
        return super.addSubtask(subtask);
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        super.deleteAllSubtasksByEpic(epic);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }
        for (Task task : history) {
            str.append(task.getId()).append(",");
        }
        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }
        return str.toString();
    }


    private static List<Integer> historyFromString(String value) {
        List<Integer> toReturn = new ArrayList<>();
        if (value != null) {
            String[] id = value.split(",");
            for (String number : id) {
                toReturn.add(Integer.parseInt(number));
            }
            return toReturn;
        }
        return toReturn;
    }

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private TypeTask getType(Task task) {
        if (task instanceof Epic) {
            return TypeTask.EPIC;
        } else if (task instanceof Subtask) {
            return TypeTask.SUBTASK;
        }
        return TypeTask.TASK;
    }

    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                task.getStatus().toString(), task.getDescription(), getParentEpicId(task),
                String.valueOf(task.getStartTime()), String.valueOf(task.getDuration()), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    private Task fromString(String value) {
        String[] params = value.split(",");
        Integer epicId = params[1].equals("SUBTASK") ? Integer.parseInt(params[7]) : null;

        if (params[1].equals("EPIC")) {
            Epic epic = new Epic(params[2], params[4], Instant.parse(params[5]), Long.parseLong(params[6]));
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(Status.valueOf(params[3].toUpperCase()));
            return epic;
        } else if (params[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(epicId, params[2], params[4], Instant.parse(params[5]), Long.parseLong(params[6]));
            subtask.setId(Integer.parseInt(params[0]));
            return subtask;
        } else {
            Task task = new Task(params[2], params[4], Instant.parse(params[5]), Long.parseLong(params[6]));
            task.setId(Integer.parseInt(params[0]));
            return task;
        }
    }
}
