package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class Day15 {

    private static final int[][] DIR;
    private static final char EMPTY = '.';
    private static final char BOX = 'O';
    private static final char LEFT_BOX = '[';
    private static final char RIGHT_BOX = ']';
    private static final char WALL = '#';

    static {
        DIR = new int[128][];
        DIR['^'] = new int[] { -1, 0 };
        DIR['>'] = new int[] { 0, 1 };
        DIR['v'] = new int[] { 1, 0 };
        DIR['<'] = new int[] { 0, -1 };
    }

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("resources/day15"));
        var rows = 0;
        while (!input.get(rows).isBlank()) {
            rows++;
        }
        var grid = new char[rows][];
        for (var i = 0; i < rows; i++) {
            grid[i] = input.get(i).toCharArray();
        }
        var moves = new ArrayList<Character>();
        for (var i = rows + 1; i < input.size(); i++) {
            for (var j = 0; j < input.get(i).length(); j++) {
                moves.add(input.get(i).charAt(j));
            }
        }

        // Solution 1: 1463715
        var score = doPart1(grid, moves);
        System.out.println("Solution 1: " + score);

        // Solution 2: 1500814 too high
        //             1066498 too low
        score = doPart2(grid, moves);
        System.out.println("Solution 2: " + score);
    }

    private static int doPart1(char[][] grid, List<Character> moves) {
        // copy input
        var newGrid = new char[grid.length][];
        for (var i = 0; i < grid.length; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        grid = newGrid;

        var start = getStart(grid);
        var row = start[0];
        var col = start[1];
        grid[row][col] = '.';

        for (var move : moves) {
            var dir = DIR[move];
            var newRow = row + dir[0];
            var newCol = col + dir[1];

            if (isValid(grid, newRow, newCol)) {
                if (grid[newRow][newCol] == EMPTY) {
                    // move
                    row = newRow;
                    col = newCol;
                } else if (grid[newRow][newCol] == BOX) {
                    // find the next space that isn't a box
                    while (isValid(grid, newRow, newCol) && grid[newRow][newCol] == BOX) {
                        newRow += dir[0];
                        newCol += dir[1];
                    }

                    // shift the barrels if there's an empty space at the end and move
                    if (isValid(grid, newRow, newCol) && grid[newRow][newCol] == EMPTY) {
                        grid[newRow][newCol] = BOX;
                        row += dir[0];
                        col += dir[1];
                        grid[row][col] = EMPTY;
                    }
                }
            }
        }

        return score(grid, 'O');
    }

    private static int doPart2(char[][] grid, List<Character> moves) {
        // copy and transform input into a double-width grid
        var newGrid = new char[grid.length][2 * grid[0].length];
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                newGrid[i][2 * j] = grid[i][j];
                newGrid[i][2 * j + 1] = grid[i][j];
                if (grid[i][j] == 'O') {
                    newGrid[i][2 * j] = '[';
                    newGrid[i][2 * j + 1] = ']';
                } else if (grid[i][j] == '@') {
                    newGrid[i][2 * j + 1] = '.';
                }
            }
        }
        grid = newGrid;

        var start = getStart(grid);
        var row = start[0];
        var col = start[1];

        for (var move : moves) {
            var dir = DIR[move];
            var newRow = row + dir[0];
            var newCol = col + dir[1];

            if (isValid(grid, newRow, newCol)) {
                if (grid[newRow][newCol] == EMPTY) {
                    // move
                    grid[row][col] = '.';
                    grid[newRow][newCol] = '@';
                    row = newRow;
                    col = newCol;
                } else if (grid[newRow][newCol] == LEFT_BOX || grid[newRow][newCol] == RIGHT_BOX) {
                    if (move == '<') {
                        // shift left, find an empty spot iterating left
                        for (var j = newCol; j >= 0; j--) {
                            if (grid[row][j] == '.') {
                                // found an empty spot, shift everything left, one by one
                                for (; j <= newCol; j++) {
                                    grid[row][j] = grid[row][j + 1];
                                }
                                // update the current position
                                grid[row][col] = '.';
                                col--;
                                break;
                            }
                        }
                    } else if (move == '>') {
                        // shift right, find an empty spot iterating right
                        for (var j = newCol; j < grid[row].length; j++) {
                            if (grid[row][j] == '.') {
                                // found an empty spot, shift everything right, one by one
                                for (; j >= newCol; j--) {
                                    grid[row][j] = grid[row][j - 1];
                                }
                                // update the current position
                                grid[row][col] = '.';
                                col++;
                                break;
                            }
                        }
                    } else {
                        // shift up or down
                        var queue = new LinkedList<Box>();
                        // always use the left side of the box
                        queue.add(new Box(newRow, col + (grid[newRow][col] == LEFT_BOX ? 0 : -1)));
                        var canMove = true;
                        var toMove = new ArrayList<Box>();
                        while (canMove && !queue.isEmpty()) {
                            // do a BFS, row by row
                            var size = queue.size();
                            for (var i = 0; i < size; i++) {
                                var box = queue.remove();
                                toMove.add(box);
                                // look above the box and add to the queue
                                var c1 = grid[box.row + dir[0]][box.col];
                                var c2 = grid[box.row + dir[0]][box.col + 1];
                                if (c1 == WALL || c2 == WALL) {
                                    canMove = false;
                                    break;
                                }
                                var rightBox = new Box(box.row + dir[0], box.col - 1);
                                if (c1 == RIGHT_BOX && !queue.contains(rightBox)) {
                                    queue.add(rightBox);
                                }
                                var straightBox = new Box(box.row + dir[0], box.col);
                                if (c1 == LEFT_BOX && !queue.contains(straightBox)) {
                                    // one box on top of the other, c2 check is redundant
                                    queue.add(straightBox);
                                }
                                var leftBox = new Box(box.row + dir[0], box.col + 1);
                                if (c2 == LEFT_BOX && !queue.contains(leftBox)) {
                                    queue.add(leftBox);
                                }
                            }
                        }

                        if (canMove) {
                            for (var box : toMove.reversed()) {
                                grid[box.row][box.col] = '.';
                                grid[box.row][box.col + 1] = '.';
                                grid[box.row + dir[0]][box.col] = '[';
                                grid[box.row + dir[0]][box.col + 1] = ']';
                            }
                            grid[row][col] = '.';
                            grid[row + dir[0]][col] = '@';
                            row += dir[0];
                        }
                    }
                }
            }

            // error check
            for (var i = 0; i < grid.length; i++) {
                for (var j = 0 ; j < grid[i].length - 1; j++) {
                    if (grid[i][j] == '[' && grid[i][j + 1] != ']' || grid[i][j] != '[' && grid[i][j + 1] == ']') {
                        print(grid);
                        return 0;
                    }
                }
            }
        }

        print(grid);
        return score(grid, '[');
    }

    private static boolean isValid(char[][] grid, int newRow, int newCol) {
        return newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[newRow].length;
    }

    private static int[] getStart(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '@') {
                    return new int[] { i, j };
                }
            }
        }
        return null;
    }

    private static int score(char[][] grid, char c) {
        var score = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == c) {
                    score += 100 * i + j;
                }
            }
        }
        return score;
    }

    private static void print(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    private record Box(int row, int col) {}
}
