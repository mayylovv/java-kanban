package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson json = new Gson();

    public HttpTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        this.client = new KVTaskClient(path);
        load();
    }

    @Override
    public void save() {
        client.put("task", json.toJson(super.getAllTasks()));
        client.put("epic", json.toJson(super.getAllEpics()));
        client.put("subtask", json.toJson(super.getAllSubtasks()));
        client.put("history", json.toJson(super.getHistory()));
    }

    public void load() {
        try {
            int maxId = -1;
            JsonArray loadedArray = null;
            String taskResult = client.load("task");

            if (taskResult != null) {
                loadedArray = JsonParser.parseString(taskResult).getAsJsonArray();
            }

            if (loadedArray != null) {
                for (JsonElement jsonTask : loadedArray) {
                    Task loadedTask = json.fromJson(jsonTask, Task.class);
                    int id = loadedTask.getId();
                    maxId = Math.max(maxId, id);
                    super.tasks.put(id, loadedTask);
                    super.prioritizedTasksSet.add(loadedTask);
                }
            }

            if (client.load("epic") != null) {
                loadedArray = JsonParser.parseString(client.load("epic")).getAsJsonArray();
            }

            if (loadedArray != null) {
                for (JsonElement jsonTask : loadedArray) {
                    Epic loadedEpic = json.fromJson(jsonTask, Epic.class);
                    int id = loadedEpic.getId();
                    maxId = Math.max(maxId, id);
                    super.epics.put(id, loadedEpic);
                }
            }

            if (client.load("subtask") != null) {
                loadedArray = JsonParser.parseString(client.load("subtask")).getAsJsonArray();
            }

            if (loadedArray != null) {
                for (JsonElement jsonTask : loadedArray) {
                    Subtask loadedSubTask = json.fromJson(jsonTask, Subtask.class);
                    int id = loadedSubTask.getId();
                    maxId = Math.max(maxId, id);
                    super.subtasks.put(id, loadedSubTask);
                    super.prioritizedTasksSet.add(loadedSubTask);
                }
            }

            if (client.load("history") != null) {
                loadedArray = JsonParser.parseString(client.load("history")).getAsJsonArray();
            }

            if (loadedArray != null) {
                taskId = ++maxId;


                for (JsonElement jsonTaskId : loadedArray) {
                    if (jsonTaskId == null) {
                        break;
                    }

                    int loadedId = jsonTaskId.getAsInt();

                    if (epics.containsKey(loadedId)) {
                        getEpicById(loadedId);
                    } else if (tasks.containsKey(loadedId)) {
                        getTaskById(loadedId);
                    } else if (subtasks.containsKey(loadedId)) {
                        getSubtaskById(loadedId);
                    }
                }
            }
        } catch (UnsupportedOperationException e) {
            System.out.println(" ");
        }
//        catch (IOException e) {
//            System.out.println("");
//        }
    }
}

