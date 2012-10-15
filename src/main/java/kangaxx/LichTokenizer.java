package kangaxx;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 4:12 PM
 */
public class LichTokenizer {

    public static final int MAX_SIZE_PREFIX_LENGTH = String.valueOf(Integer.MAX_VALUE).length();

    static enum State {
        AWAITING_INITIAL_DATA,
        EXPECTING_LEADING_SIZE_DIGIT,
        EXPECTING_ADDITIONAL_SIZE_DIGIT_OR_OPEN_MARKER,
        EXPECTING_DATA_BYTES,
        EXPECTING_CLOSE_MARKER
    }

    private State state;
    private int offset;
    private LichToken current;
    private ByteBuffer sizeAcc;


    public LichTokenizer() {
        state = State.AWAITING_INITIAL_DATA;
    }



    public boolean tokenize(byte[] data, int offset, int length, TokenizerListener tl) throws LichTokenizeException {

        if (state == State.AWAITING_INITIAL_DATA) {
            state = State.EXPECTING_LEADING_SIZE_DIGIT;
            tl.startTokenize(this);
        }

        this.sizeAcc = ByteBuffer.allocate(MAX_SIZE_PREFIX_LENGTH);

        this.offset = offset;

        for (this.offset = offset; this.offset < offset + length; this.offset++) {
            byte b = data[this.offset];

            switch (state) {
                case EXPECTING_LEADING_SIZE_DIGIT:
                    if (b >= '0' && b <= '9') {
                        push(b);
                        this.state = State.EXPECTING_ADDITIONAL_SIZE_DIGIT_OR_OPEN_MARKER;
                    } else {
                        throw new LichTokenizeException.MissingSizePrefix(this.offset);
                    }
                    break;
                case EXPECTING_ADDITIONAL_SIZE_DIGIT_OR_OPEN_MARKER:
                    if (this.current == null) {
                        throw new IllegalStateException("No current token");
                    }

                    if (b >= '0' && b <= '9') {
                        if (this.current.sizeDecLength < MAX_SIZE_PREFIX_LENGTH) {
                            this.current.sizeDecLength++;
                            sizeAcc.put(b);
                        } else {
                            throw new LichTokenizeException.ExcessiveSizePrefix(this.offset);
                        }
                    } else if (b == '<' || b == '[' || b == '{') {
                        this.current.openingMarkerOffset = this.offset;
                        this.current.openingMarkerLength = 1;

                        try {
                            this.current.parsedSize = Integer.parseInt(sizeAccToString());
                        } catch (NumberFormatException e) {
                            throw new LichTokenizeException.ExcessiveSizePrefix(this.offset);
                        }

                        this.sizeAcc.clear();

                        if (this.current.parsedSize > 0) {
                            this.current.contentOffset = this.offset +1;
                            this.current.contentLength = this.current.parsedSize;
                        } else {
                            this.current.contentOffset = 0;
                            this.current.contentLength = 0;
                        }
                        this.current.closingMarkerOffset = this.offset + 1 + this.current.parsedSize;
                        this.current.closingMarkerLength = 1;

                        switch (b) {
                            case '<':
                                this.current.parsedType = LichToken.DataType.BINARY;
                                this.state = this.current.parsedSize > 0
                                        ? State.EXPECTING_DATA_BYTES
                                        : State.EXPECTING_CLOSE_MARKER;
                                break;
                            case '[':
                                this.current.parsedType = LichToken.DataType.ARRAY;
                                this.state = this.current.parsedSize > 0
                                        ? State.EXPECTING_LEADING_SIZE_DIGIT
                                        : State.EXPECTING_CLOSE_MARKER;
                                break;
                            case '{':
                                this.current.parsedType = LichToken.DataType.DICT;
                                this.state = this.current.parsedSize > 0
                                        ? State.EXPECTING_LEADING_SIZE_DIGIT
                                        : State.EXPECTING_CLOSE_MARKER;
                                break;
                        }
                        tl.startOfToken(this.current);
                    } else {
                        throw new LichTokenizeException.InvalidSizePrefix(this.offset);
                    }
                    break;
                case EXPECTING_DATA_BYTES:
                    if (this.current == null) {
                        throw new IllegalStateException("No current token");
                    }
                    if (this.offset == this.current.endOfContent()) {
                        this.state = State.EXPECTING_CLOSE_MARKER;
                    }
                    break;
                case EXPECTING_CLOSE_MARKER:
                    if (this.current == null) {
                        throw new IllegalStateException("No current token");
                    }

                    LichToken.DataType t = this.current.parsedType;
                    if ((t == LichToken.DataType.BINARY && b == '>')
                        || (t == LichToken.DataType.ARRAY && b == ']')
                        || (t == LichToken.DataType.DICT && b == '}'))
                    {
                        tl.endOfToken(this.current);
                        if (this.current.parent != null) {
                            if (this.offset + 1 == this.current.parent.closingMarkerOffset) {
                                this.state = State.EXPECTING_CLOSE_MARKER;
                            } else {
                                state = State.EXPECTING_LEADING_SIZE_DIGIT;
                            }
                        } else {
                            this.state = State.EXPECTING_LEADING_SIZE_DIGIT;
                        }
                        this.current = this.current.parent;

                    } else {
                        throw new LichTokenizeException.IncorrectClosingMarker(this.offset);
                    }
                    break;
                default:
                    throw new LichTokenizeException.UnknownLichTokenizerState(this.state);
            }
        }

        switch (state) {
            case EXPECTING_LEADING_SIZE_DIGIT:
                break;
            case EXPECTING_ADDITIONAL_SIZE_DIGIT_OR_OPEN_MARKER:
                throw new LichTokenizeException.IncompleteSizePrefix(this.offset);
            case EXPECTING_DATA_BYTES:
                throw new LichTokenizeException.IncompleteData(this.offset);
            default:
                throw new LichTokenizeException.UnknownLichTokenizerState(this.state);
        }

        tl.finishTokenize(this);
        return true;
    }

    private void push(byte b) {
        LichToken token = new LichToken();

        token.sizeDecOffset = this.offset;
        token.sizeDecLength = 1;

        if (this.sizeAcc.position() > 0) {
            throw new IllegalStateException("Size acc buff has unexpected data: " + sizeAccToString());
        }
        this.sizeAcc.put(b);

        token.parent = this.current;
        if (this.current != null) {
            if (this.current.children == null) {
                this.current.children = new ArrayList<LichToken>();
            }
            this.current.children.add(token);
        }
        this.current = token;
    }

    private String sizeAccToString() {
        return new String(
                this.sizeAcc.array(),
                this.sizeAcc.arrayOffset(),
                this.sizeAcc.position()
        );
    }
}
