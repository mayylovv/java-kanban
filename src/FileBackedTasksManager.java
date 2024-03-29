package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import typeTask.TypeTask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;
    private HistoryManager historyManager;

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }


    public FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(historyManager, file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                if (line.isEmpty()){
                    return fileBackedTasksManager;
                }
            }
            String[] lines = line.split(System.lineSeparator());
            int i = 0;
            while (!lines[i].isEmpty()) {
                Task task = fileBackedTasksManager.fromString(lines[i]);
                ++i;
                fileBackedTasksManager.addTask(task);
                if (lines.length == i) {
                    break;
                }
            }
            if (i == lines.length) {
                return fileBackedTasksManager;
            } else {
                List<Integer> idTasks = historyFromString(lines[lines.length-1]);
                for (Integer idTask : idTasks) {
                    if (fileBackedTasksManager.getEpicById(idTask) != null) {
                        fileBackedTasksManager.getHistoryManager().add(fileBackedTasksManager.getEpicById(idTask));
                    }
                    if (fileBackedTasksManager.getTaskById(idTask) != null) {
                        fileBackedTasksManager.getHistoryManager().add(fileBackedTasksManager.getTaskById(idTask));
                    }
                    if (fileBackedTasksManager.getSubtaskById(idTask) != null) {
                        fileBackedTasksManager.getHistoryManager().add(fileBackedTasksManager.getSubtaskById(idTask));
                    }
                    return fileBackedTasksManager;
                }
            }
            return fileBackedTasksManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }


    }

    /* public void loadFromFile(File file) {
        boolean isContainsHistory = false;
        int taskCount = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] lines = line.split(System.lineSeparator());
                if (!lines.isEmpty()) {
                    List<Task> tasks = new ArrayList<>();
                    for (int i = 1; i < lines.length; i++) {
                        tasks.add(fromString(lines[i]));
                    }
                    for (Task task : tasks) {
                        if (getType(task) == TypeTask.EPIC) {
                            addEpic((Epic) task);
                            taskCount++;
                        }
                        if (getType(task) == TypeTask.SUBTASK) {
                            addSubtask((Subtask) task);
                            taskCount++;
                        }
                        if (getType(task) == TypeTask.TASK) {
                            addTask(task);
                            taskCount++;
                        }
                    }

                } else {
                    for (int id : historyFromString(line)) {
                        addToHistory(id);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла.");
        }

    }
    */

    @Override
    public void addTask (Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic (Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
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

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();
        if (!history.isEmpty()) {
            for (Task task : history) {
                str.append(task.getId()).append(",");
            }
            if (str.length() != 0) {
                str.deleteCharAt(str.length() - 1);
            }
            return str.toString();
        }
        return "";
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] lines = value.split(",");
            for (String line : lines) {
                history.add(Integer.parseInt(line));
            }
            return history;
        }
        return history;
    }

    private void save() {
        try {
            if (Files.exists(file.toPath())) {
                Files.delete(file.toPath());
            }
            Files.createFile(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось найти файл для записи данных");
        }
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");

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

    private TypeTask getType(Task task) {
        if (task instanceof Epic) {
            return TypeTask.EPIC;
        } else if (task instanceof Subtask) {
            return TypeTask.SUBTASK;
        }
        return TypeTask.TASK;
    }

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                task.getStatus().toString(), task.getDescription(), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    private Task fromString(String value) {
        String[] params = value.split(",");
        if (params[1].equals("TASK")) {
            Task task = new Task(params[2], params[4]);
            task.setId(Integer.parseInt(params[0]));
            task.setStatus(Status.valueOf(params[3]));
            return task;
        } else if (params[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(Integer.parseInt(params[5]), params[2], params[4]);
            subtask.setId(Integer.parseInt(params[0]));
            subtask.setStatus(Status.valueOf(params[3]));
            return subtask;
        } else {
            Epic epic = new Epic(params[2], params[4]);
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(Status.valueOf(params[3].toUpperCase()));
            return epic;
        }
    }


}
