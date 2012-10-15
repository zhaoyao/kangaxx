package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 5:28 PM
 */
public class LichException extends RuntimeException {

    public LichException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LichException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LichException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static final class UnexpectedLichElementType extends LichException {

        public UnexpectedLichElementType(LichToken.DataType type) {
            super(type.name());
        }

    }
}
