import com.booking.validator.service.ValidatorConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
        private final String b;

        private A(){
            a = "a";
            b = "b";
        }

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
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

        for (;;){
            if (updateLastRegistrationTime()) System.out.println("D");
        }

    }

    private final long throttlingInterval = 5000;
    private long lastRegistrationTime;

    private final AtomicBoolean registrationWeakLock = new AtomicBoolean();

    private boolean updateLastRegistrationTime(){

        long currentTime = System.currentTimeMillis();

        // Double-checked locking WITHOUT volatile:
        // Write to lastRegistrationTime happens-before its second read cause AtomicBoolean write-read sequence is in between.
        // First read may not be consistent (is racy) cause java does not guarantee atomicity for writing longs. But taking,
        // into account the nature of the value it is not a problem
        if (isTimeWindowEmpty(currentTime)){

            if (registrationWeakLock.compareAndSet(false,true)){

                if (isTimeWindowEmpty(currentTime)){

                    lastRegistrationTime = currentTime;

                    registrationWeakLock.set(false);

                    return true;

                }
            }

        }

        return false;
    }

    private boolean isTimeWindowEmpty(long currentTime){

        return currentTime - lastRegistrationTime > throttlingInterval;

    }

}
