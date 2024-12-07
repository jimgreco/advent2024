package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

/**
 * You're given a navigable 2-D grid that contains a starting point (^) and obstacles (#).
 *
 * <p>Part 1: Find the total number of unique grid coordinates visited while traversing the grid.
 * Solution: Traverse the grid, recording each coordinate visited in a hash set, until out of bounds.
 * Return the size of the hash set.
 *
 * <p>Part 2: Find the total number of grid configurations such that a single new obstacle (#) placed on the grid will
 * cause a loop.
 * Solution: Traverse the grid, recording the path taken.
 * Iterate through each unique coordinate on the path and place an obstacle there.
 * For each iteration, record each coordinate and direction traversed.
 * If the coordinate and direction is repeated then the obstacle has caused a loop.
 */
public class Day06 {

    private static final int[][] DIR = new int[][] { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } };

    public static void main(String[] args) throws IOException {
        // read into a 2-D grid
        var input = Files.readAllLines(Path.of("resources/day06"));
        var grid = new char[input.size()][];
        for (var i = 0; i < input.size(); i++) {
            grid[i] = input.get(i).toCharArray();
        }

        // Solution 1: 5208
        int unique = doPart1(grid);
        System.out.println(unique);

        // Solution 2: 1972
        var obstructions = doPart2(grid);
        System.out.println(obstructions);
    }

    private static int doPart1(char[][] grid) {
        var move = findStartCoord(grid);
        var visited = new HashSet<Coords>();
        while (move != null) {
            visited.add(move.coords);
            move = move(grid, move);
        }
        return visited.size();
    }

    private static int doPart2(char[][] grid) {
        var start = findStartCoord(grid);

        // traverse the path in part 1, recording all the coordinates that are visited
        var path = new HashSet<Coords>();
        var move = start;
        while (move != null) {
            path.add(move.coords);
            move = move(grid, move);
        }
        // make sure the starting point isn't in the list
        path.remove(start.coords);

        var obstructions = 0;
        for (var coord : path) {
            if (grid[coord.row][coord.col] == '.') {
                grid[coord.row][coord.col] = '#';
                // record every move we've made, we've gone in a circle when a move repeats
                var uniqueMoves = new HashSet<Move>();
                move = start;
                while (move != null) {
                    if (!uniqueMoves.add(move)) {
                        obstructions++;
                        break;
                    }
                    move = move(grid, move);
                }
                grid[coord.row][coord.col] = '.';
            }
        }
        return obstructions;
    }

    private static Move move(char[][] grid, Move move) {
        var next = new Coords(move.coords.row + DIR[move.dir][0], move.coords.col + DIR[move.dir][1]);
        if (next.row >= 0 && next.row < grid.length && next.col >= 0 && next.col < grid[next.row].length) {
            return grid[next.row][next.col] == '#'
                    ? new Move(move.coords, (move.dir + 1) % 4) // obstruction, turn right
                    : new Move(next, move.dir); // no obstruction, keep moving
        }
        // out of bounds, completed the maze
        return null;
    }

    private static Move findStartCoord(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '^') {
                    return new Move(new Coords(i, j), 0);
                }
            }
        }
        return null;
    }

    private record Move(Coords coords, int dir) {}
    private record Coords(int row, int col) {}
}
