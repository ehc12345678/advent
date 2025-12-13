using advent.SharedBase;

namespace advent.Advent2025.Puzzle8;

using Solution1 = long;
using Solution2 = long;


public class Puzzle8 : Base<Data, Solution1, Solution2> 
{
    public override bool Solution1TestSolution => true;
    public override bool Solution2TestSolution => true;

    protected override Data CreateData()
    {
        return new Data();
    }

    public override void ParseLine(string line, Data data)
    {
        var parts = line.Split(',');
        data.AddPoint(new Point3d(int.Parse(parts[0]), int.Parse(parts[1]), int.Parse(parts[2])));
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var pointToCircuit = new Dictionary<Point3d, Circuit>();
        for (var i = 0; i < 1000; ++i)
        {
            ConnectClosestPointsToCircuit(pointToCircuit, data);
        }
        var ordered = pointToCircuit.Values.OrderByDescending(circuit => circuit.Points.Count).ToList();
        var topThree = ordered.GetRange(0, 3);
        return topThree.Select(circuit => circuit.Points.Count).Aggregate((acc, x) => acc * x);
    }

    private void ConnectClosestPointsToCircuit(Dictionary<Point3d, Circuit> pointToCircuit, Data data)
    {
        // create a list of all points (not already connected), sort by distance.  We do not need to take the square root
        // because the square doesn't change the comparison a > b == a^2 > b^2 for all integers
        var remainingPoints = new HashSet<Point3d>(data.Points);
        remainingPoints.RemoveWhere(pointToCircuit.ContainsKey);
        
        for (int i = 0; i < remainingPoints.Count - 1; ++i)
        {
            for (int j = i; j < remainingPoints.Count; ++j)
            {
                
            }
        }
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return -1;
    }
}

public class Point3d(int x, int y, int z)
{
    public int X { get; set; } = x;
    public int Y { get; set; } = y;
    public int Z { get; set; } = z;

    public override bool Equals(object? obj)
    {
        if (obj is Point3d p)
        {
            return Equals(p);
        }

        return false;
    }

    protected bool Equals(Point3d other)
    {
        return X == other.X && Y == other.Y && Z == other.Z;
    }

    public override int GetHashCode()
    {
        return HashCode.Combine(X, Y, Z);
    }

    public override string ToString()
    {
        return $"({X},{Y},{Z})";
    }
}

public class Circuit
{
    public HashSet<Point3d> Points { get; } = [];

    public bool HasPoint(Point3d pt)
    {
        return Points.Contains(pt);
    }

    public void AddPoint(Point3d pt)
    {
        Points.Add(pt);
    }
}

public class Data
{
    public HashSet<Point3d> Points { get; } = [];

    public void AddPoint(Point3d pt)
    {
        Points.Add(pt);
    }
}