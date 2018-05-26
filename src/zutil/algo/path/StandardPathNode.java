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
