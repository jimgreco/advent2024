package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * You're given a grid of letters: 'X', 'M', 'A', and 'S'.
 *
 * <p>Part 1: Find a sequence of 4 cells that spell "XMAS".
 * The sequence must be a straight line, including diags and backwards, originating from the 'X'.
 * Solution: Linear search for each 'X' and then checking all eight possible straight lines from each 'X' coordinate.
 *
 * <p>Part 2: Find two diag lines that spell "MAS", intersecting on the 'A'.
 * "MAS" can be spelled forward and backwards.
 * Solution: Linear search for each 'A' and then checking the two diag lines both contain an 'M' and 'S'.
 */
public class Day04 {

    public static void main(String[] args) throws IOException {
        // parse file into a 2-D grid
        var input = Files.readAllLines(Path.of("resources/day04"));
        var grid = new char[input.size()][];
        for (var i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }

        // Solution 1: 2458
        int count = doPart1(grid);
        System.out.println(count);

        // Solution 2: 1945
        count = doPart2(grid);
        System.out.println(count);
    }

    private static int doPart1(char[][] grid) {
        var count = 0;
        // iterate through each grid cell looking for an 'X'
        for (var row = 0; row < grid.length; row++) {
            for (var col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 'X') {
                    // search the eight squares surrounding the 'X' for an 'M'
                    for (var i = -1; i <= 1; i++) {
                        for (var j = -1; j <= 1; j++) {
                            var newRow = row + i;
                            var newCol = col + j;
                            if (isValidCoords(grid, newRow, newCol) && grid[newRow][newCol] == 'M') {
                                // continue linear search for 'A' and then 'S'
                                count += linearSearch(grid, newRow, newCol, i, j);
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    private static int linearSearch(char[][] grid, int row, int col, int rowDelta, int colDelta) {
        var c = grid[row][col];
        if (c == 'M' || c == 'A') {
            // find the next letter in the sequence, going in the same direction
            var newRow = row + rowDelta;
            var newCol = col + colDelta;
            var next = c == 'M' ? 'A' : 'S';
            if (isValidCoords(grid, newRow, newCol) && grid[newRow][newCol] == next) {
                return linearSearch(grid, newRow, newCol, rowDelta, colDelta);
            }
        } else if (c == 'S') {
            // 'S' is the last letter, we have found all letters in the sequence
            return 1;
        }
        return 0;
    }

    private static int doPart2(char[][] grid) {
        var count = 0;
        // iterate through each grid cell looking for an 'A'
        for (var row = 0; row < grid.length; row++) {
            for (var col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 'A') {
                    // retrieve the values diag from 'A'
                    char upLeft = isValidCoords(grid, row - 1, col - 1) ? grid[row - 1][col - 1] : 0;
                    char upRight = isValidCoords(grid, row - 1, col + 1) ? grid[row - 1][col + 1] : 0;
                    char downLeft = isValidCoords(grid, row + 1, col - 1) ? grid[row + 1][col - 1] : 0;
                    char downRight = isValidCoords(grid, row + 1, col + 1) ? grid[row + 1][col + 1] : 0;
                    if (upLeft + downRight == 'M' + 'S' && downLeft + upRight == 'M' + 'S') {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static boolean isValidCoords(char[][] grid, int newRow, int newCol) {
        return newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[newRow].length;
    }
}
