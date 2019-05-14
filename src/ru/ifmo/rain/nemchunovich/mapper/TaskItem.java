package ru.ifmo.rain.nemchunovich.mapper;

import java.util.function.Function;

class TaskItem<T, R> {
    private final T arg;
    private volatile R result;
    private volatile boolean gotResult;
    private final Function<? super T, ? extends R> process;

    TaskItem(T input, Function<? super T, ? extends R> inProcess) {
        gotResult = false;
        this.arg = input;
        this.process = inProcess;
    }

    synchronized void before() {
        result = process.apply(arg);
        gotResult = true;
        notify();
    }

    synchronized R after() throws InterruptedException {
        while (!gotResult) {
            wait();
        }
        return result;
    }
}
