package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/15/12
 * Time: 3:44 PM
 */
public interface LichSerializable {

    /**
     * Write the object as kangaxx data to the buffer
     * User must manually specified the data type.
     *
     * <code>
            void write(LichBuffer buffer) {
                buffer.startBinary(len);
                buffer.putBytes(name.getBytes());
                buffer.putInt(age);
                buffer.endBinary();

                //or
                buffer.startDict()
                    .put("name".getBytes(), name.getBytes())
                    .put("age".getBytes()
                    .end();
            }
     * </code>
     *
     *
     * @param buffer
     */
    void write(LichBuffer buffer);


}
