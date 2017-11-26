
/**
 * Evaluator class used with Alpha-Beta pruning as heuristic.
 * @author Henrik
 * 11/8/2017
 */
public class OthelloNode extends SearchTreeNode<OthelloPosition> {

    public int lowerBound = -10000;
    public int upperBound = 10000;
    public boolean isMaxNode = false;

    public OthelloAction move;

    /**
     * OthelloNode Constructor
     * @param vertex State of node
     * @param move Action that caused state (edge between this node and parent)
     */
    public OthelloNode(OthelloPosition vertex, OthelloAction move) {
        super(vertex);

        this.move = move;
    }

}
