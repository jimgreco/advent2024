package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * You're given a list of start coordinates and deltas for a group of "robots".
 * The grid the robots move around on is 103 startX 101.
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
        var inputs = Files.readAllLines(Path.of("resources/day14"));
        var pattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");
        var robots = new ArrayList<RobotDef>();
        for (var input : inputs) {
            var matcher = pattern.matcher(input);
            matcher.find();
            robots.add(new RobotDef(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4))));
        }

        // Solution 1: 228690000
        var total = doPart1(robots);
        System.out.println("Solution 1: " + total);

        // Solution 2: 7093
        var seconds = doPart2(robots);
        System.out.println("Solution 2: " + seconds);
    }

    private static int doPart1(List<RobotDef> robotDef) {
        // move 100 seconds
        var robots = robotDef.stream().map(x -> new Robot(x, x.startX, x.startY)).toList();
        for (var i = 1; i <= 100; i++) {
            move(robots);
        }

        // count the number of robots in each quadrant
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

    private static int doPart2(List<RobotDef> robotDef) {
        var robots = robotDef.stream().map(x -> new Robot(x, x.startX, x.startY)).toList();
        var seconds = 0;
        while (largestBlobSize(robots) < 100) {
            move(robots);
            seconds++;
        }
        print(robots);
        return seconds;
    }

    private static void move(List<Robot> robots) {
        for (var robot : robots) {
            robot.x = (((robot.x + robot.def.deltaX) % WIDTH) + WIDTH) % WIDTH;
            robot.y = (((robot.y + robot.def.deltaY) % HEIGHT) + HEIGHT) % HEIGHT;
        }
    }

    private static int largestBlobSize(List<Robot> robots) {
        // mark where robots are in the grid
        var grid = new boolean[HEIGHT][WIDTH];
        for (var robot : robots) {
            grid[robot.y][robot.x] = true;
        }

        // label each blob of robots and count the largest blob (label with the most cells)
        var labels = new int[HEIGHT][WIDTH];
        var largestBlob = 0;
        var nLabels = 0;
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] && labels[i][j] == 0) {
                    largestBlob = Math.max(largestBlob, dfs(grid, labels, i, j, ++nLabels));
                }
            }
        }
        return largestBlob;
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
        // mark all robot locations
        var grid = new char[HEIGHT][WIDTH];
        for (var robot : robots) {
            grid[robot.y][robot.x] = '*';
        }

        // column header
        var builder = new StringBuilder();
        builder.append("  ");
        for (var i = 0; i < WIDTH; i += 10) {
            builder.append(String.format("%03d       ", i));
        }
        builder.append("\n");

        // row header and robot locations
        for (var i = 0; i < HEIGHT; i++) {
            builder.append(String.format("%03d ", i));
            for (var j = 0; j < WIDTH; j++) {
                builder.append(grid[i][j] == 0 ? ' ' : '*');
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }

    private record RobotDef(int startX, int startY, int deltaX, int deltaY) {}

    private static class Robot {

        final RobotDef def;
        int x;
        int y;

        Robot(RobotDef def, int x, int y) {
            this.def = def;
            this.x = x;
            this.y = y;
        }
    }
}
