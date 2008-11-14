package zutil.algo.path;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A class for simple path finding algorithms
 * 
 * @author Ziver
 */
public class SimplePathFinder {

	/**
	 * Returns the first path to the destination
	 * 
	 * @param start Start Node
	 * @param stop Stop Node
	 * @return A list with the path
	 */
	public LinkedList<PathNode> BreadthFirstSearch(PathNode start, PathNode stop){
		Queue<PathNode> queue = new LinkedList<PathNode>();
		
		queue.add(start);
		start.setVisited(true);
		
		PathNode tmp;
		while(!queue.isEmpty()){
			tmp = queue.poll();	
			
			for(PathNode next : tmp.getNeighbors()){				
				if(!next.visited() && tmp.getNeighborCost(next) > 0){
					queue.add(next);
					next.setVisited(true);
					next.setSourceNeighbor(tmp);
					
					if(next.equals(stop)){					
						LinkedList<PathNode> path = new LinkedList<PathNode>();
						for(PathNode current=stop; !current.equals(start) ;current = current.getSourceNeighbor()){
							path.addFirst(current);
						}
						path.addFirst(start);
						return path;
					}
				}
			}
		}
		
		return new LinkedList<PathNode>();
	}
	
	/**
	 * Returns the first path to the destination
	 * 
	 * @param start Start Node
	 * @param stop Stop Node
	 * @return A list with the path
	 */
	public LinkedList<PathNode> DepthFirstSearch(PathNode start, PathNode stop){
		LinkedList<PathNode> path = new LinkedList<PathNode>();
		PathNode current = DepthFirstSearchInternal(start, stop);
		while(current != null){
			path.addFirst(current);
			current = current.getSourceNeighbor();
			if(current.equals(start)){
				path.addFirst(start);
				break;
			}
		}	
		return path;
	}
	
	/**
	 * The DepthFirstSearch algorithm
	 * @param node The node to search from
	 * @return The stop PathNode if a path was found else null
	 */
	private PathNode DepthFirstSearchInternal(PathNode node, PathNode stop){
		node.setVisited(true);
		if(node.equals(stop)){
			return node;
		}
		
		for(PathNode next : node.getNeighbors()){
			if(!next.visited() && node.getNeighborCost(next) > 0){
				next.setSourceNeighbor(node);
				PathNode tmp = DepthFirstSearchInternal(next, stop);
				if(tmp != null){
					return tmp;
				}
			}
		}
		return null;
	}
	
}
