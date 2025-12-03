using advent.SharedBase;

namespace advent.Advent2025.Puzzle3;

using Data = List<String>;
using Solution1 = long;
using Solution2 = long;

public class Puzzle3 : Base<Data, Solution1, Solution2> 
{
    protected override Data CreateData()
    {
        return [];
    }

    public override void ParseLine(string line, Data data)
    {
        data.Add(line);
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var answer = 0;
        foreach (var line in data)
        {
            var maxFirstDigit = FindMaxDigitWithPos(line, 0, line.Length - 1);
            var maxSecondDigit = FindMaxDigitWithPos(line, maxFirstDigit.Item2 + 1, line.Length);
            var thisSum = (maxFirstDigit.Item1 * 10) + maxSecondDigit.Item1;
            answer += thisSum;
        }

        return answer;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return ComputeSolution(data);
    }

    private static Tuple<int, int> FindMaxDigitWithPos(string line, int firstPos, int lastPos)
    {
        int currentMax = (int)char.GetNumericValue(line[firstPos]);
        int maxPos = firstPos;
        for (var index = firstPos + 1; index < lastPos; index++)
        {
            var digit = (int)char.GetNumericValue(line[index]);
            if (digit > currentMax)
            {
                currentMax = digit;
                maxPos = index;
            }
        }
        return new Tuple<int, int>(currentMax, maxPos);
    }
}