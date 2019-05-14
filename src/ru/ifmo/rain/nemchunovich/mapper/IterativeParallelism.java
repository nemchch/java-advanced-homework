package ru.ifmo.rain.nemchunovich.mapper;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IterativeParallelism implements ListIP {
    private final ParallelMapper parallelMapper;

    public IterativeParallelism() {
        parallelMapper = null;
    }

    public IterativeParallelism(ParallelMapper to) {
        parallelMapper = to;
    }

    private <T> List<List<? extends T>> slice(int parts, List<? extends T> target) {
        List<List<? extends T>> parted = new ArrayList<>();
        int n = target.size();
        parts = Math.min(parts, n);
        int chunk = n / parts;
        int to = 0;
        for (int it = 0; it < parts; it += 1) {
            int from = to;
            to += chunk + ((it < n % parts) ? (1) : (0));
            parted.add(target.subList(from, to));
        }
        return parted;
    }

    private <I, O> void startAndJoin(List<Worker<I, O>> target) throws InterruptedException {
        List<Thread> threads = target.stream().map(Thread::new).collect(Collectors.toList());
        threads.forEach(Thread::start);
        try {
            for (Thread thr : threads) {
                thr.join();
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private class Worker<I, O> implements Runnable {
        private List<? extends I> data;
        private Function<List<? extends I>, O> func;
        private O result;

        Worker(Function<List<? extends I>, O> func, List<? extends I> data) {
            this.func = func;
            this.data = data;
        }

        @Override
        public void run() {
            result = func.apply(data);
        }

        O getResult() {
            return result;
        }
    }

    private <T, R> List<R> parallel(int number, List<? extends T> list, Function<List<? extends T>, R> func) throws InterruptedException {
        List<List<? extends T>> partition = slice(number, list);
        if (parallelMapper != null) {
            return parallelMapper.map(func, partition);
        }
        List<Worker<T, R>> threads = new ArrayList<>();
        for (List<? extends T> part : partition) {
            threads.add(new Worker<>(func, part));
        }
        startAndJoin(threads);
        return threads.stream().map(Worker::getResult).collect(Collectors.toList());
    }

    @Override
    public String join(int i, List<?> list) throws InterruptedException {
        StringBuilder res = new StringBuilder();
        Function<List<?>, String> toStringer = arg -> {
            StringBuilder nested = new StringBuilder();
            arg.stream().map(Object::toString).forEach(nested::append);
            return nested.toString();
        };
        parallel(i, list, toStringer).forEach(res::append);
        return res.toString();
    }

    @Override
    public <T> List<T> filter(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        List<T> res = new ArrayList<>();
        Function<List<? extends T>, List<T>> mapper = arg -> arg.stream().filter(predicate).collect(Collectors.toList());
        parallel(i, list, mapper).forEach(res::addAll);
        return res;
    }

    @Override
    public <T, U> List<U> map(int i, List<? extends T> list, Function<? super T, ? extends U> function) throws InterruptedException {
        List<U> res = new ArrayList<>();
        Function<List<? extends T>, List<U>> mapper = arg -> arg.stream().map(function).collect(Collectors.toList());
        parallel(i, list, mapper).forEach(res::addAll);
        return res;
    }

    @Override
    public <T> T maximum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        return minimum(i, list, comparator.reversed());
    }

    @Override
    public <T> T minimum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        Function<List<? extends T>, T> min = arg -> arg.stream().min(comparator).get();
        return min.apply(parallel(i, list, min));
    }

    @Override
    public <T> boolean all(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return !any(i, list, predicate.negate());
    }

    @Override
    public <T> boolean any(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        return parallel(i, list, arg -> arg.stream().anyMatch(predicate)).stream().anyMatch(Predicate.isEqual(true));
    }
}
