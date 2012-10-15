package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/15/12
 * Time: 12:38 PM
 */
public class LichArrayBuilder {

    private LichBuffer parent;
    private LichBuffer buffer;

    public LichArrayBuilder(LichBuffer parent) {
        this.parent = parent;
        this.buffer = new LichBuffer(1);
    }

    public LichArrayBuilder() {
        this(null);
    }

    public LichArrayBuilder append(byte[] elt) {
        buffer.putBinary(elt, 0, elt.length);
        return this;
    }

    public LichArrayBuilder append(LichBuffer buffer) {
        buffer.putBinary(buffer.toByteArray(), 0, buffer.length());
        return this;
    }

    public LichBuffer end() {
        if (parent != null) {
            parent.putBuffer(buffer, (byte)'[', (byte)']');
            return parent;

        } else {
            this.buffer.wrap((byte)'[', (byte)']');
            return buffer;

        }
    }
}
