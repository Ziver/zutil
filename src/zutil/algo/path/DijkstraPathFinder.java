package zutil.algo.path;

import java.util.LinkedList;

public class DijkstraPathFinder {

	public static LinkedList<PathNode> find(PathNode start, PathNode stop){
		// TODO
		/*
		 1
			 
			 5      dist[source] := 0                     // Distance from source to source
			 6      Q := copy(Graph)                      // All nodes in the graph are unoptimized - thus are in Q
			 7      while Q is not empty:                 // The main loop
			 8          u := extract_min(Q)               // Remove and return best vertex from nodes in two given nodes
			                                           // we would use a path finding algorithm on the new graph, such as depth-first search.
			 9          for each neighbor v of u:         // where v has not yet been considered
			10              alt = dist[u] + length(u, v)
			11              if alt < dist[v]              // Relax (u,v)
			12                  dist[v] := alt
			13                  previous[v] := u
			14      return previous[]
*/
	
		
		
		LinkedList<PathNode> path = new LinkedList<PathNode>();
		PathNode current = stop;
		while(true){
			path.addFirst(current);
			current = current.getSourceNeighbor();
			if(current.equals(start)){
				path.addFirst(start);
				break;
			}
		}	
		return path;
	}
}
