package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList customLinkedList = new CustomLinkedList();

    private static class CustomLinkedList {

        Map<Integer, Node<Task>> table = new HashMap<>();
        Node<Task> head;
        Node<Task> tail;

        void linkLast(Task task) { // будет добавлять задачу в конец списка
            Node<Task> newNode = new Node<>(null, tail, task);
            removeNode(table.get(task.getId()));
            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }

            tail = newNode;
            table.put(task.getId(),newNode);
        }

        void removeNode(Node<Task> node) {
            if (node != null) {
                final Node<Task> prev = node.prev;
                final Node<Task> next = node.next;
                table.remove(node.data.getId());
                if (prev == null && next == null) {
                    head = null;
                    tail = null;
                } else if (next == null) {
                    tail = prev;
                    tail.next = null;
                } else if (prev == null) {
                    head = next;
                    head.prev = null;
                } else {
                    prev.next = next;
                    next.prev = prev;
                }
            }
        }

        List<Task> getTasks() {  // будет собирать все задачи в обычный список
            List<Task> tasks = new ArrayList<>(table.size());
            Node<Task> node = head;
            while (node != null) {
                tasks.add(node.data);
                node = node.next;
            }
            return tasks;
        }

        Node<Task> getNode(int id) {
            return table.get(id);
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            customLinkedList.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        customLinkedList.removeNode(customLinkedList.getNode(id));
    }

    @Override
    public void clear() {
        customLinkedList.table.clear();
        customLinkedList.head = null;
        customLinkedList.tail = null;
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    private static class Node<T> {
        Node<T> next;
        Node<T> prev;
        T data;

        public Node(Node<T> next, Node<T> prev, T data) {
            this.next = next;
            this.prev = prev;
            this.data = data;
        }
    }
}
