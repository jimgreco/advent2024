package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * You're given a grid with symbols ("antennas") at some of the coordinates.
 *
 * <p>Part 1: Each pair of the same antenna generates two "antinodes".
 * For the first antenna in the pair, the antinode is the same distance (number of rows and columns) to the second
 * antenna in the pair, in the opposite direction.
 * And vice versa for the second antenna in the pair.
 * Count the number of unique grid positions with antinodes.
 * Solution: Iterate exhaustively through each symbol and each pair of antennas.
 * The first antinode is the first antenna plus the distance between the antennas.
 * The second antinode is the second antenna minus the distance between the antennas.
 * Add these coordinates to a hash set to find the unique number of locations.
 *
 * <p>Part 2: Each pair of the same antenna generates repeated antinodes, equidistant from each other.
 * For the first antenna in the pair, the antinode is the same distance (number of rows and columns) to the second
 * antenna in the pair, in the opposite direction.
 * Another antinode is the same distance from the first antinode as the first antinode was from the second antenna.
 * And another, at the same distance between the second and third antinode.
 * Etc, and vice versa for the second antenna in the pair.
 * Solution: Do the same as in Part 1, but continue to travel the distance between antennas until the antinode
 * coordinates are outside the grid.
 * Additionally, add the two antennas as coordinates to set the set.
 */
public class Day08 {

    public static void main(String[] args) throws IOException {
        // read into a list of coordinates
        var input = Files.readAllLines(Path.of("resources/day08"));
        var grid = new char[input.size()][];
        for (var i = 0; i < grid.length; i++) {
            grid[i] = input.get(i).toCharArray();
        }

        // Solution 1: 256
        var unqiue = doPart1(grid);
        System.out.println(unqiue);

        // Solution 2: 1005
        unqiue = doPart2(grid);
        System.out.println(unqiue);
    }

    private static int doPart1(char[][] grid) {
        var frequencies = createFrequenciesMap(grid);
        var antinodes = new HashSet<Coord>();
        for (var coords : frequencies.values()) {
            for (var i = 0; i < coords.size(); i++) {
                for (var j = i + 1; j < coords.size(); j++) {
                    var c1 = coords.get(i);
                    var c2 = coords.get(j);
                    var rowDelta = c1.row - c2.row;
                    var colDelta = c1.col - c2.col;

                    // antinode from the 1st coordinate
                    var newRow = c1.row + rowDelta;
                    var newCol = c1.col + colDelta;
                    if (isValid(grid, newRow, newCol)) {
                        antinodes.add(new Coord(newRow, newCol));
                    }

                    // antinode from the 2nd coordinate
                    newRow = c2.row - rowDelta;
                    newCol = c2.col - colDelta;
                    if (isValid(grid, newRow, newCol)) {
                        antinodes.add(new Coord(newRow, newCol));
                    }
                }
            }
        }
        return antinodes.size();
    }

    private static int doPart2(char[][] grid) {
        var frequencies = createFrequenciesMap(grid);
        var antinodes = new HashSet<Coord>();
        for (var coords : frequencies.values()) {
            for (var i = 0; i < coords.size(); i++) {
                for (var j = i + 1; j < coords.size(); j++) {
                    var c1 = coords.get(i);
                    var c2 = coords.get(j);
                    var rowDelta = c1.row - c2.row;
                    var colDelta = c1.col - c2.col;

                    // add antennas so no redundant calculations of antinodes in the opposite direction
                    antinodes.add(c1);
                    antinodes.add(c2);

                    // antinodes from the 1st coordinate
                    var newRow = c1.row + rowDelta;
                    var newCol = c1.col + colDelta;
                    while (isValid(grid, newRow, newCol)) {
                        antinodes.add(new Coord(newRow, newCol));
                        newRow += rowDelta;
                        newCol += colDelta;
                    }

                    // antinodes from the 2nd coordinate
                    newRow = c2.row - rowDelta;
                    newCol = c2.col - colDelta;
                    while (isValid(grid, newRow, newCol)) {
                        antinodes.add(new Coord(newRow, newCol));
                        newRow -= rowDelta;
                        newCol -= colDelta;
                    }
                }
            }
        }
        return antinodes.size();
    }

    private static boolean isValid(char[][] grid, int newRow, int newCol) {
        return newRow >= 0 && newRow < grid.length && newCol >= 0 && newCol < grid[0].length;
    }

    private static Map<Character, List<Coord>> createFrequenciesMap(char[][] grid) {
        var freqencies = new HashMap<Character, List<Coord>>();
        for (var i = 0; i < grid.length; i++) {
            for (var j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != '.') {
                    var list = freqencies.getOrDefault(grid[i][j], new ArrayList<>());
                    list.add(new Coord(i, j));
                    freqencies.put(grid[i][j], list);
                }
            }
        }
        return freqencies;
    }

    record Coord(int row, int col) {}
}
