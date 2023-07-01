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

	private final Tile[][] field;
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
		field = new Tile[cols][rows];
		for (byte x = 0; x < field.length; ++x)
			for (byte y = 0; y < field[x].length; ++y)
				field[x][y] = new Tile();
		mines = mineCount;
	}

	/**
	 * Outputs the current field state to console.
	 */
	void print() {
		for (int y = field[0].length - 1; y >= 0; --y) {
			for (byte x = 0; x < field.length; ++x)
				System.out.printf("%s ",
						isRevealed(x, y) ? field[x][y].mine ? "X" : field[x][y].count == 0 ? " " : field[x][y].count
								: field[x][y].flag ? "+" : ".");
			System.out.println();
		}
		System.out.println('=');
	}

	/**
	 * Reveals ({@code x}, {@code y}). If that tile does not have any mines around
	 * it, the adjacent tiles will be revealed as well and this will repeat until
	 * mines are present in adjacent tiles.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return A 2D integer array containing the coordinates of all revealed tiles
	 *         with adjacent mines.
	 */
	int[][] reveal(int x, int y) {
		if (first) {
			ArrayList<byte[]> cords = new ArrayList<>();
			for (byte c = 0; c < field.length; ++c)
				for (byte r = 0; r < field[c].length; ++r)
					if (isFartherThanOne(c, x) && isFartherThanOne(r, y))
						cords.add(new byte[] { c, r });
			Random r = new Random();
			byte[] rem;
			for (byte n = 0; n < mines; ++n) {
				field[(rem = cords.remove(r.nextInt(cords.size())))[0]][rem[1]].mine = true;
				forNeighbors(rem[0], rem[1], (incX, incY) -> field[incX][incY].count += 1);
			}
			first = false;
		}

		ArrayList<int[]> revealed = new ArrayList<>();
		Queue<int[]> queue = new LinkedList<>();
		queue.add(new int[] { x, y });
		int[] cords;
		while (queue.size() > 0) {
			field[(cords = queue.poll())[0]][cords[1]].revealed = true;
			revealed.add(cords);
			if (field[cords[0]][cords[1]].count == 0)
				forNeighbors(cords[0], cords[1], (nX, nY) -> {
					int[] tmp;
					if (isHidden(nX, nY) && isUnique(queue, tmp = new int[] { nX, nY }))
						queue.offer(tmp);
				});
		}

		if (isGameOver())
			System.out.println("Game Over.");

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
	void forNeighbors(int x, int y, NeighborHandler handler) {
		int maxX = Math.min(x + 2, field.length), maxY;
		for (int incX = Math.max(x - 1, 0); incX < maxX; ++incX) {
			maxY = Math.min(y + 2, field[incX].length);
			for (int incY = Math.max(y - 1, 0); incY < maxY; ++incY)
				if (incX != x || incY != y)
					handler.handle(incX, incY);
		}
	}

	/**
	 * If tile ({@code x}, {@code y}) is revealed, gives the displayed number, else
	 * returns -1.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The mine count of the tile or {@code -1} if that tile isn't revealed.
	 */
	int getMineCount(int x, int y) {
		if (isRevealed(x, y))
			return field[x][y].count;
		return -1;
	}

	/**
	 * Retrieves all revealed tiles with adjacent mines.
	 * 
	 * @return The retrieved tiles' coordinates.
	 */
	int[][] getHints() {
		ArrayList<int[]> hints = new ArrayList<>();
		for (byte x = 0; x < field.length; ++x)
			for (byte y = 0; y < field[x].length; ++y)
				if (field[x][y].revealed && field[x][y].count != 0)
					hints.add(new int[] { x, y });
		return hints.toArray(new int[0][]);
	}

	/**
	 * Checks if ({@code x}, {@code y}) is hidden.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if that tile is hidden.
	 */
	boolean isHidden(int x, int y) {
		return !isRevealed(x, y);
	}

	/**
	 * Checks if ({@code x}, {@code y}) is revealed.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if that tile is revealed.
	 */
	boolean isRevealed(int x, int y) {
		return field[x][y].revealed;
	}

	/**
	 * Checks if ({@code x}, {@code y}) is flagged.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if that tile is flagged.
	 */
	boolean isFlagged(int x, int y) {
		return field[x][y].flag;
	}

	/**
	 * Checks if the game is over.
	 * 
	 * @return True if the game is stopped.
	 */
	boolean isGameOver() {
		for (Tile[] c : field)
			for (Tile t : c)
				if (t.revealed) {
					if (t.mine)
						return true;
				} else if (!t.mine)
					return false;
		return true;
	}

	/**
	 * Retrieves the width of the field.
	 * 
	 * @return The width of the field.
	 */
	int getWidth() {
		return field.length;
	}

	/**
	 * Retrieves the height of the field.
	 * 
	 * @return The height of the field.
	 */
	int getHeight() {
		return field[0].length;
	}

	/**
	 * Flags a tile.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	void flag(int x, int y) {
		field[x][y].flag = isHidden(x, y);
	}

	/**
	 * Checks if {@code test} coordinates are not present in {@code cords}.
	 * 
	 * @param cords The Queue to check.
	 * @param test  The coordinates to find.
	 * @return True if {@code test} coordinates does not exist in {@code cords}.
	 */
	private static boolean isUnique(Queue<int[]> cords, int[] test) {
		for (int[] set : cords)
			if (set[0] == test[0] && set[1] == test[1])
				return false;
		return true;
	}

	/**
	 * Checks if a is farther than one away from b.
	 * 
	 * @param a The first number.
	 * @param b The second number.
	 * @return True if |a - b| > 1.
	 */
	private static boolean isFartherThanOne(int a, int b) {
		return Math.abs(a - b) > 1;
	}

	/**
	 * Holds info about a single tile.
	 * 
	 * @author Nickolas S. Bradham
	 *
	 */
	private static final class Tile {
		byte count;
		boolean mine, revealed, flag;
	}
}