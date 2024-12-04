package advent2024;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * You're given a string to parse that contains mul(X,Y), do(), and don't() instructions.
 *
 * <p>Part 1: Find the sum product of all the mul(X,Y) instructions.
 *
 * <p>Part 2: The same as Part 1, but the don't() instruction stops processing of mul(X,Y) instructions and the do
 * instruction starts processing of mul(X,Y) instructions.
 * An initial do() instruction is implied.
 *
 * <p>Solution: A text parser that reads the string character by character.
 * Inline hashes are used for "mul(", "do()", and "don't()" to prevent repetitive string comparisons.
 */
public class Day03 {

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Path.of("resources/day03"));

        // Solution 1: 188741603
        var sumProduct = parse(input, false);
        System.out.println(sumProduct);

        // Solution 2: 67269798
        sumProduct = parse(input, true);
        System.out.println(sumProduct);
    }

    private static long parse(String input, boolean enableCheck) {
        var sumProduct = 0L;
        var state = new ParseState(input);
        var enabled = true;

        while (!state.isEos()) {
            var c = state.read();

            if (enableCheck && state.hash4 == ParseState.DO_HASH) {
                enabled = true;
                state.reset();
            } else if (enableCheck && state.hash7 == ParseState.DONT_HASH) {
                enabled = false;
            } else if (enabled) {
                if (state.hash4 == ParseState.MUL_HASH) {
                    state.readingFirstNum = true;
                } else if (state.readingFirstNum) {
                    if (c >= '0' && c <= '9') {
                        state.num1 = 10 * state.num1 + (c - '0');
                        state.numCount1++;
                    } else if (c == ',') {
                        state.readingFirstNum = false;
                        state.readingSecondNum = true;
                    } else {
                        // read an invalid char, go back 1
                        state.backtrack();
                    }
                } else if (state.readingSecondNum) {
                    if (c >= '0' && c <= '9') {
                        state.num2 = 10 * state.num2 + (c - '0');
                        state.numCount2++;
                    } else if (c == ')') {
                        // only a valid number if both numbers are 1 to 3 digits
                        if (state.numCount1 >= 1 && state.numCount1 <= 3 && state.numCount2 >= 1 && state.numCount2 <= 3) {
                            sumProduct += (long) state.num1 * state.num2;
                        }
                        state.reset();
                    } else {
                        // read an invalid char, go back 1
                        state.backtrack();
                    }
                }
            }
        }
        return sumProduct;
    }

    private static class ParseState {

        static final int MUL_HASH;
        static final int DONT_HASH;
        static final int DO_HASH;

        static {
            MUL_HASH = buildHash("mul(");
            DONT_HASH = buildHash("don't()");
            DO_HASH = buildHash("do()");
        }

        private static int buildHash(String string) {
            var bytes = string.getBytes(StandardCharsets.UTF_8);
            var hash = 0;
            for (var b : bytes) {
                hash = 31 * hash + b;
            }
            return hash;
        }

        private final String input;
        boolean readingFirstNum;
        boolean readingSecondNum;
        int num1;
        int num2;
        int numCount1;
        int numCount2;
        int hash4;
        int hash7;
        int lastHash4;
        int lastHash7;
        int index;

        ParseState(String input) {
            this.input = input;
        }

        boolean isEos() {
            return index == input.length();
        }

        void reset() {
            readingFirstNum = false;
            readingSecondNum = false;
            num1 = 0;
            num2 = 0;
            numCount1 = 0;
            numCount2 = 0;
        }

        void backtrack() {
            if (index != input.length() - 1) {
                index--;
                hash4 = lastHash4;
                hash7 = lastHash7;
                reset();
            }
        }

        char read() {
            lastHash4 = hash4;
            if (index - 4 >= 0) {
                hash4 -= 31 * 31 * 31 * input.charAt(index - 4);
            }

            lastHash7 = hash7;
            if (index - 7 >= 0) {
                hash7 -= 31 * 31 * 31 * 31 * 31 * 31 * input.charAt(index - 7);
            }

            var c = input.charAt(index++);
            hash4 = 31 * hash4 + c;
            hash7 = 31 * hash7 + c;

            return c;
        }
    }
}
