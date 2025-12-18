using System.Numerics;

namespace advent.SharedBase;

/**
 * All Ranges are inclusive at the low and exclusive at the high
 */
public readonly record struct Range<T>
    (T low, T high) : IComparable<Range<T>>
    where T: INumber<T>
{
    // This allows the Range to be mutable
    // public T Low { get; set; } = low;
    // public T High { get; set; } = high;
    public T Low => low;
    public T High => high;
    
    public bool InRange(T value)
    {
        var matchesLow = value.CompareTo(low) >= 0;
        var matchesHigh = value.CompareTo(high) < 0;
        return matchesLow && matchesHigh;
    }

    public bool Overlap(Range<T> other)
    {
        return other.InRange(low) || other.InRange(high) || InRange(other.Low) || InRange(other.High);
    }

    public int CompareTo(Range<T> other)
    {
        return Low.CompareTo(other.Low);
    }

    public Range<T> Combine(Range<T> other)
    {
        if (!Overlap(other))
        {
            throw new Exception("Ranges do not overlap");
        }
        var lowValue = Low.CompareTo(other.Low) <= 0 ? Low : other.Low;
        var highValue = High.CompareTo(other.High) >= 0 ? High : other.High;
        return new Range<T>(lowValue, highValue);
    }
    
    public override string ToString()
    {
        return $"({Low}, {High})";
    }
    
}