using advent.Advent2025.Puzzle4;
using advent.SharedBase;

namespace advent.Advent2025.Puzzle7;

using Solution1 = long;
using Solution2 = long;


public class Puzzle7: Base<Data, Solution1, Solution2> 
{
    public override bool Solution1TestSolution => false;
    public override bool Solution2TestSolution => false;

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
        List<Point> visited = [];
        while (anyChanges)
        {
            var thisRoundChanges = false;
            foreach (var ray in new List<Ray>(data.Rays))
            {
                var currentPos = ray.CurrentPos;
                if (currentPos.Row < data.NumRows - 1)
                {
                    var nextRow = new Point(1, 0);
                    var newPos = currentPos + nextRow;
                    if (data.HasSplitter(newPos))
                    {
                        data.RemoveRay(ray);
                        var leftOfSplit = newPos + new Point(0, -1);
                        if (!visited.Contains(leftOfSplit))
                        {
                            data.AddRay(leftOfSplit);
                            visited.Add(leftOfSplit);
                            thisRoundChanges = true;
                        }

                        var rightOfSplit = newPos + new Point(0, 1);
                        if (!visited.Contains(rightOfSplit))
                        {
                            data.AddRay(rightOfSplit);
                            visited.Add(rightOfSplit);
                            thisRoundChanges = true;
                        }
                        split++;
                    }
                    else
                    {
                        if (!visited.Contains(newPos))
                        {
                            data.MoveRay(ray, newPos);
                            visited.Add(newPos);
                            thisRoundChanges = true;
                        }
                    }
                }
            }
            anyChanges = thisRoundChanges;
        }
        return split;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        var anyChanges = true;
        HashSet<string> visited = [];
        List<Path> allPaths = [];
        allPaths.AddRange(data.Rays.Select(ray => new Path(ray.CurrentPos)));
        while (anyChanges)
        {
            var thisRoundChanges = false;
            foreach (var path in new List<Path>(allPaths))
            {
                var currentPos = path.Last();
                if (currentPos.Row < data.NumRows - 1)
                {
                    var nextRow = new Point(1, 0);
                    var newPos = currentPos + nextRow;
                    if (data.HasSplitter(newPos))
                    {
                        allPaths.Remove(path);
                        var leftPath = path.AppendPathPoint(newPos + new Point(0, -1));
                        if (!visited.Contains(leftPath.ToString()))
                        {
                            allPaths.Add(leftPath);
                            visited.Add(leftPath.ToString());
                            thisRoundChanges = true;
                        }

                        var rightPath = path.AppendPathPoint(newPos + new Point(0, 1));
                        if (!visited.Contains(rightPath.ToString()))
                        {
                            allPaths.Add(rightPath);
                            visited.Add(rightPath.ToString());
                            thisRoundChanges = true;
                        }
                    }
                    else
                    {
                        var newPath = path.AppendPathPoint(newPos);
                        if (!visited.Contains(newPath.ToString()))
                        {
                            allPaths.Remove(path);
                            allPaths.Add(newPath);
                            visited.Add(newPath.ToString());
                            thisRoundChanges = true;
                        }
                    }
                }
            }
            anyChanges = thisRoundChanges;
        }
        return allPaths.Count;
    }
}

public class Data(HashSet<Point> splitters)
{
    private HashSet<Point> Splitters { get; } = splitters;
    public List<Ray> Rays { get; } = [];
    public int NumRows { get; set; }
    public int NumCols { get; set; }

    public bool AddRay(Point point)
    {
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
        ray.CurrentPos = newPos;
        Visit(newPos);
        return true;
    }

    private void Visit(Point point)
    {
        NumCols = Math.Max(NumCols, point.Col);
    }
}

public class Ray(Point pos)
{
    public Point CurrentPos { get; set; } = pos;

}

public class Path : List<Point>
{
    public Path(Point pos)
    {
        Add(pos);
    }

    private Path(Path path) : base(path)
    {
    }

    public Path AppendPathPoint(Point point)
    {
        return new Path(this) { point };
    }
    
    public override string ToString()
    {
        return string.Join("->", this.Select(p => p.ToString()));
    }
}
