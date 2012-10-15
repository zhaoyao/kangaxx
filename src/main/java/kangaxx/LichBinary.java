package kangaxx;

import java.nio.charset.Charset;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 10:14 PM
 */
public class LichBinary extends Lich {
    private byte[] data;
    private final int offset;
    private final int length;

    public LichBinary(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public String toString() {
        return new String(data, offset, length);
    }

    public String toString(Charset charset) {
        return new String(data, offset, length, charset);
    }

    public byte[] toByteArray() {
        return data;
    }

    public int arrayOffset() {
        return offset;
    }

    public int length() {
        return length;
    }

    public static LichBinary of(String str) {
        byte[] data = str.getBytes();
        return new LichBinary(data, 0, data.length);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < length; i++) {
            byte b = data[i+offset];
            result = 31 * result + b;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LichBinary lichBinary = (LichBinary) o;

        if (this.length != lichBinary.length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            int idx1 = offset + i;
            int idx2 = lichBinary.offset + i;

            if (data[idx1] != lichBinary.data[idx2]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public LichBinary asBinary() {
        return this;
    }

    @Override
    public LichArray asArray() {
        throw new ClassCastException();
    }

    @Override
    public LichDict asDict() {
        throw new ClassCastException();
    }
}
