package com.advent2019j.base;

@FunctionalInterface
public interface PareLineFunc<T> {
    void parseLine(String line, T data);
}
