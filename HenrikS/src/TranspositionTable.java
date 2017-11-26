import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Transposition table used for Alpha-Beta pruning with memory. It extends
 * <code>HashMap<Integer, Pair<Integer, Integer>></code> where key is Zobrist value and value is pair of lowerBound and
 * upperBound.
 * @author Henrik
 * 11/9/2017
 */
public class TranspositionTable extends LinkedHashMap<String, Pair<Integer, Integer>> {

    private int MAX_ENTRIES;

    /**
     * TranspositionTable constructor
     * @param maxEntries Maximum entries in table.
     */
    public TranspositionTable(int maxEntries) {
        super(maxEntries, 0.75f, true);
        this.MAX_ENTRIES = maxEntries;
    }

    /**
     * Get bounds.
     * @param board Board.
     */
    public Pair<Integer, Integer> getBounds (char[][] board) {

        String key = boardToString(board);
        return get(key);
    }

    /**
     * Find state.
     * @param board Board.
     */
    public boolean contains (char[][] board) {

        String key = boardToString(board);
        return containsKey(key);
    }

    /**
     * Store state.
     * @param board Board.
     * @param lowerBound Lower bound.
     * @param upperBound Upper bound.
     */
    public void store (char[][] board, int lowerBound, int upperBound) {

        String key = boardToString(board);

        if (containsKey(key)) {
            replace(key, new Pair<>(lowerBound, upperBound));
        } else {
            put(key, new Pair<>(lowerBound, upperBound));
        }
    }

    private String boardToString (char[][] board) {
        String str = "";
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                str += board[i][j];
            }
        }
        return str;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Pair<Integer, Integer>> eldest) {
        return size() > MAX_ENTRIES;
    }
}
