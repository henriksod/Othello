/**
 * Created by Henrik on 11/3/2017.
 */
public class BoardControlEvaluator implements OthelloEvaluator {

    @Override
    public int evaluate(OthelloPosition position) {
        int numBlack = 0;
        int numWhite = 0;

        for (int i = 1; i < position.BOARD_SIZE-1; i++) {
            for (int j = 1; j < position.BOARD_SIZE-1; j++) {
                numWhite = position.board[i][j] == 'W' ? numWhite + 1 : numWhite;
                numBlack = position.board[i][j] == 'B' ? numBlack + 1 : numBlack;
            }
        }
        //System.out.println("-- num X B = "+numBadXBlack);
        //System.out.println("-- num X W = "+numBadXWhite);
        //System.out.println("-- num C B = "+numBadCBlack);
        //System.out.println("-- num C W = "+numBadCWhite);

        return (numWhite - numBlack);
    }
}
