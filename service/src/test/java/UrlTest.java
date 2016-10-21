import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Created by psalimov on 10/19/16.
 */
public class UrlTest {

    @Test
    public void test(){

        URI s = URI.create("mysql://database/table?pk0=v0&pk2=%20v2");

        assertEquals(s.getScheme(),"mysql");

        assertEquals("/table",s.getPath());

        assertEquals(2,s.getPath().split("/").length);

        String t = s.getQuery();


        t.length();
    }


    @Test
    public void joinTest(){

        List<String> columns = Arrays.asList("A", "B","C");
        String quote = "'";
        String table = "table";

        String s = String.format("SELECT * FROM %s WHERE (%s) = (%s) LIMIT 2;",
                table,
                columns.stream().collect(Collectors.joining(quote + "," + quote, quote, quote)),
                Stream.generate( () -> "?" ).limit(columns.size()).collect(Collectors.joining(",")));



        assertEquals("SELECT * FROM table WHERE ('A','B','C') = (?,?,?) LIMIT 2;",s);


    }


}
