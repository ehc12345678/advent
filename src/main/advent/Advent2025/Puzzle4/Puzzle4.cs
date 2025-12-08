using advent.SharedBase;

namespace advent.Advent2025.Puzzle4;

using Solution1 = long;
using Solution2 = long;

public class Puzzle4 : Base<Data, Solution1, Solution2>
{
    private int _row;

    public override bool Solution1TestSolution => false;
    public override bool Solution2TestSolution => false;
    
    protected override Data CreateData()
    {
        _row = 0;
        return new Data();
    }

    public override void ParseLine(string line, Data data)
    {
        data.AddRow(_row++, line);
    }

    public override Solution1 ComputeSolution(Data data)
    {
        DoOneRound(data, out var numRemoved);
        return numRemoved;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        var answer = 0;
        var working = data;
        int numRemoved;
        do
        {
            working = DoOneRound(working, out numRemoved);
            answer += numRemoved;
        } while (numRemoved > 0);

        return answer;
    }

    private Data DoOneRound(Data data, out int numRemoved)
    {
        var copy = data.CreateDeepCopy();
        numRemoved = 0;
        foreach (var rollPos in data.RollsOfPaper)
        {
            var numNeighbors = data.GetNeighborsOf(rollPos.Row, rollPos.Col);
            if (numNeighbors.Count < 4)
            {
                copy.RemoveRollOfPaper(rollPos);
                numRemoved++;
            }
        }
        return copy;
    }
}

public readonly record struct Point(int Row, int Col)
{
    public static Point operator +(Point first, Point second) => new(first.Row + second.Row, first.Col + second.Col);

    public override string ToString()
    {
        return $"({Row},{Col})";
    }
}


public class Data
{
    private readonly HashSet<Point> _rollsOfPaper = [];
    
    public HashSet<Point> RollsOfPaper => _rollsOfPaper;

    public Data CreateDeepCopy()
    {
        var copy = new Data();
        foreach (var pos in _rollsOfPaper)
        {
            copy.RollsOfPaper.Add(new Point(pos.Row, pos.Col));
        }

        return copy;
    }
    
    private void AddRollOfPaper(int row, int col)
    {
        _rollsOfPaper.Add(new Point(row, col));
    }

    public void RemoveRollOfPaper(Point pos)
    {
        _rollsOfPaper.Remove(pos);
    }

    private bool HasRollOfPaper(int row, int col)
    {
        return _rollsOfPaper.Contains(new Point(row, col));
    }

    private bool HasRollOfPaper(Point pos)
    {
        return HasRollOfPaper(pos.Row, pos.Col);
    }
    
    public List<Point> GetNeighborsOf(int row, int col)
    {
        Point pos = new Point(row, col);
        List<Point> neighbors =
        [
            new Point(-1, -1),
            new Point(-1, 0),
            new Point(-1, 1),
            new Point(0, -1),
            new Point(0, 1),
            new Point(1, -1),
            new Point(1, 0),
            new Point(1, 1),
        ];
        return neighbors
            .Select(delta => pos + delta)
            .Where(HasRollOfPaper)
            .ToList();
    }

    public void AddRow(int row, string line)
    {
        for (var col = 0; col < line.Length; col++)
        {
            if (line[col] == '@')
            {
                AddRollOfPaper(row, col);
            }
        }
    }
}