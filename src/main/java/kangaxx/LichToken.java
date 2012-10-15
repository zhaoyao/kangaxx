package kangaxx;

import java.util.List;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 4:13 PM
 */
public class LichToken {

    static enum DataType {
        UNKNOWN,
        BINARY,
        ARRAY,
        DICT
    }

    public int sizeDecOffset;
    public int sizeDecLength;

    public int openingMarkerOffset;
    public int openingMarkerLength;

    public int contentOffset;
    public int contentLength;

    public int closingMarkerOffset;
    public int closingMarkerLength;

    public int parsedSize;
    public DataType parsedType = DataType.UNKNOWN;

    public LichToken parent;
    public List<LichToken> children;

    public int endOfContent() {
        return this.contentOffset + this.contentLength-1;
    }


}
