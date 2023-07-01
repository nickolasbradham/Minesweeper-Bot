package nbradham.minesweeperBot;

@FunctionalInterface
interface NeighborHandler {
	void handle(int x, int y);
}
