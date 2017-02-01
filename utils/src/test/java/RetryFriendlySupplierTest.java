import com.booking.validator.utils.CurrentTimestampProviderImpl;
import com.booking.validator.utils.CurrentTimestampProvider;
import com.booking.validator.utils.RetryFriendlySupplier;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by edmitriev on 1/25/17.
 */
public class RetryFriendlySupplierTest {

    private int retryQueueSoftSizeLimit = 1000;
    private static final int INTERNAL_SUPPLIER_NUM = 0;
    private static final int EXTERNAL_SUPPLIER_NUM = 1;

    private Supplier<TestItem> internalTaskSupplier = new TestItemSupplier(INTERNAL_SUPPLIER_NUM);
    private Supplier<TestItem> externalTaskSupplier = new TestItemSupplier(EXTERNAL_SUPPLIER_NUM);
    CurrentTimestampProvider currentTimestampProvider = mock(CurrentTimestampProviderImpl.class);
    private RetryFriendlySupplier<TestItem> retryFriendlySupplier = new RetryFriendlySupplier(internalTaskSupplier, retryQueueSoftSizeLimit, currentTimestampProvider);


    private class TestItem {
        int supplierNum = 0;

        TestItem(int supplierNum) {
            this.supplierNum = supplierNum;
        }
    }

    private class TestItemSupplier implements Supplier<TestItem> {
        int supplierNum = 0;
        TestItemSupplier(int supplierNum) {
            this.supplierNum = supplierNum;
        }
        @Override
        public TestItem get() { return new TestItem(supplierNum); }
    }

    @Before
    public void ConfigureTimestampProvider() {
        when(currentTimestampProvider.getCurrentTimeMillis()).thenCallRealMethod();
    }

    @Test
    public void InternalSupplierTest() {
        TestItem task = externalTaskSupplier.get();
        retryFriendlySupplier.accept(task, 1000);

        TestItem gotTask = retryFriendlySupplier.get();
        assertNotEquals(task, gotTask);
        assertEquals(gotTask.supplierNum, INTERNAL_SUPPLIER_NUM);
    }

    @Test
    public void SupplierDelayTest() {
        // first for NonblockingDelayingSupplier.accept() . second for NonblockingDelayingSupplier.get()
        when(currentTimestampProvider.getCurrentTimeMillis())
                .thenReturn((long) 0).thenReturn((long) 1001);

        TestItem task = externalTaskSupplier.get();
        retryFriendlySupplier.accept(task, 1000);
        TestItem gotTask = retryFriendlySupplier.get();

        assertEquals(task, gotTask);
    }
}