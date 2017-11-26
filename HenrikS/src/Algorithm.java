import java.util.HashMap;
import java.util.LinkedList;

/**
 * Algorithm uses Alpha Beta pruning with memory using a Transposition table.
 * @author Henrik
 * 11/3/2017
 */
public class Algorithm implements OthelloAlgorithm {

    private double sumTime = 0;
    private double div = 0;

    public double getMeanTime() {
        return sumTime/div;
    }

    private int depth = 1;
    private long timeMillis = 0;
    private long startTime = 0;

    private OthelloEvaluator evaluator;
    private TranspositionTable tT;

    /**
     * @param evaluator Heuristic used by the algorithm
     */
    @Override
    public void setEvaluator(OthelloEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * Evaluate a board state to determine the best move for current player
     * @param position Board state.
     * @return Best move to make.
     */
    @Override
    public OthelloAction evaluate(OthelloPosition position) {
        if (position.getMoves().size() == 0)
            return new OthelloAction(0,0,true);

        tT = new TranspositionTable(1000);

        /* The root if the Game tree */
        OthelloNode root = new OthelloNode(position, null);
        root.isMaxNode = position.playerToMove;

        startTime = System.currentTimeMillis();

        for (int d = 1; d < depth; d++) {
            //tT.clear();

            /* Results are stored in each evaluated node in the tree. */
            alphaBetaWithMemory(root, -10000, 10000, d);
            //MTDF(root, 0, d);

            if (System.currentTimeMillis() - startTime >= timeMillis)
                break;
        }

        /* Get the best move for current player. */
        OthelloAction bestMove = new OthelloAction(0,0,true);
        for (SearchTreeNode<OthelloPosition> n : root.getChildren()) {
            OthelloNode on = (OthelloNode)n;


            //System.out.println("("+on.move.getRow()+","+on.move.getColumn()+")"+" p="+on.isMaxNode+" v="+on.move.getValue());

            if (position.toMove()) {
                if (bestMove.getValue() < on.move.getValue() || bestMove.isPassMove()) {
                    bestMove = on.move;
                }
            } else {
                if (bestMove.getValue() > on.move.getValue() || bestMove.isPassMove()) {
                    bestMove = on.move;
                }
            }
        }

        if (position.toMove()) {
            sumTime += (System.currentTimeMillis() - startTime);
            div++;
        }

        return bestMove;
    }

    /*public int MTDF (OthelloNode root, int f, int d) {
        int guess = f;
        int upperBound = 10000;
        int lowerBound = -10000;

        do {
            int beta = 0;
            if (guess == lowerBound)
                beta = guess+1;
            else
                beta = guess;

            guess = alphaBetaWithMemory(root, beta - 1, beta, d);

            if (guess < beta)
                upperBound = guess;
            else
                lowerBound = guess;

        } while (lowerBound >= upperBound);

        return guess;
    }*/

    /**
     * @param depth Maximum depth to be used
     */
    @Override
    public void setSearchDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @param timeMillis Maximum search time before timeout
     */
    @Override
    public void setSearchTime(long timeMillis) {
        this.timeMillis = timeMillis;
    }


    // Pseudo code at https://people.csail.mit.edu/plaat/mtdf.html#abmem

    /**
     * Alpha Beta pruning with memory using a <code>TranspositionTable</code>.
     * @param node Current node in search tree
     * @param alpha Alpha value
     * @param beta Beta value
     * @param depth Current depth. Exits at depth==0.
     * @return
     */
    public int alphaBetaWithMemory(OthelloNode node, int alpha, int beta, int depth) {
        int guess = 0;

        /* Timeout, return guess */
        if (System.currentTimeMillis() - startTime >= timeMillis) {
            /* Set value of node */
            if (node.move != null)
                node.move.setValue(guess);
            return guess;
        }

        if (tT.contains(node.getVertex().board) == true) { /* Transposition table lookup */
            if (node.lowerBound >= beta) {
                guess = tT.getBounds(node.getVertex().board).getKey();
                /* Set value of node */
                if (node.move != null)
                    node.move.setValue(guess);
                return guess;
            }
            if (node.upperBound <= alpha) {
                guess = tT.getBounds(node.getVertex().board).getValue();
                /* Set value of node */
                if (node.move != null)
                    node.move.setValue(guess);
                return guess;
            }
            alpha = Math.max(alpha, tT.getBounds(node.getVertex().board).getKey());
            beta = Math.min(beta, tT.getBounds(node.getVertex().board).getValue());
        }

        /* If depth is 0, return guess for this node and pass it upwards in the tree */
        if (depth == 0) {
            guess = evaluator.evaluate(node.getVertex()); /* leaf node */
        } else if (node.isMaxNode) { /* node is a MAXNODE */
            guess = -10000;
            int a = alpha; /* save original alpha value */
            for (SearchTreeNode<OthelloPosition> child : extendTree(node).getChildren()) {
                if (guess < beta) {
                    guess = Math.max(guess, alphaBetaWithMemory((OthelloNode)child,a,beta,depth-1));
                    a = Math.max(a,guess);
                }
            }
        } else { /* node is a MINNODE */
            guess = 10000;
            int b = beta; /* save original beta value */
            for (SearchTreeNode<OthelloPosition> child : extendTree(node).getChildren()) {
                if (guess > alpha) {
                    guess = Math.min(guess, alphaBetaWithMemory((OthelloNode)child,alpha,b,depth-1));
                    b = Math.min(b,guess);
                }
            }
        }
        /* Traditional transposition table storing of bounds */
        /* Fail low result implies an upper bound */
        if (guess <= alpha) {
            node.upperBound = guess;
        }
        /* Found an accurate minimax value - will not occur if called with zero window */
        if (guess > alpha && guess < beta) {
            node.lowerBound = guess;
            node.upperBound = guess;
        }
        /* Fail high result implies a lower bound */
        if (guess >= beta) {
            node.lowerBound = guess;
        }

        tT.store(node.getVertex().board, node.lowerBound, node.upperBound);

        /* Set value of node */
        if (node.move != null)
            node.move.setValue(guess);
        return guess;
    }

    /**
     * Extends the search tree from the current node. The node should be a leaf node or the method will
     * return without changes.
     * @param s Current node
     * @return Current node with children states
     */
    private OthelloNode extendTree(OthelloNode s) {
        if (s.getChildren().isEmpty()) {
            LinkedList<OthelloAction> list = s.getVertex().getMoves();
            for (OthelloAction move : list) {
                try {
                    OthelloNode newChild = new OthelloNode(s.getVertex().makeMove(move), move);
                    newChild.isMaxNode = !s.isMaxNode;
                    s.addChild(newChild);
                } catch (IllegalMoveException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return s;
    }

}
