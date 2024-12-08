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
        private int size = 0;

        public T getFirst() {
            final Node<T> curHead = head;
            if (curHead == null)
                throw new NoSuchElementException();
            return head.item;
        }

        public void linkLast(T element) {
            if (head == null) {
                head = new Node<>(null, element, null);
                size++;
            } else if (tail == null) {
                tail = new Node<>(head, element, null);
                head.next = tail;
                size++;
            } else {
                final Node<T> oldTail = tail;
                final Node<T> newTail = new Node<>(oldTail, element, null);
                tail = newTail;
                oldTail.next = newTail;
                size++;
            }
        }

        public T getLast() {
            if (tail == null) {
                throw new NoSuchElementException();
            }
            return tail.item;
        }

        public int size() {
            return this.size;
        }

        public List<T> getTasks() {
            List<T> list = new ArrayList<>(size);
            for (Node<T> current = head; current != null; current = current.next) {
                list.add(current.item);
            }
            return list;
        }
    }

    private final DoublyLinkedList<Task> customLinkedList = new DoublyLinkedList<>();
    private final Map<Long, Node<Task>> taskHistoryMap = new HashMap<>();

    @Override
    public void add(Task task) {
        Task copy = CopyUtils.copyForHistory(task);
        customLinkedList.linkLast(copy);
        customLinkedList.size++;
        taskHistoryMap.put(task.getId(), customLinkedList.tail);
    }

    @Override
    public void remove(long id) {
        var node = taskHistoryMap.remove(id);
        removeNode(node);
        customLinkedList.size--;
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
        customLinkedList.size--;
    }

}
