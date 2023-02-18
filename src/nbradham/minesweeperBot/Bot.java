package nbradham.minesweeperBot;

final class Bot {

	private final Game game;

	private Bot(Game inputGame) {
		game = inputGame;
	}

	private void solve() {
		System.out.println("Starting board:");
		game.printBoard();
		System.out.println("Starting reveal (1,1):");
		game.reveal(1,1);
		game.printBoard();
	}

	public static final void main(String[] args) {
		if (args.length != 3) {
			System.out.println(
					"Args: <width> <height> <bombs>\n width - The width of the field.\n height - The height of the field.\n bombs - The number of bombs to place on the field");
			return;
		}

		new Bot(new Game(Byte.parseByte(args[0]), Byte.parseByte(args[1]), Byte.parseByte(args[2]))).solve();
	}
}