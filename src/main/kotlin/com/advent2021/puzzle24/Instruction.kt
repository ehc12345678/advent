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

abstract class CouldBe {
    abstract fun minValue(): Value?
    abstract fun maxValue(): Value?
}

class CouldBeExact(val number: Int): CouldBe() {
    override fun minValue(): Value = Number(number)
    override fun maxValue(): Value = Number(number)
}

class CouldBeRange(val min: Value, val max: Value): CouldBe() {
    override fun minValue(): Value = min
    override fun maxValue(): Value = max
}

abstract class Value {
    open fun currentValue(): Value = this
    open fun getNumberValue(): Int? = null

    abstract fun getPossibleRange(): Range

    abstract fun add(otherValue: Value): Value
    abstract fun multiply(otherValue: Value): Value
    abstract fun divide(otherValue: Value): Value
    abstract fun mod(otherValue: Value): Value
    abstract fun eql(otherValue: Value): Value
    abstract fun mustBe(otherValue: Value): Value
//    override fun toString(): String = getNumberValue()?.toString() ?: getRange().toString()

    abstract fun couldBe(): CouldBe
}

class Number(val number: Int): Value() {
    override fun add(otherValue: Value): Value {
        val numberValue = otherValue.getNumberValue()
        if (numberValue != null) {
            return Number(number + numberValue)
        }
        return otherValue.add(this)
    }

    override fun multiply(otherValue: Value): Value {
        val numberValue = otherValue.getNumberValue()
        if (numberValue != null) {
            return Number(number * numberValue)
        }
        return otherValue.multiply(this)
    }

    override fun divide(otherValue: Value): Value {
        val numberValue = otherValue.getNumberValue()
        if (numberValue != null) {
            return Number(number / numberValue)
        }
        return otherValue.divide(this)
    }

    override fun mod(otherValue: Value): Value {
        val numberValue = otherValue.getNumberValue()
        if (numberValue != null) {
            return Number(number % numberValue)
        }
        return otherValue.mod(this)
    }

    override fun eql(otherValue: Value): Value {
        val numberValue = otherValue.getNumberValue()
        if (numberValue != null) {
            return Number(if (numberValue == number) 1 else 0)
        }
        return otherValue.eql(this)
    }

    override fun mustBe(otherValue: Value): Value {
        // not yet
        return this
    }

    override fun getNumberValue(): Int = number
    override fun getPossibleRange(): Range = Range(this, this)

    override fun couldBe(): CouldBe = CouldBeExact(number)
}

open class Variable(val name: String): Value() {
    var value: Value = Number(0)
    var range: Range = value.getPossibleRange()
    var couldBe: CouldBe = CouldBeExact(0)
    override fun getPossibleRange(): Range = range

    fun assign(avalue: Value): Variable {
        value = avalue
        return this
    }
    override fun currentValue(): Value = value
    override fun getNumberValue(): Int? = value.getNumberValue()

    override fun add(otherValue: Value): Value {
        val newValue = value.add(otherValue)
        addCouldBe(value.couldBe())
        return newValue
    }
    override fun multiply(otherValue: Value): Value {
        val newValue = value.multiply(otherValue)
        addCouldBe(value.couldBe())
        return newValue
    }

    override fun divide(otherValue: Value): Value {
        val newValue = value.multiply(otherValue)
        addCouldBe(value.couldBe())
        return newValue
    }
    override fun mod(otherValue: Value): Value {
        val newValue = value.mod(otherValue)
        addCouldBe(value.couldBe())
        return newValue
    }
    override fun eql(otherValue: Value): Value {
        val newValue = value.eql(otherValue)
        addCouldBe(value.couldBe())
        return newValue
    }
    override fun mustBe(otherValue: Value): Value {
        val newValue = value.mustBe(otherValue)
        addCouldBe(value.couldBe())
        return newValue
    }

    private fun addCouldBe(couldBe: CouldBe) {
        // meh
    }

    override fun couldBe(): CouldBe = couldBe

    //    override fun getRange(): Range = Range(value, value)
    //    override fun toString(): String = "$name=$value"
    override fun toString(): String = "$name"
}

class InputVar(val number: Int): Variable("input$number") {
    init {
        range = IntegerRange(1, 9)
    }

    fun maxCouldBe(): Int? {
        return couldBe.maxValue()?.getNumberValue()
    }

    override fun add(otherValue: Value): Value {
        return super.add(otherValue)
    }

    override fun multiply(otherValue: Value): Value {
        return super.multiply(otherValue)
    }

    override fun divide(otherValue: Value): Value {
        return super.divide(otherValue)
    }

    override fun mod(otherValue: Value): Value {
        return super.mod(otherValue)
    }

    override fun eql(otherValue: Value): Value {
        return super.eql(otherValue)
    }

    override fun mustBe(otherValue: Value): Value {
        return super.mustBe(otherValue)
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
        if ((value.getNumberValue() ?: 0) <= 0) {
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

class MustBe(dest: Variable, value: Value): Instruction(dest, value) {
    override fun execute(symbolTable: SymbolTable) {
        dest.assign(dest.mustBe(value))
    }
    override fun toString(): String = "mustBe $dest $value"

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
