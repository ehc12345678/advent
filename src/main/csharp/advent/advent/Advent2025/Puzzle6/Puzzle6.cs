using System.Numerics;
using System.Text.RegularExpressions;
using advent.SharedBase;

namespace advent.Advent2025.Puzzle6;

using Solution1 = BigInteger;
using Solution2 = BigInteger;

public class Puzzle6 : Base<Data, Solution1, Solution2> 
{
    public override bool Solution1TestSolution => true;
    public override bool Solution2TestSolution => false;

    protected override Data CreateData()
    {
        return new Data();
    }
    
    public override void ParseLine(string line, Data data)
    {
        data.RawLines.Add(line);
    }
    
    public override Solution1 ComputeSolution(Data data)
    {
        TransformRawIntoCols(data);
        return data.Calculate();
    }

    private static void TransformRawIntoCols(Data data)
    {
        var operators = data.RawLines.Last();
        var matches = Regex.Matches(operators,  @"[+*]");
        foreach (Match match in matches)
        {
            data.Columns.Add(new Column()
            {
                Op = match.Value[0] == '*' ? ColumnOperation.Multiply : ColumnOperation.Add,
            }); 
        }

        var indexes = matches.Select(match => match.Index).ToList();
        var count = 0;
        foreach (var line in data.RawLines)
        {
            if (count == data.RawLines.Count - 1)
            {
                break;
            }
            var first = indexes.First();
            for (var colIndex = 0; colIndex < data.Columns.Count - 1; colIndex++)
            {
                var column = data.Columns[colIndex];
                var next = indexes[colIndex + 1];
                var thisNumber = line[first..next];
                column.Numbers.Add(thisNumber);
                first = next;
            }
            data.Columns.Last().Numbers.Add(line[first..]);
            count++;
        }
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        TransformRawIntoCols(data);
        
        // we can transform this solution into the previous
        Data transformedData = new Data();
        
        // simplifying assumption is that no numbers have a space in the middle like 7 4, so we can just parse it directly
        foreach (var column in data.Columns)
        {
            transformedData.Columns.Add(TransformColumnFor2(column));
        }
        return transformedData.Calculate();
    }

    private Column TransformColumnFor2(Column column)
    {
        var newColumn = new Column
        {
            Op = column.Op
        };
        var longestString = column.Numbers.Max(str => str.Length);
        for (var stringPos = 0; stringPos <= longestString; stringPos++)
        {
            var numberString = column.Numbers.Aggregate(
                "", (acc, entry) => acc + (stringPos < entry.Length ? entry[stringPos] : ""));

            numberString = numberString.Trim();
            if (numberString.Length > 0)
            {
                newColumn.Numbers.Add(numberString);
            }
        }
        return newColumn;
    }
}

public enum ColumnOperation { Multiply, Add }

public class Column
{
    public List<string> Numbers { get; } = [];
    public ColumnOperation Op { get; set; } = ColumnOperation.Multiply;
}

public class Data
{
    public List<String> RawLines { get; } = [];
    public List<Column> Columns { get; } = [];

    public BigInteger Calculate()
    {
        var sum = new BigInteger();
        foreach (var column in Columns)
        {
            var columnTotal = column.Op == ColumnOperation.Multiply ? 
                column.Numbers.Aggregate(new BigInteger(1), (acc, next) => acc * (long.Parse(next.Trim()))) : 
                column.Numbers.Aggregate(new BigInteger(0), (acc, next) => acc + (long.Parse(next.Trim())));
            sum += columnTotal;
        }
        return sum;
    }
}