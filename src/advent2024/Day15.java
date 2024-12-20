package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class Day15 {

    private static final Coord[] DIR;
    private static final char EMPTY = '.';
    private static final char BOX = 'O';
    private static final char LEFT_BOX = '[';
    private static final char RIGHT_BOX = ']';
    private static final char WALL = '#';
    private static final char ME = '@';
    private static final char MOVE_LEFT = '<';
    private static final char MOVE_RIGHT = '>';
    private static final char UP = '^';
    private static final char DOWN = 'v';

    static {
        DIR = new Coord[128];
        DIR[UP] = new Coord(-1, 0);
        DIR[MOVE_RIGHT] = new Coord(0, 1);
        DIR[DOWN] = new Coord(1, 0);
        DIR[MOVE_LEFT] = new Coord(0, -1);
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
        //             1066498 too low   1481392
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

        var curr = getStart(grid);
        for (var move : moves) {
            var dir = DIR[move];
            var next = new Coord(curr.row + dir.row, curr.col + dir.col);

            if (isValid(grid, next) && grid[next.row][next.col] == EMPTY) {
                // move
                grid[curr.row][curr.col] = EMPTY;
                grid[next.row][next.col] = ME;
                curr = next;
            } else if (isValid(grid, next) && grid[next.row][next.col] == BOX) {
                // find the next space that isn't a box
                var walk = new Coord(next.row + dir.row, next.col + dir.col);
                while (isValid(grid, walk) && grid[walk.row][walk.col] == BOX) {
                    walk = new Coord(walk.row + dir.row, walk.col + dir.col);
                }

                // shift the barrels if there's an empty space at the end and move
                if (isValid(grid, walk) && grid[walk.row][walk.col] == EMPTY) {
                    grid[curr.row][curr.col] = EMPTY;
                    grid[next.row][next.col] = ME;
                    grid[walk.row][walk.col] = BOX;
                    curr = next;
                }
            }
        }
        return score(grid);
    }

    private static int doPart2(char[][] grid, List<Character> moves) {
        // copy and transform input into a double-width grid
        var newGrid = new char[grid.length][2 * grid[0].length];
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                newGrid[i][2 * j] = grid[i][j];
                newGrid[i][2 * j + 1] = grid[i][j];
                if (grid[i][j] == BOX) {
                    newGrid[i][2 * j] = LEFT_BOX;
                    newGrid[i][2 * j + 1] = RIGHT_BOX;
                } else if (grid[i][j] == ME) {
                    newGrid[i][2 * j + 1] = EMPTY;
                }
            }
        }
        grid = newGrid;

        var curr = getStart(grid);
        for (var move : moves) {
            var dir = DIR[move];
            var next = new Coord(curr.row + dir.row, curr.col + dir.col);

            if (isValid(grid, next) && grid[next.row][next.col] == EMPTY) {
                // move
                grid[curr.row][curr.col] = EMPTY;
                grid[next.row][next.col] = ME;
                curr = next;
            } else if (isValid(grid, next)
                    && (grid[next.row][next.col] == LEFT_BOX || grid[next.row][next.col] == RIGHT_BOX)) {
                if (move == MOVE_LEFT) {
                    curr = shiftLeft(grid, curr, next);
                } else if (move == MOVE_RIGHT) {
                    curr = shiftRight(grid, curr, next);
                } else {
                    curr = shiftVertical(grid, curr, dir, next);
                }
            }

            //print(grid, move);

            // error check
            for (var i = 0; i < grid.length; i++) {
                for (var j = 0 ; j < grid[i].length - 1; j++) {
                    if (grid[i][j] == LEFT_BOX && grid[i][j + 1] != RIGHT_BOX
                            || grid[i][j] != LEFT_BOX && grid[i][j + 1] == RIGHT_BOX) {
                        print(grid, move);
                        return 0;
                    }
                }
            }
        }

        print(grid, 'X');
        return score(grid);
    }

    private static Coord shiftLeft(char[][] grid, Coord curr, Coord next) {
        // shift left, find an empty spot iterating left
        for (var j = next.col - 2; j >= 0; j -= 2) {
            if (grid[curr.row][j] == EMPTY) {
                // found an empty spot, shift everything left, including '@', cell by cell
                for (; j <= next.col; j++) {
                    grid[curr.row][j] = grid[curr.row][j + 1];
                }
                // update the current position
                grid[curr.row][curr.col] = EMPTY;
                return next;
            }
        }
        return curr;
    }

    private static Coord shiftRight(char[][] grid, Coord curr, Coord next) {
        // shift right, find an empty spot iterating right
        for (var j = next.col + 2; j < grid[curr.row].length; j++) {
            if (grid[curr.row][j] == EMPTY) {
                // found an empty spot, shift everything right, including '@', cell by cell
                for (; j >= next.col; j--) {
                    grid[curr.row][j] = grid[curr.row][j - 1];
                }
                // update the current position
                grid[curr.row][curr.col] = EMPTY;
                return next;
            }
        }
        return curr;
    }

    private static Coord shiftVertical(char[][] grid, Coord curr, Coord dir, Coord next) {
        // shift up or down
        var queue = new LinkedList<Coord>();
        // always use the left side of the box
        queue.add(new Coord(next.row, next.col + (grid[next.row][next.col] == LEFT_BOX ? 0 : -1)));
        var toMove = new ArrayList<Coord>();

        while (!queue.isEmpty()) {
            // do a BFS, row by row
            var size = queue.size();
            for (var i = 0; i < size; i++) {
                var box = queue.remove();
                toMove.add(box);

                var walkRow = box.row + dir.row;
                var walkLeft = grid[walkRow][box.col];
                if (walkLeft == RIGHT_BOX) {
                    var leftBox = new Coord(walkRow, box.col - 1);
                    if (!queue.contains(leftBox)) {
                        queue.add(leftBox);
                    }
                } else if (walkLeft == LEFT_BOX) {
                    var straightBox = new Coord(walkRow, box.col);
                    if (!queue.contains(straightBox)) {
                        queue.add(straightBox);
                    }
                } else if (walkLeft == WALL) {
                    return curr;
                }

                var walkRight = grid[walkRow][box.col + 1];
                if (walkRight == LEFT_BOX) {
                    var rightBox = new Coord(walkRow, box.col + 1);
                    if (!queue.contains(rightBox)) {
                        queue.add(rightBox);
                    }
                } else if (walkRight == WALL) {
                    return curr;
                }
            }
        }

        for (var box : toMove.reversed()) {
            grid[box.row][box.col] = EMPTY;
            grid[box.row][box.col + 1] = EMPTY;
            grid[box.row + dir.row][box.col] = LEFT_BOX;
            grid[box.row + dir.row][box.col + 1] = RIGHT_BOX;
        }
        grid[curr.row][curr.col] = EMPTY;
        grid[next.row][next.col] = ME;
        return next;
    }

    private static boolean isValid(char[][] grid, Coord coord) {
        return coord.row >= 0 && coord.row < grid.length && coord.col >= 0 && coord.col < grid[coord.row].length;
    }

    private static Coord getStart(char[][] grid) {
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == ME) {
                    return new Coord(i, j);
                }
            }
        }
        return null;
    }

    private static int score(char[][] grid) {
        var score = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == LEFT_BOX || grid[i][j] == BOX) {
                    score += 100 * i + j;
                }
            }
        }
        return score;
    }

    private static void print(char[][] grid, Character move) {
        System.out.println("Move: " + move);

        System.out.print("  ");
        for (var i = 0; i < grid[0].length; i++) {
            if (i % 10 == 0) {
                System.out.print(String.format("%02d        ", i));
            }
        }
        System.out.println();
        for (var i = 0; i < grid.length; i++) {
            System.out.print(String.format("%02d ", i));
            for (var j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private record Coord(int row, int col) {}
}
