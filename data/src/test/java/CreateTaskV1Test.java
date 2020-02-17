import com.booking.validator.data.source.ActiveDataSourceConnections;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.constant.ConstantDataSource;
import com.booking.validator.data.source.constant.ConstantDataSourceQueryOptions;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import com.booking.validator.task.TaskV1;

public class CreateTaskV1Test {

    @Test
    public void testCompareConstantDataSources() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("a", "a");
        DataSource dataSource = new ConstantDataSource(
                "constantSource",
                Types.CONSTANT.getValue(),
                new ConstantDataSourceQueryOptions((Map<String, Object>)data, null));
        Task taskV1 = new TaskV1("unit_test", dataSource, dataSource, null);
        final ObjectMapper mapper = new ObjectMapper();
        String task = mapper.writeValueAsString(taskV1);
        System.out.println(task);
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);
        TaskComparisonResult r1 = taskV1.get().get();

        System.out.println(r1);
    }
}