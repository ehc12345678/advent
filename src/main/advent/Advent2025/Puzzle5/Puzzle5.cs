using advent.SharedBase;

namespace advent.Advent2025.Puzzle5;

using Solution1 = long;
using Solution2 = long;

public class Data
{
    public List<Range<long>> Ranges { get; } = [];
    public List<long> Ids { get; } = [];
}

public class Puzzle5 : Base<Data, Solution1, Solution2> 
{
    private bool _pastRanges;

    public override bool Solution1TestSolution => false;
    public override bool Solution2TestSolution => true;

    protected override Data CreateData()
    {
        _pastRanges = false;
        return new Data();
    }

    public override void ParseLine(string line, Data data)
    {
        if (!_pastRanges)
        {
            if (line != "")
            {
                var split = line.Split("-");
                var range = new Range<long>(long.Parse(split[0]), long.Parse(split[1]), true, true);
                data.Ranges.Add(range);
            }
            else
            {
                _pastRanges = true;
            }
        }
        else
        {
            data.Ids.Add(long.Parse(line));
        }
    }

    public override Solution1 ComputeSolution(Data data)
    {
        var answer = 0L;
        foreach (var id in data.Ids)
        {
            var isInRange = data.Ranges
                .Exists(range => range.InRange(id));
            if (isInRange)
            {
                ++answer;
            }
        }

        return answer;
    }

    public override Solution2 ComputeSolution2(Data data)
    {
        return -1;
    }
}