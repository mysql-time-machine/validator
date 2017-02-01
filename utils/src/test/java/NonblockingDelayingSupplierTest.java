import com.booking.validator.utils.CurrentTimestampProviderImpl;
import com.booking.validator.utils.CurrentTimestampProvider;
import com.booking.validator.utils.NonblockingDelayingSupplier;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by edmitriev on 1/25/17.
 */
public class NonblockingDelayingSupplierTest {

    private long[] retryDelay = {1000, 2000, 3000};
    private int retryQueueSoftSizeLimit = 1000;

    Random random = new Random();
    private Supplier<Integer> testsTaskSupplier = new TestItemSupplier();
    CurrentTimestampProvider currentTimestampProvider = mock(CurrentTimestampProviderImpl.class);
    private NonblockingDelayingSupplier<Integer> nonblockingDelayingSupplier = new NonblockingDelayingSupplier(retryQueueSoftSizeLimit, retryQueueSoftSizeLimit/2, currentTimestampProvider);

    @Before
    public void ConfigureTimestampProvider() {
        when(currentTimestampProvider.getCurrentTimeMillis()).thenCallRealMethod();
    }

    private class TestItemSupplier implements Supplier<Integer> {
        @Override
        public Integer get() { return random.nextInt(); }
    }

    private class TestItemConsumer implements Consumer<Integer> {
        @Override
        public void accept(Integer testItem) {
        }
    }

    private class TestItemProducer<T> implements Runnable {
        Supplier<T> supplierInput;
        NonblockingDelayingSupplier<T> supplierOutput;
        int produceItems = 0;

        public TestItemProducer(Supplier<T> supplierInput, NonblockingDelayingSupplier<T> supplierOutput, int produceItems) {
            this.supplierInput = supplierInput;
            this.supplierOutput = supplierOutput;
            this.produceItems = produceItems;
        }

        @Override
        public void run() {
            for (int i=0; i<produceItems; i++) supplierOutput.accept(supplierInput.get(), 0);
        }
    }

    private class TestItemProcessor<T> implements Runnable {
        NonblockingDelayingSupplier<T> supplier;
        Consumer<T> consumer;
        CountDownLatch latch;
        int waitForItems;

        private TestItemProcessor(NonblockingDelayingSupplier<T> supplier, Consumer<T> consumer, CountDownLatch latch, int waitForItems) {
            this.supplier = supplier;
            this.consumer = consumer;
            this.latch = latch;
            this.waitForItems = waitForItems;
        }

        @Override
        public void run() {
            for (int i=0; i<waitForItems;) {
                T task = supplier.get();
                if (task != null) {
                    i++;
                    consumer.accept(task);
                }
            }
            latch.countDown();
        }
    }

    @Test
    public void supplyAndConsumeDelayTest() {


        boolean[] validResults = new boolean[] {true, true, true};

        // first for NonblockingDelayingSupplier.accept() . second for NonblockingDelayingSupplier.get()
        when(currentTimestampProvider.getCurrentTimeMillis())
                .thenReturn((long) 0).thenReturn(retryDelay[0]+1)
                .thenReturn((long) 0).thenReturn(retryDelay[1]+1)
                .thenReturn((long) 0).thenReturn(retryDelay[2]+1);
        boolean[] results = new boolean[retryDelay.length];

        for (int i=0; i<retryDelay.length; i++) {
            Integer task = testsTaskSupplier.get();
            nonblockingDelayingSupplier.accept(task, retryDelay[i]);

            Integer gotTask = nonblockingDelayingSupplier.get();
            results[i]= (task.equals(gotTask));
        }

        assertArrayEquals(validResults, results);

    }

    @Test
    public void supplyAndConsumeFalseDelayTest() {

        /// first for NonblockingDelayingSupplier.accept() . second for NonblockingDelayingSupplier.get()
        when(currentTimestampProvider.getCurrentTimeMillis())
                .thenReturn((long) 0).thenReturn((long) 0);

        Integer task = testsTaskSupplier.get();
        nonblockingDelayingSupplier.accept(task, 1000);
        Integer gotTask = nonblockingDelayingSupplier.get();

        assertNotEquals(task, gotTask);

    }

    @Test(timeout = 10000)
    public void supplyAndConsumeMultiThreadTest() throws ExecutionException {

        int producersNum = 10;
        int processorsNum = 1;
        int tasksToProduce = 1000;
        CountDownLatch latchProcessors = new CountDownLatch(processorsNum);

        for (int j = 0; j < processorsNum; j++ ) {
            TestItemProcessor processor = new TestItemProcessor(nonblockingDelayingSupplier, new TestItemConsumer(), latchProcessors, (tasksToProduce * producersNum / processorsNum));
            new Thread(processor).start();
        }

        for (int j = 0; j < producersNum; j++ ) {
            TestItemProducer producer = new TestItemProducer<>(testsTaskSupplier, nonblockingDelayingSupplier, tasksToProduce);
            new Thread(producer).start();
        }

        while (true) {
            try {
                latchProcessors.await();
                break;
            } catch (InterruptedException ignored) {
            }
        }

        assertNull(nonblockingDelayingSupplier.get());

    }

}