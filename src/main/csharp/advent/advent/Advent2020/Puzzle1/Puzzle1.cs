using advent.SharedBase;

namespace advent.Advent2020.Puzzle1;

using Data = HashSet<int>;
using Solution1 = long;
using Solution2 = long;

public class Puzzle1 : Base<Data, Solution1, Solution2>
{
    private const int Goal = 2020;

    protected override Data CreateData()
    {
        return [];
    }
    
    public override void ParseLine(string line, Data data)
    {
        data.Add(int.Parse(line));
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var pair = FindTwoSumOperands(data, Goal);
        if (pair != null)
        {
            return pair.Item1 * pair.Item2;
        }

        return -1;
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