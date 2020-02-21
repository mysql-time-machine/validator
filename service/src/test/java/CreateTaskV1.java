import com.booking.validator.connectors.ActiveDataSourceConnections;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.constant.ConstantQueryOptions;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;
import com.booking.validator.task.TaskV1;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CreateTaskV1 {
    @Test
    public void testCompareConstantDataSources() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("a", "a");
        DataSource dataSource = new DataSource(
                "constantSource",
                Types.CONSTANT.getValue(),
                new ConstantQueryOptions("constant", (Map<String, Object>)data, null));
        Task taskV1 = new TaskV1("unit_test", dataSource, dataSource, null);
        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);
        TaskComparisonResult r1 = taskV1.validate(ActiveDataSourceConnections.getInstance().query(dataSource),
                                                  ActiveDataSourceConnections.getInstance().query(dataSource));
        ObjectMapper mapper = new ObjectMapper();

        TaskV1 pls = mapper.readValue(taskV1.toJson(), TaskV1.class);
        System.out.println(((ConstantQueryOptions)pls.getSource().getOptions()).getData().get("a"));
    }

}
