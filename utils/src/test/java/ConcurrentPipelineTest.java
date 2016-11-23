import com.booking.validator.utils.ConcurrentPipeline;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by psalimov on 11/23/16.
 */
public class ConcurrentPipelineTest {

    @Test
    public void testLimit() {

        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger taskCounter = new AtomicInteger();

        final Random r = new Random();

        ConcurrentPipeline<Integer> pipe = new ConcurrentPipeline(

                () -> {
                    int c = taskCounter.incrementAndGet();
                    System.out.println("Supply Task " + c + " concurrency " + counter.incrementAndGet());

                    try {
                        Thread.sleep(r.nextInt(100) + 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (r.nextInt(10) < 2) {
                        throw new RuntimeException("Supplier error" + c);
                    }

                    return new Supplier<CompletableFuture>() {
                        @Override
                        public CompletableFuture get() {
                            return CompletableFuture.supplyAsync(() -> {

                                try {
                                    Thread.sleep(r.nextInt(400) + 100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (r.nextInt(10) < 2) {
                                    throw new RuntimeException("Transformer error " + c);
                                }

                                return c;
                            });
                        }
                    };

                },

                (x, t) -> {
                    counter.decrementAndGet();

                    if (r.nextInt(10) < 2) {
                        throw new RuntimeException("Consumer error " + x);
                    }

                    if (t != null) {
                        System.out.println("Task error: " + t);
                    } else {
                        System.out.println("Task completed: " + x);
                    }
                },
                2);

        pipe.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}