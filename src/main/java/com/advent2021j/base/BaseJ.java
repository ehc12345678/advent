package com.advent2021j.base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class BaseJ<T, V, U>  {
    protected T readInput(String fileName, T data, PareLineFunc<T> pareLineFunc)
        throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            do {
                if ((line = reader.readLine()) != null) {
                    pareLineFunc.parseLine(line, data);
                }
            } while (line != null);
        }
        return data;
    }

    protected abstract void parseLine(String line, T data);
    protected void parseLine2(String line, T data) {
        parseLine(line, data);
    }

    protected V solvePuzzle(String filename, T data) throws IOException {
        T newData = readInput(filename, data, this::parseLine);
        return computeSolution(newData);
    }

    protected U solvePuzzle2(String filename, T data) throws IOException {
        T newData = readInput(filename, data, this::parseLine2);
        return computeSolution2(newData);
    }

    protected abstract V computeSolution(T data);
    protected abstract U computeSolution2(T data);
}

