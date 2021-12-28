package com.advent2021.puzzle24

import java.lang.IllegalStateException


open class Range(
    open val min: Value? = null,
    open val max: Value? = null
) {
    override fun toString(): String = "($min..$max)"

    fun add(value: Value): Range {
        return Range(min?.add(value), max?.add(value))
    }
}

open class IntegerRange(minNumber: Value, maxNumber: Value): Range(minNumber, maxNumber) {
    constructor(min: Int, max: Int): this(Number(min), Number(max))
}

open class CouldBe(val range: Range) {
}

abstract class Value {
    open fun currentValue(): Value = this
    open fun getNumberValue(): Int? = null

    abstract fun add(otherValue: Value): Value
    abstract fun multiply(otherValue: Value): Value
    abstract fun divide(otherValue: Value): Value
    abstract fun mod(otherValue: Value): Value
    abstract fun eql(otherValue: Value): Value
//    override fun toString(): String = getNumberValue()?.toString() ?: getRange().toString()
}

class Number(val number: Int): Value() {
    override fun add(otherValue: Value): Value {
        val numberValue = otherValue.getNumberValue()
        if (numberValue != null) {
            return Number(number + numberValue)
        }

        // work on could be

        TODO()
    }

    override fun multiply(otherValue: Value): Value {
        TODO("Not yet implemented")
    }

    override fun divide(otherValue: Value): Value {
        TODO("Not yet implemented")
    }

    override fun mod(otherValue: Value): Value {
        TODO("Not yet implemented")
    }

    override fun eql(otherValue: Value): Value {
        TODO("Not yet implemented")
    }

    override fun getNumberValue(): Int = number
}

open class Variable(val name: String): Value() {
    var value: Value = Number(0)
    var couldBe: CouldBe = CouldBe(IntegerRange(0, 0))

    fun assign(avalue: Value): Variable {
        value = avalue
        return this
    }
    override fun currentValue(): Value = value
    override fun getNumberValue(): Int? = value.getNumberValue()

    override fun add(otherValue: Value): Value {
        val newValue = value.add(otherValue)
        // work on could be
        return newValue
    }
    override fun multiply(otherValue: Value): Value {
        val newValue = value.multiply(otherValue)
        // work on could be
        return newValue
    }

    override fun divide(otherValue: Value): Value {
        val newValue = value.multiply(otherValue)
        // work on could be
        return newValue
    }
    override fun mod(otherValue: Value): Value {
        val newValue = value.mod(otherValue)
        // work on could be
        return newValue
    }
    override fun eql(otherValue: Value): Value {
        val newValue = value.eql(otherValue)
        // work on could be
        return newValue
    }

    //    override fun getRange(): Range = Range(value, value)
    //    override fun toString(): String = "$name=$value"
    override fun toString(): String = "$name"
}

class InputVar(val number: Int): Variable("input$number") {
    init {
        couldBe = CouldBe(IntegerRange(1, 9))
    }

    fun maxCouldBe(): Int {
        TODO()
    }
}

// --- Instructions
abstract class Instruction(val dest: Variable, val value: Value) {
    abstract fun execute(symbolTable: SymbolTable)
}

class InputInstruction(dest: Variable, inputVar: InputVar): Instruction(dest, inputVar) {
    override fun execute(symbolTable: SymbolTable) {
        dest.assign(symbolTable.currentInput())
    }

    override fun toString(): String = "inp $value"
}

class Add(dest: Variable, value: Value): Instruction(dest, value) {
    override fun execute(symbolTable: SymbolTable) {
        if (value.getNumberValue() == 0) {
            return
        }
        dest.assign(dest.add(value))
    }

    override fun toString(): String = "add $dest $value"
}

class Multiply(dest: Variable, value: Value): Instruction(dest, value) {
    override fun execute(symbolTable: SymbolTable) {
        var newValue: Value = value
        // no op
        if (value.getNumberValue() == 1) {
            return
        }
        dest.assign(dest.multiply(value))
    }

    override fun toString(): String = "mul $dest $value"
}

class Divide(dest: Variable, value: Value): Instruction(dest, value) {
    override fun execute(symbolTable: SymbolTable) {
        // no op
        if (value.getNumberValue() == 1) {
            return
        }
        if (value.getNumberValue() == 0) {
            throw IllegalStateException("Cannot divide by zero")
        }
        dest.assign(dest.divide(value))
    }

    override fun toString(): String = "div $dest $value"

}

class Mod(dest: Variable, value: Value): Instruction(dest, value) {
    override fun execute(symbolTable: SymbolTable) {
        if (value.getNumberValue() ?: 0 <= 0) {
            throw IllegalStateException("Mod must be a positive non zero integer")
        }
        dest.assign(dest.divide(value))
    }

    override fun toString(): String = "mod $dest $value"
}

class Equal(dest: Variable, value: Value): Instruction(dest, value) {
    override fun execute(symbolTable: SymbolTable) {
        dest.assign(dest.eql(value))
    }

    override fun toString(): String = "eql $dest $value"
}

// --- Symbol table ---

class SymbolTable {
    val symbols = HashMap<String, Variable>()
    val inputs = ArrayList<InputVar>()

    fun createInputVar(): InputVar {
        val ret  = InputVar(inputs.size)
        inputs.add(ret)
        return ret
    }

    fun assignVar(name: String, value: Value): Variable {
        symbols.putIfAbsent(name, Variable(name))
        return symbols[name]!!.assign(value)
    }

    fun getVar(name: String): Variable = symbols[name] ?: throw IllegalArgumentException("cannot find $name")

    fun currentInput(): InputVar = inputs.last()
}
