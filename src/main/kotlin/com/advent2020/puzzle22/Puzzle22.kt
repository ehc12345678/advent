package com.advent.advent2020.puzzle22

import java.io.File
import java.util.LinkedList

fun main() {
    val puzzle = Puzzle22()
    try {
        val data = puzzle.readInputs("test.txt")
        puzzle.playWar(data)
        val answerA = data.score(data.winner())
        println("Answer A: $answerA")

        val data2 = puzzle.readInputs("inputs.txt")
        val winner = puzzle.playRecursiveWar(data2)
        val answerB = data2.score(winner)
        println("Answer B: $answerB")
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

typealias Deck = LinkedList<Int>
typealias SeenCards = String
typealias SeenDecks = HashSet<SeenCards>
class Data(var playerOne: Deck = Deck(), var playerTwo: Deck = Deck()) {
    fun winner() : Deck = if (playerOne.size > playerTwo.size) playerOne else playerTwo
    fun score(deck: Deck) : Long {
        return deck.foldIndexed(0L) { index, acc, i ->
            acc + ((deck.size - index) * i)
        }
    }

    // by adding a space, we can be sure that (1,2,3) (4,5) != (1,2) (3,4,5)
    fun seenCards() : SeenCards {
        return "${playerOne.joinToString(",")} ${playerTwo.joinToString(",")}"
    }
}

class Puzzle22 {
    fun readInputs(filename: String): Data {
        val file = File(filename)
        val lines = file.readLines()
        val data = Data()

        var x = 1
        while (lines[x].isNotEmpty()) {
            data.playerOne.add(lines[x++].toInt())
        }
        ++x
        if (lines[x++] != "Player 2:") throw IllegalArgumentException("Found unexpected $lines[x]")
        while (x < lines.size) {
            data.playerTwo.add(lines[x++].toInt())
        }
        return data
    }

    fun playWar(data: Data) {
        val playerOne = data.playerOne
        val playerTwo = data.playerTwo
        while (playerOne.isNotEmpty() && playerTwo.isNotEmpty()) {
            val playerOneCard = playerOne.removeFirst()
            val playerTwoCard = playerTwo.removeFirst()
            if (playerOneCard > playerTwoCard) {
                playerOne.add(playerOneCard)
                playerOne.add(playerTwoCard)
            } else {
                playerTwo.add(playerTwoCard)
                playerTwo.add(playerOneCard)
            }
        }
    }

    fun playRecursiveWar(data: Data): Deck {
        recurse(data)
        return data.winner()
    }

    private var gameNum = 0
    fun recurse(data: Data): Boolean {
        val game = ++gameNum
        println("=== Game $game ===")

        val playerOne = data.playerOne
        val playerTwo = data.playerTwo

        var round = 0
        val seen = SeenDecks()
        while (playerOne.isNotEmpty() && playerTwo.isNotEmpty()) {
            if (seen.contains(data.seenCards())) {
                return true
            }
            seen.add(data.seenCards())

            ++round
            println("\n-- Round $round (Game $game) --")
            println("Player 1's deck: ${playerOne.joinToString(", ")}")
            println("Player 2's deck: ${playerTwo.joinToString(", ")}")

            val playerOneCard = playerOne.removeFirst()
            val playerTwoCard = playerTwo.removeFirst()

            println("Player 1 plays: $playerOneCard")
            println("Player 2 plays: $playerTwoCard")
            val playerOneWon = if (playerOneCard <= playerOne.size && playerTwoCard <= playerTwo.size) {
                println("Playing a sub-game to determine the winner...\n")
                val newDeck = Data(
                    Deck(playerOne.subList(0, playerOneCard)),
                    Deck(playerTwo.subList(0, playerTwoCard))
                )
                recurse(newDeck)
            } else {
                playerOneCard > playerTwoCard
            }

            // two ways to win... higher card or recursion
            println("Player ${if (playerOneWon) 1 else 2} wins round $round of game $game!")
            if (playerOneWon) {
                playerOne.add(playerOneCard)
                playerOne.add(playerTwoCard)
            } else {
                playerTwo.add(playerTwoCard)
                playerTwo.add(playerOneCard)
            }
        }
        return playerOne.isNotEmpty()
    }
}
