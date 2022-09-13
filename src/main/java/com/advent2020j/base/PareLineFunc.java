package com.advent2020j.base;

@FunctionalInterface
public interface PareLineFunc<T> {
    void parseLine(String line, T data);
}
