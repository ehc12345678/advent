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
        const int maxPosition = 100;
        int newPosition;
        numTimesCrossZero = 0;
        if (safeInstruction.Direction == Direction.Right)
        {
            newPosition = position + safeInstruction.NumTurns;
        }
        else
        {
            newPosition = position - safeInstruction.NumTurns;
        }

        switch (newPosition)
        {
            case > 0:
                numTimesCrossZero = newPosition / maxPosition;
                newPosition %= maxPosition;
                break;
            case < 0:
                numTimesCrossZero = (-newPosition / maxPosition) + 1;
                newPosition += (numTimesCrossZero * maxPosition);
                if (newPosition == 0)
                {
                    ++numTimesCrossZero;
                }
                break;
            default:
                ++numTimesCrossZero;
                break;
        }

        // Console.Write($"The dial is rotated");
        // Console.Write(safeInstruction.Direction == Direction.Left ? " L" : " R");
        // Console.Write(safeInstruction.NumTurns);
        // Console.Write($" to point at {newPosition}");
        // Console.WriteLine($"; during this rotation, it points at 0 {numTimesCrossZero} times.");
        return newPosition;
    }

    public int GetNewPosition(int position, Direction direction, int numTurns, out int numTimesCrossZero)
    {
        const int maxPosition = 100;
        var delta = (direction == Direction.Right ? numTurns : -numTurns);
        var newPosition = position + delta;

        if (delta > 0)
        {
            numTimesCrossZero = newPosition / maxPosition;
        }
        else if (delta < 0)
        {
            numTimesCrossZero = -(newPosition / maxPosition) + 1;
        }
        else
        {
            numTimesCrossZero = 0;
        }

        return ((position % maxPosition) + maxPosition) % maxPosition;
    }
}