package nbradham.minesweeperBot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import nbradham.minesweeperBot.Game.State;

final class Bot {

	private final Game game;
	private final Queue<int[]> revealQ = new LinkedList<>(), flagQ = new LinkedList<>();

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

		revealQ.addAll(ret);

		while (!revealQ.isEmpty()) {
			int[] chk = revealQ.poll();
			System.out.printf("Checking (%d, %d)...%n", chk[0], chk[1]);
			byte bombs = game.getCount(chk[0], chk[1]), unrevs = 0;
			int ex = Math.min(chk[0] + 1, game.getWidth() - 1), ey = Math.min(chk[1] + 1, game.getHeight() - 1);
			for (int rx = Math.max(chk[0] - 1, 0); rx <= ex; rx++)
				for (int ry = Math.max(chk[1] - 1, 0); ry <= ey; ry++)
					unrevs += game.isRevealed(rx, ry) ? 0 : 1;
			if (bombs == unrevs ) {
				System.out.println("Remaining unrevealed are bombs. Flagging...");
				for (int rx = Math.max(chk[0] - 1, 0); rx <= ex; rx++)
					for (int ry = Math.max(chk[1] - 1, 0); ry <= ey; ry++)
						if (!game.isRevealed(rx, ry) && !game.isFlagged(rx, ry)) {
							game.flag(rx, ry);
							if (!flagQ.contains(chk))
								flagQ.add(chk);
						}
				game.printBoard();
			}
		}
		System.out.println("Gave up.");
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