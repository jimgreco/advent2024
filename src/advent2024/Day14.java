package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * You're given a list of start coordinates and deltas for a group of "robots".
 * The grid the robots move around on is 103 x 101.
 * Moves out of bounds wrap around the grid.
 *
 * <p>Part 1: Do a 100 moves and find the number of robots in each quadrant of the grid.
 * Ignore the "middle" row and column when calculating the quadrant.
 * Solution: A for loop.
 *
 * <p>Part 2: Find the number of moves until the robots are grouped in a "Christmas tree pattern".
 * Solution: The pattern isn't defined and I didn't want to step through N patterns until I saw a Christmas tree.
 * For each move, we label the clusters in the graph.
 * If there's a large cluster (100+ cells) then we stop iterating.
 * A print of the current robot positions can confirm the pattern visually.
 */
public class Day14 {

    private static final int HEIGHT = 103;
    private static final int WIDTH = 101;
    private static final int[][] NEXT = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    public static void main(String[] args) throws IOException {
        // Solution 1: 228690000
        var robots = readFile();
        var total = doPart1(robots);
        System.out.println(total);

        // Solution 2: 7093
        robots = readFile();
        var seconds = doPart2(robots);
        System.out.println(seconds);
        print(robots);
    }

    private static List<Robot> readFile() throws IOException {
        var inputs = Files.readAllLines(Path.of("resources/day14"));
        var pattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");
        var robots = new ArrayList<Robot>();
        for (var input : inputs) {
            var matcher = pattern.matcher(input);
            matcher.find();
            robots.add(new Robot(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4))));
        }
        return robots;
    }

    private static int doPart1(List<Robot> robots) {
        for (var i = 1; i <= 100; i++) {
            move(robots);
        }

        var midX = WIDTH / 2;
        var midY = HEIGHT / 2;
        var quad = new int[4];
        for (var robot : robots) {
            quad[0] += robot.x < midX && robot.y < midY ? 1 : 0;
            quad[1] += robot.x > midX && robot.y < midY ? 1 : 0;
            quad[2] += robot.x < midX && robot.y > midY ? 1 : 0;
            quad[3] += robot.x > midX && robot.y > midY ? 1 : 0;
        }
        return quad[0] * quad[1] * quad[2] * quad[3];
    }

    private static int doPart2(List<Robot> robots) {
        var seconds = 0;
        while (countLabels(robots) < 100) {
            move(robots);
            seconds++;
        }
        return seconds;
    }

    private static void move(List<Robot> robots) {
        for (var robot : robots) {
            robot.x = (((robot.x + robot.deltaX) % WIDTH) + WIDTH) % WIDTH;
            robot.y = (((robot.y + robot.deltaY) % HEIGHT) + HEIGHT) % HEIGHT;
        }
    }

    private static int countLabels(List<Robot> robots) {
        var grid = new boolean[HEIGHT][WIDTH];
        for (var robot : robots) {
            grid[robot.y][robot.x] = true;
        }
        var labels = new int[HEIGHT][WIDTH];
        var labelCounts = new ArrayList<Integer>();
        var nLabels = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] && labels[i][j] == 0) {
                    labelCounts.add(dfs(grid, labels, i, j, ++nLabels));
                }
            }
        }
        labelCounts.sort(Integer::compareTo);
        return labelCounts.reversed().get(0);
    }

    private static int dfs(boolean[][] grid, int[][] labels, int row, int col, int label) {
        var count = 0;
        if (row >= 0 && row < grid.length && col >= 0 && col < grid[row].length
                && grid[row][col] && labels[row][col] == 0) {
            labels[row][col] = label;
            count++;
            for (var next : NEXT) {
                count += dfs(grid, labels , row + next[0], col + next[1], label);
            }
        }
        return count;
    }

    private static void print(List<Robot> robots) {
        var grid = new int[HEIGHT][WIDTH];
        for (var robot : robots) {
            grid[robot.y][robot.x]++;
        }

        var builder = new StringBuilder();
        for (var i = 0; i < HEIGHT; i++) {
            for (var j = 0; j < WIDTH; j++) {
                if (grid[i][j] == 0) {
                    builder.append(' ');
                } else if (grid[i][j] >= 1 && grid[i][j] <= 9) {
                    builder.append((char) ('0' + grid[i][j]));
                } else {
                    builder.append('*');
                }
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }

    private static class Robot {

        int x;
        int y;
        final int deltaX;
        final int deltaY;

        private Robot(int x, int y, int deltaX, int deltaY) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }
    }
}
