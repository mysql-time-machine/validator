import com.mysql.cj.jdbc.io.JdbcTimestampValueFactory;
import org.jcodings.specific.UTF8Encoding;
import org.junit.Test;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
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
    public void another() throws Throwable{

        //String s = URI.create("hbase://rescore/delta:rescore_b_hotelreservation_20161101?row=7758383d;2098035396&cf=d").toString();


        String s = new URI("hbase","rescore","/delta:rescore_b_hotelreservation_20161101", "row="+URLEncoder.encode("7758383d;20 9й80?35396")+"&cf=d",null).toString();
        URI u = new URI(s);


        System.out.println(s);
        System.out.println(u.getQuery());
        System.out.println(u.getPath());

        String q = u.getQuery();

        String qp = URLDecoder.decode(q.split("&")[0].split("=")[1], "UTF-8");

        System.out.println(qp);

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


    @Test
    public void Timestamp(){

        JdbcTimestampValueFactory f = new JdbcTimestampValueFactory(TimeZone.getTimeZone("Europe/Amsterdam"));

        Timestamp t = f.createFromTimestamp(2016,10,10,14,04,18,0);

        System.out.println(t.getTime());

        System.out.println(t.toString());

        f = new JdbcTimestampValueFactory(TimeZone.getTimeZone("GMT"));

        t = f.createFromTimestamp(2016,10,10,14,04,18,0);

        System.out.println(t.getTime());

        System.out.println(t.toString());

    }

}
