package reversi;

import java.awt.Point;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Most of the code are written during the Applied Artificial Intelligence
 * (EDAF70) course, 2018-01-19.
 */

public class ReversiBoard {
	private ReversiPiece[][] gameBoard;

	public final static int BLANK = 0, WHITE = 1, BLACK = 2;

	/**
	 * Homogeneous heuristics. Counts occupied tiles that belongs to certain color.
	 *  Own Tile: 	+1
	 *  Enemy Tile:	-1
	 */
	public static final BoardEvaluator HEURISTICS_H1 = new BoardEvaluator() {
		@Override
		public int evaluateGameState(ReversiBoard game, int color) {

			int score = 0;

			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 7; y++) {
					if (game.gameBoard[x][y].getColor() == color) {
						score++;
					} else if (game.gameBoard[x][y].getColor() == ReversiBoard.getInvertedColor(color)) {
						score--;
					}
				}
			}

			return score;
		}
	};

	/**
	 * Avoids buffers and tries to get the edges and corners.
	 *  Edge: 		+5
	 *  Buffer: 	-5
	 *  Corner:		+10
	 *  Other own:	+1
	 */
	public static final BoardEvaluator HEURISTICS_H2 = new BoardEvaluator() {

		@Override
		public int evaluateGameState(ReversiBoard game, int color) {
			int score = 0;
			int c;

			if (game.isTerminalState()) {
				if (game.evalState(color, HEURISTICS_H1) < 0) {
					// If true, this means the opponent wins!!
					return Integer.MIN_VALUE;
				} else {
					return game.evalState(color, HEURISTICS_H1);
				}
			}

			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					c = game.gameBoard[x][y].getColor();
					if (c == color && isCorner(x, y)) {
						score += 8;
					} else if (c == color && isBuffer(x, y)) {
						score += -2;
					} else if (c == color && isEdge(x, y)) {
						score += 2;
					} else if (c == BLANK) {
					} else if (c == color) {
						score++;
					}
				}
			}
			return score;
		}
	};
	
	/**
	 * Avoid buffers and try to get edges. Also includes the enemy's score. Uses the HEURISTICS_H1 to evaluate the enemy's score.
	 */
	public static final BoardEvaluator HEURISTICS_H3 = new BoardEvaluator() {

		@Override
		public int evaluateGameState(ReversiBoard game, int color) {
			int score = ReversiBoard.HEURISTICS_H2.evaluateGameState(game, color);
			score = score - ReversiBoard.HEURISTICS_H2.evaluateGameState(game, ReversiBoard.getInvertedColor(color));
			return score;
		}
	};

	public ReversiBoard() {
		gameBoard = new ReversiPiece[8][8];
		initializeBoard();
	}

	/**
	 * 
	 * @return Returns true if every game tile is occupied.
	 */
	public boolean isTerminalState() {

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				if (gameBoard[x][y].getColor() == BLANK) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean noMovesPossible() {
		if (getLegalActions(BLACK).size() == 0 && getLegalActions(WHITE).size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return Returns copy of this Reversi board.
	 */
	public ReversiBoard copy() {

		ReversiBoard new_game = new ReversiBoard();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				new_game.addPiece(gameBoard[i][j].getColor(), i, j);
			}
		}

		return new_game;
	}

	/*
	 * Set up the game board with middle pieces as per Reversi rules
	 */
	private void initializeBoard() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if ((i == 3 && j == 3) || (i == 4 && j == 4)) {
					gameBoard[i][j] = new ReversiPiece(WHITE, i, j);
				} else if ((i == 4 && j == 3) || (i == 3 && j == 4)) {
					gameBoard[i][j] = new ReversiPiece(BLACK, i, j);
				} else {
					gameBoard[i][j] = new ReversiPiece(BLANK, i, j);
				}
			}
		}
	}

	public boolean isLegalAction(int color, int x, int y) {
		return traverse(color, x, y, false);
	}

	public boolean addNFlip(int color, int x, int y) {
		boolean can_flip = traverse(color, x, y, true);
		gameBoard[x][y].setColor(color);
		return can_flip;
	}

	private boolean traverse(int color, int x, int y, boolean isPlayerMove) {
		if (isOutOfBounds(x, y)) {
			return false;
		}
		if (gameBoard[x][y].getColor() != BLANK) {
			return false;
		}

		boolean isLegal = false;
		// For every possible direction.. (dx = [-1..1] and dy = [-1..1])
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {

				if (!(dx == 0 && dy == 0)) {

					if (checkDirection(color, x + dx, y + dy, dx, dy, 1, isPlayerMove)) {

						isLegal = true;

						if (!isPlayerMove) {
							return true;
						}
					}
				}
			}
		}

		return isLegal;
	}

	public static int getInvertedColor(int color) {
		return (color == BLACK) ? WHITE : BLACK;
	}

	private boolean checkDirection(int color, int x, int y, int dx, int dy, int distance, boolean isPlayerMove) {

		if (isOutOfBounds(x, y) || gameBoard[x][y].getColor() == BLANK) {
			return false;
		}

		if (gameBoard[x][y].getColor() == color && distance > 1) {
			// If the distance is over 1 and the same color is found again we
			// have surrounded the opponent.
			return true;
		} else if (gameBoard[x][y].getColor() == getInvertedColor(color)) {
			// If we find the opponent's color we continue traversing the board.
			boolean legalDirection = checkDirection(color, x + dx, y + dy, dx, dy, distance + 1, isPlayerMove);

			if (legalDirection && isPlayerMove) {
				gameBoard[x][y].flipPiece();
			}
			return legalDirection;

		} else {
			// If we find our own color and the distance is <1.
			return false;
		}

	}

	/*
	 * Apply the action that player made on GUI with XY coordinates?
	 */
	private void addPiece(int color, int x, int y) {
		gameBoard[x][y].setColor(color);
	}

	public int evalState(int color, BoardEvaluator heuristics) {
		return heuristics.evaluateGameState(this, color);

	}

	private static boolean isCorner(int x, int y) {
		return (x == 0 && (y == 0 || y == 7)) || (x == 7 && (y == 0 || y == 7));
	}

	private static boolean isEdge(int x, int y) {
		boolean onSide = (x == 0 || y == 0 || x == 7 || y == 7);
		return onSide && ((!isBuffer(x, y)) && (!isBuffer(x, y))); // On the side and not an edge or buffer.
	}

	private static boolean isBuffer(int x, int y) {
		boolean a = ((x == 1) || x == 6) && (y == 0 || y == 1 || y == 6 || y == 7);
		boolean b = ((x == 0) || (x == 7)) && (y == 1 || y == 6);
		return a || b;
	}

	/*
	 * See all possible actions for player
	 */
	public Set<Point> getLegalActions(int color) {

		Set<Point> legal = new HashSet<>();

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (isLegalAction(color, x, y)) {
					legal.add(new Point(x, y));
				}
			}
		}
		return legal;
	}

	public boolean isOutOfBounds(int x, int y) {
		if (x > 7 || x < 0 || y > 7 || y < 0)
			return true;
		else
			return false;
	}

	public int colorScore(int color) {
		int score = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (gameBoard[i][j].getColor() == color) {
					score++;
				}
			}
		}
		return score;
	}

	public String getASCII(int perspective) {

		final int COLOR = perspective;

		StringBuilder builder = new StringBuilder();

		builder.append("   ");
		for (int x = 0; x < 8; x++) {
			builder.append(x);
			builder.append("  ");
		}

		builder.append("x -->");
		builder.append("\n");

		Set<Point> legal_moves = getLegalActions(COLOR);

		for (int y = 0; y < 8; y++) {

			builder.append(y);
			builder.append(" ");

			for (int x = 0; x < 8; x++) {

				builder.append("[");

				String p = " ";
				if (gameBoard[x][y].getColor() == WHITE) {
					p = "O";
				} else if (gameBoard[x][y].getColor() == BLACK) {
					p = "#";
				}

				for (Point point : legal_moves) {
					if (point.x == x && point.y == y) {
						p = "+";
					}
				}

				builder.append(p);
				builder.append("]");

				if (x == 7 && y == 1) {
					builder.append("  State evaluation (#)(H2): " + evalState(BLACK, HEURISTICS_H2));
				} else if (x == 7 && y == 2) {
					builder.append("  State evaluation (O)(H2): " + evalState(WHITE, HEURISTICS_H2));
				} else if (x == 7 && y == 4) {
					builder.append("\tO = WHITE");
				} else if (x == 7 && y == 5) {
					builder.append("\t# = BLACK");
				} else if (x == 7 && y == 6) {
					builder.append("\t+ = LEGAL MOVES");
				} else if (x == 7 && y == 7) {
					builder.append("\tScore  #=" + colorScore(BLACK) + " : O=" + colorScore(WHITE));
				}
			}

			builder.append("\n");
		}

		builder.append("y\n|\nv");

		return builder.toString();
	}

	public interface BoardEvaluator {

		/**
		 * 
		 * @param color Color to evaluate for
		 * @return Returns state score for inserted color
		 */
		public int evaluateGameState(ReversiBoard game, int color);

	}

}
