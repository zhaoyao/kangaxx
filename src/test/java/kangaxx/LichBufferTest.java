package kangaxx;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * User: zhaoyao
 * Date: 10/15/12
 * Time: 2:17 PM
 */
public class LichBufferTest {

    private LichBuffer buffer;

    @Test
    public void testEnsureCap() {
        buffer = new LichBuffer(1);

        buffer.ensureCap(1);
        assertEquals(1, buffer.toByteArray().length);

        buffer.ensureCap(2);
        assertEquals(2, buffer.toByteArray().length);
    }

    @Test
    public void testAppendByteArray() {
        byte[] bytes = "hello world".getBytes();
        buffer = new LichBuffer(bytes.length);

        buffer.putBinary(bytes, 0, bytes.length);

        assertEquals(2 + 2 + bytes.length, buffer.length());
        assertEquals("11<hello world>", buffer.toString());
    }


}
