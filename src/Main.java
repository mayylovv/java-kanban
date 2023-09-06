import server.InstantAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.KVServer;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws IOException {

        print(null);
        print(1);
        print("string");
        print(0.2d);
        print(false);
        print('A');
    }

    static void print(int i) {
        System.out.println("i." + i);
    }

    static void print(String s) {
        System.out.println("s." + s);
    }

    static void print(boolean b) {
        System.out.println("b." + b);
    }

    static void print(Object o) {
        System.out.println("o." + o);
    }
}



        // спринт 8
       /*
        KVServer server;
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task firstTask = new Task("Отдохнуть на выходных", "Поехать в Сергиев Посад", Instant.now(), 1);
            httpTaskManager.addTask(firstTask);

            Epic firstEpic = new Epic("Защитить диплом", "Написать и защитить бакалаврскую работу", Instant.now(), 2);
            httpTaskManager.addEpic(firstEpic);


            Subtask firstSubtask = new Subtask(firstEpic.getId(), "Написать работу", "Написать бакалаврскую работу", Instant.now(), 3);
            httpTaskManager.addSubtask(firstSubtask);


            httpTaskManager.getTaskById(firstTask.getId());
            httpTaskManager.getEpicById(firstEpic.getId());
            httpTaskManager.getSubtaskById(firstSubtask.getId());

            System.out.println("Все задачи");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("Все эпики");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("Все подзадачи");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));
            System.out.println("Загруженный менеджер");
            System.out.println(httpTaskManager);
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        */

/*
        Path path = Path.of("data.csv");
        File file = new File(String.valueOf(path));
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);

        Task firstTask = new Task("Отдохнуть на выходных", "Поехать в Сергиев Посад");
        fileBackedTasksManager.addTask(firstTask); // id = 0
        Task secondTask = new Task("Оплатить штраф", "Оплатить штраф через госуслуги");
        fileBackedTasksManager.addTask(secondTask); // id = 1


        Epic firstEpic = new Epic("Защитить диплом", "Написать и защитить бакалаврскую работу");
        fileBackedTasksManager.addEpic(firstEpic); // id = 2

        Subtask firstSubtask = new Subtask(2, "Написать работу", "Написать бакалаврскую работу");
        fileBackedTasksManager.addSubtask(firstSubtask);  // id = 3

        fileBackedTasksManager.getTaskById(firstTask.getId());
        fileBackedTasksManager.getTaskById(secondTask.getId());
        System.out.println();

        System.out.println("--- Считывание из файла ---");
        Path path2 = Path.of("data.csv");
        File file2 = new File(String.valueOf(path));
        fileBackedTasksManager.loadFromFile(file2);
        System.out.println("Задачи");
        System.out.println(fileBackedTasksManager.getAllTasks());
        System.out.println("Эпики");
        System.out.println(fileBackedTasksManager.getAllEpics());
        System.out.println("Подзадачи");
        System.out.println(fileBackedTasksManager.getAllSubtasks());
        System.out.println("История");
        System.out.println(fileBackedTasksManager.getHistory());


        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

        System.out.println("Добавляем задачи:");
        taskManager.addTask(new Task("task1","описание_task1")); // id = 0
        taskManager.addTask(new Task("task2", "описание_task1")); // id = 1
        taskManager.addEpic(new Epic("epic1","описание_epic1")); // id = 2
        taskManager.addEpic(new Epic("epic2","описание_epic2")); // id = 3
        taskManager.addSubtask(new Subtask(3,"subtask1", "описание_subtask1")); // id = 4
        taskManager.addSubtask(new Subtask(3,"subtask2", "описание_subtask2")); // id = 5
        taskManager.addSubtask(new Subtask(3,"subtask3", "описание_subtask3")); // id = 6

        System.out.println("Получим задачу по id:");
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getTaskById(3);
        taskManager.getSubtaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(6);

        System.out.println("Получим историю:");
        List<Task> history = taskManager.getHistory();
        System.out.println(history);

        System.out.println("Удаление из истории:");
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);

        List<Task> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);




    }



    public void printTasks() {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        if (taskManager.getAllTasks().isEmpty()) {
            System.out.println("Список простых задач пуст");
            return;
        }
        for (Task task : taskManager.getAllTasks()) {
            System.out.println("Task{" + "id=" + task.getId() + ", title='" + task.getTitle() +
                    "', description='" + task.getDescription() + "', status=" + task.getStatus() + "};");
        }
    }


    public void printEpics() {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        if (taskManager.getAllEpics().isEmpty()) {
            System.out.println("Список сложных задач пуст");
            return;
        }
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println("Epic{" +"id=" + epic.getId() + ", title='" + epic.getTitle() +
                    "', description='" + epic.getDescription() + "', " +
                    "status=" + epic.getStatus() + ", subtasksIds=" + epic.getSubtaskIds() + "};");
        }
    }


    public void printSubtasks() {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        if (taskManager.getAllSubtasks().isEmpty()) {
            System.out.println("Список подзадач пуст");
            return;
        }
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println("Subtask{" + "epicId=" + subtask.getEpicId() + ", id=" + subtask.getId() +
                    ", title='" + subtask.getTitle() + "', description='" + subtask.getDescription() +
                    "', status=" + subtask.getStatus() + "}");
        }

    }
}

 */
