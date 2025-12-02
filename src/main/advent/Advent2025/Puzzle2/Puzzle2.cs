using System.Numerics;
using advent.SharedBase;

namespace advent.Advent2025.Puzzle2;
using Data = List<Range>;
using Solution1 = BigInteger;
using Solution2 = BigInteger;

public struct Range
{
    public long Min { get; }
    public long Max { get; }

    public Range(long min, long max)
    {
        Min = min;
        Max = max;
    }
}

public class Puzzle2 : Base<Data, Solution1, Solution2> 
{
    protected override Data CreateData()
    {
        return [];
    }

    public override void ParseLine(string line, Data data)
    {
        data.AddRange(line.Split(",").Select(s =>
        {
            var parts = s.Split("-");
            return new Range(long.Parse(parts[0]), long.Parse(parts[1]));
        }));
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var answer = new BigInteger();
        foreach (var range in data)
        {
            answer += CountHitsBruteFource(range);
        }
        return answer;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return -1;
    }

    public BigInteger CountHitsBruteFource(Range range)
    {
        var numHits = new BigInteger();
        for (var i = range.Min; i <= range.Max; i++)
        {
            String numRepresentation = i.ToString();
            if ((numRepresentation.Length % 2) == 0)
            {
                var half = (numRepresentation.Length / 2);
                var firstHalf = numRepresentation[..half];
                var secondHalf = numRepresentation[half..];
                if (firstHalf == secondHalf)
                {
                    numHits += i;
                }
            }
        }

        return numHits;
    }
}