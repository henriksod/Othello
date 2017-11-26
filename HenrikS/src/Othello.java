import java.util.LinkedList;
import java.util.Scanner;

/**
 * This is an Othello AI program that takes two parameters (W/B){(W/B/E)}[64] (TimeLimit),
 * where (W/B) is which player's turn, {(W/B/E)}[64] is the 8x8 board and (TimeLimit) is the maximum
 * time for the program to run. The AI uses Alpha Beta pruning with memory using a Transposition table. It also uses a
 * simple heuristic based upon a weighted board that determines the score of the board state.
 *
 * Made by Henrik Söderlund, 11/3/2017
 */
public class Othello {

    public static int maxDepth = 9;

    public static final OthelloAlgorithm othelloAlgorithm = new Algorithm();
    public static final OthelloEvaluator othelloEvaluator = new Evaluator();

    public static volatile OthelloAction bestMove = new OthelloAction(0,0);

    /* =================== BEGIN MAIN =================== */
    public static void main(String[] args) {
        runProgram(args);
        //testProgramWithItself();
        //testProgramWithHuman();
    }
    /* =================== END MAIN =================== */

    /**
     * Runs the Othello AI in evaluation mode
     * @param args
     */
    public static void runProgram (String[] args) {

        if (args.length < 2)
            throw new IllegalArgumentException("You need to specify two arguments to run this program!\n" +
                    "Usage: (W/B){(W/B/E)}[64] (TimeLimit)\n" +
                    "(W/B) is which player's turn\n" +
                    "{(W/B/E)}[64] is the 8x8 board\n" +
                    "(TimeLimit) is the maximum time for the program to run");

        final String gameState     = args[0];
        final long timeLimitMillis = (long)((new Double(args[1]))*1000.0);

        // Translate time into depth
        maxDepth = (int)Math.round((400/331)*Math.log((2500*timeLimitMillis)/10713)+2);

        final OthelloPosition othelloPosition = new OthelloPosition(gameState);

        othelloAlgorithm.setEvaluator(othelloEvaluator);
        othelloAlgorithm.setSearchDepth(maxDepth);
        othelloAlgorithm.setSearchTime(timeLimitMillis);
        bestMove = othelloAlgorithm.evaluate(othelloPosition);

        bestMove.print();

    }

    /**
     * Let's the AI be tested against a human.
     */
    public static void testProgramWithHuman () {
        Scanner scanner = new Scanner(System.in);

        final long timeLimitMillis = 3000;
        maxDepth = (int)Math.round((400/331)*Math.log((2500*timeLimitMillis)/10713)+2);

        System.out.println("Chosen depth "+maxDepth);

        OthelloPosition othelloPosition = new OthelloPosition();
        othelloPosition.initialize();
        LinkedList<OthelloAction> list = othelloPosition.getMoves();
        do {
            othelloPosition.illustrate();
            System.out.print("Make move. Row = ");
            int uRow = Integer.parseInt(scanner.nextLine());
            System.out.print("Make move. Col = ");
            int uCol = Integer.parseInt(scanner.nextLine());
            try {
                othelloPosition = othelloPosition.makeMove(new OthelloAction(uRow, uCol));

                othelloPosition.illustrate();

                othelloAlgorithm.setEvaluator(othelloEvaluator);
                othelloAlgorithm.setSearchDepth(maxDepth);
                othelloAlgorithm.setSearchTime(timeLimitMillis);
                bestMove = othelloAlgorithm.evaluate(othelloPosition);

                if (!bestMove.isPassMove()) {
                    System.out.println("Computer moves (" + bestMove.getRow() + ", " + bestMove.getColumn() + "):");
                    othelloPosition = othelloPosition.makeMove(new OthelloAction(bestMove.getRow(), bestMove.getColumn()));

                } else {
                    System.out.println("Computer pass");
                }

            } catch (IllegalMoveException ex) {
                ex.printStackTrace();
            }
            list = othelloPosition.getMoves();
        } while (!list.isEmpty());

        System.out.println("Result: "+getScore(othelloPosition));
    }

    /**
     * Tests the AI against itself.
     */
    public static void testProgramWithItself () {

        final long timeLimitMillis = 3000;
        maxDepth = (int)Math.round((400/331)*Math.log((2500*timeLimitMillis)/10713)+2);
        System.out.println("Depth = "+maxDepth);
        System.out.println("Real = "+(((400/331)*Math.log((2500*timeLimitMillis)/10713))+2));

        OthelloPosition othelloPosition = new OthelloPosition();
        othelloPosition.initialize();
        LinkedList<OthelloAction> list = othelloPosition.getMoves();
        boolean whitePass = false;
        boolean blackPass = false;
        do {

            try {
                othelloPosition.illustrate();

                othelloAlgorithm.setEvaluator(othelloEvaluator);
                othelloAlgorithm.setSearchDepth(maxDepth);
                othelloAlgorithm.setSearchTime(timeLimitMillis);
                bestMove = othelloAlgorithm.evaluate(othelloPosition);


                if (!bestMove.isPassMove()) {
                    System.out.println("Computer 1 moves ("+bestMove.getRow()+", "+bestMove.getColumn()+"):");
                    othelloPosition = othelloPosition.makeMove(new OthelloAction(bestMove.getRow(), bestMove.getColumn()));
                    whitePass = false;
                } else {
                    whitePass = true;
                    System.out.println("Computer 1 pass");
                    othelloPosition.setMove(!othelloPosition.toMove());
                }

                othelloPosition.illustrate();

                othelloAlgorithm.setEvaluator(othelloEvaluator);
                othelloAlgorithm.setSearchDepth(maxDepth);
                othelloAlgorithm.setSearchTime(timeLimitMillis);
                bestMove = othelloAlgorithm.evaluate(othelloPosition);


                if (!bestMove.isPassMove()) {
                    System.out.println("Computer 2 moves (" + bestMove.getRow() + ", " + bestMove.getColumn() + "):");
                    othelloPosition = othelloPosition.makeMove(new OthelloAction(bestMove.getRow(), bestMove.getColumn()));
                    blackPass = false;

                } else {
                    blackPass = true;
                    System.out.println("Computer 2 pass");
                    othelloPosition.setMove(!othelloPosition.toMove());
                }

            } catch (IllegalMoveException ex) {
                ex.printStackTrace();
            }
            list = othelloPosition.getMoves();
        } while (!whitePass || !blackPass);

        System.out.println("Result: "+getScore(othelloPosition));
        System.out.println("Depth: "+maxDepth);
        System.out.println("Time: "+othelloAlgorithm.getMeanTime());
    }

    /**
     * Gives the score of the board by counting markers for each player.
     * @param position Board state
     * @return score
     */
    public static int getScore(OthelloPosition position) {
        int score = 0;
        for (int i = 1; i < position.BOARD_SIZE-1; i++) {
            for (int j = 1; j < position.BOARD_SIZE-1; j++) {
                score += position.board[i][j] == 'W' ? 1 : 0;
                score -= position.board[i][j] == 'B' ? 1 : 0;
            }
        }

        return score;
    }
}
