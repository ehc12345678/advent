package com.advent2020j.puzzle1;

import com.advent2020j.base.BaseJ;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Puzzle1 extends BaseJ<Set<Integer>, Long, Long> {
    public static void main(String[] args) throws IOException {
        Puzzle1 puz = new Puzzle1();
        Set<Integer> data = new HashSet<>();
        Long solution1 = puz.solvePuzzle("inputs.txt", data);
        System.out.println("Solution1: " + solution1);
    }

    @Override
    protected void parseLine(String line, Set<Integer> data) {
        data.add(Integer.parseInt(line.trim()));
    }

    @Override
    protected Long computeSolution(Set<Integer> data) {
        final int GOAL = 2020;
        for (Integer value : data) {
            Integer other = GOAL - value;
            if (data.contains(other)) {
                return other.longValue() * value.longValue();
            }
        }
        return -1L;
    }

    @Override
    protected Long computeSolution2(Set<Integer> data) {
        return null;
    }
}
