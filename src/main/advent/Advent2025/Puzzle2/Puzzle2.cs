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
            answer += CountHitsBruteForce(range, IsHit);
        }
        return answer;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        var answer = new BigInteger();
        foreach (var range in data)
        {
            answer += CountHitsBruteForce(range, IsHit2);
        }
        return answer;
    }

    public BigInteger CountHitsBruteForce(Range range, Func<long, bool> hitTestFunc)
    {
        var numHits = new BigInteger();
        for (var i = range.Min; i <= range.Max; i++)
        {
            if (hitTestFunc(i))
            {
                numHits += i;
            }
        }

        return numHits;
    }
    
    private static bool IsHit(long id)
    {
        String numRepresentation = id.ToString();
        if ((numRepresentation.Length % 2) == 0)
        {
            var half = (numRepresentation.Length / 2);
            var firstHalf = numRepresentation[..half];
            var secondHalf = numRepresentation[half..];
            return firstHalf == secondHalf;
        }

        return false;
    }

    public static bool IsHit2(long id)
    {
        String numRepresentation = id.ToString();
        var half = (numRepresentation.Length / 2);
        for (var i = 1; i <= half; i++)
        {
            if ((numRepresentation.Length % i) == 0)
            {
                var match = numRepresentation[..i];
                var good = true;
                for (var index = i; good && index < numRepresentation.Length; index += i)
                {
                    var check = numRepresentation[index..(index + i)];
                    good = check == match;
                }

                if (good)
                {
                    return true;
                }
            }
        }
        return false;
    }
}