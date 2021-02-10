package reversi;

public class ReversiPiece {
	private int color;
	private int x;
	private int y;

	public ReversiPiece(int i, int x, int y) {
		color = i;
		this.x = x;
		this.y = y;

	}

	public void flipPiece() {
		if (color != 0) {
			if (color == 1) {
				setColor((2));
			} else {
				setColor(1);
			}
		}
	}

	public void setColor(int x) {
		color = x;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getColor() {
		return color;
	}
}
