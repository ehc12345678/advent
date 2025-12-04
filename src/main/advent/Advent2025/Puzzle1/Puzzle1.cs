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
    public override bool Solution1TestSolution => false;
    public override bool Solution2TestSolution => false;

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
            var newPosition = GetNewPosition(position, safeInstruction, safeInstruction.Direction, safeInstruction.NumTurns, out _);
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
            var newPosition = GetNewPosition(position, safeInstruction, safeInstruction.Direction, safeInstruction.NumTurns, out var numTimesCrossZero);
            answer += numTimesCrossZero;
            position = newPosition;
        }
        return answer;
    }

    public int GetNewPosition(int position, SafeInstruction safeInstruction, Direction direction, int numTurns,
        out int numTimesCrossZero)
    {
        const int max = 100; // positions 0..99
        numTimesCrossZero = 0;
        if (direction == Direction.Right)
        {
            var k0 = (position == 0) ? max : (max - position); // if start==0, first hit is at k=100
            if (k0 <= numTurns)
            {
                numTimesCrossZero = 1 + (numTurns - k0) / max;
            }
        }
        else 
        {
            var k0 = (position == 0) ? max : position; 
            if (k0 <= numTurns)
            {
                numTimesCrossZero = 1 + (numTurns - k0) / max;
            }
        }

        // compute new position normalized into 0..99
        var delta = (direction == Direction.Right) ? numTurns : -numTurns;
        var end = position + delta;
        var newPosition = ((end % max) + max) % max;
        return newPosition;
    }
    
    public int GetNewPositionBruteForce(int position, SafeInstruction safeInstruction, Direction direction,
        int numTurns, out int numTimesCrossZero)
    {
        const int max = 100; // range 0–99
        
        // Awful brute force
        var delta = direction == Direction.Right ? 1 : -1;
        var newPosition = position;
        numTimesCrossZero = 0;
        for (var i = 0; i < numTurns; ++i)
        {
            newPosition += delta;
            if (newPosition == max)
            {
                newPosition = 0;
            }
            else if (newPosition < 0)
            {
                newPosition = max - 1;
            }

            if (newPosition == 0)
            {
                numTimesCrossZero++;
            }
        }
        
        return newPosition;
    }
    
    
}