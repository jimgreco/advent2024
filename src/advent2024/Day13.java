package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * You're given a given of letters.
 * Grid cells next to each other with the same letters form shapes.
 *
 * <p>Part 1: Sum the product of the area and perimeter for each shape.
 * Solution: Two different shapes can share the same letter so first classify each shape with a unique identifier using
 * a DFS.
 * The area for each shape is the number of cells with that identifier.
 * The perimeters for each shape is the number of times, for each cell in the shape, an adjacent cell (in each of the
 * four directions) have different identifiers.
 *
 * <p>Part 2: Sum the product of the area and the number of sides for each shape.
 * Solution: Do the same classification as part 1.
 * Sides are calculated by keeping track of the current cell, last cell, the adjacent cell, and the last adjacent cell.
 * See the code for an example.
 */
public class Day13 {

    private static final int[][] DIRS = new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    public static void main(String[] args) throws IOException {
        // read into a list
        var input = Files.readAllLines(Path.of("resources/day13"));
        var grid = new char[input.size()][];
        for (var i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }

        // Solution 1: 1573474
        var sumProduct = doPart1(grid);
        System.out.println(sumProduct);

        // Solution 2: 966476
        sumProduct = doPart2(grid);
        System.out.println(sumProduct);
    }

    private static long doPart1(char[][] grid) {
        var classifiedGrid = new int[grid.length][grid[0].length];
        var length = classify(grid, classifiedGrid);
        var perimeter = new int[length];
        var areas = new int[length];

        for (var i = 0; i < classifiedGrid.length; i++) {
            for (var j = 0; j < classifiedGrid[i].length; j++) {
                var id = classifiedGrid[i][j];
                // each cell adds to the area
                areas[id - 1]++;
                for (var dir : DIRS) {
                    // look up, down, left, right for whether there is a diff in id with the current cell
                    var adjacentRow = i + dir[0];
                    var adjacentCol = j + dir[1];
                    if (isValid(grid, adjacentRow, adjacentCol)) {
                        // adjacent cell is in bounds, only a perimeter if different ids
                        var adjacentId = classifiedGrid[adjacentRow][adjacentCol];
                        if (id != adjacentId) {
                            perimeter[id - 1]++;
                        }
                    } else {
                        // adjacent cell is out of bounds, always a perimeter
                        perimeter[id - 1]++;
                    }
                }
            }
        }
        return sumProduct(perimeter, areas);
    }

    private static long doPart2(char[][] grid) {
        var classifiedGrid = new int[grid.length][grid[0].length];
        var length = classify(grid, classifiedGrid);
        var areas = new int[length];
        var sides = new int[length];

        // find sides in the up and down direction
        // iterate over cols for each row
        for (var i = 0; i < classifiedGrid.length; i++) {
            var lastId = -1;
            var lastUpId = -1;
            var lastDownId = -1;
            for (var j = 0; j < classifiedGrid[i].length; j++) {
                var id = classifiedGrid[i][j];

                // We have a new side if one of the following conditions is true:
                // 1) left shape != current shape && current shape != up shape
                //       1 1
                //       1 2
                // 2) current shape == diag shape && up shape != diag shape
                //       1 2
                //       1 1
                var upId = i - 1 >= 0 ? classifiedGrid[i - 1][j] : 0;
                if ((id != lastId && id != upId) || (id == lastUpId && upId != lastUpId)) {
                    sides[id - 1]++;
                }

                var downId = i + 1 < classifiedGrid.length ? classifiedGrid[i + 1][j] : 0;
                if ((id != lastId && id != downId) || (id == lastDownId && downId != lastDownId)) {
                    sides[id - 1]++;
                }

                lastId = id;
                lastUpId = upId;
                lastDownId = downId;
                // only count the area once
                areas[id - 1]++;
            }
        }

        // find sides in the left and right direction
        for (var j = 0; j < classifiedGrid[0].length; j++) {
            var lastId = -1;
            var lastLeftId = -1;
            var lastRightId = -1;
            for (var i = 0; i < classifiedGrid.length; i++) {
                var id = classifiedGrid[i][j];

                var leftId = j - 1 >= 0 ? classifiedGrid[i][j - 1] : 0;
                if ((id != lastId && id != leftId) || (id == lastLeftId && leftId != lastLeftId)) {
                    sides[id - 1]++;
                }

                var rightId = j + 1 < classifiedGrid.length ? classifiedGrid[i][j + 1] : 0;
                if ((id != lastId && id != rightId) || (id == lastRightId && rightId != lastRightId)) {
                    sides[id - 1]++;
                }

                lastId = id;
                lastLeftId = leftId;
                lastRightId = rightId;
            }
        }
        return sumProduct(sides, areas);
    }

    private static int classify(char[][] grid, int[][] classified) {
        var length = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (classified[i][j] == 0) {
                    dfs(grid, i, j, classified, ++length);
                }
            }
        }
        return length;
    }

    private static void dfs(char[][] grid, int row, int col, int[][] classified, int id) {
        var c = grid[row][col];
        classified[row][col] = id;
        for (var dir : DIRS) {
            var adjacentRow = row + dir[0];
            var adjacentCol = col + dir[1];
            if (isValid(grid, adjacentRow, adjacentCol)
                    && classified[adjacentRow][adjacentCol] == 0
                    && grid[adjacentRow][adjacentCol] == c) {
                dfs(grid, adjacentRow, adjacentCol, classified, id);
            }
        }
    }

    private static boolean isValid(char[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[row].length;
    }

    private static long sumProduct(int[] a, int[] b) {
        var sumProduct = 0L;
        for (var i = 0; i < a.length; i++) {
            sumProduct += (long) a[i] * b[i];
        }
        return sumProduct;
    }
}
