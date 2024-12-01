package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

/**
 * You're given a sequence of pairs of numbers.
 * The first number in each pair is an element in list 1.
 * The second number in each pair is an element in list 2.
 *
 * <p>Part 1: Find the sum of the absolute difference between each element in list 1 and list 2,
 * in sorted order.
 * Solution: Sort each list.
 * Sum the absolute value of the difference between the elements at each index in the list.
 *
 * <p>Part 2: Find the sum of the product of each element in list 1 and the number of times that
 * element occurs in list 2.
 * Solution: Create a map of the elements in list 2 to the number of times those elements occur.
 * Sum the product of the elements in list 1 and the corresponding map value.
 */
public class Day01 {

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Path.of("resources/day01"));
        var list1 = new int[lines.size()];
        var list2 = new int[lines.size()];

        for (var i = 0; i < lines.size(); i++) {
            var nums = lines.get(i).strip().split("\\s+");
            list1[i] = Integer.parseInt(nums[0]);
            list2[i] = Integer.parseInt(nums[1]);
        }

        // Solution: 3508942
        var distance = doPart1(list1, list2);
        System.out.println(distance);

        // Solution: 26593248
        var similarity = doPart2(list1, list2);
        System.out.println(similarity);
    }

    private static int doPart1(int[] list1, int[] list2) {
        Arrays.sort(list1);
        Arrays.sort(list2);

        var dist = 0;
        for (var i = 0; i < list1.length; i++) {
            dist += Math.abs(list1[i] - list2[i]);
        }
        return dist;
    }

    private static int doPart2(int[] list1, int[] list2) {
        var map2 = new HashMap<Integer, Integer>();

        for (var i = 0; i < list1.length; i++) {
            map2.put(list2[i], map2.getOrDefault(list2[i], 0) + 1);
        }

        var similarity = 0;
        for (int el : list1) {
            similarity += el * map2.getOrDefault(el, 0);
        }
        return similarity;
    }
}
