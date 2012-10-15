package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/15/12
 * Time: 12:16 PM
 */
class LichBuffer {

    private byte[] array;
    private int length;

    public LichBuffer(int initialSize) {
        this.array = new byte[initialSize];
        this.length = 0;
    }

    public LichArrayBuilder startArray() {
        return new LichArrayBuilder(this);
    }

    public LichDictBuilder startDict() {
        return new LichDictBuilder(this);
    }

    private int startBinary(int len) {
        return prependLengthPrefix(len, (byte) '<');
    }

    private void endBinary() {
        array[length++] = '>';
    }

    //borrowed from jedis
    private final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    private final static byte[] DigitTens = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };

    private final static byte[] DigitOnes = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };
    private final static byte[] digits = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    /**
     * 返回整个chunk的大小
     * if 20<xxxxxxxx> will return 20 + 2('<', '>') + 2(20)
     *
     * @param value
     * @param start
     * @return
     */
    public int prependLengthPrefix(int value, byte start) {

        int size = lengthPrefixLength(value);
        ensureCap(size + 2 + value);// length<xxxx>

        int q, r;
        int charPos = length + size;

        while (value >= 65536) {
            q = value / 100;
            r = value - ((q << 6) + (q << 5) + (q << 2));
            value = q;
            array[--charPos] = DigitOnes[r];
            array[--charPos] = DigitTens[r];
        }

        for (; ; ) {
            q = (value * 52429) >>> (16 + 3);
            r = value - ((q << 3) + (q << 1));
            array[--charPos] = digits[r];
            value = q;
            if (value == 0) break;
        }
        length += size;

        array[length++] = start;

        return size;
    }

    @Override
    public String toString() {
        return new String(array, 0, length);
    }

    void ensureCap(int expected) {
        if (array.length - this.length >= expected) {
            return;
        }

        byte[] narray = new byte[this.length + expected];
        System.arraycopy(this.array, 0, narray, 0, this.length);
        this.array = narray;
    }


    public void putBuffer(LichBuffer buffer, byte start, byte end) {
        prependLengthPrefix(buffer.length, start);
        System.arraycopy(buffer.array, 0, this.array, this.length, buffer.length);
        this.length += buffer.length;
        array[length++] = end;
    }

    public void putBuffer(LichBuffer buffer) {
        putBinary(buffer.toByteArray(), 0, buffer.length());
    }


    public static int lengthPrefixLength(int length) {
        int ret = 0;
        while (length > sizeTable[ret])
            ret++;
        return ++ret;
    }

    void wrap(byte start, byte end) {

        int oldLength = length;
        this.length = 0;
        int lenPrefixLen = lengthPrefixLength(oldLength);

        //move data
        System.arraycopy(array, 0, array, lenPrefixLen + 1, oldLength);

        //prepend prefix
        this.length = 0;
        prependLengthPrefix(oldLength, start);

        //append suffix
        this.length += oldLength;
        array[this.length++] = end;

    }

    public byte[] toByteArray() {
        return array;
    }

    public int length() {
        return length;
    }

    public LichBuffer putInt(int value) {
        startBinary(4);
        array[length++] = (byte) (value >>> 24);
        array[length++] = (byte) (value >>> 16);
        array[length++] = (byte) (value >>> 8);
        array[length++] = (byte) (value);
        endBinary();

        return this;
    }

    public LichBuffer putLong(long value) {
        startBinary(8);
        array[length++] = (byte) (value >>> 56);
        array[length++] = (byte) (value >>> 48);
        array[length++] = (byte) (value >>> 40);
        array[length++] = (byte) (value >>> 32);
        array[length++] = (byte) (value >>> 24);
        array[length++] = (byte) (value >>> 16);
        array[length++] = (byte) (value >>> 8);
        array[length++] = (byte) (value);
        endBinary();

        return this;
    }

    public LichBuffer putShort(short value) {
        startBinary(2);
        array[length++] = (byte) (value >>> 8);
        array[length++] = (byte) (value);
        endBinary();

        return this;
    }

    public LichBuffer putByte(byte value) {
        startBinary(1);
        array[length++] = value;
        endBinary();
        return this;
    }

    public LichBuffer putBinary(byte[] value, int offset, int length) {
        startBinary(value.length);
        appendRawBytes(value, offset, length);
        endBinary();
        return this;
    }

    public LichBuffer putFloat(float value) {
        int bytes = Float.floatToRawIntBits(value);
        return putInt(bytes);
    }

    public LichBuffer putDouble(double value) {
        long bytes = Double.doubleToRawLongBits(value);
        return putLong(bytes);
    }

    public LichBuffer putChar(int value) {
        return putShort((short) value);
    }

    public LichBuffer putBool(boolean value) {
        return putByte((byte) (value ? 1 : 0));
    }

    public LichBuffer appendRawBytes(byte[] bytes, int offset, int length) {
        ensureCap(length);
        System.arraycopy(bytes, offset, this.array, this.length, length);
        this.length += length;
        return this;
    }

    public byte[] copyArray() {
        byte[] b = new byte[length()];
        System.arraycopy(this.array, 0, b, 0, length());
        return b;
    }
}
