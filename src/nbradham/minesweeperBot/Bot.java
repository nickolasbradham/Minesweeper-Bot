package nbradham.minesweeperBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The agen that tries to solve the Minesweeper game.
 * 
 * @author Nickolas S. Bradham
 *
 */
final class Bot {

	/**
	 * Keeps track of what check to perform on a queued tile.
	 * 
	 * @author Nickolas S. Bradham
	 *
	 */
	private static enum Phase {
		FLAG_REMAINING, REVEAL_IF_SAFE
	};

	private final Queue<int[]> queue = new LinkedList<>();
	private final Game game;
	private final Phase[][] phase;

	/**
	 * Constructs a new Bot for {@code inputGame}.
	 * 
	 * @param inputGame
	 */
	private Bot(Game inputGame) {
		game = inputGame;
		phase = new Phase[game.getWidth()][game.getHeight()];
		for (byte x = 0; x < phase.length; ++x)
			Arrays.fill(phase[x], Phase.FLAG_REMAINING);
	}

	/**
	 * Attempts to solve the game.
	 */
	private void solve() {
		printField("Starting field:");
		queue.addAll(Arrays.asList(reveal("Opening Reveal", 1, 1)));
		ArrayList<int[]> tmp = new ArrayList<>();
		while (!game.isGameOver()) {
			while (queue.size() != 0) {
				int[] cords = queue.poll();
				System.out.printf("Processing %s...%n", Arrays.toString(cords));
				byte[] flags = { 0 };
				switch (phase[cords[0]][cords[1]]) {
				case FLAG_REMAINING:
					tmp.clear();
					game.forNeighbors(cords[0], cords[1], (x, y) -> {
						if (game.isHidden(x, y))
							tmp.add(new int[] { x, y });
					});
					if (tmp.size() == game.getMineCount(cords[0], cords[1])) {
						System.out.println("Flagging remaining tiles...");
						tmp.forEach(at -> {
							game.flag(at[0], at[1]);
							game.forNeighbors(at[0], at[1], (x, y) -> {
								queueUp(x, y, Phase.REVEAL_IF_SAFE);
							});
						});
						break;
					} else
						phase[cords[0]][cords[1]] = Phase.REVEAL_IF_SAFE;
				case REVEAL_IF_SAFE:
					flags[0] = 0;
					tmp.clear();
					game.forNeighbors(cords[0], cords[1], (x, y) -> {
						if (game.isFlagged(x, y))
							++flags[0];
						else if (game.isHidden(x, y))
							tmp.add(new int[] { x, y });
					});
					if (flags[0] == game.getMineCount(cords[0], cords[1]))
						tmp.forEach(xy -> {
							queueUp(reveal("Safe Reveal", xy[0], xy[1]));
							game.forNeighbors(xy[0], xy[1], (x, y) -> queueUp(x, y, Phase.FLAG_REMAINING));
						});
					break;
				default:
					System.out.printf("Skipped %s.%n", Arrays.toString(cords));
				}
				printField("After Iteration:");
			}
			System.out.println("Requeuing...");
			queueUp(game.getHints());
		}
	}

	/**
	 * Calls {@link #queueUp(int, int, Phase)} on each element of {@code arr}
	 * 
	 * @param arr The coordinates to queue.
	 */
	private void queueUp(int[][] arr) {
		for (int[] xy : arr)
			queueUp(xy[0], xy[1], Phase.FLAG_REMAINING);
	}

	/**
	 * Checks if ({@code x}, {@code y}) is revealed and not already queued before
	 * adding it to the queue.
	 * 
	 * @param x        The x coordinate.
	 * @param y        The y coordinate.
	 * @param chkPhase The Phase to give the tile.
	 */
	private void queueUp(int x, int y, Phase chkPhase) {
		if (game.isRevealed(x, y) && notQueued(x, y)) {
			queue.offer(new int[] { x, y });
			phase[x][y] = chkPhase;
		}
	}

	/**
	 * Checks if ({@code x}, {@code y}) is queued for processing.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return True if the tile is not queued for processing.
	 */
	private boolean notQueued(int x, int y) {
		for (int[] cords : queue)
			if (cords[0] == x && cords[1] == y)
				return false;
		return true;
	}

	/**
	 * Reveals and prints ({@code x}, {@code y}).
	 * 
	 * @param label The console label to display.
	 * @param x     The x coordinate.
	 * @param y     The y coordinate.
	 * @return An array of all revealed tiles.
	 */
	private int[][] reveal(String label, int x, int y) {
		int[][] revealed = game.reveal(x, y);
		System.out.printf("%s (%d, %d): %s%n", label, x, y, Arrays.deepToString(revealed));
		game.print();
		return revealed;
	}

	/**
	 * Prints the game field to console.
	 * 
	 * @param label The console label to display.
	 */
	private void printField(String label) {
		System.out.println(label);
		game.print();
	}

	/**
	 * Constructs a new {@link Game} and Bot to solve the game. Then calls
	 * {@link Bot#solve()}.
	 * 
	 * @param args Command line arguments.
	 */
	public static final void main(String[] args) {
		if (args.length != 3) {
			System.out.println(
					"Args: <width> <height> <bombs>\n width - The width of the field.\n height - The height of the field.\n bombs - The number of bombs to place on the field");
			return;
		}

		new Bot(new Game(Byte.parseByte(args[0]), Byte.parseByte(args[1]), Byte.parseByte(args[2]))).solve();
	}
}