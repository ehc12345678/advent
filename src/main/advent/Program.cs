// See https://aka.ms/new-console-template for more information

using advent.Advent2025.Puzzle1;
using advent.Advent2025.Puzzle2;
using advent.Advent2025.Puzzle3;
using advent.Advent2025.Puzzle4;
using advent.Advent2025.Puzzle5;
using advent.Advent2025.Puzzle6;
using advent.SharedBase;

namespace advent;

class Program
{
    private static readonly IPuzzle[] AllPuzzles =
    [
        new Puzzle1(),
        new Puzzle2(),
        new Puzzle3(),
        new Puzzle4(),
        new Puzzle5(),
        new Puzzle6(),
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
        puzzle.Run(puzzle.Solution1TestSolution, puzzle.Solution2TestSolution);
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
