package zutil.algo.path;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * An standard implementation of PathNode
 * @author Ziver
 *
 */
public class StandardPathNode implements PathNode{
	private HashMap<PathNode,Integer> neighbors;
	private PathNode parent;
	
	public StandardPathNode(){
		neighbors = new HashMap<PathNode,Integer>();
	}

	public int getNeighborCost(PathNode neighbor) {
		return neighbors.get(neighbor);
	}

	public Iterable<PathNode> getNeighbors() {
		return neighbors.keySet();
	}

	public PathNode getParentNeighbor() {
		return parent;
	}

	public void setParentNeighbor(PathNode parent) {
		this.parent = parent;		
	}

	public LinkedList<PathNode> traversTo(PathNode goal) {
		LinkedList<PathNode> path = new LinkedList<PathNode>();
		PathNode current = this;
        while(current != null){
                path.addFirst(current);
                current = current.getParentNeighbor();
                if(goal.equals( current )){
                        path.addFirst( goal );
                        return path;
                }
        }       
        return new LinkedList<PathNode>();
	}

}
