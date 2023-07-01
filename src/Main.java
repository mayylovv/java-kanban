import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
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



        /*
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



         */
    }



    public void printTasks() {
        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

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
        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

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
        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

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
