package test.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import log.ConcurrentLogBuffer;

class ConcurrentLogBufferTest {
    private ConcurrentLogBuffer<String> buffer;
    private final int CAPACITY = 5;

    @BeforeEach
    void setUp() {
        buffer = new ConcurrentLogBuffer<>(CAPACITY);
    }

    @Test
    void shouldRewriteEntriesCycling() {
        for (int i = 0; i < CAPACITY; i++) {
            buffer.add("Message " + i);
        }
        assertEquals(CAPACITY, buffer.size());

        buffer.add("Test log message");

        assertEquals(CAPACITY, buffer.size());

        assertNotEquals("Message 0", buffer.get(0));
        assertEquals("Test log message", buffer.get(CAPACITY - 1));
    }

    @Test
    void shouldHandleConcurrentWrites() {
        final int THREADS = 10;
        final int ITERATIONS = 100;
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < THREADS; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < ITERATIONS; j++) {
                        buffer.add(Thread.currentThread().getName() + "-" + j);
                    }
                }));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    fail("Concurrent writes test failed: " + e.getMessage());
                }
            }

            assertTrue(buffer.size() <= CAPACITY);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldReturnCorrectSegment() {
        for (int i = 0; i < CAPACITY; i++) {
            buffer.add("Msg " + i);
        }

        List<String> segment = buffer.getSegment(1, 4);
        assertEquals(3, segment.size());
        assertEquals("Msg 1", segment.get(0));
        assertEquals("Msg 3", segment.get(2));
    }

    @Test
    void shouldProvideThreadSafeIterator() throws InterruptedException {
        for (int i = 0; i < CAPACITY; i++) {
            buffer.add("Iter " + i);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try {
                Thread.sleep(100);
                buffer.add("New during iteration");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        int count = 0;
        for (String s : buffer) {
            assertNotNull(s);
            count++;
            Thread.sleep(50);
        }

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            fail("Concurrent test failed: " + e.getMessage());
        }
        executor.shutdown();

        assertEquals(CAPACITY, count);
    }

    @Test
    void shouldThrowIndexOutOfBoundsForInvalidSegment() {
        buffer.add("A");
        buffer.add("B");

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getSegment(-1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getSegment(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getSegment(2, 1));
    }
}
