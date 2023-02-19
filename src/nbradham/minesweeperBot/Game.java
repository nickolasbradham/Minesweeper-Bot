package nbradham.minesweeperBot;

import java.util.ArrayList;
import java.util.Random;

final class Game {
	
	static enum State{PLAYING, FAILED, WON};

	private final Cell[][] board;
	private final byte bombs;
	
	private State state = State.PLAYING;
	private boolean first = true;

	Game(byte width, byte height, byte numBombs) {
		board = new Cell[height][width];
		for (byte x = 0; x < board.length; x++)
			for (byte y = 0; y < board[x].length; y++)
				board[x][y] = new Cell();

		bombs = numBombs;
	}

	final void printBoard() {
		for (Cell[] row : board) {
			for (Cell c : row)
				System.out.print((c.revealed ? c.count : c.flagged ? "F" : " ") + "|");
			System.out.println();
			for (byte n = 0; n < row.length; n++)
				System.out.print("-|");
			System.out.println();
		}
	}

	final ArrayList<int[]> reveal(int x, int y) {
		if (first) {
			Random rand = new Random();
			for (byte n = 0; n < bombs; n++) {
				int tx, ty;
				do {
					tx = rand.nextInt(board.length);
					ty = rand.nextInt(board[0].length);
				} while (board[tx][ty].bomb || (Math.abs(x - tx) <= 1 && Math.abs(y - ty) <= 1));
				board[tx][ty].bomb = true;
			}
			first = false;
		}

		ArrayList<int[]> revealArr = new ArrayList<>();

		board[x][y].revealed = true;
		revealArr.add(new int[] { x, y });

		if (board[x][y].bomb) {
			state = State.FAILED;
			return new ArrayList<>();
		}

		int ex = Math.min(x + 1, board.length - 1), ey = Math.min(y + 1, board[0].length - 1);
		for (int rx = Math.max(x - 1, 0); rx <= ex; rx++)
			for (int ry = Math.max(y - 1, 0); ry <= ey; ry++)
				board[x][y].count += board[rx][ry].bomb ? 1 : 0;

		if (board[x][y].count == 0)
			for (int rx = Math.max(x - 1, 0); rx <= ex; rx++)
				for (int ry = Math.max(y - 1, 0); ry <= ey; ry++)
					if (!board[rx][ry].revealed)
						revealArr.addAll(reveal(rx, ry));
		
		for (Cell[] row : board)
			for (Cell c : row)
				if(!c.revealed && !c.bomb)
					return revealArr;
		
		state = State.WON;
		return new ArrayList<>();
	}
	
	final State getState() {
		return state;
	}

	private static final class Cell {
		private byte count;
		private boolean revealed, flagged, bomb;
	}
}