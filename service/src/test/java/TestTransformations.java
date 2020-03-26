import com.booking.validator.connectors.ActiveDataSourceConnections;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.source.Types;
import com.booking.validator.data.source.constant.ConstantQueryOptions;
import com.booking.validator.data.source.mysql.MysqlQueryOptions;
import com.booking.validator.data.transformation.TransformationTypes;
import com.booking.validator.service.supplier.data.source.QueryConnectorsForTask;
import com.booking.validator.task.Task;
import com.booking.validator.task.TaskComparisonResult;
import com.booking.validator.task.extra.AcceptableRanges;
import com.booking.validator.task.extra.Extra;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTransformations {

    public void finalize() {
        ActiveDataSourceConnections.getInstance().closeAll();
    }

    @Test
    public void testConstantDataSourceDeserialization() throws Exception {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("a", "a");
        DataSource dataSource = new DataSource(
                "constantSource",
                new ConstantQueryOptions(Types.CONSTANT.getValue(), data, null));
        Task taskV1 = new Task("unit_test", dataSource, dataSource, null);
        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);
        TaskComparisonResult r1 = taskV1.validate(ActiveDataSourceConnections.getInstance().query(dataSource),
                                                  ActiveDataSourceConnections.getInstance().query(dataSource));
        ObjectMapper mapper = new ObjectMapper();
        Task pls = mapper.readValue(taskV1.toJson(), Task.class);
        assertEquals(((ConstantQueryOptions)pls.getSource().getOptions()).getData().get("a"), "a");

        finalize();
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
        Task taskV1 = new Task("unit_test", dataSource, dataSource2, null);
        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);

        ObjectMapper mapper = new ObjectMapper();
        Task taskDeserialized = mapper.readValue(taskV1.toJson(), Task.class);

        QueryConnectorsForTask qcft = new QueryConnectorsForTask(taskDeserialized);
        TaskComparisonResult taskComparisonResult = qcft.get().get();
        assertTrue(taskComparisonResult.isOk());

        finalize();
    }

    @Test
    public void testIgnoreColumnsTransformation() throws Exception {
        HashMap<String, Object> data1 = new HashMap<String, Object>();
        data1.put("a", "a");
        data1.put("b", "a");
        HashMap<String, Object> transformations = new HashMap<String, Object>();
        transformations.put(TransformationTypes.IGNORE_COLUMNS.getValue(), new ArrayList<String>(){{add("a");}});
        DataSource dataSource = new DataSource(
                "constantSource",
                new ConstantQueryOptions("constant", (Map<String, Object>)data1, transformations));

        HashMap<String, Object> data2 = new HashMap<String, Object>();
        data2.put("b", "a");
        DataSource dataSource2 = new DataSource(
                "constantSource",
                new ConstantQueryOptions("constant", (Map<String, Object>)data2, null));
        Task taskV1 = new Task("unit_test", dataSource, dataSource2, null);
        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);

        ObjectMapper mapper = new ObjectMapper();
        Task taskDeserialized = mapper.readValue(taskV1.toJson(), Task.class);
        System.out.println("Deserialized:\n"+taskDeserialized.toJson());
        QueryConnectorsForTask qcft = new QueryConnectorsForTask(taskDeserialized);
        TaskComparisonResult taskComparisonResult = qcft.get().get();
        assertTrue(taskComparisonResult.isOk());

        finalize();
    }

    @Test
    public void testMysqlQueryOptions() throws Exception {
        Map<String, Object> prima = new HashMap<String, Object>(){{put("name", "dbatheja");}};
        DataSource dataSource = new DataSource(
                "mysqlSource",
                new MysqlQueryOptions(Types.MYSQL.getValue(),
                                      "tab",
                                      prima,
                                      null));
        Task task = new Task("unit_test", dataSource, dataSource, null);
        System.out.println(task.toJson());
        ObjectMapper mapper = new ObjectMapper();
        Task taskDeserialized = mapper.readValue(task.toJson(), Task.class);
        System.out.println(taskDeserialized.toJson());
    }

    @Test
    public void testRangedComparison() throws Exception {
        HashMap<String, Object> data1 = new HashMap<String, Object>();
        data1.put("a", 12);
        HashMap<String, Object> transformations = new HashMap<String, Object>();
        transformations.put(TransformationTypes.ALIAS_COLUMNS.getValue(), new HashMap<String, String>(){{put("a", "b");}});
        DataSource dataSource = new DataSource(
                "constantSource",
                new ConstantQueryOptions("constant", (Map<String, Object>)data1, transformations));

        HashMap<String, Object> data2 = new HashMap<String, Object>();
        data2.put("b", 15);
        DataSource dataSource2 = new DataSource(
                "constantSource",
                new ConstantQueryOptions("constant", (Map<String, Object>)data2, null));
        List<Extra> extraList = Extra.builder();
        extraList.add(new AcceptableRanges()
                    .add("b", 3.0)
                    .add("c", 4));
        Task taskV1 = new Task("unit_test", dataSource, dataSource2, extraList);

        System.out.println(taskV1.toJson());
        ActiveDataSourceConnections.getInstance().add("constantSource", Types.CONSTANT.getValue(), null);

        ObjectMapper mapper = new ObjectMapper();
        Task taskDeserialized = mapper.readValue(taskV1.toJson(), Task.class);

        QueryConnectorsForTask qcft = new QueryConnectorsForTask(taskDeserialized);
        TaskComparisonResult taskComparisonResult = qcft.get().get();

        assertTrue(taskComparisonResult.isOk());

        finalize();
    }


}
