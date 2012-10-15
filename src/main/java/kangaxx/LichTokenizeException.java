package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 4:32 PM
 */
public class LichTokenizeException extends Exception {

    private int offset;

    public LichTokenizeException(int offset) {
        this.offset = offset;
    }

    @Override
    public String getMessage() {
        return "Error offset: " + offset;
    }

    public int getOffset() {
        return offset;
    }

    public static final class MissingSizePrefix extends LichTokenizeException {
        public MissingSizePrefix(int offset) {
            super(offset);
        }
    }
    public static final class ExcessiveSizePrefix extends LichTokenizeException {
        public ExcessiveSizePrefix(int offset) {
            super(offset);
        }
    }
    public static final class InvalidSizePrefix extends LichTokenizeException {
        public InvalidSizePrefix(int offset) {
            super(offset);
        }
    }
    public static final class IncompleteSizePrefix extends LichTokenizeException {
        public IncompleteSizePrefix(int offset) {
            super(offset);
        }
    }

    public static final class IncompleteData extends LichTokenizeException {
        public IncompleteData(int offset) {
            super(offset);
        }
    }

    public static final class IncorrectClosingMarker extends LichTokenizeException {
        public IncorrectClosingMarker(int offset) {
            super(offset);
        }
    }

    public static final class UnknownLichTokenizerState extends LichException {
        private LichTokenizer.State state;

        public UnknownLichTokenizerState(LichTokenizer.State state) {
            super(state.name());
        }

    }


}
