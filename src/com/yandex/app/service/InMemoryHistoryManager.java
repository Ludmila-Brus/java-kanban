package com.yandex.app.service;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        private Task task;
        private Node prev;
        private Node next;

        private Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        public Task getTask() {
            return task;
        }

        public Node getPrev() {
            return prev;
        }

        public Node getNext() {
            return next;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        @Override
        public String toString() {
            Integer strPrev = null;
            Integer strNext = null;
            if (prev != null) {
                strPrev = prev.task.getId();
            }
            if (next != null) {
                strNext = next.task.getId();
            }
            return "Node{" +
                    "task=" + task +
                    ", prev=" + strPrev +
                    ", next=" + strNext +
                    '}';
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = first;
        while (node != null) {
            tasks.add(node.getTask());
            node = node.getNext();
        }
        return tasks;
    }

    private Node linkLast(Task task) {
        final Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node);
        }
        last = node;
        return node;
    }

    private void removeNode(int id) {
        final Node node = nodeMap.remove(id);
        if (node == null) {
            return;
        }
        if (node.getPrev() == null && node.getNext() == null) {
            first = null;
            last = null;
        } else if (node.getPrev() == null && node.getNext() != null) {
            first = node.getNext();
            node.getNext().setPrev(null);
        } else if (node.getPrev() != null && node.getNext() != null) {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        } else  {
            last = node.getPrev();
            node.getPrev().setNext(null);
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int id = task.getId();
        removeNode(id);
        Node node = linkLast(task);
        nodeMap.put(id, node);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(id);
        nodeMap.remove(id);
    }

}
