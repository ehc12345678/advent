// See https://aka.ms/new-console-template for more information

using advent.Advent2020.Puzzle1;
using advent.SharedBase;

namespace advent;

class Program
{
    private static readonly IPuzzle[] AllPuzzles =
    [
        new Puzzle1()
        // Add Puzzle2(), Puzzle3(), etc.
    ];
    
    static void Main(string[] args)
    {
        var index = (args.Length == 0) ? AllPuzzles.Length : int.Parse(args[0]);
        var puzzle = AllPuzzles[index - 1];
        puzzle.Run();
    }
}
