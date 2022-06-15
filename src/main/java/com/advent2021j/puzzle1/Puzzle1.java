package com.advent2021j.puzzle1;

import com.advent2021j.base.BaseJ;

import java.io.IOException;
import java.util.ArrayList;

class Solution {
    public int count = 0;
}

public class Puzzle1 extends BaseJ<ArrayList<Integer>, Solution, Solution> {
    public static void main(String[] args) {
        try {
            Puzzle1 puz = new Puzzle1();
            Solution solution1 = puz.solvePuzzle("inputs.txt", new ArrayList<>());
            System.out.println("Solution1: " + solution1.count);

            Solution solution2 = puz.solvePuzzle2("inputs.txt", new ArrayList<>());
            System.out.println("Solution2: " + solution2.count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void parseLine(String line, ArrayList<Integer> data) {
        data.add(Integer.parseInt(line));
    }

    @Override
    protected Solution computeSolution(ArrayList<Integer> data) {
        return computeWindow(data, 1);
    }

    @Override
    protected Solution computeSolution2(ArrayList<Integer> data) {
        return computeWindow(data, 3);
    }

    private Solution computeWindow(ArrayList<Integer> data, int windowSize) {
        Solution solution = new Solution();
        int last = sumWindow(data, 0, windowSize);
        for (int index = 1; index <= data.size() - windowSize; ++index) {
            int window = sumWindow(data, index, windowSize);
            if (window > last) {
                solution.count++;
            }
            last = window;
        }
        return solution;
    }

    private int sumWindow(ArrayList<Integer> data, int index, int windowSize) {
        return data.subList(index, index + windowSize).stream().reduce(Integer::sum).orElse(0);
    }
}
