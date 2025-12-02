// See https://aka.ms/new-console-template for more information

using advent.Advent2025.Puzzle1;
using advent.SharedBase;

namespace advent;

class Program
{
    const bool TestSolution1 = true;
    const bool TestSolution2 = false;

    private static readonly IPuzzle[] AllPuzzles =
    [
        new Puzzle1()
        // Add Puzzle2(), Puzzle3(), etc.
    ];
    
    public static void Main(string[] args)
    {
        int index;
        if (args.Length == 0)
        {
            PrintPuzzleList();
            index = AllPuzzles.Length;
        }
        else
        {
            index = int.Parse(args[0]);
        }
        var puzzle = AllPuzzles[index - 1];
        
        var puzzle1 = new Advent2025.Puzzle1.Puzzle1();
        int position = 0;
        SafeInstruction[] tests = [
            new SafeInstruction(Direction.Right, 100),
            new SafeInstruction(Direction.Left, 100),
            new SafeInstruction(Direction.Left, 101),
            new SafeInstruction(Direction.Left, 25),
            new SafeInstruction(Direction.Left, 100),
            new SafeInstruction(Direction.Left, 200),
            new SafeInstruction(Direction.Left, 50),
            new SafeInstruction(Direction.Right, 80),
            new SafeInstruction(Direction.Right, 680),
            new SafeInstruction(Direction.Left, 720),
        ];
        // foreach (var test in tests)
        // {
        //     var newPos = puzzle1.GetNewPosition(position, test, out var numTimesCrossZero);
        //     Console.WriteLine($"{position} ({test.Direction} {test.NumTurns}): {newPos} cross={numTimesCrossZero}");
        //     position = newPos;
        // }
        Console.WriteLine("-----");

        puzzle.Run(TestSolution1, TestSolution2);
        
    }
    
    private static void PrintPuzzleList()
    {
        Console.WriteLine("Available puzzles:");
        for (var i = 0; i < AllPuzzles.Length; i++)
        {
            Console.WriteLine($"  {i + 1}: {AllPuzzles[i].GetType().Name}");
        }
        Console.WriteLine();
    }
}
