using System.Numerics;
using System.Text.RegularExpressions;
using advent.SharedBase;

namespace advent.Advent2025.Puzzle6;

using Solution1 = BigInteger;
using Solution2 = BigInteger;

public class Puzzle6 : Base<Data, Solution1, Solution2> 
{
    public override bool Solution1TestSolution => false;
    public override bool Solution2TestSolution => true;

    protected override Data CreateData()
    {
        return new Data();
    }

    public override void ParseLine(string line, Data data)
    {
        var parts = Regex.Matches(line, @"\S+")
            .Select(m => m.Value)
            .ToList();        
        
        if (data.Columns.Count == 0)
        {
            for (var i = 0; i < parts.Count; i++)
            {
                data.Columns.Add(new Column());
            }
        }

        var count = 0;
        foreach (var part in parts)
        {
            switch (part)
            {
                case "*":
                    data.Columns[count].Op = ColumnOperation.Multiply;
                    break;
                
                case "+":
                    data.Columns[count].Op = ColumnOperation.Add;
                    break;
                
                default:
                    data.Columns[count].Numbers.Add(long.Parse(part));
                    break;
            }
            count++;
        }
    }

    public override Solution1 ComputeSolution(Data data)
    {
        BigInteger sum = new BigInteger();
        foreach (var column in data.Columns)
        {
            BigInteger columnTotal = column.Op == ColumnOperation.Multiply ? 
                column.Numbers.Aggregate(new BigInteger(1), (acc, next) => acc * next) : 
                column.Numbers.Aggregate(new BigInteger(0), (acc, next) => acc + next);
            sum += columnTotal;
        }
        return sum;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return new BigInteger(0);
    }
}

public enum ColumnOperation { Multiply, Add }
public class Column
{
    public List<long> Numbers { get; } = [];
    public ColumnOperation Op { get; set; } = ColumnOperation.Multiply;
}

public class Data
{
    public List<Column> Columns { get; } = [];
}