package com.advent2021j.puzzle2;

import com.advent2021j.base.BaseJ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

enum Direction { forward, up, down }
class InputLine {
    Direction dir;
    int x;

    public InputLine(Direction dir, int x) {
        this.dir = dir;
        this.x = x;
    }
}

class Position {
    int depth = 0;
    int distance = 0;
    int aim = 0;

    public Position() {
    }

    public Position(int depth, int distance, int aim) {
        this.depth = depth;
        this.distance = distance;
        this.aim = aim;
    }

    int solution() { return depth * distance; }

}

public class Puzzle2 extends BaseJ<List<InputLine>, Integer, Integer> {
    public static void main(String[] args) {
        try {
            Puzzle2 puz = new Puzzle2();
            Integer solution1 = puz.solvePuzzle("inputs.txt", new ArrayList<>());
            System.out.println("Solution1: " + solution1);

            Integer solution2 = puz.solvePuzzle2("inputs.txt", new ArrayList<>());
            System.out.println("Solution2: " + solution2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void parseLine(String line, List<InputLine> data) {
         String[] parts = line.split(" ");
         Direction dir = Direction.valueOf(parts[0]);
         int x = Integer.parseInt(parts[1]);
         data.add(new InputLine(dir, x));
    }

    @Override
    protected Integer computeSolution(List<InputLine> data) {
        Position pos = new Position();
        for (InputLine datum : data) {
            switch (datum.dir) {
                case forward:
                    pos = new Position(pos.depth, pos.distance + datum.x, pos.aim);
                    break;
                case up:
                    int x = -datum.x;
                    pos = new Position(pos.depth + x, pos.distance, pos.aim);
                    break;
                case down:
                    pos = new Position(pos.depth + datum.x, pos.distance, pos.aim);
                    break;
            }
        }
        return pos.solution();
    }

    @Override
    protected Integer computeSolution2(List<InputLine> data) {
        Position pos = new Position();
        for (InputLine datum : data) {
            switch (datum.dir) {
                case forward:
                    pos = new Position(pos.depth, pos.distance + datum.x, pos.aim);
                    pos = new Position(pos.depth + pos.aim * datum.x, pos.distance, pos.aim);
                    break;
                case up:
                    int x = -datum.x;
                    pos = new Position(pos.depth, pos.distance, pos.aim + x);
                    break;
                case down:
                    pos = new Position(pos.depth, pos.distance, pos.aim + datum.x);
                    break;
            }
        }
        return pos.solution();
    }
}
