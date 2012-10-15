package kangaxx;

/**
 * User: zhaoyao
 * Date: 10/12/12
 * Time: 4:12 PM
 */
public interface TokenizerListener {

    void startTokenize(LichTokenizer lt);

    void finishTokenize(LichTokenizer lt);

    void startOfToken(LichToken token);

    void endOfToken(LichToken token);

}
