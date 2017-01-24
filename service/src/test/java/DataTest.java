import com.booking.validator.data.Data;
import com.booking.validator.data.EqualityTester;
import org.junit.Test;

import java.util.HashMap;

import static com.booking.validator.data.Data.discrepancy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by edmitriev on 1/9/17.
 */
public class DataTest {

    EqualityTester equalityTester = new EqualityTester();

    @Test
    public void doubleToDoubleEqualityPassTest() {
        double value1 = Double.parseDouble("53.6813223652125");
        double value2 = Double.parseDouble("53.681322365212495");
        assertTrue("Values have to match: " + value1 + ", " + value2, equalityTester.testEquality(value1, value2));
    }

    @Test
    public void doubleToDoubleEqualityFailTest() {
        double x = Double.parseDouble("53.6813223652126");
        double y = Double.parseDouble("53.681322365212495");
        assertFalse("Values mustn't match: " + x + ", " + y, equalityTester.testEquality(x, y));
    }

    @Test
    public void doubleToStringEqualityTest() {
        double value1 = Double.parseDouble("53.6813223652125");
        String value2 = "53.681322365212495";
        assertTrue("Values have to match: " + value1 + ", " + value2, equalityTester.testEquality(value1, value2));
        assertTrue("Values have to match: " + value1 + ", " + value2, equalityTester.testEquality(value2, value1));
    }

    @Test
    public void intToDoubleEqualityTest() {
        int x = 42;
        double y = Double.parseDouble("23.3");
        assertFalse("Should fail with current type: " + x + ", " + y, equalityTester.testEquality(x, y));
        assertFalse("Should fail with current type: " + x + ", " + y, equalityTester.testEquality(y, x));
    }

    @Test
    public void floatToFloatEqualityPassTest() {
        float x = Float.parseFloat("53.6813224");
        float y = Float.parseFloat("53.6813297");
        assertTrue("Values have to match: " + x + ", " + y, equalityTester.testEquality(x, y));
    }

    @Test
    public void floatToFloatEqualityFailTest() {
        float x = Float.parseFloat("53.6813224");
        float y = Float.parseFloat("53.6813298");
        assertFalse("Values mustn't match: " + x + ", " + y, equalityTester.testEquality(x, y));
    }

    @Test
    public void floatToStringEqualityTest() {
        float value1 = Float.parseFloat("53.6813224");
        String value2 = "53.6813297";
        assertTrue("Values have to match: " + value1 + ", " + value2, equalityTester.testEquality(value1, value2));
        assertTrue("Values have to match: " + value1 + ", " + value2, equalityTester.testEquality(value2, value1));
    }

    @Test
    public void intToFloatEqualityTest() {
        int x = 42;
        float y = Float.parseFloat("23.3");
        assertFalse("Should fail with current type: " + x + ", " + y, equalityTester.testEquality(x, y));
        assertFalse("Should fail with current type: " + x + ", " + y, equalityTester.testEquality(y, x));
    }

    @Test
    public void discrepancyTest() {
        HashMap<String, Object> x = new HashMap<>();
        x.put("a", "123");
        x.put("b", "0.24");
        x.put("c", 0.26);
        HashMap<String, Object> y = new HashMap<>();
        y.put("a", "123");
        y.put("b", "0.24");
        y.put("c", 0.26);
        Data.Discrepancy discrepancy = discrepancy(new Data(x), new Data(y));
        assertFalse(discrepancy.toString(), discrepancy.hasDiscrepancy());

        x.put("d", "123");
        y.put("d", "1234");
        discrepancy = discrepancy(new Data(x), new Data(y));
        assertTrue(discrepancy.toString(), discrepancy.hasDiscrepancy());
        x.remove("d");
        y.remove("d");

        y.put("e", "0.25");
        x.put("e", "0.24");
        discrepancy = discrepancy(new Data(x), new Data(y));
        assertTrue(discrepancy.toString(), discrepancy.hasDiscrepancy());
        x.remove("e");
        y.remove("e");

        x.put("f", 0.26);
        y.put("f", 0.27);
        discrepancy = discrepancy(new Data(x), new Data(y));
        assertTrue(discrepancy.toString(), discrepancy.hasDiscrepancy());
    }

}
