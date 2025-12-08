using advent.Advent2025.Puzzle4;
using advent.SharedBase;

namespace advent.Advent2025.Puzzle7;

using Solution1 = long;
using Solution2 = long;

public class Data(HashSet<Point> splitters)
{
    private HashSet<Point> Splitters { get; } = splitters;
    public List<Ray> Rays { get; } = [];
    public int NumRows { get; set; }
    public int NumCols { get; set; }

    private HashSet<Point> _visited = [];
    
    public bool AddRay(Point point)
    {
        if (_visited.Contains(point)) return false;
        Rays.Add(new Ray(point));
        Visit(point);
        return true;
    }

    public void RemoveRay(Ray ray)
    {
        Rays.Remove(ray);
    }

    public void AddSplitter(Point point)
    {
        Splitters.Add(point);
        NumCols = Math.Max(NumCols, point.Col);
    }

    public bool HasSplitter(Point point)
    {
        return Splitters.Contains(point);
    }

    public bool MoveRay(Ray ray, Point newPos)
    {
        if (_visited.Contains(newPos)) return false;
        ray.CurrentPos = newPos;
        Visit(newPos);
        return true;
    }

    private void Visit(Point point)
    {
        _visited.Add(point);
        NumCols = Math.Max(NumCols, point.Col);
    }

    public string ToPrintable()
    {
        string s = "";
        for (var row = 0; row < NumRows; row++)
        {
            for (var col = 0; col < NumCols; col++)
            {
                if (_visited.Contains(new Point(row, col)))
                {
                    s += '|';
                }
                else if (HasSplitter(new Point(row, col)))
                {
                    s += '^';
                }
                else
                {
                    s += '.';
                }
            }

            s += "\n";
        }

        return s;
    }
}

public class Ray(Point pos)
{
    //public List<Point> Path { get; } = [pos];
    public Point CurrentPos { get; set; } = pos;
}

public class Puzzle7: Base<Data, Solution1, Solution2> 
{
    public override bool Solution1TestSolution => false;
    public override bool Solution2TestSolution => true;

    protected override Data CreateData()
    {
        return new Data([]);
    }

    public override void ParseLine(string line, Data data)
    {
        for (var col = 0; col < line.Length; col++)
        {
            var point = new Point(data.NumRows, col);
            switch (line[col])
            {
                case 'S':
                    data.AddRay(point);
                    break;
                case '^':
                    data.AddSplitter(point);
                    break;
            }
        }
        data.NumRows++;
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var anyChanges = true;
        var split = 0;
        while (anyChanges)
        {
            var thisRoundChanges = false;
            foreach (var ray in new List<Ray>(data.Rays))
            {
                var currentPos = ray.CurrentPos;
                if (currentPos.Row < data.NumRows - 1)
                {
                    var newPos = currentPos + new Point(1, 0);
                    if (data.HasSplitter(newPos))
                    {
                        data.RemoveRay(ray);
                        thisRoundChanges = data.AddRay(newPos + new Point(0, -1)) || thisRoundChanges;
                        thisRoundChanges = data.AddRay(newPos + new Point(0, 1)) || thisRoundChanges;
                        split++;
                    }
                    else
                    {
                        thisRoundChanges = data.MoveRay(ray, newPos);
                    }
                }
            }
            anyChanges = thisRoundChanges;
        }
        // Console.WriteLine(data.ToPrintable());
        return split;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return 0;
    }
}