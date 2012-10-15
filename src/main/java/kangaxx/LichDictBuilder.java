package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/15/12
 * Time: 1:48 PM
 */
public class LichDictBuilder {

    private LichBuffer parent;
    private LichBuffer buffer;

    public LichDictBuilder(LichBuffer parent) {
        this.parent = parent;
        this.buffer = new LichBuffer(1);
    }

    public LichDictBuilder put(byte[] key, byte[] val) {
        return this.put(key, 0, key.length, val, 0, val.length);
    }


    public LichDictBuilder put(byte[] key, int keyOffset, int keyLen,
                               byte[] val, int valOffset, int valLen) {

        buffer.putBinary(key, keyOffset, keyLen);
        buffer.putBinary(val, valOffset, valLen);
        return this;
    }

    public LichDictBuilder put(byte[] key, int keyOffset, int keyLen,
                               LichBuffer val) {

        buffer.putBinary(key, keyOffset, keyLen);
        buffer.putBuffer(val);
        return this;
    }

    public LichBuffer end() {
        if (parent != null) {
            parent.putBuffer(buffer, (byte) '{', (byte) '}');
            return parent;
        } else {
            this.buffer.wrap((byte) '{', (byte) '}');
            return buffer;
        }
    }

}
