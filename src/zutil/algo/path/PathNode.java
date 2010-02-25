package zutil.algo.path;

import java.util.LinkedList;


public interface PathNode {

	/**
	 * @return an Iterator with all its neighbors
	 */
	public Iterable<PathNode> getNeighbors();
	
	/**
	 * @param neighbor is the neighbor
	 * @return the cost to the neighbor
	 */
	public int getNeighborCost(PathNode neighbor);
	
	/**
	 * Sets the parent node to this one
	 */
	public void setParentNeighbor(PathNode parent);
	
	/**
	 * @return the parent node
	 */
	public PathNode getParentNeighbor();
	
	/**
	 * Traverses the parent tree and returns the path.
	 * 
	 * @param goal is the node to reach
	 * @return the path to the goal, empty list if there is no goal
	 */
	public LinkedList<PathNode> traversTo(PathNode goal);
}