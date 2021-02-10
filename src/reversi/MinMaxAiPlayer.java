
package reversi;

import java.awt.Point;
import java.util.Set;

public class MinMaxAiPlayer extends ReversiPlayer {

	private long global_time_limit;
	public final long DESIRED_TIME_LIMIT;
	private final ReversiBoard.BoardEvaluator heuristics;
	
	// Because the server is unpredictable if the time limit is exceeded this factor is used. 
	// Hopefully it will compensate for network delays etc.
	private double time_margin_factor = 0.9; 
	

	public MinMaxAiPlayer(long time_limit, ReversiBoard.BoardEvaluator heuristics) {
		this.global_time_limit = (long) (time_margin_factor* time_limit);
		this.DESIRED_TIME_LIMIT = (long) (time_margin_factor* time_limit);
		this.heuristics = heuristics;
	}

	public void scaleTimeLimit(double f) {
		global_time_limit = (long) (global_time_limit * f);
	}

	public long getTimeLimit() {
		return global_time_limit;
	}

	@Override
	public ReversiPiece makeMove(ReversiBoard board, int ai_color) {
		long start_time = System.nanoTime();

		Set<Point> legal_actions = board.getLegalActions(ai_color);

		if (legal_actions.size() == 0) {
			return null;
		}

		int max_value = Integer.MIN_VALUE;
		Point best_tile = null;
		long time_limit = global_time_limit / legal_actions.size();
		long done_by = start_time + time_limit;
		for (Point tile : legal_actions) {

			ReversiBoard game_copy = board.copy();
			game_copy.addNFlip(ai_color, tile.x, tile.y);

			int branch_value = calculateBranchScore(game_copy, false, ai_color, done_by, time_limit);

			if (branch_value >= max_value) {
				max_value = branch_value;
				best_tile = tile;
			}
			done_by += time_limit;

		}

		if (best_tile == null) {
			// No move to make, do nothing
		} else {
			// Make calculated best move.

			board.addNFlip(ai_color, best_tile.x, best_tile.y);
		}
		
		return new ReversiPiece(ai_color, best_tile.x, best_tile.y);

	}

	private int calculateBranchScore(ReversiBoard simulated_game, boolean is_maximizer_turn, int ai_color, long done_by,
			long time_limit) {

		long start_time = System.nanoTime();

		if (System.nanoTime() >= done_by - 12000) {

			int state_score = simulated_game.evalState(ai_color, heuristics);

			// Return state score
			return state_score;

			// Else: keep simulating.
		} else {

			// Current player
			int current_sim_color = (is_maximizer_turn) ? ai_color : ReversiBoard.getInvertedColor(ai_color);

			// If no legal actions exists...
			Set<Point> legal_actions = simulated_game.getLegalActions(current_sim_color);
			if (legal_actions.size() == 0) {
				return simulated_game.evalState(ai_color, heuristics);
			}
			

			int min_value = Integer.MAX_VALUE;
			int max_value = -Integer.MAX_VALUE;

			// For each legal action in current state...
			time_limit = time_limit / legal_actions.size();
			done_by = start_time + time_limit;
			for (Point tile : legal_actions) {

				// Create a copy of the current game and add piece.
				ReversiBoard game_copy = simulated_game.copy();

				game_copy.addNFlip(current_sim_color, tile.x, tile.y);

				int branch_value = calculateBranchScore(game_copy, !is_maximizer_turn, ai_color, done_by, time_limit);
				done_by += time_limit;
				// Update min/max values
				if (branch_value >= max_value) {
					max_value = branch_value;
				} else if (branch_value <= min_value) {
					min_value = branch_value;
				}
			} // END OF LOOP

			// Determine what value to return based on who's turn it is.
			if (is_maximizer_turn) {
				return max_value;
			} else {
				return min_value;
			}

		}

	}

	@Override
	public String toString() {
		return "MinMax Ai";
	}

}
