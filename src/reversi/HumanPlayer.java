package reversi;

import java.util.Scanner;

public class HumanPlayer extends ReversiPlayer {

	@Override
	public ReversiPiece makeMove(ReversiBoard game, int color) {
		Scanner input = new Scanner(System.in);
		System.out.println("Your move <xy>: ");

		if (game.getLegalActions(color).size() == 0) {
			System.out.println("No legal actions. Press ENTER to pass");
			input.nextLine();
			return null;
		}
		
		while (true) {

			String s1 = input.next();
			char[] s = s1.toCharArray();
			int x = 0, y = 0;

			try {
				x = Character.getNumericValue(s[0]);
				y = Character.getNumericValue(s[1]);

			} catch (Exception e) {

			}

			if (game.isLegalAction(color, x, y)) {
				game.addNFlip(color, x, y);
				return new ReversiPiece(color, x, y);
			} else {
				System.out.println("Not a legal action, try again.");
			}
		}
	}
	
	@Override
	public String toString() {
		return "Player";
	}

}
