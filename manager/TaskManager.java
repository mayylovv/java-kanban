package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;


import java.util.List;

public interface TaskManager {

     List<Task> getHistory();

     void addTask(Task task);

     void addEpic(Epic epic);

     void addSubtask(Subtask subtask);

     void deleteTaskById(int id);

     void deleteEpicById(int id);

     void deleteSubtaskById(int id);

     void deleteAllTasks();

     void deleteAllEpics();

     void deleteAllSubtasks();

     Task getTaskById(int id);

     Epic getEpicById(int id);

     Subtask getSubtaskById(int id);

     List<Task> getAllTasks();

     List<Epic> getAllEpics();

     List<Subtask> getAllSubtasks();

     List<Subtask> getAllSubtasksByEpicId(int id);

     void updateTask(Task task);

     void updateSubtask(Subtask subtask);

     void updateEpic(Epic epic);

}


