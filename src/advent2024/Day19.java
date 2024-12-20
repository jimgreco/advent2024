package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * You're given two lists of strings.
 *
 * <p>Part 1:For each of the second list of strings, find if there is any combination of the first list of strings
 * (including using any of the strings in the first list multiple times) that can form the string in the second list.
 *
 * <p>Part 2:For each of the second list of strings, find all the combinations of the first list of strings that can
 * form the string in the second list.
 *
 * <p>Solution: Build a Trie of the first string list to make search log(N) through the list and remove string compares.
 * Start at the root of the Trie and the first letter in the string.
 * Then, for each subsequent letter in the string, go to a child of the current Trie node, and if the Trie is terminal
 * (i.e., a complete string from the first list has been processed), go to the root of the Trie and navigate again.
 * A valid combination is recorded when the Trie is terminal and there are no more characters in the string left to
 * process.
 */
public class Day19 {

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("resources/day19"));
        var towels = input.get(0).split(", ");
        var patterns = input.subList(2, input.size());

        // Solution 1: 327
        var numOk = doPart1(towels, patterns);
        System.out.println("Solution 1: " + numOk);

        // Solution 2: 772696486795255
        var nPatterns = doPart2(towels, patterns);
        System.out.println("Solution 2: " + nPatterns);
    }

    private static long doPart1(String[] towels, List<String> patterns) {
        var root = buildTrie(towels);
        return patterns.stream().mapToLong(p -> search(root, root.getChild(p), p, new HashMap<>()) > 0 ? 1 : 0).sum();
    }

    private static long doPart2(String[] towels, List<String> patterns) {
        var root = buildTrie(towels);
        return patterns.stream().mapToLong(p -> search(root, root.getChild(p), p, new HashMap<>())).sum();
    }

    private static long search(Trie root, Trie curr, String currStr, Map<String, Long> memo) {
        if (curr == null) {
            return 0;
        }
        if (currStr.length() == 1) {
            // one char left, only valid if a pattern is complete
            return curr.terminal ? 1 : 0;
        }
        var remaining = currStr.substring(1);

        // search the next level of the Trie
        var res = search(root, curr.getChild(remaining), remaining, memo);
        if (curr.terminal) {
            // by using terminal we don't need to keep track of so much state in our memo
            if (memo.containsKey(remaining)) {
                res += memo.get(remaining);
            } else {
                // navigate from the root again, new search
                var rootRes = search(root, root.getChild(remaining), remaining, memo);
                memo.put(remaining, rootRes);
                res += rootRes;
            }
        }
        return res;
    }

    private static Trie buildTrie(String[] towels) {
        var trie = new Trie((char) 0);
        for (var towel : towels) {
            trie.addString(towel);
        }
        return trie;
    }

    private static class Trie {

        final Map<Character, Trie> children;
        private final char c;
        private boolean terminal;

        private Trie(char c) {
            this.c = c;
            children = new HashMap<>();
        }

        Trie getChild(String str) {
            return children.get(str.charAt(0));
        }

        void addString(String str) {
            if (str.length() == 0) {
                terminal = true;
            } else {
                var c = str.charAt(0);
                var child = children.get(c);
                if (child == null) {
                    child = new Trie(c);
                    children.put(c, child);
                }
                child.addString(str.substring(1));
            }
        }
    }
}
