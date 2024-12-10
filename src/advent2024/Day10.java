package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * You're given a 2-D grid with the characters '0' to '9'.
 *
 * <p>Part 1: From each '0' traverse the grid from '0', '1', ..., '8', '9'.
 * Sum all the unique destinations (the '9's) for each '0'.
 * Solution 1: DFS. Use a hash (memo) to keep track of each unique cell in the grid visited.
 * The size of the grid makes this optimization unnecessary.
 *
 * <p>Part 2: Do the same traversal except count unique paths from '0' to '9'.
 * Solution 2: DFS. Use a memo to keep track of how the number of paths from each cell.
 * The size of the grid makes this optimization unnecessary.
 */
public class Day10 {

    private static final int[][] NEXT = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    public static void main(String[] args) throws IOException {
        // read into a grid
        var input = Files.readAllLines(java.nio.file.Path.of("resources/day10"));
        var grid = new char[input.size()][];
        for (var i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }

        // Solution 1: 531
        var unqiue = doPart1(grid);
        System.out.println(unqiue);

        // Solution 1: 1210
        unqiue = doPart2(grid);
        System.out.println(unqiue);
    }

    private static int doPart1(char[][] grid) {
        var sum = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                var visited = new HashSet<Coord>();
                sum += dfs(grid, i, j, '0', visited);
            }
        }
        return sum;
    }

    private static int doPart2(char[][] grid) {
        var memo = new HashMap<Coord, Integer>();
        var sum = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                sum += dfs2(grid, i, j, '0', memo);
            }
        }
        return sum;
    }

    private static int dfs(char[][] grid, int row, int col, char expected, Set<Coord> visited) {
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[row].length
                && grid[row][col] == expected && visited.add(new Coord(row, col))) {
            if (expected == '9') {
                return 1;
            } else {
                var result = 0;
                for (var next : NEXT) {
                    result += dfs(grid, row + next[0], col + next[1], (char) (expected + 1), visited);
                }
                return result;
            }
        }
        return 0;
    }

    private static int dfs2(char[][] grid, int row, int col, char expected, HashMap<Coord, Integer> memo) {
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[row].length && grid[row][col] == expected) {
            if (expected == '9') {
                return 1;
            } else {
                var key = new Coord(row, col);
                var value = memo.get(key);
                if (value != null) {
                    return value;
                }
                var result = 0;
                for (var next : NEXT) {
                    result += dfs2(grid, row + next[0], col + next[1], (char) (expected + 1), memo);
                }
                memo.put(key, result);
                return result;
            }
        }
        return 0;
    }

    private record Coord(int row, int col) {}
}
