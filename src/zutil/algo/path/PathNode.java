package zutil.algo.path;


public interface PathNode {

	public void setVisited(boolean b);
	
	public boolean visited();
	
	public Iterable<PathNode> getNeighbors();
	
	public int getNeighborCost(PathNode neighbor);
	
	public void setSourceNeighbor(PathNode neighbor);
	
	public PathNode getSourceNeighbor();
}