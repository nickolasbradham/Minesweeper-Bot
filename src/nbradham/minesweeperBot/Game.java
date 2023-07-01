package nbradham.minesweeperBot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * A simplified Minesweeper game emulator.
 * 
 * @author Nickolas S. Bradham
 *
 */
final class Game {

	private static final byte F_REVEALED = 0x10, F_FLAGGED = 0x20, F_MINE = 0x40, M_COUNT = 0xF;

	private final byte[][] field;
	private final byte mines;
	private boolean first = true;

	/**
	 * Constructs a new Game
	 * 
	 * @param cols      The number of columns.
	 * @param rows      The number of rows.
	 * @param mineCount The number of mines.
	 */
	public Game(byte cols, byte rows, byte mineCount) {
		field = new byte[cols][rows];
		mines = mineCount;
	}

	/**
	 * Outputs the current field state to console.
	 */
	void print() {
		for (int y = field[0].length - 1; y >= 0; --y) {
			for (byte x = 0; x < field.length; ++x)
				System.out.printf("%s|",
						flag(field[x][y], F_REVEALED) ? flag(field[x][y], F_MINE) ? "X" : getCount(x, y)
								: flag(field[x][y], F_FLAGGED) ? "P" : "-");
			System.out.println();
		}
	}

	/**
	 * Reveals ({@code x}, {@code y}). If that tile does not have any mines around
	 * it, the adjacent tiles will be revealed as well and this will repeat until
	 * mines are present.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return A 2D integer array containing the coordinates of all revealed tiles.
	 */
	int[][] reveal(int x, int y) {
		if (first) {
			ArrayList<byte[]> cords = new ArrayList<>();
			for (byte c = 0; c < field.length; ++c)
				for (byte r = 0; r < field[x].length; ++r)
					if (c != x && r != y)
						cords.add(new byte[] { c, r });
			Random r = new Random();
			byte[] rem;
			for (byte n = 0; n < mines; ++n) {
				field[(rem = cords.remove(r.nextInt(cords.size())))[0]][rem[1]] |= F_MINE;
				forNeighbors(rem[0], rem[1], (incX, incY) -> field[incX][incY] += 1);
			}
			first = false;
		}

		ArrayList<int[]> revealed = new ArrayList<>();
		Queue<int[]> queue = new LinkedList<>();
		queue.add(new int[] { x, y });
		int[] cords;
		while (queue.size() > 0) {
			revealed.add(cords = queue.poll());
			field[cords[0]][cords[1]] |= F_REVEALED;
			if (getCount(cords[0], cords[1]) == 0)
				forNeighbors(cords[0], cords[1], (nX, nY) -> {
					if ((field[nX][nY] & F_REVEALED) != F_REVEALED)
						queue.offer(new int[] { nX, nY });
				});
		}
		return revealed.toArray(new int[0][]);
	}

	/**
	 * Calls {@link NeighborHandler#handle(int, int)} on all tiles adjacent to
	 * ({@code x}, {@code y}).
	 * 
	 * @param x       The x coordinate.
	 * @param y       The y coordinate.
	 * @param handler The NeighborHandler to call.
	 */
	private void forNeighbors(int x, int y, NeighborHandler handler) {
		int maxX = Math.min(x + 2, field.length), maxY;
		for (int incX = Math.max(x - 1, 0); incX < maxX; ++incX) {
			maxY = Math.min(y + 2, field[incX].length);
			for (int incY = Math.max(y - 1, 0); incY < maxY; ++incY)
				if (incX != x || incY != y)
					handler.handle(incX, incY);
		}
	}

	/**
	 * Retrieves the reported mine count of ({@code x}, {@code y});
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The number of adjacent mines.
	 */
	private int getCount(int x, int y) {
		return field[x][y] & M_COUNT;
	}

	/**
	 * Checks if {@code var} contains {@code flag} bit.
	 * 
	 * @param var  The variable to check.
	 * @param flag The flag to check.
	 * @return True if {@code flag} bit is true.
	 */
	private static boolean flag(byte var, byte flag) {
		return (var & flag) == flag;
	}
}