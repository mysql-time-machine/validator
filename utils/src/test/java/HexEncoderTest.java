import com.booking.validator.utils.HexEncoder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by psalimov on 11/22/16.
 */
public class HexEncoderTest {

    @Test
    public void encodeTest(){

        byte[] data = new byte[]{0,(byte)254};

        String encoded = HexEncoder.encode(data);

        assertEquals("00FE", encoded);

    }


}
