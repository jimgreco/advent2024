package advent2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 *
 */
public class Day17 {

    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Path.of("resources/day17"));
        Function<String, String> fn = str -> str.split(":")[1].strip();
        var registers = new long[] {
                Long.parseLong(fn.apply(input.get(0))),
                Long.parseLong(fn.apply(input.get(1))),
                Long.parseLong(fn.apply(input.get(2)))
        };
        var program = Arrays.stream(fn.apply(input.get(4)).split(",")).mapToInt(Integer::parseInt).toArray();

        // Solution 1: 5,1,3,4,3,7,2,1,7
        var output = runProgram(program, registers);
        System.out.println("Solution 1: " + String.join(",", output.stream().map(Object::toString).toList()));
    }

    private static int findRegA(int[] program) {
        // 2,4, 1,3, 7,5, 1,5, 0,3, 4,2, 5,5, 3,0
        // 2,4 regB = regA % 8
        // 1,3 regB = regB XOR 3
        // 7,5 regC = regA / 2^regB
        // 1,5 regB = regB XOR 5            - regB =
        // 0,3 regA = regA / 8              - regA drop last 3 bits
        // 4,2 regB = regB XOR regC         - regB = (regB XOR regC) last three bits
        // 5,5 output regB % 8              - RegB last 3 bits
        // 3,0 jump to start if regA != 0

        for (var i = 0; i < 100000; i++) {
            var instr = 0;
            var registers = new long[] { i, 0, 0 };
            var output = new ArrayList<Integer>();
            long[] lastRegisters = null;
            long[] lastLastRegisters = null;
            while (instr < program.length) {
                instr = runInstruction(instr, program[instr], program[instr + 1], registers, output);
                if (output.size() == 1 && lastLastRegisters == null) {
                    if (output.get(0) != program[0]) {
                        break;
                    }
                    lastLastRegisters = Arrays.copyOf(registers, 3);
                } else if (output.size() == 2 && lastRegisters == null) {
                    if (output.get(1) != program[1]) {
                        break;
                    }
                    lastRegisters = Arrays.copyOf(registers, 3);
                } else if (output.size() == 3) {
                    if (output.get(2) != program[2]) {
                        break;
                    }
                    System.out.println(i + ": " + lastLastRegisters[0] + ", " + lastLastRegisters[1] + ", " + lastLastRegisters[2]);
                    System.out.println(" - " + lastRegisters[0] + ", " + lastRegisters[1] + ", " + lastRegisters[2]);
                    System.out.println(" - " + registers[0] + ", " + registers[1] + ", " + registers[2]);
                    break;
                }
            }

        }

        return 0;

        // 4,2 regA = X, regB = regB XOR regC, regC = Z
        // 0,3 regA = regA / 8, regB = (regB XOR regC), regC = Z
        // 1,5 regA = regA / 8, regB = ((regB XOR 5) XOR regC), regC = Z
        // 7,5 regA = regA / 8, regB = ((regB XOR 5) XOR regC), regC = regA / 2^regB
        // 1,3 regA = (regA XOR 3) / 8, regB = ((regB XOR 5) XOR regC), regC = regA / 2^regB
        // 2,4 regA = (regA XOR 3) / 8, regB = (((regA / 8) XOR 5) XOR regC), regC = regA / 2^regB

        // 16 passes through the program
        // register B must be 2,4, 1,3, 7,5, 1,5, 0,3, 4,2, 5,5, 3,0 at the end of each pass
        // register A must be X,X, X,X, X,X, X,X, X,X, X,X, X,X, X,0 at the end of each pass
        // output register B % 8
        // register A 0 on the last one
    }

    private static List<Integer> runProgram(int[] program, long[] registers) {
        var output = new ArrayList<Integer>();
        var instr = 0;
        while (instr < program.length) {
            instr = runInstruction(instr, program[instr], program[instr + 1], registers, output);
        }
        return output;
    }

    private static int runInstruction(int inst, int opcode, int operand, long[] registers, List<Integer> output) {
        var combo = comboOperand(operand, registers);
        switch (opcode) {
            case 0 -> registers[0] = registers[0] / (1L << combo);
            case 1 -> registers[1] = registers[1] ^ operand;
            case 2 -> registers[1] = combo % 8;
            case 3 -> {
                if (registers[0] != 0) {
                    return operand;
                }
            }
            case 4 -> registers[1] = registers[1] ^ registers[2];
            case 5 -> output.add((int) (combo % 8));
            case 6 -> registers[1] = registers[0] / (1L << combo);
            case 7 -> registers[2] = registers[0] / (1L << combo);
            default -> throw new IllegalArgumentException();
        }
        return inst + 2;
    }

    private static long comboOperand(int operand, long[] registers) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> registers[0];
            case 5 -> registers[1];
            case 6 -> registers[2];
            default -> throw new IllegalArgumentException();
        };
    }
}
