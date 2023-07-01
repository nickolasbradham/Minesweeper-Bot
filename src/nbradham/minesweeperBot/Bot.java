package nbradham.minesweeperBot;

import java.util.Arrays;

/**
 * The agen that tries to solve the Minesweeper game.
 * 
 * @author Nickolas S. Bradham
 *
 */
final class Bot {

	private final Game game;

	/**
	 * Constructs a new Bot for {@code inputGame}.
	 * 
	 * @param inputGame
	 */
	private Bot(Game inputGame) {
		game = inputGame;
	}

	/**
	 * Attempts to solve the game.
	 */
	private void solve() {
		System.out.println("Starting field:");
		game.print();
		System.out.println("Opening reveal (1, 1):");
		System.out.println("Revealed: " + Arrays.deepToString(game.reveal(1, 1)));
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