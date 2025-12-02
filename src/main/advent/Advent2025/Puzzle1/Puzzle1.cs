using advent.SharedBase;

namespace advent.Advent2025.Puzzle1;

using Data = List<SafeInstruction>;
using Solution1 = long;
using Solution2 = long;

public enum Direction { Left, Right };

public struct SafeInstruction(Direction dir, int numTurns)
{
   public Direction Direction = dir;
   public int NumTurns = numTurns;
}

public class Puzzle1 : Base<Data, Solution1, Solution2> 
{
    protected override Data CreateData()
    {
        return [];
    }

    public override void ParseLine(string line, Data data)
    {
        var dir = line.StartsWith('L') ? Direction.Left : Direction.Right;
        var numTurns = int.Parse(line[1..]);
        data.Add(new SafeInstruction(dir, numTurns));
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var position = 50;
        var answer = 0;
        foreach (var safeInstruction in data)
        {
            var newPosition = GetNewPosition(position, safeInstruction, out _);
            if (newPosition == 0)
            {
                answer++;
            }
            position = newPosition;
        }
        return answer;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        var position = 50;
        var answer = 0;
        foreach (var safeInstruction in data)
        {
            var newPosition = GetNewPosition(position, safeInstruction, out var numTimesCrossZero);
            answer += numTimesCrossZero;
            position = newPosition;
        }
        return answer;
    }

    public int GetNewPosition(int position, SafeInstruction safeInstruction, out int numTimesCrossZero)
    {
        const int max = 100; // range 0–99
        var delta = (safeInstruction.Direction == Direction.Right ? safeInstruction.NumTurns : -safeInstruction.NumTurns);
        var end = position + delta;

        if (delta >= 0)
        {
            numTimesCrossZero = end / max;
        }
        else
        {
            switch (end)
            {
                case 0:
                    numTimesCrossZero = 1; // exactly land on zero
                    break;
                case < 0:
                    numTimesCrossZero = (-end) / max + 1;
                    break;
                default:
                    numTimesCrossZero = 0;
                    break;
            }
        }

        var newPosition = ((end % max) + max) % max;
        return newPosition;
    }
}