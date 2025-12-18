using advent.SharedBase;

namespace advent.Advent2025.Puzzle0;

using Data = List<int>;
using Solution1 = long;
using Solution2 = long;

public class Puzzle0 : Base<Data, Solution1, Solution2> 
{
    public override bool Solution1TestSolution => true;
    public override bool Solution2TestSolution => true;

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
        return data.Aggregate((acc, n) => n + acc);
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return data.Aggregate((acc, n) => n * acc);
    }
}