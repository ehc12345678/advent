package com.advent2026.base;

@FunctionalInterface
public interface PareLineFunc<T> {
    void parseLine(String line, T data);
}
