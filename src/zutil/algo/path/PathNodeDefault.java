package zutil.algo.path;

import java.util.HashMap;

public class PathNodeDefault implements PathNode{
	private HashMap<PathNode,Integer> neighbors;
	private PathNode neighbor; 
	private boolean visited;
	
	public PathNodeDefault(){
		neighbors = new HashMap<PathNode,Integer>();
		visited = false;
	}
	
	public void setVisited(boolean b){
		visited = b;
	}

	public int getNeighborCost(PathNode neighbor) {
		return neighbors.get(neighbor);
	}

	public Iterable<PathNode> getNeighbors() {
		return neighbors.keySet();
	}

	public boolean visited() {
		return visited;
	}

	public void setSourceNeighbor(PathNode n) {
		neighbor = n;
	}
	
	public PathNode getSourceNeighbor() {
		return neighbor;
	}
}
