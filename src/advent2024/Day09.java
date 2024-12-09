package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * You're given a string of single-digit integers.
 * For every pair of numbers, the first number represents the size of a file, the second number represents the size of
 * the freespace after the file.
 * These files and freespace are laid out sequentially so the first file is at position 0 and the freespace immediately
 * follows at {@code position = len(file 0)}.
 * The second file is at {@code position = length(file 0) + length(freespace 0)}.
 * And so on for all pairs of numbers.
 * Each file is assigned a monotonically increasing id starting at 0.
 */
public class Day09 {

    public static void main(String[] args) throws IOException {
        // read into a string and convert characters to bytes
        var input = Files.readString(Path.of("resources/day09"));
        var file = new byte[input.length()];
        for (var i = 0; i < input.length(); i++) {
            file[i] = (byte) (input.charAt(i) - '0');
        }

        // Solution 1: 6337921897505
        var checksum = doPart1(file);
        System.out.println(checksum);

        // Solution 2 : 6362722604045
        checksum = doPart2(file);
        System.out.println(checksum);
    }

    private static long doPart1(byte[] input) {
        var fileIdx = 0;
        var leftPtr = 0;
        var rightPtr = input.length + 1; // no freespace in the last byte
        var checksum = 0L;
        var rightFileSize = 0;

        while (leftPtr < rightPtr) {
            var leftId = leftPtr / 2;
            var leftFileSize = input[leftPtr];
            var leftFreespaceSize = input[leftPtr + 1];
            leftPtr += 2;

            for (var i = 0; i < leftFileSize; i++) {
                checksum += (long) fileIdx * leftId;
                fileIdx++;
            }

            for (var i = 0; i < leftFreespaceSize; i++) {
                if (rightFileSize == 0) {
                    rightPtr -= 2;
                    rightFileSize = input[rightPtr];
                }

                var rightId = rightPtr / 2;
                checksum += (long) fileIdx * rightId;
                fileIdx++;
                rightFileSize--;
            }
        }

        // clean up the remainder from the right pointer
        for (var i = 0; i < rightFileSize; i++) {
            var rightId = rightPtr / 2;
            checksum += (long) fileIdx * rightId;
            fileIdx++;
        }

        return checksum;
    }

    private static long doPart2(byte[] sizes) {
        var blocks = new ArrayList<Block>();
        var blocksWithFreeSpace = new LinkedList<Block>();
        var position = 0;

        // create the model
        for (var i = 0; i < sizes.length; i += 2) {
            var fileSize = sizes[i];
            var freespaceSize = i + 1 < sizes.length ? sizes[i + 1] : 0;
            var block = new Block(i / 2, position, freespaceSize);
            block.files.add(new File(blocks.size(), fileSize));
            blocks.add(block);
            position += fileSize + freespaceSize;

            if (block.freeSpaceAfterFiles > 0) {
                blocksWithFreeSpace.add(block);
            }
        }

        // iterate backwards through blocks and attempt to move them
        for (var i = blocks.size() - 1; i >= 0; i--) {
            var blockToMove = blocks.get(i);
            var blockToMoveFileSize = blockToMove.files.getFirst().size;

            var freespaceIt = blocksWithFreeSpace.iterator();
            while (freespaceIt.hasNext()) {
                var blockToRecv = freespaceIt.next();
                if (blockToRecv.index >= i) {
                    // surpassed the file that is being moved, cannot move file
                    break;
                } else if (blockToRecv.freeSpaceAfterFiles >= blockToMoveFileSize) {
                    // file is smaller than free space, can move file here
                    var file = blockToMove.files.removeFirst();
                    blockToRecv.files.addLast(file);
                    blockToMove.freeSpaceBeforeFiles += file.size;
                    blockToRecv.freeSpaceAfterFiles -= file.size;
                    if (blockToRecv.freeSpaceAfterFiles == 0) {
                        freespaceIt.remove();
                    }
                    break;
                }
            }
        }

        // calculate the checksum
        position = 0;
        var checksum = 0L;
        for (var block : blocks) {
            position += block.freeSpaceBeforeFiles;
            for (var file : block.files) {
                for (var i = 0; i < file.size; i++) {
                    checksum += (long) file.id * position;
                    position++;
                }
            }
            position += block.freeSpaceAfterFiles;
        }
        return checksum;
    }

    private record File(int id, int size) { }

    private static class Block {

        final int index;
        final int startPosition;
        final List<File> files;
        int freeSpaceBeforeFiles;
        int freeSpaceAfterFiles;

        Block(int index, int startPosition, int freeSpaceAfterFiles) {
            this.index = index;
            this.startPosition = startPosition;
            this.freeSpaceAfterFiles = freeSpaceAfterFiles;
            files = new LinkedList<>();
        }
    }
}
