package com.yandex.app.service;

import com.yandex.app.model.Task;

public class Node {
    private Task task;
    private Node prev;
    private Node next;

    public Node(Task task, Node prev, Node next) {
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

