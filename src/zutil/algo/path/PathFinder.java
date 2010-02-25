package zutil.algo.path;

import java.util.LinkedList;

/**
 * All path finding algorithms should implement this interface
 * 
 * @author Ziver
 */
public interface PathFinder {

	/**
	 * Starts the search for the path from the start
	 * node to the goal.
	 * 
	 * @param start is the starting point of the search
	 * @param goal is the search goal
	 * @return a LinkedList of the path, empty list if no path was found
	 */
	public LinkedList<PathNode> find(PathNode start, PathNode goal);
}
