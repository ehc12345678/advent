using System.Runtime.InteropServices.JavaScript;
using advent.SharedBase;

namespace advent.Advent2025.Puzzle0;

using Data = List<int>;
using Solution1 = long;
using Solution2 = long;

public class Puzzle0 : Base<Data, Solution1, Solution2> 
{
    public static void Run()
    {
        try
        {
            var puz = new Puzzle0();
            var solution1 = puz.SolvePuzzle("inputs.txt", []);
            Console.WriteLine($"Solution1: {solution1}");

            var solution2 = puz.SolvePuzzle2("inputsTest.txt", []);
            Console.WriteLine($"Solution2: {solution2}");
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
        }
    }
    public override void ParseLine(string line, Data data)
    {
        data.Add(int.Parse(line));
    }

    public override Solution1 ComputeSolution(Data data)
    {
        return data.Aggregate((acc, n) => n + acc);
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return data.Aggregate((acc, n) => n * acc);
    }
}