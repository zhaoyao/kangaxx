package kangaxx;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 11:33 PM
 */
public class LichEncoder {

    public static LichBuffer encode(Object obj) {

        LichBuffer buffer = new LichBuffer(4096);

        if (obj instanceof Map) {
            Map map = (Map) obj;
            return encodeMap(buffer, map);

        } else if (obj instanceof Iterable) {
            Iterable iterable = (Iterable) obj;

            LichArrayBuilder arrayBuilder = buffer.startArray();
            for (Object elt : iterable) {
                arrayBuilder.append(encode(elt));
            }
            return arrayBuilder.end();

        } else if (obj.getClass().isArray()) {

            int numElt = Array.getLength(obj);
            LichArrayBuilder arrayBuilder = buffer.startArray();
            for (int i = 0; i < numElt; i++) {
                arrayBuilder.append(encode(Array.get(obj, i)));
            }
            return arrayBuilder.end();

        } else {

            Class t = obj.getClass();
            if (t == Integer.class || t == Integer.TYPE) {
                buffer.putInt((Integer) obj);

            } else if (t == Short.class || t == Short.TYPE) {
                buffer.putShort((Short) obj);

            } else if (t == Long.class || t == Long.TYPE) {
                buffer.putLong((Long) obj);

            } else if (t == Byte.class || t == Byte.TYPE) {
                buffer.putByte((Byte) obj);

            } else if (t == Float.class || t == Float.TYPE) {
                buffer.putFloat((Float) obj);

            } else if (t == Double.class || t == Double.TYPE) {
                buffer.putDouble((Double) obj);

            } else if (t == Character.class || t == Character.TYPE) {
                buffer.putChar((Character) obj);

            } else if (obj instanceof byte[]) {
                buffer.putBinary((byte[]) obj, 0, ((byte[]) obj).length);

            } else if (obj instanceof LichSerializable) {
                ((LichSerializable) obj).write(buffer);

            } else {
                throw new LichException("Does not know how to encode object " + obj.getClass());

            }


        }

        return buffer;
    }



    private static LichBuffer encodeMap(LichBuffer buffer, Map map) {
        LichDictBuilder dictBuilder = buffer.startDict();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object mapKey = entry.getKey();
            Object mapVal = entry.getValue();

            if (mapKey == null) {
                throw new NullPointerException("dict key");
            }

            if (!(mapKey instanceof String) &&
                    !(mapKey instanceof byte[]) &&
                    !(mapKey instanceof LichBinary)) {
                throw new LichException("Dict key expect to be a String or byte[] or LichBinary, which is " + mapKey.getClass());
            }

            byte[] keyArr;
            int keyOffset, keyLen;
            if (mapKey instanceof byte[]) {
                keyArr = (byte[]) mapKey;
                keyOffset = 0;
                keyLen = keyArr.length;

            } else if (mapKey instanceof String) {
                keyArr = ((String) mapKey).getBytes();
                keyOffset = 0;
                keyLen = keyArr.length;

            } else {
                keyArr = ((LichBinary) mapKey).toByteArray();
                keyOffset = ((LichBinary)mapKey).arrayOffset();
                keyLen = ((LichBinary) mapKey).length();

            }

            dictBuilder.put(keyArr, keyOffset, keyLen, encode(mapVal));
        }

        return dictBuilder.end();
    }


}
