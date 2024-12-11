package history;

import models.Node;
import models.Task;
import utils.CopyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class InMemoryHistoryManager implements HistoryManager {

    public static class DoublyLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;

        public DoublyLinkedList() {
            head = new Node<>(null, null, null);
            tail = new Node<>(null, null, null);
            head.next = tail;
            tail.prev = head;
        }

        public T getFirst() {
            final Node<T> curHead = head;
            if (curHead == null)
                throw new NoSuchElementException();
            return head.item;
        }

        public void linkLast(Node<T> newNode) {
            if (newNode == null) {
                throw new IllegalArgumentException("Node can't be null");
            }

            final Node<T> oldPrev = tail.prev;
            oldPrev.next = newNode;
            newNode.prev = oldPrev;
            newNode.next = tail;
            tail.prev = newNode;
        }

        public T getLast() {
            if (tail == null) {
                throw new NoSuchElementException();
            }
            return tail.item;
        }


        public List<T> getTasks() {
            List<T> list = new ArrayList<>();
            for (Node<T> current = head; current != null; current = current.next) {
                var item = current.item;
                if (item != null) {
                    list.add(current.item);
                }
            }
            return list;
        }
    }

    private final Map<Long, Node<Task>> taskHistoryMap = new HashMap<>();
    private final DoublyLinkedList<Task> customLinkedList = new DoublyLinkedList<>();

    @Override
    public void add(Task task) {
        Task copy = CopyUtils.copyForHistory(task);

        Long taskId = task.getId();
        if (taskHistoryMap.containsKey(taskId)) {
            removeNode(taskHistoryMap.get(taskId));
        }
        var newNode = new Node<>(null, copy, null);
        customLinkedList.linkLast(newNode);
        taskHistoryMap.put(taskId, newNode);
    }

    @Override
    public void remove(long id) {
        var node = taskHistoryMap.remove(id);
        removeNode(node);
    }

    @Override
    public void removeAllHistory() {
        customLinkedList.head.next = customLinkedList.tail;
        customLinkedList.tail.prev = customLinkedList.head;
        taskHistoryMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (prev == null) {
            customLinkedList.head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            customLinkedList.tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.item = null;
    }

}
