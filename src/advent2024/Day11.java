package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * You're given a list of numbers.
 * For each iteration, each number in the list transforms into 1 or 2 numbers.
 * <ol>
 *     <li>If the number is zero, the number is now 1
 *     <li>If the number has an odd number of digits, the number is now multiplied by 2024
 *     <li>Otherwise, the number is string split into two halves, i.e., "12" becomes "1 and "2"
 * </ol>
 *
 * <p>Part 1: Count the size of the list after 25 iterations.
 *
 * <p>Part 2: Count the size of the list after 75 iterations.
 *
 * <p>Solution: Create a function that counts the size of the list generated for one number after N iterations.
 * The function can be called recursively so as to not need to return the list (only the count) from the function.
 * Use memoization to record the size of the list for this state as part 2 takes too long to finish otherwise.
 */
public class Day11 {

    private static final long[] POW = new long[18];

    static {
        POW[0] = 1;
        for (var i = 1; i < POW.length; i++) {
            POW[i] = 10 * POW[i - 1];
        }
    }

    public static void main(String[] args) throws IOException {
        // read into a list
        var input = Files.readString(java.nio.file.Path.of("resources/day11"));
        var nums = Arrays.stream(input.split(" ")).mapToLong(Long::parseLong).toArray();

        // Solution 1: 185894
        var count = countForList(nums, 25);
        System.out.println(count);

        // Solution 2: 221632504974231
        count = countForList(nums, 75);
        System.out.println(count);
    }

    private static long countForList(long[] list, int iterations) {
        var memo = new HashMap<State, Long>();
        var count = 0L;
        for (var num : list) {
            count += countForNum(num, iterations, memo);
        }
        return count;
    }

    private static long countForNum(long num, int iterations, Map<State, Long> memo) {
        if (iterations == 0) {
            // we've done every iteration, the number cannot be modified
            return 1;
        }

        // unique state is the number we are modifiying and the number of iterations remaining
        var state = new State(num, iterations);
        var count = memo.get(state);
        if (count != null) {
            return count;
        }

        if (num == 0) {
            // condition 1: num is 0, make 1
            count = countForNum(1, iterations - 1, memo);
        } else {
            // count number of digits in num
            var digits = 1;
            while (num >= POW[digits]) {
                digits++;
            }

            if (digits % 2 == 0) {
                // condition 2: num has even number of digits, string split the number into two halves
                count = countForNum(num / POW[digits / 2], iterations - 1, memo)
                        + countForNum(num % POW[digits / 2], iterations - 1, memo);
            } else {
                // condition 3: otherwise, multiply num by 2024
                count = countForNum(2024 * num, iterations - 1, memo);
            }
        }

        memo.put(state, count);
        return count;
    }

    private record State(long num, int remainingIterations) {}
}
