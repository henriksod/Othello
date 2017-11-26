import java.util.*;
import java.lang.*;

/**
 * This class is used to represent game positions. It uses a 2-dimensional char
 * array for the board and a Boolean to keep track of which player has the move.
 * 
 * @author Henrik Bj&ouml;rklund
 */

public class OthelloPosition {

	/** For a normal OthelloHelperClasses game, BOARD_SIZE is 8. */
	protected static final int BOARD_SIZE = 8;

	/** True if the first player (white) has the move. */
	protected boolean playerToMove;

	/**
	 * The representation of the board. For convenience, the array actually has
	 * two columns and two rows more that the actual game board. The 'middle' is
	 * used for the board. The first index is for rows, and the second for
	 * columns. This means that for a standard 8x8 game board,
	 * <code>board[1][1]</code> represents the upper left corner,
	 * <code>board[1][8]</code> the upper right corner, <code>board[8][1]</code>
	 * the lower left corner, and <code>board[8][8]</code> the lower right
	 * corner. In the array, the charachters 'E', 'W', and 'B' are used to
	 * represent empty, white, and black board squares, respectively.
	 */
	protected char[][] board;

    protected LinkedList<OthelloAction> moves_B = new LinkedList<>();
    protected LinkedList<OthelloAction> moves_W = new LinkedList<>();

	/** Creates a new position and sets all squares to empty. */
	public OthelloPosition() {
		board = new char[BOARD_SIZE + 2][BOARD_SIZE + 2];
		for (int i = 0; i < BOARD_SIZE + 2; i++)
			for (int j = 0; j < BOARD_SIZE + 2; j++)
				board[i][j] = 'E';

	}

	public OthelloPosition(String s) {
		if (s.length() != 65) {
			board = new char[BOARD_SIZE + 2][BOARD_SIZE + 2];
			for (int i = 0; i < BOARD_SIZE + 2; i++)
				for (int j = 0; j < BOARD_SIZE + 2; j++)
					board[i][j] = 'E';
		} else {
			board = new char[BOARD_SIZE + 2][BOARD_SIZE + 2];
			if (s.charAt(0) == 'W') {
				playerToMove = true;
			} else {
				playerToMove = false;
			}
			for (int i = 1; i <= 64; i++) {
				char c;
				if (s.charAt(i) == 'E') {
					c = 'E';
				} else if (s.charAt(i) == 'O') {
					c = 'W';
				} else {
					c = 'B';
				}
				int column = ((i - 1) % 8) + 1;
				int row = (i - 1) / 8 + 1;
				board[row][column] = c;
			}
		}

	}

	/**
	 * Initializes the position by placing four markers in the middle of the
	 * board.
	 */
	public void initialize() {
		board[BOARD_SIZE / 2][BOARD_SIZE / 2] = board[BOARD_SIZE / 2 + 1][BOARD_SIZE / 2 + 1] = 'W';
		board[BOARD_SIZE / 2][BOARD_SIZE / 2 + 1] = board[BOARD_SIZE / 2 + 1][BOARD_SIZE / 2] = 'B';
		playerToMove = true;
	}

	/* getMoves and helper functions */

	/**
	 * Returns a linked list of <code>OthelloAction</code> representing all
	 * possible moves in the position. If the list is empty, there are no legal
	 * moves for the player who has the move.
	 */

	public LinkedList getMoves() {

        if (!this.moves_W.isEmpty() && toMove() == true)
            return this.moves_W;

        if (!this.moves_B.isEmpty() && toMove() == false)
            return this.moves_B;

		char currentPlayer = toMove() == true ? 'W' : 'B';
        char otherPlayer = toMove() == true ? 'B' : 'W';

		LinkedList<OthelloAction> moves = new LinkedList<OthelloAction>();
        for (int row = 1; row <= BOARD_SIZE; row++) {
            for (int col = 1; col <= BOARD_SIZE; col++) {
                if (board[row][col] == 'E') { // We can only play on empty tiles
                    Pair<char[], int[]> columns = getColumnElemsAt(row, col);
                    Pair<char[], int[]> rows = getRowElemsAt(row, col);
                    Pair<char[], int[]> diagRight = getDiagonalElemsAtRight(row, col);
                    Pair<char[], int[]> diagLeft = getDiagonalElemsAtLeft(row, col);

                    /* Check columns */
                    boolean validMove = isValidMoveDir(columns, currentPlayer, otherPlayer).size() > 0;

                    /* Check rows */
                    if (!validMove)
                        validMove = isValidMoveDir(rows, currentPlayer, otherPlayer).size() > 0;

                    /* Check diagonal right */
                    if (!validMove)
                        validMove = isValidMoveDir(diagRight, currentPlayer, otherPlayer).size() > 0;

                    /* Check diagonal left */
                    if (!validMove)
                        validMove = isValidMoveDir(diagLeft, currentPlayer, otherPlayer).size() > 0;

                    /* Add move if valid */
                    if (validMove)
                        moves.add(new OthelloAction(row, col));
                }
            }
        }

        if (toMove() == true)
            this.moves_W = moves;
        if (toMove() == false)
            this.moves_B = moves;

        return moves;
	}

    public boolean isValidMove (int row, int col) {

        char currentPlayer = toMove() == true ? 'W' : 'B';
        char otherPlayer = toMove() == true ? 'B' : 'W';

        if (board[row][col] == 'E') { // We can only play on empty tiles
            Pair<char[], int[]> columns = getColumnElemsAt(row, col);
            Pair<char[], int[]> rows = getRowElemsAt(row, col);
            Pair<char[], int[]> diagRight = getDiagonalElemsAtRight(row, col);
            Pair<char[], int[]> diagLeft = getDiagonalElemsAtLeft(row, col);

                    /* Check columns */
            boolean validMove = isValidMoveDir(columns, currentPlayer, otherPlayer).size() > 0;

                    /* Check rows */
            if (!validMove)
                validMove = isValidMoveDir(rows, currentPlayer, otherPlayer).size() > 0;

                    /* Check diagonal right */
            if (!validMove)
                validMove = isValidMoveDir(diagRight, currentPlayer, otherPlayer).size() > 0;

                    /* Check diagonal left */
            if (!validMove)
                validMove = isValidMoveDir(diagLeft, currentPlayer, otherPlayer).size() > 0;

                    /* Add move if valid */
            if (validMove)
                return true;
        }

        return false;
    }

    /**
     * Check if valid move along a particular direction.
     * @param line Line to evaluate.
     * @param currentPlayer Current player
     * @param otherPlayer Opponent player
     * @return List of <code>Integer</code> which consists of all opponent disc indices that has
     *         to be flipped for this move.
     */
	private List<Integer> isValidMoveDir(Pair<char[], int[]> line, char currentPlayer, char otherPlayer) {
        List<Integer> opponentDiscs = new ArrayList<Integer>();
        boolean validMove = false;
        boolean foundPlayerDisc = false;
        boolean foundPlacementDisc = false;
        boolean foundOpponentDisc = false;
        for (int i = 0; i < line.getKey().length; i++) {
            if (line.getValue()[i] != 0) {
                // Check if at a disc belonging to current player
                if (line.getKey()[i] == currentPlayer && foundOpponentDisc == false) {
                    validMove = true;
                    foundPlayerDisc = true;

                    // Check if at placement disc
                } else if (line.getKey()[i] == 'P' && foundOpponentDisc == false) {
                    validMove = true;
                    foundPlacementDisc = true;

                    // Check if at an opponent's disc
                } else if (line.getKey()[i] == otherPlayer && validMove == true) {
                    foundOpponentDisc = true;
                    opponentDiscs.add(line.getValue()[i]);

                    // Check if placement disc and previous discs were found and valid
                } else if (line.getKey()[i] == 'P' && foundOpponentDisc == true &&
                        foundPlayerDisc == true && foundPlacementDisc == false && validMove == true) {
                    return opponentDiscs;

                    // Check if disc belonging to current player and previous discs were found and valid
                } else if (line.getKey()[i] == currentPlayer && foundOpponentDisc == true &&
                        foundPlacementDisc == true && foundPlayerDisc == false && validMove == true) {
                    return opponentDiscs;

                    // Check if found player disc again but not placement disc, start over
                } else if (line.getKey()[i] == currentPlayer && foundOpponentDisc == true &&
                        validMove == true && foundPlacementDisc == false) {
                    opponentDiscs.clear();
                    foundOpponentDisc = false;
                    validMove = true;
                    foundPlayerDisc = true;

                    // Check if placement disc next to current player disc and no opponent adjacent
                } else if (line.getKey()[i] == currentPlayer && foundPlacementDisc == true &&
                        validMove == true && foundOpponentDisc == false) {
                    opponentDiscs.clear();
                    return opponentDiscs;

                    // Check if empty disc, then it is an invalid move (for this position at least) start over
                } else if (line.getKey()[i] == 'E' && validMove == true) {
                    opponentDiscs.clear();
                    foundOpponentDisc = false;
                    validMove = true;
                    foundPlayerDisc = true;
                }
            }
        }
        opponentDiscs.clear();
        return opponentDiscs;
    }

    /**
     * Get all elements in the column containing the provided location.
     * @param row Row
     * @param col Column
     * @return Pair of <code>char[]</code> and <code>int[]</code> where the key is tile type and value is tile index.
     */
	private Pair<char[], int[]> getColumnElemsAt(int row, int col) {
        char[] columnElems = new char[BOARD_SIZE];
        int[] indices = new int[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i+1 == row) {
                columnElems[i] = 'P'; // Denote current position by P
            } else {
                columnElems[i] = board[i + 1][col];
            }
            indices[i] = (i + 1) * BOARD_SIZE + col;
        }
        return new Pair(columnElems, indices);
    }

    /**
     * Get all elements in the row containing the provided location.
     * @param row Row
     * @param col Column
     * @return Pair of <code>char[]</code> and <code>int[]</code> where the key is tile type and value is tile index.
     */
    private Pair<char[], int[]> getRowElemsAt(int row, int col) {
        char[] rowElems = new char[BOARD_SIZE];
        int[] indices = new int[BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i+1 == col) {
                rowElems[i] = 'P'; // Denote current position by P
            } else {
                rowElems[i] = board[row][i + 1];
            }
            indices[i] = row * BOARD_SIZE + (i + 1);
        }
        return new Pair(rowElems, indices);
    }

    /**
     * Get all elements in the left diagonal containing the provided location.
     * @param row Row
     * @param col Column
     * @return Pair of <code>char[]</code> and <code>int[]</code> where the key is tile type and value is tile index.
     */
    private Pair<char[], int[]> getDiagonalElemsAtLeft(int row,int col) {
        int x = 0;
        int y = 0;
        char[][] diagonals = new char[BOARD_SIZE*2-1][BOARD_SIZE+1];
        int[][] indices = new int[BOARD_SIZE*2-1][BOARD_SIZE+1];
        /* Init 2d arrays */
        for (int i = 0; i < BOARD_SIZE*2-1; i++) {
            for (int j = 0; j < BOARD_SIZE+1; j++) {
                diagonals[i][j] = '\0';
                indices[i][j] = 0;
            }
        }

        /* Find diagonal elems */
        for( int k = 0 ; k < BOARD_SIZE * 2 ; k++ ) {
            for( int j = 0 ; j <= k ; j++ ) {
                int i = k - j;
                if( i < BOARD_SIZE && j < BOARD_SIZE ) {
                    indices[y][x] = (i+1) * BOARD_SIZE + (j+1);
                    diagonals[y][x++] = board[i+1][j+1];
                }
            }
            x = 0;
            y++;
        }

        /* Construct */
        char[] diagonalAtPosition = new char[BOARD_SIZE];
        int[] diagonalIndicesAtPosition = new int[BOARD_SIZE];
        for (int i = 0; i < indices.length; i++) {
            for (int j = 0; j < indices[i].length; j++) {
                if (indices[i][j] == row * BOARD_SIZE + col && indices[i][j] != 0) {
                    diagonalAtPosition = diagonals[i];
                    diagonalAtPosition[j] = 'P'; // Denote current position by P
                    diagonalIndicesAtPosition = indices[i];

                    return new Pair(diagonalAtPosition,diagonalIndicesAtPosition);
                }
            }
        }

        return new Pair(diagonalAtPosition,diagonalIndicesAtPosition);
    }

    /**
     * Get all elements in the right diagonal containing the provided location.
     * @param row Row
     * @param col Column
     * @return Pair of <code>char[]</code> and <code>int[]</code> where the key is tile type and value is tile index.
     */
    private Pair<char[], int[]> getDiagonalElemsAtRight(int row,int col) {

        int x = 0;
        int y = 0;
        char[][] diagonals = new char[BOARD_SIZE*2-1][BOARD_SIZE+1];
        int[][] indices = new int[BOARD_SIZE*2-1][BOARD_SIZE+1];
        /* Init 2d arrays */
        for (int i = 0; i < BOARD_SIZE*2-1; i++) {
            for (int j = 0; j < BOARD_SIZE+1; j++) {
                diagonals[i][j] = '\0';
                indices[i][j] = 0;
            }
        }

        /* Find diagonal elems */
        for( int k = 0 ; k < BOARD_SIZE * 2 ; k++ ) {
            for( int j = 0 ; j <= k ; j++ ) {
                int i = k - j;
                if( i < BOARD_SIZE && j < BOARD_SIZE ) {
                    indices[y][x] = (i+1) * BOARD_SIZE + (BOARD_SIZE-j);
                    diagonals[y][x++] = board[i+1][BOARD_SIZE-j];
                }
            }
            x = 0;
            y++;
        }

        /* Construct */
        char[] diagonalAtPosition = new char[BOARD_SIZE];
        int[] diagonalIndicesAtPosition = new int[BOARD_SIZE];
        for (int i = 0; i < indices.length; i++) {
            for (int j = 0; j < indices[i].length; j++) {
                if (indices[i][j] == row * BOARD_SIZE + col && indices[i][j] != 0) {
                    diagonalAtPosition = diagonals[i];
                    diagonalAtPosition[j] = 'P'; // Denote current position by P
                    diagonalIndicesAtPosition = indices[i];
                    return new Pair(diagonalAtPosition,diagonalIndicesAtPosition);
                }
            }
        }

        return new Pair(diagonalAtPosition,diagonalIndicesAtPosition);
    }

	/* toMove */

	/** Returns true if the first player (white) has the move, otherwise false. */
	public boolean toMove() {
		return playerToMove;
	}

    /** Returns true if the first player (white) has the move, otherwise false. */
    public void setMove(boolean toMove) {
        playerToMove = toMove;
    }

	/* makeMove and helper functions */

	/**
	 * Returns the position resulting from making the move <code>action</code>
	 * in the current position. Observe that this also changes the player to
	 * move next.
	 */
	public OthelloPosition makeMove(OthelloAction action)
			throws IllegalMoveException {

        /* Check if valid move by looking at available moves in state. */
        boolean validMove = false;
        for(Object move : getMoves()) {
            if (((OthelloAction)move).getRow() == action.getRow()
                    && ((OthelloAction)move).getColumn() == action.getColumn()) {
                validMove = true;
            }
        }

        /* Not valid move, throw exception. */
        if (!validMove)
            throw new IllegalMoveException(action);

        char currentPlayer = toMove() == true ? 'W' : 'B';
        char otherPlayer = toMove() == true ? 'B' : 'W';

        /* Only play on empty tiles. */
        if (board[action.getRow()][action.getColumn()] == 'E') { // We can only play on empty tiles

            /* Get all columns rows and diagonals from move location. */
            Pair<char[], int[]> columns = getColumnElemsAt(action.getRow(), action.getColumn());
            Pair<char[], int[]> rows = getRowElemsAt(action.getRow(), action.getColumn());
            Pair<char[], int[]> diagRight = getDiagonalElemsAtRight(action.getRow(), action.getColumn());
            Pair<char[], int[]> diagLeft = getDiagonalElemsAtLeft(action.getRow(), action.getColumn());

            /* Create new board position. */
            OthelloPosition newPosition = clone();

            // Update Columns
            List<Integer> opponentDiscs = isValidMoveDir(columns, currentPlayer, otherPlayer);
            if (opponentDiscs.size() > 0) {
                for (Integer i : opponentDiscs) {
                    int coli = ((i - 1) % BOARD_SIZE) + 1;
                    int rowi = (i - 1) / BOARD_SIZE;
                    newPosition.board[rowi][coli] = currentPlayer;
                }
            }

            // Update Rows
            opponentDiscs = isValidMoveDir(rows, currentPlayer, otherPlayer);
            if (opponentDiscs.size() > 0) {
                for (Integer i : opponentDiscs) {
                    int coli = ((i - 1) % BOARD_SIZE) + 1;
                    int rowi = (i - 1) / BOARD_SIZE;
                    newPosition.board[rowi][coli] = currentPlayer;
                }
            }

            // Update Right Diagonal
            opponentDiscs = isValidMoveDir(diagRight, currentPlayer, otherPlayer);
            if (opponentDiscs.size() > 0) {
                for (Integer i : opponentDiscs) {
                    int coli = ((i - 1) % BOARD_SIZE) + 1;
                    int rowi = (i - 1) / BOARD_SIZE;
                    newPosition.board[rowi][coli] = currentPlayer;
                }
            }

            // Update Left Diagonal
            opponentDiscs = isValidMoveDir(diagLeft, currentPlayer, otherPlayer);
            if (opponentDiscs.size() > 0) {
                for (Integer i : opponentDiscs) {
                    int coli = ((i - 1) % BOARD_SIZE) + 1;
                    int rowi = (i - 1) / BOARD_SIZE;
                    newPosition.board[rowi][coli] = currentPlayer;
                }
            }

            /* Change move location tile to current player and switch turn. */
            newPosition.board[action.getRow()][action.getColumn()] = currentPlayer;
            newPosition.setMove(!toMove());
            return newPosition;
        } else {
            throw new IllegalMoveException(action);
        }
	}


    public LinkedList<OthelloAction> getActions () {
        if (toMove() == true)
            return moves_W;
        else
            return moves_B;
    }

	/**
	 * Returns a new <code>OthelloPosition</code>, identical to the current one.
	 */
	protected OthelloPosition clone() {
		OthelloPosition newPosition = new OthelloPosition();
		newPosition.playerToMove = playerToMove;
		for (int i = 0; i < BOARD_SIZE + 2; i++)
			for (int j = 0; j < BOARD_SIZE + 2; j++)
				newPosition.board[i][j] = board[i][j];
		return newPosition;
	}

	/* illustrate and other output functions */

	/**
	 * Draws an ASCII representation of the position. White squares are marked
	 * by '0' while black squares are marked by 'X'.
	 */
	public void illustrate() {
		System.out.print("   ");
		for (int i = 1; i <= BOARD_SIZE; i++)
			System.out.print("| " + i + " ");
		System.out.println("|");
		printHorizontalBorder();
		for (int i = 1; i <= BOARD_SIZE; i++) {
			System.out.print(" " + i + " ");
			for (int j = 1; j <= BOARD_SIZE; j++) {
				if (board[i][j] == 'W') {
					System.out.print("| 0 ");
				} else if (board[i][j] == 'B') {
					System.out.print("| X ");
				} else {
					System.out.print("|   ");
				}
			}
			System.out.println("| " + i + " ");
			printHorizontalBorder();
		}
		System.out.print("   ");
		for (int i = 1; i <= BOARD_SIZE; i++)
			System.out.print("| " + i + " ");
		System.out.println("|\n");
	}

	private void printHorizontalBorder() {
		System.out.print("---");
		for (int i = 1; i <= BOARD_SIZE; i++) {
			System.out.print("|---");
		}
		System.out.println("|---");
	}

	public String toString() {
		String s = "";
		char c, d;
		if (playerToMove) {
			s += "W";
		} else {
			s += "B";
		}
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				d = board[i][j];
				if (d == 'W') {
					c = 'O';
				} else if (d == 'B') {
					c = 'X';
				} else {
					c = 'E';
				}
				s += c;
			}
		}
		return s;
	}

}
