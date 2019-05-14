package ru.ifmo.rain.nemchunovich.mapper;

import java.util.LinkedList;
import java.util.Queue;

class Task {
    private final Queue<TaskItem<?, ?>> taskItems;

    Task() {
        taskItems = new LinkedList<>();
    }

    synchronized void newTask(TaskItem<?, ?> input) {
        taskItems.add(input);
        notify();
    }

    synchronized TaskItem<?, ?> getTask() throws InterruptedException {
        while (taskItems.isEmpty()) {
            wait();
        }
        return taskItems.poll();
    }
}
