/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
