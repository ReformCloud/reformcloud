package systems.reformcloud.reformcloud2.executor.api.task.defaults;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class DefaultTaskTest {

    @Test
    void testTaskSupply() {
        Task<Boolean> task = Task.supply(() -> Boolean.TRUE);

        Boolean b = task.getUninterruptedly();
        Assertions.assertNotNull(b);
        Assertions.assertTrue(b);
    }

    @Test
    void testCompletedTask() {
        Task<Boolean> task = Task.completedTask(true);

        Boolean b = task.getUninterruptedly();
        Assertions.assertNotNull(b);
        Assertions.assertTrue(b);
    }

    @Test
    void testGetUninterruptedly() {
        Task<Boolean> task = new DefaultTask<>();

        long start = System.currentTimeMillis();
        Boolean result = task.getUninterruptedly(TimeUnit.SECONDS, 3);
        long end = System.currentTimeMillis();

        Assertions.assertTrue(this.between(end, start + 2900, start + 3100));
        Assertions.assertNull(result);
    }

    @Test
    @Timeout(5)
    void testFutureListenerSuccess() {
        Task<Boolean> task = new DefaultTask<>();
        task.onComplete(result -> {
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result);
        });

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                task.complete(true);
            } catch (InterruptedException exception) {
                Assertions.fail();
            }
        }).start();

        task.awaitUninterruptedly();

        Assertions.assertTrue(task.isDone());
        Assertions.assertFalse(task.isCompletedExceptionally());
    }

    @Test
    @Timeout(5)
    void testFutureListenerFailure() {
        Task<Boolean> task = new DefaultTask<>();
        task.onFailure(Assertions::assertNotNull);

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                task.completeExceptionally(new TimeoutException());
            } catch (InterruptedException exception) {
                Assertions.fail();
            }
        }).start();

        task.awaitUninterruptedly();

        Assertions.assertTrue(task.isDone());
        Assertions.assertTrue(task.isCompletedExceptionally());
    }

    private boolean between(long i, long min, long max) {
        return i >= min && i <= max;
    }
}
