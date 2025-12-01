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
        const int maxPosition = 100;
        var position = 50;
        var answer = 0;
        foreach (var safeInstruction in data)
        {
            var newPosition = (safeInstruction.Direction == Direction.Left) ?
                position - safeInstruction.NumTurns :
                position + safeInstruction.NumTurns;
            if (newPosition < 0)
            {
                newPosition += maxPosition;
            }

            newPosition %= maxPosition;
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
        const int maxPosition = 100;
        var position = 50;
        var answer = 0;
        foreach (var safeInstruction in data)
        {
            var newPosition = (safeInstruction.Direction == Direction.Left) ?
                position - safeInstruction.NumTurns:
                position + safeInstruction.NumTurns;

            int numTimesCrossZero = 0;
            if (safeInstruction.Direction == Direction.Right)
            {
                // 089 = 0, 137 = 1, 238 = 2, so this works
                numTimesCrossZero = newPosition / maxPosition;
                newPosition %= maxPosition;
            }
            else
            {
                // 089 = 0, -11 = 1, -145 = 2, -238 = 3 
                if (newPosition < 0)
                {
                    numTimesCrossZero = (-newPosition / maxPosition) + 1;
                    newPosition += maxPosition * numTimesCrossZero;
                }
            }
            answer += numTimesCrossZero;
            position = newPosition;
        }
        return answer;
    }
}