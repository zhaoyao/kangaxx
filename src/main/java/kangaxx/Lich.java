package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 4:01 PM
 */
public abstract class Lich {

    public boolean isBinary() { return false; }
    public LichBinary asBinary() {
        if (isBinary()) {
            return (LichBinary) this;
        } else {
            throw new ClassCastException(getClass() + " can not be cast to LichBinary");
        }

    }

    public boolean isArray() {return false;}
    public LichArray asArray() {
        if (isArray()) {
            return (LichArray) this;
        } else {
            throw new ClassCastException(getClass() + " can not be cast to LichArray");
        }
    }

    public boolean isDict() {return false;}
    public LichDict asDict() {
        if (isDict()) {
            return (LichDict) this;
        } else {
            throw new ClassCastException(getClass() + " can not be cast to LichDict");
        }
    }
}
