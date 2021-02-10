package reversi;

import java.util.Scanner;

public class HumanVsAiGame {
	
	public static void main(String[] args) {
		new HumanVsAiGame().start();
	}
	
	
	public void start() {
		ReversiBoard rg = new ReversiBoard();
		Scanner input = new Scanner(System.in);

		int human_color = -1;

		while (true) {
			System.out.print("Choose Color (b/w): ");

			String color = input.nextLine();

			if (color.equals("b")) {
				human_color = ReversiBoard.BLACK;
				break;
			} else if (color.equals("w")) {
				human_color = ReversiBoard.WHITE;
				break;
			}
		}

		int ai_color = ReversiBoard.getInvertedColor(human_color);
		long ai_time = 1;
		while (true) {
			System.out.print("Choose AI target calculation time (ms): ");

			try {
				ai_time = Integer.parseInt(input.nextLine()) * 1000000l;
				break;
			} catch (Exception e) {
			}
		}

	
		//MinMaxAiPlayer ai_player = new MinMaxAiPlayer(ai_time, ReversiBoard.HEURISTICS_H2);
		MinMaxAiPlayer ai_player = new MinMaxAiPlayer(ai_time, ReversiBoard.HEURISTICS_H3);
		ReversiPlayer human_player = new HumanPlayer();

		boolean my_turn = true;
		while (true) {

			System.out.println(rg.getASCII(human_color));

			long tic = 0, toc = 0;
			if (my_turn) {
				human_player.makeMove(rg, human_color);
			} else {

				tic = System.currentTimeMillis();
				ai_player.makeMove(rg, ai_color);
				toc = System.currentTimeMillis() - tic;
				
				System.out.println("\n_______________________________________");
				System.out.println("Time taken by AI: " + toc + " ms");
				/*long ai_time_limit = ai_player.DESIRED_TIME_LIMIT / 1000000;
				
				if (toc > ai_time_limit) {
					double adjustment_factor =  1 - (toc - ai_time_limit) / (double) toc;
					
					ai_player.scaleTimeLimit(adjustment_factor);  // Reduce the time limit
					
					System.err.println("Calculation time was above threashold. \nReadjusting time limit.");
					System.err.println("Time limit adjustment factor: " + Math.round(adjustment_factor*100)/100.0 );
				} else {
					
				}*/
				System.out.println("_______________________________________");
			}
			
			my_turn = !my_turn;

			if (rg.isTerminalState() || rg.noMovesPossible()) {
				break;
			}
			

		}
		
		System.out.println(rg.getASCII(human_color));

		System.out.println("*********");
		System.out.println("GAME OVER");
		System.out.println(human_player.toString() + ": " + rg.colorScore(human_color));
		System.out.println(ai_player.toString() + ": " + rg.colorScore(ai_color));

	}

}
