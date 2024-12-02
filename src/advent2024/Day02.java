package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * You're given a sequence of numbers.
 *
 * <p>Part 1: Find the number of sequences where all numbers are monotonically increasing or decreasing by 1, 2, or 3.
 * Solution: Take the difference of each pair of subsequent elements in each sequence.
 * Count the sequence as valid if all pairs in a sequence increase or decrease by 1, 2, or 3.
 * Sum the valid sequences.
 *
 * <p>Part 2: Find the number of sequences where when 0 or 1 elements of the sequence are removed, all numbers are
 * monotonically increasing or decreasing by 1, 2, or 3
 * Solution: In addition to Part 1's solution, iterate over each index in the sequence and create a new sequences
 * without the element at that index and then check if the new sequence is valid.
 */
public class Day02 {

    public static void main(String[] args) throws IOException {
        // parse file into two lists
        var lines = Files.readAllLines(Path.of("resources/day02"));
        var sequences = new int[lines.size()][];
        for (var i = 0; i < lines.size(); i++) {
            var nums = lines.get(i).strip().split("\\s+");
            sequences[i] = new int[nums.length];
            for (var j = 0; j < nums.length; j++) {
                sequences[i][j] = Integer.parseInt(nums[j]);
            }
        }

        // Solution 1: 510
        var valid = doPart1(sequences);
        System.out.println(valid);

        // Solution 2: 553
        valid = doPart2(sequences);
        System.out.println(valid);
    }

    private static int doPart1(int[][] sequences) {
        var sequencesValid = 0;
        for (var sequence : sequences) {
            if (checkValid(sequence)) {
                sequencesValid++;
            }
        }
        return sequencesValid;
    }

    private static int doPart2(int[][] sequences) {
        var sequencesValid = 0;
        for (var sequence : sequences) {
            if (checkValid(sequence) || checkValidSkip(sequence)) {
                sequencesValid++;
            }
        }
        return sequencesValid;
    }

    private static boolean checkValid(int[] sequence) {
        var increasing = 0;
        var decreasing = 0;
        for (var i = 0; i < sequence.length - 1; i++) {
            var diff = sequence[i + 1] - sequence[i];
            increasing += diff >= 1 && diff <= 3 ? 1 : 0;
            decreasing += diff >= -3 && diff <= -1 ? 1 : 0;
        }
        return increasing == sequence.length - 1 || decreasing == sequence.length - 1;
    }

    private static boolean checkValidSkip(int[] sequence) {
        // this exhaustively iterates over indexes to skip, it's inefficient, but simple to understand
        for (var skip = 0; skip < sequence.length; skip++) {
            // copy into a new array
            var skippedSequence = new int[sequence.length - 1];
            var j = 0;
            for (var i = 0; i < sequence.length; i++) {
                if (skip != i) {
                    skippedSequence[j++] = sequence[i];
                }
            }

            if (checkValid(skippedSequence)) {
                return true;
            }
        }
        return false;
    }
}
