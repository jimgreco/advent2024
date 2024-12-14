package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Solution: Use Cramer's rule to solve a system of N equations with N variables.
 * Floating point solutions are invalid so check the determinant against the variable numerators.
 */
public class Day13 {

    public static void main(String[] args) throws IOException {
        // read into a list of buttons and prizes
        var input = Files.readAllLines(Path.of("resources/day13"));
        var machines = new ArrayList<Machine>();
        for (var i = 0; i < input.size(); i += 4) {
            machines.add(new Machine(parse(input.get(i)), parse(input.get(i + 1)), parse(input.get(i + 2))));
        }

        var total = calculate(machines, 0);
        System.out.println(total);

        total = calculate(machines, 10000000000000L);
        System.out.println(total);
    }

    private static long calculate(ArrayList<Machine> machines, long offset) {
        var total = 0L;
        for (var machine : machines) {
            var sln = cramers(machine, offset);
            if (sln != null) {
                total += 3 * sln[0] + sln[1];
            }
        }
        return total;
    }

    private static Coord parse(String input) {
        var pattern = Pattern.compile("X[+=](\\d+),\\s*Y[+=](\\d+)");
        var matcher = pattern.matcher(input);
        matcher.find();
        return new Coord(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
    }

    private static long[] cramers(Machine machine, long offset) {
        // equations:
        // buttonA.x (a) * n + buttonB.x (b) * m = prize.x (c)
        // buttonA.y (d) * n + buttonB.y (e) * m = prize.y (f)
        var a = (long) machine.buttonA.x;
        var b = (long) machine.buttonB.x;
        var c = offset + machine.prize.x;
        var d = (long) machine.buttonA.y;
        var e = (long) machine.buttonB.y;
        var f = offset + machine.prize.y;
        var determinant = a * e - b * d;
        var numX = c * e - b * f;
        var numY = a * f - c * d;
        return determinant != 0 && numX % determinant == 0 && numY % determinant == 0
                ? new long[] { numX / determinant, numY / determinant } : null;
    }

    private record Coord(int x, int y) {}
    private record Machine(Coord buttonA, Coord buttonB, Coord prize) { }
}
