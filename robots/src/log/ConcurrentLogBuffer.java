package log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLogBuffer<E> implements Iterable<E> {
    private final E[] buffer;
    private final int capacity;
    private int head = 0;
    private int tail = 0;
    private int size = 0;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    public ConcurrentLogBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = (E[]) new Object[capacity];
    }

    public void add(E element) {
        lock.writeLock().lock();
        try {
            buffer[tail] = element;
            if (size == capacity) {
                head = (head + 1) % capacity;
            } else {
                size++;
            }
            tail = (tail + 1) % capacity;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        lock.readLock().lock();
        try {
            return buffer[(head + index) % capacity];
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<E> getSegment(int from, int to) {
        if (from < 0 || to > size || from > to) {
            throw new IndexOutOfBoundsException();
        }

        lock.readLock().lock();
        try {
            List<E> segment = new ArrayList<>(to - from);
            int currentHead = head;
            for (int i = from; i < to; i++) {
                segment.add(buffer[(currentHead + i) % capacity]);
            }
            return segment;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        lock.readLock().lock();
        ArrayList<E> snapshot;
        try {
            snapshot = new ArrayList<>(size);
            int currentHead = head;
            for (int i = 0; i < size; i++) {
                snapshot.add(buffer[(currentHead + i) % capacity]);
            }
        } finally {
            lock.readLock().unlock();
        }
        return snapshot.iterator();
    }
}
