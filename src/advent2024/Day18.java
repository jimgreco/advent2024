package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * You're given a sequence of coordinates of unpassable areas of a grid.
 * You need to walk from the start (0,0) to the finish (70,70).
 *
 * <p>Part 1: Find the minimum number of steps for first 1024 coordinates.
 * Solution: Dijkstra's algorithm.
 *
 * <p>Part 2: Find the point in the sequence where the grid goes from finishable to unfinishable.
 * Solution: Binary search and Dijkstra's algorithm.
 */
public class Day18 {

    private static final int SIZE = 70;
    private static final int PART1_LENGTH = 1024;

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("resources/day18"));
        var coords = input.stream().map(x -> new Coord(
                Integer.parseInt(x.split(",")[1]), Integer.parseInt(x.split(",")[0]))).toList();

        var minSteps = doPart1(coords);
        System.out.println("Solution 1: " + minSteps);

        // 64,44 not it
        var lastBlock = doPart2(coords);
        System.out.println("Solution 2: " + lastBlock.col + "," + lastBlock.row);
    }

    private static int doPart1(List<Coord> coords) {
        var blocked = new HashSet<Coord>();
        for (var i = 0; i < PART1_LENGTH; i++) {
            blocked.add(coords.get(i));
        }

        var minSteps = dijkstras(blocked);
        return minSteps.get(new Coord(SIZE, SIZE));
    }

    private static Coord doPart2(List<Coord> coords) {
        var left = PART1_LENGTH;
        var right = coords.size() - 1;
        Coord minUnfinished = null;

        // binary search
        while (left <= right) {
            var mid = (left + right) / 2;

            var blocked = new HashSet<Coord>();
            for (var i = 0; i < mid; i++) {
                blocked.add(coords.get(i));
            }

            var minSteps = dijkstras(blocked);
            if (minSteps.get(new Coord(SIZE, SIZE)) == null) {
                // did not finish the path
                right = mid - 1;
                minUnfinished = coords.get(mid - 1);
            } else {
                // finished the path
                left = mid + 1;
            }
        }

        return minUnfinished;
    }

    private static HashMap<Coord, Integer> dijkstras(HashSet<Coord> blocked) {
        var minSteps = new HashMap<Coord, Integer>();
        var queue = new PriorityQueue<State>(Comparator.comparingInt(s -> s.steps));
        queue.add(new State(new Coord(0, 0), 0));
        while (!queue.isEmpty()) {
            var state = queue.remove();
            var row = state.coord.row;
            var col = state.coord.col;
            var steps = state.steps;
            if (!blocked.contains(state.coord)
                    && row >= 0 && row <= SIZE && col >= 0 && col <= SIZE
                    && steps < minSteps.getOrDefault(state.coord, Integer.MAX_VALUE)) {
                minSteps.put(state.coord, steps);
                queue.add(new State(new Coord(row, col - 1), steps + 1));
                queue.add(new State(new Coord(row, col + 1), steps + 1));
                queue.add(new State(new Coord(row - 1, col), steps + 1));
                queue.add(new State(new Coord(row + 1, col), steps + 1));
            }
        }
        return minSteps;
    }

    private record Coord(int row, int col) {}
    private record State(Coord coord, int steps) {}
}
