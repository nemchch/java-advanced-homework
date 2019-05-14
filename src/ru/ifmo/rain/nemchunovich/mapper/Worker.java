package ru.ifmo.rain.nemchunovich.mapper;

public class Worker implements Runnable {
    private final Task task;

    Worker(Task source) {
        task = source;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                task.getTask().before();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
