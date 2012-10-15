package kangaxx;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 7:23 PM
 */
public class LichDecoderTest {

    LichDecoder decoder;

    @org.junit.Before
    public void setUp() throws Exception {
        decoder = new LichDecoder();
    }

    @Test
    public void testDecodeBinary() {
        byte[] data = "11<hello world>".getBytes();

        Lich ret = decoder.decode(data, 0, data.length);


        LichBinary b = (LichBinary) ret;

        assertEquals(3, b.arrayOffset());
        assertEquals(data.length, b.length());
    }

    @Test
    public void testDecodeArray() {
        byte[] data = "26[5<apple>6<banana>6<orange>]".getBytes();

        Lich ret = decoder.decode(data, 0, data.length);
        Assert.assertTrue(ret.isArray());

        LichArray array =  ret.asArray();
        assertEquals(3, array.size());

        assertEquals("apple", array.get(0).toString());
        assertEquals("banana", array.get(1).toString());
        assertEquals("orange", array.get(2).toString());
    }

    @Test
    public void testDecodeDict() {
        byte[] data = "54{8<greeting>11<hello world>9<greeting1>12<hello world5>}".getBytes();

        Lich ret = decoder.decode(data, 0, data.length);
        Assert.assertTrue(ret.isDict());

        LichDict dict = ret.asDict();
        assertEquals(2, dict.size());


        Lich value = dict.get(LichBinary.of("greeting"));
        assertNotNull(value);
        assertEquals("hello world", value.asBinary().toString());

        value = dict.get(LichBinary.of("greeting1"));
        assertNotNull(value);
        assertEquals("hello world5", value.asBinary().toString());
    }


}
