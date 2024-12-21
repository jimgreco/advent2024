package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class Day20 {

    private static final Coord[] DIRS = new Coord[] {
            new Coord(-1, 0),
            new Coord(1, 0),
            new Coord(0, -1),
            new Coord(0, 1),
    };

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("resources/day20"));
        var grid = new char[input.size()][];
        for (var i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }

        // Solution 1: 1406
        var numAlternatives = execute(grid, 2);
        System.out.println("Solution 1: " + numAlternatives);

        // Solution 2: 981280 too low, 1054838 too high
                //     221550, 223692 prev wrong answers
        numAlternatives = execute(grid, 20);
        System.out.println("Solution 2: " + numAlternatives);
    }

    private static long execute(char[][] grid, int skips) {
        var curr = buildGraph(grid, getStart(grid), skips, 72);
        var alternatives = 0;
        while (curr != null) {
            alternatives += curr.alternatives.size();
            curr = curr.next;
        }
        return alternatives;
    }

    private static Node buildGraph(char[][] grid, Coord start, int skips, int target) {
        // build normal path
        var index = 0;
        var root = new Node(index++, start);
        var nodes = new HashMap<Coord, Node>();
        nodes.put(root.coord, root);

        var curr = root;
        Node prev = null;
        while (grid[curr.coord.row][curr.coord.col] != 'E') {
            for (var dir : DIRS) {
                var nextCoord = new Coord(curr.coord.row + dir.row, curr.coord.col + dir.col);
                if (isValid(grid, nextCoord)
                        && grid[nextCoord.row][nextCoord.col] != '#'          // not a wall
                        && (prev == null || !prev.coord.equals(nextCoord))) { // not back the way we came
                    if (prev != null) {
                        // link prev -> curr if not the root
                        prev.next = curr;
                    }
                    prev = curr;
                    curr = new Node(index++, nextCoord);
                    nodes.put(curr.coord, curr);
                }
            }
        }

        // step through all cells in the path to find alternative paths through "cheats"
        curr = root;
        while (curr != null) {
            for (var dir : DIRS) {
                var next = new Coord(curr.coord.row + dir.row, curr.coord.col + dir.col);
                var visited = new HashSet<State>();
                if (isValid(grid, next) && grid[next.row][next.col] == '#') {
                    // start our "cheat"
                    findAlts(grid, skips - 1, curr, next, nodes, skips - 1, visited, target);
                }
            }
            curr = curr.next;
        }
        return root;
    }

    private static void findAlts(
            char[][] grid, int maxSteps,
            Node start, Coord curr, HashMap<Coord, Node> nodes, int remainingSteps,
            Set<State> visited, int target) {
        var state = new State(curr, remainingSteps);
        if (remainingSteps >= 0 && !visited.contains(state)) {
            visited.add(state);
            if (grid[curr.row][curr.col] == '#') {
                for (var dir : DIRS) {
                    var next = new Coord(curr.row + dir.row, curr.col + dir.col);
                    if (isValid(grid, next)) {
                        findAlts(grid, maxSteps, start, next, nodes, remainingSteps - 1, visited, target);
                    }
                }
            } else  {
                var currNode = nodes.get(curr);
                if (currNode != null) {
                    var saved = currNode.index - start.index - (maxSteps - remainingSteps);
                    if (saved >= target) {
                        start.alternatives.add(curr);
                    }
                }
            }
        }
    }

    private static boolean isValid(char[][] grid, Coord next) {
        return next.row >= 0 && next.row < grid.length && next.col >= 0 && next.col < grid[next.row].length;
    }

    private static Coord getStart(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'S') {
                    return new Coord(i, j);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private record State(Coord curr, int remaining) {}
    private record Coord(int row, int col) {}

    private static class Node {

        final int index;
        final Coord coord;
        final Set<Coord> alternatives;
        Node next;

        private Node(int index, Coord coord) {
            this.index = index;
            this.coord = coord;
            alternatives = new HashSet<>();
        }
    }
}
