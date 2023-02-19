package nbradham.minesweeperBot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import nbradham.minesweeperBot.Game.State;

final class Bot {

	private final Game game;
	private final Queue<int[]> revealedQueue = new LinkedList<>();

	private Bot(Game inputGame) {
		game = inputGame;
	}

	private void solve() {
		System.out.println("Starting board:");
		game.printBoard();

		System.out.println("Starting reveal (1,1):");
		ArrayList<int[]> ret = game.reveal(1, 1);
		game.printBoard();

		if (ret.size() == 0) {
			System.out.println(game.getState() == State.FAILED ? "Failed." : "Win.");
			return;
		}

		revealedQueue.addAll(ret);
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