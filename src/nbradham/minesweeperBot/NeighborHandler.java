package nbradham.minesweeperBot;

/**
 * Used when performing operations on neighboring tiles.
 * 
 * @author Nickolas S. Bradham
 *
 */
@FunctionalInterface
interface NeighborHandler {

	/**
	 * Called on each neighbor.
	 * 
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	void handle(int x, int y);
}
