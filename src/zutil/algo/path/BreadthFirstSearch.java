/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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
    public LinkedList<PathNode> find(PathNode start, PathNode stop) {
        Queue<PathNode> queue = new LinkedList<>();
        HashSet<PathNode> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        PathNode tmp;
        while (!queue.isEmpty()) {
            tmp = queue.poll();

            for (PathNode next : tmp.getNeighbors()) {
                if (!visited.contains(next) && tmp.getNeighborCost(next) > 0) {
                    queue.add(next);
                    visited.add(next);
                    next.setParentNeighbor(tmp);

                    if (next.equals(stop)) {
                        return stop.traversTo(start);
                    }
                }
            }
        }

        return new LinkedList<>();
    }
}
