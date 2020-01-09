import com.booking.validator.service.ValidatorConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import kafka.utils.Time;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * Created by psalimov on 9/19/16.
 */
public class ValidatorConfigurationTest {

    @Test
    public void deserializationTest() throws IOException {

        String serialized = "data_sources:\n"+
                "    - name: 'a'\n" +
                "      type: 'aT'\n"+
                "      configuration:\n"+
                "         akey1 : av1\n"+
                "         akey2 : av2\n"+
                "    - name: 'b'\n" +
                "      type: 'bT'\n"+
                "      configuration:\n"+
                "         akey1 : bv1\n"+
                "         akey2 : bv2\n";

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ValidatorConfiguration conf = mapper.readValue(serialized, ValidatorConfiguration.class);

        for (ValidatorConfiguration.DataSource dp : conf.getDataSources()){
            System.out.println("name: "+ dp.getName());
            System.out.println("type: "+ dp.getType());

            for (Map.Entry<String,String> e : dp.getConfiguration().entrySet()){
                System.out.println("conf: "+e.getKey()+" - "+e.getValue());
            }
        }

    }

    private static class Entity{

        public static class EntitySection{
            public int c;
            public int d=20;
            public int e=30;
        }

        public int a;
        public int b=10;

        @JsonDeserialize
        public EntitySection section;
    }

    private static class Prop{

        public static class PropSection{
            public String name;
            public Map<String, Object> props;
        }


        public PropSection[] props;

    }

    @Test
    public void desealizeNestedMapsExperiment() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Prop p = new Prop();
        p.props = new Prop.PropSection[2];

        p.props[0] = new Prop.PropSection();
        p.props[0].name = "prop1";
        p.props[0].props = new HashMap<>();
        p.props[0].props.put("a","0");

        Map<String,Object> b = new HashMap<>();
        p.props[0].props.put("b", b );
        b.put("ba",1);
        b.put("bb",2);
        b.put("bbb",new int[4]);

        Map<String,Object> c = new HashMap<>();
        p.props[1]= new Prop.PropSection();
        p.props[0].name = "prop2";

        String s = mapper.writeValueAsString(p);
        System.out.println(s);

        Prop p2 = mapper.readValue(s, Prop.class);

        assertEquals(p.props[0].props, p2.props[0].props);

    }


    @Test
    public void deserializeMissingTest() throws IOException {

        String serialized = "a: 1\nsection:\n   e: 35\n   c: 3";

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Entity e = mapper.readValue(serialized, Entity.class);

        assertEquals(10, e.b);
        assertEquals(1, e.a);
        assertEquals(3, e.section.c);
        assertEquals(20, e.section.d);
        assertEquals(35, e.section.e);
    }

    private static final class A{
        private final String a;
        @JsonProperty("b")
        private final String b;

        private A(){
            a = "a";
            b = "b";
        }

    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void serializeTest(){

        A a = new A();

        try {
            String s = mapper.writeValueAsString(a);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ccTest(){

        long numberOfIntervals = 10;
        long testTimeInterval =  numberOfIntervals  * throttlingInterval;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        // schedule 10 tasks to run every 0.1s
        for (int i = 0; i < 10; i++) {
            executor.scheduleWithFixedDelay(() -> {
                    if (ValidatorConfigurationTest.this.updateLastRegistrationTime()) {
                        System.out.println("D");
                    }
                },
                0,     // initial delay
                100,  // re-run period
                TimeUnit.MILLISECONDS
            );
        }
        try {
            executor.awaitTermination(testTimeInterval, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();

        assertEquals(numberOfIntervals, unsafeCounter);
    }

    private final long throttlingInterval = 1000;
    private long lastRegistrationTime;

    private long unsafeCounter = 0;

    private final AtomicBoolean registrationWeakLock = new AtomicBoolean();

    // onceEveryNSeconds
    private boolean updateLastRegistrationTime(){

        long currentTime = System.currentTimeMillis();

        // This method can be called all the time concurrently, but the update of the
        // value should happen * once every 5sec * and should be non-blocking for other
        // threads.

        // Double-checked locking WITHOUT volatile:
        // Write to lastRegistrationTime happens-before its second read cause AtomicBoolean write-read sequence is in between.
        // First read may not be consistent (is racy) cause java does not guarantee atomicity for writing longs. But taking,
        // into account the nature of the value it is not a problem
        if (isOutOfThrottlingInterval(currentTime)){

            if (registrationWeakLock.compareAndSet(false,true)) {

                if (isOutOfThrottlingInterval(currentTime)){

                    lastRegistrationTime = currentTime;

                    unsafeCounter++;
                    System.out.println("weakly protected counter => " + unsafeCounter);

                    registrationWeakLock.set(false);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean isOutOfThrottlingInterval(long currentTime){
        return currentTime - lastRegistrationTime > throttlingInterval;
    }

    @Test
    public void floatTest(){

        String s ="0.018626829609274864000000000000";

        BigDecimal d = new BigDecimal(s);

        System.out.println(d.toString());


        float f = Float.valueOf(s+"f");

        System.out.println(String.format("%.32f",f));


        int i = Float.floatToIntBits(f);


        i += 1;

        float f2 = Float.intBitsToFloat(i);

        System.out.println(String.format("%.32f",f2));

        i -= 2;

        float f3 = Float.intBitsToFloat(i);

        System.out.println(String.format("%.32f",f3));
    }

    @Test
    public void doubleTest(){
        double d1 = Double.valueOf("505502.279999999970000000000000000000");

        System.out.println(new BigDecimal(d1));

        long l1 = Double.doubleToLongBits(d1);

        double d2 = Double.valueOf("505502.28");
        System.out.println(new BigDecimal(d2));

        long l2 = Double.doubleToLongBits(d2);

        double d3 = Double.longBitsToDouble(l1+1);

        double d4 = Double.longBitsToDouble(l2-1);

        System.out.println(d3);
        System.out.println(d4);

        assertEquals(d1,d2, 0.01);
    }

}
