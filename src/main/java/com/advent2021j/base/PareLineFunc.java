package com.advent2021j.base;

@FunctionalInterface
public interface PareLineFunc<T> {
    void parseLine(String line, T data);
}
