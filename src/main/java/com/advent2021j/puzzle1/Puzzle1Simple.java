package com.advent2021j.puzzle1;

import com.advent2021j.base.BaseJ;

import java.io.IOException;
import java.util.ArrayList;

public class Puzzle1Simple extends BaseJ<ArrayList<Integer>, Solution, Solution> {
    public static void main(String[] args) {
        try {
            Puzzle1Simple puz = new Puzzle1Simple();
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
        Solution solution = new Solution();
        int last = data.get(0);
        for (int i = 1; i < data.size(); ++i) {
            int n = data.get(i);
            if (n > last) {
                ++solution.count;
            }
            last = n;
        }
        return solution;
    }

    @Override
    protected Solution computeSolution2(ArrayList<Integer> data) {
        Solution solution = new Solution();
        int last = data.get(0) + data.get(1) + data.get(2);
        for (int i = 1; i < data.size() - 2; ++i) {
            int n = data.get(i) + data.get(i + 1) + data.get(i + 2);
            if (n > last) {
                ++solution.count;
            }
            last = n;
        }
        return solution;
    }
}
