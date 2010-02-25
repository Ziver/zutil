package zutil.algo.path;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A class that uses BFS to find a path
 * 
 * @author Ziver
 */
public class BreadthFirstSearch implements PathFinder{

	/**
	 * Returns the first path to the destination
	 * 
	 * @param start is the start Node
	 * @param stop is the goal Node
	 * @return A list with the path
	 */
	public LinkedList<PathNode> find(PathNode start, PathNode stop){
		Queue<PathNode> queue = new LinkedList<PathNode>();
		HashSet<PathNode> visited = new HashSet<PathNode>();
		
		queue.add(start);
		visited.add( start );
		
		PathNode tmp;
		while(!queue.isEmpty()){
			tmp = queue.poll();	
			
			for(PathNode next : tmp.getNeighbors()){				
				if(!visited.contains( next ) && tmp.getNeighborCost(next) > 0){
					queue.add(next);
					visited.add( next );
					next.setParentNeighbor(tmp);
					
					if(next.equals(stop)){
						return stop.traversTo(start);
					}
				}
			}
		}
		
		return new LinkedList<PathNode>();
	}
}
