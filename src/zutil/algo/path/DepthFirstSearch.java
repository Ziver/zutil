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

import java.util.HashSet;
import java.util.LinkedList;

/**
 * A PathFinder class implemented with DFS
 *
 * @author Ziver
 */
public class DepthFirstSearch {
    private HashSet<PathNode> visited = new HashSet<>();

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
