package kangaxx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 10:15 PM
 */
public class LichArray extends Lich implements Iterable<Lich> {

    private Lich[] data;
    private int size;

    public LichArray(int cap) {
        this.data = new Lich[cap];
        this.size = 0;
    }

    public void add(Lich lich) {
        if (size >= data.length) {
            throw new ArrayIndexOutOfBoundsException(size);
        }
        data[size++] = lich;
    }

    public Lich get(int i) {
        if (i < 0 || i >= data.length) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        if (i >= size) {
            throw  new NoSuchElementException();
        }
        return data[i];
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public LichArray asArray() {
        return this;
    }

    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Lich> iterator() {
        return new Iterator<Lich>() {
            int at = 0;

            @Override
            public boolean hasNext() {
                return at < size;
            }

            @Override
            public Lich next() {
                if (at >= size) {
                    throw new NoSuchElementException();
                }
                return data[at++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String toString() {
        Lich[] fitToSize = new Lich[size()];
        System.arraycopy(this.data, 0, fitToSize, 0, size());
        return Arrays.toString(fitToSize);
    }
}
