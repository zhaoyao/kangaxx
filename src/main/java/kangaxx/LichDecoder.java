package kangaxx;

import java.util.ArrayList;
import java.util.List;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 3:58 PM
 */
public class LichDecoder implements LichTokenizerListener {

    private int currentDepth;
    private List<LichToken> topLevelTokens;

    private byte[] data;
    private Lich result;

    public Lich decode(byte[] chunk, int offset, int len) {
        this.data = chunk;
        try {
            new LichTokenizer().tokenize(chunk, offset, len, this);
        } catch (LichTokenizeException e) {
            throw new LichException("Tokenize error at " + new String(chunk, e.getOffset(), 10), e);
        }

        return this.result;
    }

    private Lich parseTokenTree(LichToken token) {
        Lich ret;
        switch (token.parsedType) {
            case BINARY:
                ret = newBinary(token);
                break;
            case ARRAY:
                ret = new LichArray(token.children.size());
                for (LichToken child : token.children) {
                    ret.asArray().add(parseTokenTree(child));
                }
                break;
            case DICT:
                ret = new LichDict(token.children.size() / 2);
                for (int i = 0; i < token.children.size(); i += 2) {
                    LichToken k = token.children.get(i);
                    LichToken v = token.children.get(i + 1);
                    ret.asDict().put(newBinary(k), parseTokenTree(v));
                }
                break;
            default:
                throw new LichException.UnexpectedLichElementType(token.parsedType);

        }

        return ret;
    }

    private LichBinary newBinary(LichToken token) {
        return new LichBinary(data, token.contentOffset, token.contentLength);
    }

    @Override
    public void startTokenize(LichTokenizer lt) {
        this.currentDepth = 0;
        this.topLevelTokens = new ArrayList<LichToken>();
    }

    @Override
    public void finishTokenize(LichTokenizer lt) {
        switch (topLevelTokens.size()) {
            case 0:
                break;
            case 1:
                this.result = parseTokenTree(this.topLevelTokens.get(0));
                break;
            default:
                LichToken root = new LichToken();
                root.parsedType = LichToken.DataType.ARRAY;
                root.children = this.topLevelTokens;
                this.result = parseTokenTree(root);

        }
    }

    @Override
    public void startOfToken(LichToken token) {
        if (this.currentDepth == 0) {
            this.topLevelTokens.add(token);
        }
        switch (token.parsedType) {
            case BINARY:
                break;
            case ARRAY:
            case DICT:
                this.currentDepth++;
                break;
            default:
                throw new LichException.UnexpectedLichElementType(token.parsedType);
        }
    }

    @Override
    public void endOfToken(LichToken token) {
        switch (token.parsedType) {
            case BINARY:
                break;
            case ARRAY:
            case DICT:
                this.currentDepth--;
                break;
            default:
                throw new LichException.UnexpectedLichElementType(token.parsedType);
        }
    }
}
