package ru.ifmo.rain.nemchunovich.collections;

import java.util.*;

public class ArraySet<T extends Comparable<? super T>> extends AbstractSet<T> implements NavigableSet<T> {
    private final List<T> base;
    private final Comparator<? super T> comparator;

    private ArraySet(Collection<T> collection, Comparator<? super T> comp) {
        Objects.requireNonNull(collection);
        if (comp == null) {
            comparator = Comparator.naturalOrder();
        } else {
            comparator = comp;
        }
        if (collection.isEmpty()) {
            base = Collections.emptyList();
        } else {
            ArrayList<T> tmp = new ArrayList<>(collection);
            tmp.sort(comp);
            int result = 0;
            for (int i = 1; i < tmp.size(); i++) {
                if (comparator.compare(tmp.get(result), tmp.get(i)) != 0) {
                    tmp.set(++result, tmp.get(i));
                }
            }
            tmp.subList(result + 1, tmp.size()).clear();
            base = Collections.unmodifiableList(tmp);
        }
    }

    ArraySet(Collection<T> collection) {
        this(collection, null);
    }

    @Override
    public T lower(T e) {
        int pos = lowerPos(e);
        return pos == -1 ? null : base.get(pos);
    }

    private int lowerPos(T e) {
        Objects.requireNonNull(e);
        int pos = Collections.binarySearch(base, e, comparator);
        if (pos >= 0) {
            return pos - 1;
        }
        return -(pos + 1) - 1;
    }

    @Override
    public T floor(T e) {
        int ind = floorPos(e);
        return ind == -1 ? null : base.get(ind);
    }

    private int floorPos(T e) {
        Objects.requireNonNull(e);
        int ind = Collections.binarySearch(base, e, comparator);
        if (ind < 0) {
            ind = -(ind + 1);
            return ind - 1;
        } else {
            return ind;
        }
    }

    @Override
    public T ceiling(T e) {
        int ind = ceilingPos(e);
        return ind == base.size() ? null : base.get(ind);
    }

    private int ceilingPos(T e) {
        Objects.requireNonNull(e);
        int ind = Collections.binarySearch(base, e, comparator);
        if (ind < 0) {
            ind = -(ind + 1);
        }
        return ind;
    }

    @Override
    public T higher(T e) {
        int ind = higherPos(e);
        return ind == base.size() ? null : base.get(ind);
    }

    private int higherPos(T e) {
        int ind = Collections.binarySearch(base, e, comparator);
        if (ind >= 0) {
            return ind + 1;
        } else {
            ind = -(ind + 1);
            return ind;
        }
    }

    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return base.iterator();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        Comparator tmp;
        if (comparator == Comparator.naturalOrder()) {
            tmp = Comparator.reverseOrder();
        } else {
            tmp = Comparator.naturalOrder();
        }

        return new ArraySet<>(base, tmp);
    }

    @Override
    public Iterator<T> descendingIterator() {

        return new Iterator<T>() {
            private ListIterator<T> a = base.listIterator(size() - 1);

            @Override
            public boolean hasNext() {
                return a.hasPrevious();
            }

            @Override
            public T next() {
                return a.previous();
            }

        };
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        Objects.requireNonNull(fromElement);
        Objects.requireNonNull(toElement);
        int from = fromInclusive ? ceilingPos(fromElement) : higherPos(fromElement);
        int to = toInclusive ? floorPos(toElement) : lowerPos(toElement);
        if (from > to) {
            if (comparator.compare(fromElement, toElement) == 0) {
                return new ArraySet(Collections.emptyList(), comparator);
            }
        }
        return new ArraySet<>(base.subList(from, to + 1), comparator);
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        return new ArraySet<>(base.subList(0, (inclusive ? floorPos(toElement) : lowerPos(toElement)) + 1), comparator);
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        return new ArraySet<>(base.subList(inclusive ? ceilingPos(fromElement) : higherPos(fromElement), base.size()), comparator);
    }

    @Override
    public Comparator<? super T> comparator() {
        return (comparator.equals(Comparator.<T>naturalOrder())) ? null : comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public T first() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return base.get(0);
    }

    @Override
    public T last() {
        if (size() == 0) {
            throw new NoSuchElementException();
        }
        return base.get(base.size() - 1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return Collections.binarySearch(base, (T) o, comparator) >= 0;
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }
}
