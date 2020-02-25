import com.booking.validator.connectors.ActiveDataSourceConnections;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.constant.ConstantQueryOptions;
import com.booking.validator.data.transformation.TransformationTypes;
import com.booking.validator.service.supplier.data.source.QueryConnectorsForTask;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;
import com.booking.validator.task.TaskComparisonResultV1;
import com.booking.validator.task.TaskV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class CreateTaskV1 {
    @Test
    public void testConstantDataSourceDeserialization() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("a", "a");
        DataSource dataSource = new DataSource(
                "constantSource",
                new ConstantQueryOptions(Types.CONSTANT.getValue(), (Map<String, Object>)data, null));
        Task taskV1 = new TaskV1("unit_test", dataSource, dataSource, null);
        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);
        TaskComparisonResult r1 = taskV1.validate(ActiveDataSourceConnections.getInstance().query(dataSource),
                                                  ActiveDataSourceConnections.getInstance().query(dataSource));
        ObjectMapper mapper = new ObjectMapper();
        TaskV1 pls = mapper.readValue(taskV1.toJson(), TaskV1.class);
        assertEquals(((ConstantQueryOptions)pls.getSource().getOptions()).getData().get("a"), "a");
    }

    @Test
    public void testQueryConnectionWithTransformation() throws Exception {
        HashMap<String, Object> data1 = new HashMap<String, Object>();
        data1.put("a", "a");
        HashMap<String, Object> transformations = new HashMap<String, Object>();
        transformations.put(TransformationTypes.ALIAS_COLUMNS.getValue(), new HashMap<String, String>(){{put("a", "b");}});
        DataSource dataSource = new DataSource(
                "constantSource",
                new ConstantQueryOptions("constant", (Map<String, Object>)data1, transformations));

        HashMap<String, Object> data2 = new HashMap<String, Object>();
        data2.put("b", "a");
        DataSource dataSource2 = new DataSource(
                "constantSource",
                new ConstantQueryOptions("constant", (Map<String, Object>)data2, null));
        Task taskV1 = new TaskV1("unit_test", dataSource, dataSource2, null);
        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);
        QueryConnectorsForTask qcft = new QueryConnectorsForTask(taskV1);
        TaskComparisonResultV1 taskComparisonResult = (TaskComparisonResultV1) qcft.get().get();
        assertTrue(taskComparisonResult.isOk());
    }

}
