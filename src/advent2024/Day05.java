package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * You're given:
 * <ol>
 *     <li>A sequence of pairs that define order dependency rules.
 *         If both numbers are in a list of numbers then the first number must come after the second number.
 *     <li>A sequence of a list of numbers which, if valid, respect the order dependency rules.
 * </ol>
 *
 * <p>Part 1: Find all valid lists of numbers.
 * Solution: Create a map from the order dependency rules of each number to a list of its dependencies.
 * Iterate through each list of numbers.
 * For each number, check that for each dependency in the list that it has been seen in earlier in the list of numbers.
 *
 * <p>Part 2: Correct the invalid lists of numbers.
 * Solution: For each invalid list of numbers you can sort the invalid list using the order dependency rules.
 */
public class Day05 {

    public static void main(String[] args) throws IOException {
        // read the ordering rules into a list of pairs
        // read the page lists in a list of lists of numbers
        var input = Files.readAllLines(Path.of("resources/day05"));
        var orderingRules = new ArrayList<int[]>();
        var pages = new ArrayList<List<Integer>>();
        for (var line : input) {
            if (line.contains("|")) {
                orderingRules.add(Arrays.stream(line.split("\\|")).mapToInt(Integer::parseInt).toArray());
            } else if (line.contains(",")) {
                pages.add(Arrays.stream(line.split(",")).map(Integer::parseInt).toList());
            }
        }

        // Solution 1: 5588
        int count = doPart1(orderingRules, pages);
        System.out.println(count);

        // Solution 2: 5331
        count = doPart2(orderingRules, pages);
        System.out.println(count);
    }

    private static int doPart1(List<int[]> orderingRules, List<List<Integer>> pageLists) {
        var pageDependencies = buildOrderRuleDependencies(orderingRules);
        var sum = 0;
        for (var pageList : pageLists) {
            var valid = isValidOrder(pageDependencies, pageList);
            if (valid) {
                sum += pageList.get(pageList.size() / 2); // the middle page
            }
        }
        return sum;
    }

    private static int doPart2(List<int[]> orderingRules, List<List<Integer>> pageLists) {
        var pageDependencies = buildOrderRuleDependencies(orderingRules);
        var sum = 0;
        for (var pageList : pageLists) {
            var valid = isValidOrder(pageDependencies, pageList);
            if (!valid) {
                var sortedList = new ArrayList<>(pageList);
                sortedList.sort((i1, i2) -> !pageDependencies.get(i1).contains(i2) ? -1 : 1);
                sum += sortedList.get(sortedList.size() / 2); // the middle page
            }
        }
        return sum;
    }

    private static HashMap<Integer, Set<Integer>> buildOrderRuleDependencies(List<int[]> orderingRules) {
        var pageDependencies = new HashMap<Integer, Set<Integer>>();
        for (var orderingRule : orderingRules) {
            var pageDependency = pageDependencies.computeIfAbsent(orderingRule[1], key -> new HashSet<>());
            pageDependency.add(orderingRule[0]);
        }
        return pageDependencies;
    }

    private static boolean isValidOrder(HashMap<Integer, Set<Integer>> pageDependencies, List<Integer> pageList) {
        var valid = true;
        var pagesSeen = new HashSet<Integer>();
        for (var page : pageList) {
            for (var dependency : pageDependencies.get(page)) {
                valid &= !pageList.contains(dependency) || pagesSeen.contains(dependency);
            }
            pagesSeen.add(page);
        }
        return valid;
    }
}
