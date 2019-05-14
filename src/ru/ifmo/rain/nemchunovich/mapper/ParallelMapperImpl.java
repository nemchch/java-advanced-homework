package ru.ifmo.rain.nemchunovich.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threadPool = new ArrayList<>();
    private final Task task = new Task();

    public ParallelMapperImpl(int threadCount) {
        Worker sleepAndWork = new Worker(task);
        for (int i = 0; i < threadCount; i++) {
            Thread tmp = new Thread(sleepAndWork);
            threadPool.add(tmp);
            tmp.start();
        }
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<TaskItem<? super T, ? extends R>> currentTaskItems = new ArrayList<>();
        for (T part : args) {
            TaskItem<? super T, ? extends R> tmp = new TaskItem<>(part, f);
            currentTaskItems.add(tmp);
            task.newTask(tmp);
        }
        List<R> result = new ArrayList<>();
        for (TaskItem<? super T, ? extends R> tt : currentTaskItems) {
            result.add(tt.after());
        }
        return result;
    }

    @Override
    public void close() {
        threadPool.forEach(Thread::interrupt);
    }
}
