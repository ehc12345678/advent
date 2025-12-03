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
        var answer = 0L;
        foreach (var line in data)
        {
            var thisSum = FirstSolutionCompute(line);
            var thisSum1 = SecondSolutionCompute(line,  2);
            if (thisSum != thisSum1)
            {
                Console.WriteLine(thisSum);
            }
            answer += thisSum1;
        }

        return answer;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        var answer = 0L;
        foreach (var line in data)
        {
            var thisSum1 = SecondSolutionCompute(line,  12);
            answer += thisSum1;
        }

        return answer;
    }

    private static long FirstSolutionCompute(string line)
    {
        var maxFirstDigit = FindMaxDigitWithPos(line, 0, line.Length - 1);
        var maxSecondDigit = FindMaxDigitWithPos(line, maxFirstDigit.Item2 + 1, line.Length);
        var thisSum = (maxFirstDigit.Item1 * 10) + maxSecondDigit.Item1;
        return thisSum;
    }
    
    private static long SecondSolutionCompute(string line, int codeLength)
    {
        var lastPossible = line.Length - codeLength + 1;
        var thisSum = 0L;

        var pos = 0;
        for (var i = 0; i < codeLength; i++)
        {
            var maxDigit = FindMaxDigitWithPos(line, pos, lastPossible);
            pos = maxDigit.Item2 + 1;
            var powerOfTen = codeLength - i - 1;
            thisSum += (long)(Math.Pow(10, powerOfTen)) * maxDigit.Item1;
            lastPossible++;
        }
        return thisSum;
    }

    private static Tuple<int, int> FindMaxDigitWithPos(string line, int firstPos, int lastPos)
    {
        var currentMax = (int)char.GetNumericValue(line[firstPos]);
        var maxPos = firstPos;
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