package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * You're given a grid.
 *
 * <p>Part 1: Find the shortest weighted route from the start cell ('S') to end cell ('E').
 * You start at 'S' facing East.
 * Going forward cost 1 one cell.
 * Turning left or right cost 1000.
 *
 * <p>Part 2: Find all the cells on the shortest weighted routes.
 *
 * <p>Solution: Dijkstra's Algorithm.
 * The state to keep track of is the current cell, the direction, the score, and the cells visited.
 */
public class Day16 {

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("resources/day16"));
        var grid = new char[input.size()][];
        for (var i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }

        var result = execute(grid);

        // Solution 1: 109496
        System.out.println("Solution 1: " + result.score);

        // Solution 2: 550 too low
        System.out.println("Solution 2: " + result.path.size());
    }

    private static Result execute(char[][] grid) {
        var minScore = Integer.MAX_VALUE;
        var minPath = new HashSet<Cell>();
        var visited = new HashMap<State, Integer>();

        // start from the 'S' facing East
        var queue = new PriorityQueue<>(Comparator.comparingInt(FullState::score));
        queue.add(new FullState(new State(find(grid, 'S'), 0, 1), 0, new HashSet<>()));

        while (!queue.isEmpty()) {
            var size = queue.size();
            for (var i = 0; i < size; i++) {
                var fullState = queue.remove();
                var state = fullState.state;
                var cell = state.cell;
                var score = fullState.score;

                if (isValid(grid, cell) && (!visited.containsKey(state) || score <= visited.get(state))) {
                    visited.put(state, score);
                    var path = fullState.path;
                    if (!path.contains(cell)) {
                        path = new HashSet<>(path);
                        path.add(cell);
                    }

                    if (grid[cell.row][cell.col] == 'E') {
                        // made it to the end
                        if (score < minScore) {
                            minScore = score;
                            minPath.clear();
                            minPath.addAll(path);
                        } else if (score == minScore) {
                            minPath.addAll(path);
                        }
                    } else {
                        // move forward
                        queue.add(new FullState(new State(
                                new Cell(cell.row + state.deltaRow, cell.col + state.deltaCol),
                                state.deltaRow, state.deltaCol), score + 1, path));
                        if (state.deltaRow == 0) {
                            // was going east or west, turn north and south
                            queue.add(new FullState(new State(cell, -1, 0), score + 1000, path));
                            queue.add(new FullState(new State(cell, 1, 0), score + 1000, path));
                        } else {
                            // was going north or south, turn east and west
                            queue.add(new FullState(new State(cell, 0, -1), score + 1000, path));
                            queue.add(new FullState(new State(cell, 0, 1), score + 1000, path));
                        }
                    }
                }
            }
        }
        return new Result(minScore, minPath);
    }

    private static boolean isValid(char[][] grid, Cell cell) {
        return cell.row >= 0 && cell.row < grid.length
                && cell.col >= 0 && cell.col < grid[cell.row].length
                && grid[cell.row][cell.col] != '#';
    }

    private static Cell find(char[][] grid, char c) {
        for (var i = 0 ; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == c) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }

    private record Cell(int row, int col) {}
    private record State(Cell cell, int deltaRow, int deltaCol) {}
    private record FullState(State state, int score, Set<Cell> path) {}
    private record Result(int score, Set<Cell> path) {}
}
