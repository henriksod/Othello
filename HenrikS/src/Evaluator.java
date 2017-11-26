
/**
 * Evaluator class used with Alpha-Beta pruning as heuristic.
 * @author Henrik
 * 11/3/2017
 */
public class Evaluator implements OthelloEvaluator {

    /**
     * The Magnitude Board contains weights based on how good or bad it is
     * to have a marker on a particular tile.
     */
    int[][] magnitudeBoard =
                   {{100,-10, 5, 5, 5, 5, -10, 100},
                    {-10, -50,1, 1, 1, 1, -50, -10},
                    {5,  1,  1, 1, 1, 1, 1,   5},
                    {5,  1,  1, 0, 0, 1, 1,   5},
                    {5,  1,  1, 0, 0, 1, 1,   5},
                    {5,  1,  1, 1, 1, 1, 1,   5},
                    {-10, -50,1, 1, 1, 1, -50, -10},
                    {100,-10, 5, 5, 5, 5, -10, 100}};


    /**
     * Board evaluation method.
     * @param position The current board state.
     * @return Cost. Negative favors min player, positive favors max player.
     */
    @Override
    public int evaluate(OthelloPosition position) {
        /* Immediate Mobility */
        int mobilityAdvantage = 0;

        if (position.toMove() == true) {
            int numWhiteMoves = position.getMoves().size();
            position.setMove(false);
            int numBlackMoves = position.getMoves().size();
            position.setMove(true);
            mobilityAdvantage = numWhiteMoves - numBlackMoves;
        } else {
            int numBlackMoves = position.getMoves().size();
            position.setMove(true);
            int numWhiteMoves = position.getMoves().size();
            position.setMove(false);
            mobilityAdvantage = numWhiteMoves - numBlackMoves;
        }

        /* Potential Mobility */
        int potentialAdvantage = 0;

        for (int i = 1; i < position.BOARD_SIZE+1; i++) {
            for (int j = 1; j < position.BOARD_SIZE+1; j++) {
                if (position.board[i][j] == 'W') {
                    // Check adjacent tiles
                    for (int k = -1; k <= 1; k++) {
                        for (int m = -1; m <= 1; m++) {
                            if (i+k >= 1 && i+k <= position.BOARD_SIZE) {
                                if (j+m >= 1 && j+m <= position.BOARD_SIZE) {
                                    if (position.board[i+k][j+m] == 'E') {
                                        potentialAdvantage++;
                                    }
                                }
                            }
                        }
                    }
                } else if (position.board[i][j] == 'B') {
                    // Check adjacent tiles
                    for (int k = -1; k <= 1; k++) {
                        for (int m = -1; m <= 1; m++) {
                            if (i+k >= 1 && i+k <= position.BOARD_SIZE) {
                                if (j+m >= 1 && j+m <= position.BOARD_SIZE) {
                                    if (position.board[i+k][j+m] == 'E') {
                                        potentialAdvantage--;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* Weighted Board */
        int weightScore = 0;

        for (int i = 1; i < position.BOARD_SIZE+1; i++) {
            for (int j = 1; j < position.BOARD_SIZE+1; j++) {
                if (position.board[i][j] == 'W') {
                    weightScore += magnitudeBoard[i-1][j-1];
                } else if (position.board[i][j] == 'B') {
                    weightScore -= magnitudeBoard[i-1][j-1];
                }
            }
        }

        /* Killer move */
        int whiteMarkers = 0;
        int blackMarkers = 0;
        for (int i = 1; i < position.BOARD_SIZE-1; i++) {
            for (int j = 1; j < position.BOARD_SIZE-1; j++) {
                whiteMarkers = position.board[i][j] == 'W' ? whiteMarkers + 1 : whiteMarkers;
                blackMarkers = position.board[i][j] == 'B' ? blackMarkers + 1 : blackMarkers;
            }
        }

        int boardControl = 0;
        if (whiteMarkers != 0 && blackMarkers == 0)
            boardControl = 1000;
        if (whiteMarkers == 0 && blackMarkers != 0)
            boardControl = -1000;



        return mobilityAdvantage*20 + potentialAdvantage + boardControl + weightScore;// + cornerControl*100 - XRisk*10 ;
    }
}
