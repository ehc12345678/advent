package com.advent2026.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class BaseJ<Data, Solution1, Solution2>  {
    protected Data readInput(String fileName, Data data, PareLineFunc<Data> pareLineFunc)
        throws IOException {

        // Maybe a hack, but makes this much easier to debug in general
        String className = this.getClass().getName().toLowerCase();
        className = className.substring(0, className.lastIndexOf("."));
        File file = new File("src/main/java/" + className.replace(".", "/"), fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            do {
                if ((line = reader.readLine()) != null) {
                    pareLineFunc.parseLine(line, data);
                }
            } while (line != null);
        }
        return data;
    }

    protected abstract void parseLine(String line, Data data);
    protected void parseLine2(String line, Data data) {
        parseLine(line, data);
    }

    protected Solution1 solvePuzzle(String filename, Data data) throws IOException {
        Data newData = readInput(filename, data, this::parseLine);
        return computeSolution(newData);
    }

    protected Solution2 solvePuzzle2(String filename, Data data) throws IOException {
        Data newData = readInput(filename, data, this::parseLine2);
        return computeSolution2(newData);
    }

    protected abstract Solution1 computeSolution(Data data);
    protected abstract Solution2 computeSolution2(Data data);
}

