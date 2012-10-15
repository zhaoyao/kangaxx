package kangaxx;

import java.util.*;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 10:18 PM
 */
public class LichDict extends Lich implements Map<LichBinary, Lich> {

    private Map<LichBinary, Lich> map;

    public LichDict(int initialCapacity) {
        this.map = new HashMap<LichBinary, Lich>(initialCapacity);
    }

    public Lich put(LichBinary key, Lich value) {
        return map.put(key, value);
    }

    @Override
    public Lich remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends LichBinary, ? extends Lich> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<LichBinary> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Lich> values() {
        return map.values();
    }

    @Override
    public Set<Entry<LichBinary, Lich>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean isDict() {
        return true;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Lich get(Object key) {
        return map.get(key);
    }

    @Override
    public String toString() {
        return map.toString();
    }

}
