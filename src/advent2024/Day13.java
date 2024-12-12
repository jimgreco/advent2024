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
 * Sides are calculated by doing corner detection. (Thanks Aaron!)
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
        var cfGrid = new int[grid.length][grid[0].length];
        var length = classify(grid, cfGrid);
        var areas = new int[length];
        var sides = new int[length];
        var rows = cfGrid.length;
        var cols = cfGrid[0].length;

        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                var id = cfGrid[i][j];
                var up = i - 1 >= 0 ? cfGrid[i - 1][j] : 0;
                var upRight = i - 1 >= 0 && j + 1 < cols ? cfGrid[i - 1][j + 1] : 0;
                var right = j + 1 < rows ? cfGrid[i][j + 1] : 0;
                var downRight = i + 1 < rows && j + 1 < cols ? cfGrid[i + 1][j + 1] : 0;
                var down = i + 1 < rows ? cfGrid[i + 1][j] : 0;
                var downLeft = i + 1 < rows && j - 1 >= 0 ? cfGrid[i + 1][j - 1] : 0;
                var left = j - 1 >= 0 ? cfGrid[i][j - 1] : 0;
                var upLeft = i - 1 >= 0 && j - 1 >= 0 ? cfGrid[i - 1][j - 1] : 0;
                sides[id - 1] += ((id != up && id != left) ? 1 : 0)
                        + ((id == up && id == left && id != upLeft) ? 1 : 0)
                        + ((id != down && id != left) ? 1 : 0)
                        + ((id == down && id == left && id != downLeft) ? 1 : 0)
                        + ((id != up && id != right) ? 1 : 0)
                        + ((id == up && id == right && id != upRight) ? 1 : 0)
                        + ((id != down && id != right) ? 1 : 0)
                        + ((id == down && id == right && id != downRight) ? 1 : 0);
                areas[id - 1]++;
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
