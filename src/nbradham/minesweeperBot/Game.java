package nbradham.minesweeperBot;

import java.util.Random;

final class Game {

	private final Cell[][] board;
	private final byte bombs;
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

	final boolean reveal(int x, int y) {
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

		board[x][y].revealed = true;

		if (board[x][y].bomb)
			return false;

		int ex = Math.min(x + 1, board.length - 1), ey = Math.min(y + 1, board[0].length - 1);
		for (int rx = Math.max(x - 1, 0); rx <= ex; rx++)
			for (int ry = Math.max(y - 1, 0); ry <= ey; ry++)
				board[x][y].count += board[rx][ry].bomb ? 1 : 0;

		if (board[x][y].count == 0)
			for (int rx = Math.max(x - 1, 0); rx <= ex; rx++)
				for (int ry = Math.max(y - 1, 0); ry <= ey; ry++)
					if (!board[rx][ry].revealed)
						reveal(rx, ry);
		return true;
	}

	private static final class Cell {
		private byte count;
		private boolean revealed, flagged, bomb;
	}
}