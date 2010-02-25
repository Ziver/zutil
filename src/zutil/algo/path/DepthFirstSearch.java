package zutil.algo.path;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A PathFinder class implemented with DFS
 * 
 * @author Ziver
 */
public class DepthFirstSearch {
	private HashSet<PathNode> visited = new HashSet<PathNode>();

	/**
	 * Returns the first path to the destination
	 * 
	 * @param start Start Node
	 * @param stop Stop Node
	 * @return A list with the path
	 */
	public LinkedList<PathNode> find(PathNode start, PathNode stop){
		visited.clear();
		PathNode node = dfs(start, stop);
		return node.traversTo( start );
	}

	/**
	 * The DepthFirstSearch algorithm
	 * @param node The node to search from
	 * @return The stop PathNode if a path was found else null
	 */
	private PathNode dfs(PathNode node, PathNode stop){
		visited.add( node );
		if(node.equals(stop)){
			return node;
		}

		for(PathNode next : node.getNeighbors()){
			if(!visited.contains( next ) && node.getNeighborCost(next) > 0){
				next.setParentNeighbor(node);
				PathNode tmp = dfs(next, stop);
				if(tmp != null){
					return tmp;
				}
			}
		}
		return null;
	}

}
