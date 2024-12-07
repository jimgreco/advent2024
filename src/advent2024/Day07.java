package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * You're given an equation with a solution and a sequence of numbers.
 * Each pair in the sequence can be added, multiplied, or string concat'd together.
 * A valid equation has a set of operations that equal the solution.
 * Do not obey order of operations in adding/multiplying.
 *
 * <p>Part 1: Find all the valid equations with the add and multiply operations.
 *
 * <p>Part 2: Find all the valid equations with the add, multiply, and string concat operations.
 *
 * <p>Solution: Recursively search all possible solutions.
 * To optimize, compute a running total and perform a single operation at a time.
 * If the running total is equal to the solution and all operations have been performed then this is a valid solution.
 * If the running total is larger than the solution then bail out of the recursion early.
 * If the running total is smaller than the solution then try all three operations in the next recursion call.
 */
public class Day07 {

    public static void main(String[] args) throws IOException {
        // read into a list of equations
        var input = Files.readAllLines(Path.of("resources/day07"));
        var equations = new Equation[input.size()];
        for (var i = 0; i < input.size(); i++) {
            var parts = input.get(i).strip().split(":");
            equations[i] = new Equation(
                    Long.parseLong(parts[0]),
                    Arrays.stream(parts[1].strip().split(" ")).mapToInt(Integer::parseInt).toArray());
        }

        // Solution 1: 850435817339
        var sum = searchAll(equations, false);
        System.out.println(sum);

        // Solution 2: 104824810233437
        sum = searchAll(equations, true);
        System.out.println(sum);
    }

    private static long searchAll(Equation[] equations, boolean allowConcat) {
        var sum = 0L;
        for (var equation : equations) {
            if (search(equation, 1, equation.values[0], allowConcat)) {
                sum += equation.solution;
            }
        }
        return sum;
    }

    private static boolean search(Equation equation, int idx, long total, boolean allowConcat) {
        if (idx == equation.values.length) {
            // all values included in the solution, check total matches solution
            return equation.solution == total;
        }
        if (total > equation.solution) {
            // result is too large, this is not a valid solution, bail out early
            return false;
        }
        // search all possibilities: adding, multiplying, and unioning the current number
        var val = equation.values[idx];
        return search(equation, idx + 1, total + val, allowConcat)
                || search(equation, idx + 1, total * val, allowConcat)
                || allowConcat && search(equation, idx + 1, shiftLeft(total, val) + val, allowConcat);
    }

    private static long shiftLeft(long total, int val) {
        while (val > 0) {
            val /= 10;
            total *= 10;
        }
        return total;
    }

    record Equation(long solution, int[] values) {}
}
