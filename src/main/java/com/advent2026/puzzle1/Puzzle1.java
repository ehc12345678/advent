package com.advent2026.puzzle1;

import com.advent2026.base.BaseJ;

import java.io.IOException;
import java.util.ArrayList;

public class Puzzle1 extends BaseJ<ArrayList<Integer>, Integer, Integer> {
    public static void main(String[] args) {
        try {
            Puzzle1 puz = new Puzzle1();
            Integer solution1 = puz.solvePuzzle("inputsTest.txt", new ArrayList<>());
            System.out.println("Solution1: " + solution1);

            Integer solution2 = puz.solvePuzzle2("inputsTest.txt", new ArrayList<>());
            System.out.println("Solution2: " + solution2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void parseLine(String line, ArrayList<Integer> data) {
        data.add(Integer.parseInt(line));
    }

    @Override
    protected Integer computeSolution(ArrayList<Integer> data) {
        return data.stream().reduce(Integer::sum).orElse(0);
    }

    @Override
    protected Integer computeSolution2(ArrayList<Integer> data) {
        return data.stream().reduce(Integer::sum).orElse(0);
    }
}
