import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Задача 1", "Купить хлеб", "NEW");
        manager.addTask(task1);
        Task task2 = new Task("Задача2", "Продать машину", "NEW");
        manager.addTask(task2);
        Epic epic1 = new Epic("Эпик1", "Найти работу", "NEW");
        manager.addEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Закончить университет", "NEW");
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask(2, "Написать резюме", "", "NEW");
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask(2, "Подобрать вакансию",
                "Найти подходящую вакансию", "NEW");
        manager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask(3, "Сдать экзамены",
                "Выучить термех и сопромат", "NEW");
        manager.addSubtask(subtask3);
        Subtask subtask4 = new Subtask(3, "Защитить диплом",
                "Написать дипломную работу и выступить с ней", "NEW");
        manager.addSubtask(subtask4);

        ArrayList<Task> taskList = manager.getAllTasks();
        ArrayList<Epic> epicList = manager.getAllEpics();
        ArrayList<Subtask> subtaskList = manager.getAllSubtasks();

        System.out.println("Все простые задачи:");
        System.out.println(taskList);
        System.out.println("Все сложные задачи:");
        System.out.println(epicList);
        System.out.println("Все подзадачи:");
        System.out.println(subtaskList);
        System.out.println();

        System.out.println("Получение по идентификатору простой задачи:");
        Task task = manager.getTaskById(1);
        System.out.println(task);
        System.out.println("Получение по идентификатору сложной задачи:");
        Epic epic = manager.getEpicById(3);
        System.out.println(epic);
        System.out.println("Получение по идентификатору подзадачи:");
        Subtask subtask = manager.getSubtaskById(5);
        System.out.println(subtask);
        System.out.println();

        System.out.println("Меняем статус простой задачи");
        task.setStatus("IN_PROGRESS");
        manager.updateTask(task);
        System.out.println(task);
        System.out.println("Меняем статус подзадачи");
        subtask.setStatus("IN_PROGRESS");
        manager.updateSubtask(subtask);
        System.out.println(subtask);
        System.out.println("При этом изменился статус сложной задачи:");
        manager.printEpics();
        System.out.println();

        System.out.println("Получение подзадач по идентификатору сложной задачи:");
        ArrayList<Subtask> subtasksByEpicId = manager.getAllSubtasksByEpicId(3);
        System.out.println(subtasksByEpicId);

        // алгоритмы удаления
        System.out.println("Удаление задачи по id:");
        manager.deleteTaskById(1);
        System.out.println(taskList);
        System.out.println("Удалить все задачи:");
        manager.deleteAllTasks();
        manager.printTasks();
        System.out.println("Удалить подзадачу по id:");
        manager.deleteSubtaskById(5);
        manager.printSubtasks();
        System.out.println("Удалить все подзадачи:");
        manager.deleteAllSubtasks();
        manager.printSubtasks();
        System.out.println("Удалить эпик по id:");
        manager.deleteEpicById(2);
        manager.printEpics();
        System.out.println("Удалить все эпики:");
        manager.deleteAllEpics();
        manager.printEpics();
    }
}
