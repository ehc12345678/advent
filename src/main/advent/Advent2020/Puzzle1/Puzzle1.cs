using advent.SharedBase;

namespace advent.Advent2020.Puzzle1;

using Data = HashSet<int>;
using Solution1 = long;
using Solution2 = long;

public class Puzzle1 : Base<Data, Solution1, Solution2>
{
    private const int Goal = 2020;

    public static void Run()
    {
        try
        {
            var puz = new Puzzle1();
            var solution1 = puz.SolvePuzzle("inputs.txt", []);
            Console.WriteLine($"Solution1: {solution1}");

            var solution2 = puz.SolvePuzzle2("inputs.txt", []);
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
        var pair = FindTwoSumOperands(data, Goal);
        return pair.Item1 * pair.Item2;
    }

    private static Tuple<int, int>? FindTwoSumOperands(Data data, int goal)
    {
        return 
            data
                .Where(item => data.Contains(goal - item))
                .Select(item => new Tuple<int, int>(item, goal - item))
                .FirstOrDefault();
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        foreach (var item in data)
        {
            var diff = Goal - item;
            var otherTwo = FindTwoSumOperands(data, diff);
            if (otherTwo != null)
            {
                return item * otherTwo.Item1 * otherTwo.Item2;
            }
        }

        return 0;
    }
}